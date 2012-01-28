package org.tomhume.morse;


public class MorseReceiver {
	private ReceiverTransport transport;
	private boolean stopWord = false;
	
	public MorseReceiver(ReceiverTransport rt) {
		this.transport = rt;
	}

	public char receiveCharacter() {
		byte[] charBytes = new byte[5];
		int idx = 0;
		
		byte b = transport.receive();
				
		while ((b!=-1) && (!MorseToken.isStop(b)) && (idx<5)) {
			charBytes[idx++] = b;
			b = transport.receive();
		}
		if (b==MorseToken.STOP_WORD) stopWord = true;
		if (MorseToken.isStop(b)) return MorseToken.parseFrom(charBytes, idx);
		transport.rollback(idx);
		// need to push back a certain number
		return 0;
	}

	public String receiveWord() {
		String s = null;
		char c;
		stopWord = false;
		while ((!stopWord) && (c = receiveCharacter())!=0) {
			if (s==null) s = "";
			s += c;
		}
		return s;
	}

	public String receive() {
		String ret = "";
		String word;
		while ((word = receiveWord())!=null) {
			ret = ret + word + " ";
		}
		return ret.trim();
	}
}
