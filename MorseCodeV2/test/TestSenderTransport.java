import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.tomhume.morse.MorseToken;
import org.tomhume.morse.SenderTransport;

/**
 * Test implementation of a SenderTransport, used for unit tests
 * 
 * @author twhume
 *
 */

public class TestSenderTransport implements SenderTransport {

	List<Byte> queue = new ArrayList<Byte>();
	
	@Override
	public void push(byte b) {
		queue.add(b);
	}

	@Override
	public void push(byte[] b) {
		for (int i=0; i<b.length; i++) push(b[i]);
	}

	public String asString() {
		Byte[] bytes = queue.toArray(new Byte[queue.size()]);
		return MorseToken.byteArrayToString(ArrayUtils.toPrimitive(bytes));
	}
}
