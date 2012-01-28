import static org.junit.Assert.*;

import org.junit.Test;
import org.tomhume.morse.MorseReceiver;
import org.tomhume.morse.MorseToken;


public class ReceivingTests {

	@Test
	public void testReceiveCharacterOK() {
		TestReceiverTransport transport = new TestReceiverTransport();
		MorseReceiver mr = new MorseReceiver(transport);
		transport.setBytes(new byte[]{MorseToken.DOT,MorseToken.DOT,MorseToken.DOT,MorseToken.STOP_CHAR});
		assertEquals('S', mr.receiveCharacter());
	}

	@Test
	public void testReceiveCharacterUnterminated() {
		TestReceiverTransport transport = new TestReceiverTransport();
		MorseReceiver mr = new MorseReceiver(transport);
		transport.setBytes(new byte[]{MorseToken.DOT,MorseToken.DOT,MorseToken.DOT});
		assertEquals(0, mr.receiveCharacter());
		assertEquals(3, transport.getRollbacks());
	}

	@Test
	public void testReceiveCharacterTooLong() {
		TestReceiverTransport transport = new TestReceiverTransport();
		MorseReceiver mr = new MorseReceiver(transport);
		transport.setBytes(new byte[]{MorseToken.DOT,MorseToken.DOT,MorseToken.DOT,MorseToken.DOT,MorseToken.DOT,MorseToken.DOT});
		assertEquals(0, mr.receiveCharacter());
		assertEquals(5, transport.getRollbacks());
		
	}

}
