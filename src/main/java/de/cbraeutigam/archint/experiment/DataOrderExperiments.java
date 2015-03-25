package de.cbraeutigam.archint.experiment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.cbraeutigam.archint.hashforest.InvalidInputException;
import de.cbraeutigam.archint.util.ChecksumProvider;
import de.cbraeutigam.archint.util.Ordering;

public class DataOrderExperiments {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidInputException {
		int i = 0x9ABC;
		System.out.println(i);
		System.out.println(i >> 24);
		System.out.println(i);
		
		
		System.out.println("\n write string \n");
		
		Ordering o1 = new Ordering(new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
		o1.add("foo");
		o1.add("bar");
		o1.add("baz");
		System.out.println(o1.getIdentifiers());
		
		StringWriter sw = new StringWriter();
		o1.writeTo(sw);
		
		String s = sw.toString();
		System.out.println(s);
		
		System.out.println("\n read string \n");
		
		Ordering o2 = new Ordering(new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
		StringReader sr = new StringReader(s);
		o2.readFrom(sr);
		System.out.println(o2.getIdentifiers());
		System.out.println(o2.isValid());
		
		System.out.println("\n is valid \n");
		
		Ordering o3 = new Ordering(new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
		System.out.println(o3.getIdentifiers());
		System.out.println(o3.isValid());
		o3.add("foo");
		System.out.println(o3.isValid());
		
		System.out.println("\n incomplete input string \n");
		
		Ordering o4 = new Ordering(new ChecksumProvider(MessageDigest.getInstance("SHA-512")));
		o4.readFrom(new StringReader(s.substring(0, 42)));
		System.out.println(o4.getIdentifiers());
		System.out.println(o4.isValid());
		
		System.out.println("\n output/input byte stream \n");
		
	}

}
