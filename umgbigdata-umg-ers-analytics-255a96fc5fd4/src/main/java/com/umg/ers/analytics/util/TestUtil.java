package com.umg.ers.analytics.util;

import java.io.IOException;

import org.apache.http.ParseException;

public class TestUtil {
	public static void main(String[] args){
		String [] a = "spotify:user:-2k-:playlist:5cCL6j5NolSxZpzko2Sx6F".split(":");
		System.out.println(" 0 " + a[0]);
		System.out.println(" 1 " + a[1]);
		System.out.println(" 2 " + a[2]);
		System.out.println(" 3 " + a[3]);
		System.out.println(" 4 " + a[4]);
		System.out.println("Your first argument is: "+args[0]);
		System.out.println("Your first argument is: "+args[1]);
		
	}

}
