package org.tomhume.morse;

/**
 * Class to abstract away the details of actually sending Morse tokens - i.e. transmitting
 * via a shared queue, over a Phidget, or some other mechanism.
 * 
 * @author twhume
 *
 */
public interface SenderTransport {

	public void push(byte b);
	
	public void push(byte[] b);
}
