import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tomhume.morse.MorseReceiver;
import org.tomhume.morse.MorseSender;
import org.tomhume.morse.PhidgetReceiverTransport;
import org.tomhume.morse.PhidgetSenderTransport;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;


public class PhidgetReceivingTests {
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
	public void testCalibrate() throws PhidgetException {
		PhidgetReceiverTransport transport = new PhidgetReceiverTransport(ik, 30);
		transport.calibrate();
		assertTrue(transport.getLitValue()<1000);
		assertTrue(transport.getUnlitValue()>0);
		transport.close();
	}

	@Test
	public void testSendAndReceiveSingleWord() throws PhidgetException {
		PhidgetReceiverTransport recv_transport = new PhidgetReceiverTransport(ik, 30);
		PhidgetSenderTransport send_transport = new PhidgetSenderTransport(ik, 30);
//		recv_transport.calibrate();
		MorseSender sender = new MorseSender(send_transport);
		MorseReceiver receiver = new MorseReceiver(recv_transport);
		
		sender.send("THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG");
		assertEquals("THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG", receiver.receiveWord());
		recv_transport.close();
		send_transport.close();
	}

	@Test
	public void testSendAndReceiveTwoWords() throws PhidgetException {
		PhidgetReceiverTransport recv_transport = new PhidgetReceiverTransport(ik, 30);
		PhidgetSenderTransport send_transport = new PhidgetSenderTransport(ik, 30);
//		recv_transport.calibrate();
		MorseSender sender = new MorseSender(send_transport);
		MorseReceiver receiver = new MorseReceiver(recv_transport);
		
		sender.send("A B");
		assertEquals("A B", receiver.receive());
		recv_transport.close();
		send_transport.close();
	}

	@Test
	public void testSendAndReceiveSentence() throws PhidgetException {
		PhidgetReceiverTransport recv_transport = new PhidgetReceiverTransport(ik, 30);
		PhidgetSenderTransport send_transport = new PhidgetSenderTransport(ik, 30);
		recv_transport.calibrate();
		MorseSender sender = new MorseSender(send_transport);
		MorseReceiver receiver = new MorseReceiver(recv_transport);
		
		sender.send("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG");
		assertEquals("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG", receiver.receive());
		recv_transport.close();
		send_transport.close();
	}

	
}
