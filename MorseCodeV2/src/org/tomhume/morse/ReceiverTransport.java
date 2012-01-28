package org.tomhume.morse;

/**
 * Class to abstract away the details of receiving Morse tokens - i.e. getting
 * via a shared queue, over a Phidget, or some other mechanism.
 * 
 * @author twhume
 *
 */
public interface ReceiverTransport {

	/**
	 * Get a single byte out from the transport, or -1 if none available
	 * @return
	 */
	
	public byte receive();
	
	/**
	 * In some situations we need to tell the transport that we wish to pass bytes already received back to it.
	 * For instance, we may have read 2/3 of a morse symbol and the last third may not be there for us to read.
	 * In this case we need to tell the transport to keep those first 2, so we can read them with the third,
	 * and thus get the whole symbol, later on.
	 * 
	 * @param num Number of bytes to roll back
	 */
	
	public void rollback(int num);
	
}
