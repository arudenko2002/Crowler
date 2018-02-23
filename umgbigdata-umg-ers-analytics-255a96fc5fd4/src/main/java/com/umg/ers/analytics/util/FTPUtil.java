package com.umg.ers.analytics.util;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtil {

	public static FTPClient getFTPClient(String ftpHost, String ftpUserName, String ftpPassword){
		FTPClient ftpClient = new FTPClient();
	    boolean error = false;
	    try {
	      int reply;
	      ftpClient.connect(ftpHost);
	      System.out.println("Connected to " + ftpHost + ".");
	      System.out.print(ftpClient.getReplyString());

	      // After connection attempt, you should check the reply code to verify
	      // success.
	      reply = ftpClient.getReplyCode();

	      if(!FTPReply.isPositiveCompletion(reply)) {
	        ftpClient.disconnect();
	        System.err.println("FTP server refused connection.");
	      }
	      ftpClient.enterLocalPassiveMode();
		  ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		  ftpClient.login(ftpUserName, ftpPassword);
	    } catch(IOException e) {
	      error = true;
	      e.printStackTrace();
	    }
	    return error ? null : ftpClient;
	}

	public static void disconnect(FTPClient ftpClient){
		if(ftpClient != null && ftpClient.isConnected()) {
	        try {
	        	ftpClient.disconnect();
	        } catch(IOException ioe) {
	          // do nothing//kjh
	        }
	      }
	}
}
