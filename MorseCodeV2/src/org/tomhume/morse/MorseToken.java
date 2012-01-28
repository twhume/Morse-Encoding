package org.tomhume.morse;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class MorseToken {
	public static final byte DOT = 0;
	public static final byte DASH = 1;
	public static final byte STOP_CHAR = 2;
	public static final byte STOP_WORD = 3;
	
	/* A-Z encodings for characters */
	public static final byte[][] ALPHA_CHARS = {
		{DOT, DASH},
		{DASH, DOT, DOT, DOT},
		{DASH, DOT, DASH, DOT},
		{DASH, DOT, DOT},
		{DOT},
		{DOT, DOT, DASH, DOT},
		{DASH, DASH, DOT},
		{DOT, DOT, DOT, DOT},
		{DOT, DOT},
		{DOT, DASH, DASH, DASH},
		{DASH, DOT, DASH},
		{DOT, DASH, DOT, DOT},
		{DASH, DASH},
		{DASH, DOT},
		{DASH, DASH, DASH},
		{DOT, DASH, DASH, DOT},
		{DASH, DASH, DOT, DASH},
		{DOT, DASH, DOT},
		{DOT, DOT, DOT},
		{DASH},
		{DOT, DOT, DASH},
		{DOT, DOT, DOT, DASH},
		{DOT, DASH, DASH},
		{DASH, DOT, DOT, DASH},
		{DASH, DOT, DASH, DASH},
		{DASH, DASH, DOT, DOT}
	};
	
	private static Logger logger = Logger.getLogger(MorseToken.class);

	/**
	 * Return the ITU Morse tokens for the character supplied
	 * 
	 * @param c Character to encode into Morse
	 * @return array of bytes, each containing a constant from this class
	 */
	
	public static byte[] tokensFor(char c) {
		char ch = Character.toUpperCase(c);
		if ((ch>='A') && (ch<='Z')) return ALPHA_CHARS[ch-'A'];
		else throw new IllegalArgumentException("can't encode '" + c + "'");
	}
	
	/**
	 * Return the Morse character corresponding to the contents of the byte array
	 * passed in.
	 * 
	 * @param in byte array containing Morse characters encoded as constants from this class
	 * @return character matching them
	 * @throws IllegalArgumentException if an array containing no valid Morse sequence is supplied
	 */
	
	public static char parseFrom(byte[] in, int length) {
		
		/* add all possible matches to a Hashtable */
		
		Hashtable<Character,byte[]> possibleCharacters = new Hashtable<Character,byte[]>();
		for (int i=0; i<ALPHA_CHARS.length; i++) {
			if (length==ALPHA_CHARS[i].length) possibleCharacters.put(Character.valueOf((char) ('A'+i)), ALPHA_CHARS[i]);
		}
		
		/* work through the input array, removing any that don't match */
		
		for (int i=0; (i<length) && (possibleCharacters.size()>1); i++) {
			Iterator<Map.Entry<Character,byte[]>> it = possibleCharacters.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<Character,byte[]> entry = it.next();
				if (entry.getValue().length<=i) {
					it.remove();
				} else if (entry.getValue()[i]!=in[i]) {
					it.remove();
				}
			}
		}
		if (possibleCharacters.size()==1) return possibleCharacters.keys().nextElement().charValue();
		
		if (possibleCharacters.size()==0) logger.warn("parseFrom() no match found");
		else if (possibleCharacters.size()>1) logger.warn("parseFrom() " + possibleCharacters.size() + " matches found");
		return '?';
	}
	
	/**
	 * Helper method to encode a byte-array of Morse characters as a string:
	 *  dashes as    '-'
	 *  dots as      '.'
	 *  stop_char as '_'
	 *  stop_word as '!'
	 *  
	 * @param in Byte array to convert
	 * @return String representation of that array
	 */
	
	public static String byteArrayToString(byte[] in) {
		if (in==null) return "null";
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<in.length; i++) {
			switch (in[i]) {
				case DOT: sb.append('.'); break;
				case DASH: sb.append('-'); break;
				case STOP_CHAR: sb.append('_'); break;
				case STOP_WORD: sb.append('!'); break;
				default: throw new IllegalArgumentException("byteArrayToString() weird character " + in[i]);
			}
		}
		return sb.toString();
	}

	public static boolean isStop(byte b) {
		return (b>MorseToken.DASH);
	}
	
}
