package com.mengcraft.account.util;

import java.security.MessageDigest;
import java.security.SecureRandom;

public final class SecureUtil {

	public static final SecureUtil DEFAULT = new SecureUtil();

	private static final char[] HEX_CHARS = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f'
	};

	private final SecureRandom random = new SecureRandom();

	public String digest(String in) throws Exception {
		if (in == null) {
			throw new NullPointerException();
		}
		return digest(in.getBytes());
	}

	private String digest(byte[] in) throws Exception {
		if (in == null) {
			throw new NullPointerException();
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(in);
		byte[] out = md.digest();
		return hex(out);
	}

	/**
	 * Generate a random string of a fixed length.
	 * 
	 * @param size Half length of output string. 
	 * @return
	 */
	public String random(int size) {
		byte[] input = new byte[size];
		random.nextBytes(input);
		return hex(input);
	}

	private String hex(byte[] out) {
		if (out == null) {
			throw new NullPointerException();
		}
		StringBuilder buf = new StringBuilder();
		for (byte b : out) {
			buf.append(HEX_CHARS[b >>> 4 & 0xf]);
			buf.append(HEX_CHARS[b & 0xf]);
		}
		return buf.toString();
	}

}
