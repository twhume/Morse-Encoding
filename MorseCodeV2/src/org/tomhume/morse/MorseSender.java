package org.tomhume.morse;

import java.util.StringTokenizer;

/**
 * Sends text out over a SenderTransport.
 * 
 * @author twhume
 *
 */

public class MorseSender {

	private SenderTransport transport;
	
	public MorseSender(SenderTransport st) {
		this.transport = st;
	}

	/**
	 * Breaks a supplied sentence into words and delivers each, one at a time, to the SenderTransport
	 * 
	 * @param sentence
	 */
	public void send(String s) {
		StringTokenizer sentence = new StringTokenizer(s, " ");
		while (sentence.hasMoreTokens())
			sendWord(sentence.nextToken());
	}

	public void sendWord(String word) {
		for (int i=0; i<word.length(); i++) {
			sendCharacter(word.charAt(i));
		}
		transport.push(MorseToken.STOP_WORD);
	}
	
	public void sendCharacter(char ch) {
		transport.push(MorseToken.tokensFor(ch));
		transport.push(MorseToken.STOP_CHAR);
	}
	
}
