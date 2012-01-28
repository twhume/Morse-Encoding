import org.tomhume.morse.ReceiverTransport;

/**
 * Test implementation of a ReceiverTransport, used for unit tests
 * 
 * @author twhume
 *
 */

public class TestReceiverTransport implements ReceiverTransport {

	private byte[] contents = null;
	private int idx = 0;
	private int rollbacks = 0;
	
	public void setBytes(byte[] b) {
		this.contents = b;
		this.idx = 0;
	}

	@Override
	public byte receive() {
		if (idx<contents.length) return contents[idx++];
		return -1;
	}

	@Override
	public void rollback(int num) {
		rollbacks += num;
	}

	public int getRollbacks() {
		return rollbacks;
	}
}
