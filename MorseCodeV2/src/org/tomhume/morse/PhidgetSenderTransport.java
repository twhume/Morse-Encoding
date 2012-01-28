package org.tomhume.morse;

import org.apache.log4j.Logger;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

public class PhidgetSenderTransport implements SenderTransport {
	private InterfaceKitPhidget ik;
	private int wpm;
	
	private static final int BULB = 0;
	private static Logger logger = Logger.getLogger(PhidgetSenderTransport.class);
	
	public PhidgetSenderTransport(InterfaceKitPhidget i, int w) throws PhidgetException {
		wpm = w;
		ik = i;
		ik.openAny();
		ik.waitForAttachment();
		ik.setOutputState(BULB, false);
	}
	
	public void close() throws PhidgetException {
		if (ik!=null) {
			ik.close();
			ik = null;
		}
	}
	
	@Override
	public void push(byte b) {
		try {
			switch (b) {
				case MorseToken.DOT:		flash(1);
											break;
				case MorseToken.DASH:		flash(3);
											break;
				case MorseToken.STOP_CHAR:	delay(3);
											break;
				case MorseToken.STOP_WORD:	delay(4); // actually 7, but always follows a STOP_CHAR
											break;
				default:					logger.warn("push() weird byte " + b);
			}
		} catch (PhidgetException e) {
			e.printStackTrace();
			logger.warn("push() threw " + e);
		}
	}
	
	private void flash(int ticks) throws PhidgetException {
		ik.setOutputState(BULB, true);
		delay(ticks);
		ik.setOutputState(BULB, false);
		delay(1);
	}
	
	private void delay(int ticks) {
		int delay = (1200/wpm) * ticks;
		try { Thread.sleep(delay); } catch (InterruptedException e) {}
	}

	@Override
	public void push(byte[] b) {
		for (int i=0; i<b.length; i++)
			push(b[i]);
	}

}
