package de.cbraeutigam.archint.experiment;

public class IntExp {

	public static void main(String[] args) {
		byte b = (byte) 0x80;
		int i = 0xFF & (int) b;
		System.out.println(Integer.toBinaryString(i));

	}

}
