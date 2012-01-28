

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.tomhume.morse.MorseReceiver;
import org.tomhume.morse.MorseSender;
import org.tomhume.morse.PhidgetReceiverTransport;
import org.tomhume.morse.PhidgetSenderTransport;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;


public class StatisticalTests {

	static Logger logger = Logger.getLogger(StatisticalTests.class);
	@Test
	public void test() {
		
		int TEST_RUNS = 10;
		int[] wpms = new int[]{100,95,90,85,80,75,70,65,60,55,50,45,40,35,30,25,20,15,10,5};
		String input = "Nymphs blitz quick vex dwarf jog DJs flock by when MTV ax quiz prog Big fjords vex quick waltz nymph Bawds jog flick quartz vex nymph Junk MTV quiz graced by fox whelps Bawds jog flick quartz vex nymp";

		for (int i=0; i<wpms.length; i++) {
			for (int j=0; j<TEST_RUNS; j++) {
				long startTime = System.currentTimeMillis();
				int result = runTest(wpms[i],input);
				long endTime = System.currentTimeMillis();
				logger.info("Test " + j+"," + wpms[i] +","+(endTime-startTime)+ "," + result);
			}
		}
	}
	
	private int runTest(int wpm, String input) {
		try {
			InterfaceKitPhidget ik = new InterfaceKitPhidget();
			PhidgetReceiverTransport recv_transport = new PhidgetReceiverTransport(ik, wpm);
			PhidgetSenderTransport send_transport = new PhidgetSenderTransport(ik, wpm);
			recv_transport.calibrate();
			MorseSender sender = new MorseSender(send_transport);
			MorseReceiver receiver = new MorseReceiver(recv_transport);
			
			sender.send(input);
			String output = receiver.receive();
			logger.debug(output);
			recv_transport.close();
			send_transport.close();
			ik.close();
			return StringUtils.getLevenshteinDistance(input.toUpperCase(), output);
		} catch (PhidgetException pe) {
			pe.printStackTrace();
			logger.warn("runTest() wpm="+wpm+": " + pe);
			return -1;
		}
	}

}
