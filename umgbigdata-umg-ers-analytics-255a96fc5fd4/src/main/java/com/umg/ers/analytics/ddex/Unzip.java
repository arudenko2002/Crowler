package com.umg.ers.analytics.ddex;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
public class Unzip {
public static String gzippath = "";
public static String unzippath = "";

      public static void main(String[] args) {
          Unzip gZipFile = new Unzip();
	      Unzip.unGunzipFile(gzippath, unzippath);
	}
	public static void unGunzipFile(String compressedFile, String decompressedFile) {
	 byte[] buffer = new byte[1028];
	  try {
	 FileInputStream fileIn = new FileInputStream(compressedFile);
	 GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
	 FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);
	 int bytes_read;
	    while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
	fileOutputStream.write(buffer,0 , bytes_read);
	
	  }
	 gZIPInputStream.close();
	fileOutputStream.close();
	
      System.out.println("The file was decompressed successfully!");
	} catch (IOException ex) {
	ex.printStackTrace();
	System.out.println("Error Message: " + ex.getMessage());
	        }
	    }
	}