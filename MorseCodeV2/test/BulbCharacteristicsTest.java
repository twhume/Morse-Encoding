import org.apache.log4j.Logger;
import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;


public class BulbCharacteristicsTest implements SensorChangeListener {
	private InterfaceKitPhidget ik = null;

	static Logger logger = Logger.getLogger(BulbCharacteristicsTest.class);
	private long startTime = 0;

	public static void main(String[] argv) throws PhidgetException {
		BulbCharacteristicsTest bct = new BulbCharacteristicsTest();
		bct.init();
		int TEST_RUNS = 200;
		for (int j=0; j<TEST_RUNS; j++) bct.runTest();
	}
	
	public void init() throws PhidgetException {
		ik = new InterfaceKitPhidget();
		ik.openAny();
		ik.waitForAttachment();
		ik.addSensorChangeListener(this);
		System.err.println("init done");
	}
	
	private void runTest() throws PhidgetException  {
		try {
			startTime=System.currentTimeMillis();
			ik.setOutputState(0,  false);
			try { Thread.sleep(100); } catch (InterruptedException e) {};
			ik.setOutputState(0,  true);
			try { Thread.sleep(1000); } catch (InterruptedException e) {};
			ik.setOutputState(0,  false);
			try { Thread.sleep(1000); } catch (InterruptedException e) {};
		} catch (PhidgetException pe) {
			pe.printStackTrace();
			logger.warn("runTest() : " + pe);

		}
	}

	@Override
	public void sensorChanged(SensorChangeEvent arg0) {
		if (arg0.getIndex()!=0) return;
		System.out.println(System.currentTimeMillis()-startTime+","+ arg0.getValue());
	}
}
