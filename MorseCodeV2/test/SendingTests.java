import static org.junit.Assert.*;

import org.junit.Test;
import org.tomhume.morse.MorseSender;


public class SendingTests {

	@Test
	public void testCharacter() {
		TestSenderTransport transport = new TestSenderTransport();
		MorseSender sender = new MorseSender(transport);
		sender.sendCharacter('S');
		assertEquals("..._", transport.asString());
	}

	@Test
	public void testWord() {
		TestSenderTransport transport = new TestSenderTransport();
		MorseSender sender = new MorseSender(transport);
		sender.sendWord("SOS");
		assertEquals("..._---_..._!", transport.asString());
	}

	@Test
	public void testSentence() {
		TestSenderTransport transport = new TestSenderTransport();
		MorseSender sender = new MorseSender(transport);
		sender.send("SOS SOS");
		assertEquals("..._---_..._!..._---_..._!", transport.asString());
	}

}
