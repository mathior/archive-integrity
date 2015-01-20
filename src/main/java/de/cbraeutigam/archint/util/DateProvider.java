package de.cbraeutigam.archint.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateProvider {
	
//	private static final String DATEPATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DATEPATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	
	public static Date string2Date(String formattedDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATEPATTERN);
		return sdf.parse(formattedDate);
	}
	
	public static String date2String(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATEPATTERN);
		return sdf.format(date);
	}
	

	public static void main(String[] args) throws ParseException {
		Date date = new Date();
		String formattedDate = date2String(date);
		System.out.println(formattedDate);
		
//		String anotherFormattedDate = "2015-01-01T01:23:45";
		String anotherFormattedDate = "2015-01-01T01:23:45.678-01:00";
		Date anotherDate = string2Date(anotherFormattedDate);
		System.out.println(anotherDate);
		
		// this fails if milliseconds and timezone information is lost
		Date date2 = new Date();
		String formattedDate2 = date2String(date2);
		Date date2Again = string2Date(formattedDate2);
		System.out.println(date2.equals(date2Again));
		
		String anotherFormattedDate2 = "2015-01T01:23:45";
		Date anotherDate2 = string2Date(anotherFormattedDate2);
		System.out.println(anotherDate);
	}

}
