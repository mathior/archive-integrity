package de.cbraeutigam.archint.util;

import java.security.MessageDigest;

import de.cbraeutigam.archint.hashforest.HashValue;

public class ChecksumProvider {

	private final MessageDigest md;
	public String checksum;

	/*
	 * TODO: maybe replace the checksum by a CRC32 to prevent
	 * "NoSuchAlgorithmException" when instantiating an algorithm.
	 * 
	 */
	public ChecksumProvider(MessageDigest md) {
		this.md = md;
		md.reset();
		checksum = null;
	}

	public void update(byte[] bytes) {
		md.update(bytes);
		checksum = null;
	}

	public String get() {
		if (checksum == null) {
			checksum = HashValue.bytes2hex(md.digest());
		}
		return checksum;
	}

}
