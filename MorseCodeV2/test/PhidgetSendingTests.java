import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tomhume.morse.MorseSender;
import org.tomhume.morse.PhidgetSenderTransport;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;


public class PhidgetSendingTests {
	static InterfaceKitPhidget ik = null;
	
	@BeforeClass
	public static void setUp() throws PhidgetException {
		ik = new InterfaceKitPhidget();
	}
	
	@AfterClass
	public static void tearDown() throws PhidgetException {
		ik.close();
	}
	
	@Test
	public void testSendSOS() throws PhidgetException {
		PhidgetSenderTransport phidget = new PhidgetSenderTransport(ik, 30);
		MorseSender sender = new MorseSender(phidget);
		sender.sendWord("SOS");
		phidget.close();
	}

	@Test
	public void testSendWords() throws PhidgetException {
		PhidgetSenderTransport phidget = new PhidgetSenderTransport(ik, 30);
		MorseSender sender = new MorseSender(phidget);
		sender.send("AS TO");
		phidget.close();
	}

}
