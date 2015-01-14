package de.cbraeutigam.archint.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class OrderingTest {
	
	private ChecksumProvider cp = null;
	private List<String> testEmptyList = new ArrayList<String>();
	private List<String> testItems = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
	{
		add("foo"); add("bar"); add("baz");
	}};
	private String validString1 =
			"cb377c10b0f5a62c803625a799d9e908be45e767f5d147d4744907cb05597aa4"
			+ "edd329a0af147add0cf4181ed328fa1e7994265826b3ed3d7ef6f067ca99185a"
			+ "\nfoo\nbar\nbaz";
	private String inValidString1 =
			"cb377c10b0f5a62c803625a799d9e908be45e767f5d147d4744907cb05597aa4"
			+ "edd329a0af147add0cf4181ed328fa1e7994265826b3ed3d7ef6f067ca99185a"
			+ "\nfoo\nbaz\nbar";
	private String inValidString2 =
			"cb377c10b0f5a62c803625a799d9e908be45e767f5d147d4744907cb05597aa4"
			+ "edd329a0af147add0cf4181ed328fa1e799"
			+ "\nfoo\nbaz\nbar";
	private String inValidString3 =
			"cb377c10b0f5a62c803625a799d9e908be45e767f5d147d4744907cb05597aa4"
			+ "edd329a0af147add0cf4181ed328fa1e7994265826b3ed3d7ef6f067ca99185a"
			+ "\nfoo\nbaz";
	
	@Before
	public void setUp() {
		try {
			cp = new ChecksumProvider(MessageDigest.getInstance("SHA-512"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAddAndGet() {
		Ordering o = new Ordering(cp);
		assertTrue(o.getIdentifiers().equals(testEmptyList));
		for (String s : testItems) {
			o.add(s);
		}
		assertTrue(o.getIdentifiers().equals(testItems));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testGetIdentifiers() {
		Ordering o = new Ordering(cp);
		for (String s : testItems) {
			o.add(s);
		}
		o.getIdentifiers().add("quux");
	}
	
	@Test
	public void testWriteToString() throws IOException {
		Ordering o = new Ordering(cp);
		for (String s : testItems) {
			o.add(s);
		}
		StringWriter sw = new StringWriter();
		o.writeTo(sw);
		assertTrue(sw.toString().equals(validString1));
	}
	
	@Test
	public void testReadFromString() throws IOException {
		Ordering o = new Ordering(cp);
		o.readFrom(new StringReader(validString1));
		assertTrue(o.isValid());
		assertTrue(o.getIdentifiers().equals(testItems));
	}

	@Test
	public void testIsValid() throws IOException {
		Ordering o = new Ordering(cp);
		assertTrue(o.isValid());
		o.add("foo");
		assertFalse(o.isValid());
		o.readFrom(new StringReader(validString1));
		assertTrue(o.isValid());
		o.readFrom(new StringReader(inValidString1));
		assertFalse(o.isValid());
		o.readFrom(new StringReader(inValidString2));
		assertFalse(o.isValid());
		o.readFrom(new StringReader(inValidString3));
		assertFalse(o.isValid());
	}
	
	@Test
	public void testWriteReadBinary() throws IOException {
		Ordering o = new Ordering(cp);
		for (String s : testItems) {
			o.add(s);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		o.writeTo(bos);
		assertTrue(bos.size() == 157);
		
		Ordering o2 = new Ordering(cp);
		o2.readFrom(new ByteArrayInputStream(bos.toByteArray()));
		assertTrue(o2.isValid());
		assertTrue(o2.getIdentifiers().equals(testItems));
	}
	
	// regression
	@Test
	public void testWriteReadInt() throws IOException {
		Ordering o = new Ordering(cp);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int outVal = 0;
		o.writeInt(outVal, bos);
		int inVal = o.readInt(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals(inVal, outVal);
		
		bos.reset();
		outVal = -1;
		o.writeInt(outVal, bos);
		inVal = o.readInt(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals(inVal, outVal);
		
		bos.reset();
		outVal = 128;
		o.writeInt(outVal, bos);
		inVal = o.readInt(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals(inVal, outVal);
		
		bos.reset();
		outVal = Integer.MAX_VALUE;
		o.writeInt(outVal, bos);
		inVal = o.readInt(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals(inVal, outVal);
		
		bos.reset();
		outVal = Integer.MIN_VALUE;
		o.writeInt(outVal, bos);
		inVal = o.readInt(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals(inVal, outVal);
	}

}
