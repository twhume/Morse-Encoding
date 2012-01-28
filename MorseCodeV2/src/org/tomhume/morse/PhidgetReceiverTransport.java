package org.tomhume.morse;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;

public class PhidgetReceiverTransport implements ReceiverTransport, SensorChangeListener {
	private InterfaceKitPhidget ik;
	private int wpm;
	
	/* values for the bulb being lit or unlit, updated by calibrate() */
	
	private int litValue = 1000;
	private int unlitValue = 0;
	
	private boolean bulbOn = false;			/* flag: is the bulb currently on? */
	private long bulbLastChanged = 0;		/* timestamp of the last change in bulb state */
	private boolean lastWasntStop = false;	/* was the last character sent a stop? */
	
	private static final int BULB = 0;
	private static final int SENSOR = 0;
	private static final int CALIBRATION_READINGS = 3;

	private List<Byte> queue = new ArrayList<Byte>();
	
	private static Logger logger = Logger.getLogger(PhidgetSenderTransport.class);
	
	public PhidgetReceiverTransport(InterfaceKitPhidget i, int w) throws PhidgetException {
		wpm = w;
		ik = i;
		ik.openAny();
		ik.waitForAttachment();
		ik.setOutputState(BULB, false);
		ik.addSensorChangeListener(this);
	}
	
	@Override
	public byte receive() {
		if (queue.size()==0) return flush();
		byte b = queue.get(0);
		queue.remove(0);
		return b;
	}

	@Override
	public void rollback(int num) {
		logger.warn("rollback not implemented yet!");
	}

	private void queue(byte b) {
		queue.add(b);
		lastWasntStop = (b<MorseToken.STOP_CHAR);
	}
	
	/**
	 * When we reach the end of a message and stop processing incoming sensor
	 * changes, we need to append stop characters to the end of the feed,
	 * so the parser can rely on their being there.
	 * 
	 * This method will be called if the queue appears empty. If the last
	 * item added to the queue was a DOT or DASH and some time has passed,
	 * then add in a stop character.
	 * 
	 * @return -1 if the queue is definitely empty, or a STOP_CHAR if it's
	 * been a while since the last character
	 */
	
	private byte flush() {
		if (lastWasntStop) {
			long now = System.currentTimeMillis();
			if (calculateTicks(now-bulbLastChanged)>=3) {
				queue(MorseToken.STOP_CHAR);
				return receive();
			} else {
				try { Thread.sleep(3 * (1200/wpm)); } catch (InterruptedException e) {}
				return flush();
			}
		} else return -1;
	}
	
	@Override
	public void sensorChanged(SensorChangeEvent sce) {
		if (sce.getIndex()!=SENSOR) return;

		/* Work out whether we're nearer to lit or unlit */
		
		if (Math.abs(litValue-sce.getValue()) < Math.abs(unlitValue-sce.getValue())) {
			bulbLit(true);
		} else {
			bulbLit(false);
		}
	}
	
	private int calculateTicks(long time) {
		return (int) (time / (1200/wpm));
	}
	
	private void bulbLit(boolean state) {
		long now = System.currentTimeMillis();
		
		/*
		 * We're turning the bulb on. See how long it has been turned off for and insert
		 * the approach sized delay: none if it's been off for 1 tick, STOP_CHAR if it's
		 * been off for 3 ticks, STOP_WORD if it's been off for 7 ticks
		 */
		
		if ((state) && (!bulbOn)) {
			if (bulbLastChanged==0) {
				/* this is the first time the bulb has been turned on, so we don't
				 * do anything to insert delays now.
				 */
			} else {
				int ticks = calculateTicks(now - bulbLastChanged);
				if (ticks<=1) { /* do nothing, it's a natural inter-token delay */ }
				else if (ticks<=4) { queue(MorseToken.STOP_CHAR); }
				else if (ticks<=8) { queue(MorseToken.STOP_WORD); }
				else {
					logger.warn("bulbLit() ignoring a delay of " + ticks + " ticks");
				}
			}
			bulbLastChanged = now;
		}
		
		/*
		 * We're turning the bulb off. See how long it has been turned on for and insert
		 * the appropriate sized token: a dot if it's been on for 1 tick, a dash if it's 
		 * been on for 3 ticks.
		 */
		
		else if ((!state) && (bulbOn)) {
			int ticks = calculateTicks(now - bulbLastChanged);
			if (ticks<=1) { queue(MorseToken.DOT); }
			else if (ticks<=3) { queue(MorseToken.DASH); }
			else {
				logger.warn("bulbLit() ignoring a flash of " + ticks + " ticks");
			}
			bulbLastChanged = now;
		}
		
		/* If the bulb has been off for a while, and is still off, then put a STOP_WORD
		 * into the queue.. it's the end of the transmission
		 */
		
		else if ((!state) && (!bulbOn) && (calculateTicks(now-bulbLastChanged)>7))
			queue(MorseToken.STOP_WORD);
		
		bulbOn = state;
	}
	
	public void close() throws PhidgetException {
		if (ik!=null) {
			ik.removeSensorChangeListener(this);
			ik.close();
			ik = null;
		}
	}
	
	/**
	 * Calibrates the sensor for this run by taking three readings each when the bulb is lit
	 * or unlit, and averaging them to provide typical lit/unlit values
	 */
	
	public void calibrate() {
		logger.warn("taking calibration readings, ensure kit is set up");
		try {
			ik.setOutputState(BULB, false);
			
			unlitValue = 0;
			for (int i=0; i<CALIBRATION_READINGS; i++) {
				try { Thread.sleep(500); } catch (InterruptedException e) {}
				int reading = ik.getSensorValue(SENSOR);
				unlitValue += reading;
			}
			unlitValue /= CALIBRATION_READINGS;

			ik.setOutputState(BULB, true);

			litValue = 0;
			for (int i=0; i<CALIBRATION_READINGS; i++) {
				try { Thread.sleep(500); } catch (InterruptedException e) {}
				int reading = ik.getSensorValue(SENSOR);
				litValue += reading;
			}
			litValue /= CALIBRATION_READINGS;

			ik.setOutputState(BULB, false);
			
			logger.debug("calibrated, lit=" + litValue + ",unlit="+unlitValue);

			/* wait for the bulb to go out properly */
			try { Thread.sleep(2000); } catch (InterruptedException e) {};
		} catch (PhidgetException e) {
			e.printStackTrace();
			logger.fatal("couldn't calibrate sensor: " + e);
			
		}
		
	}

	public int getLitValue() {
		return litValue;
	}

	public int getUnlitValue() {
		return unlitValue;
	}

	public static int getBulb() {
		return BULB;
	}

}
