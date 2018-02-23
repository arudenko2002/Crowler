package com.umg.ers.analytics.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {
	
	public static void main(String[] args) throws Exception {
		String outFilename = "E:/abdul_workspace/Hadoop/cloud-workspace/spotify-core/playlist_tracks_20160325.txt.gz";
       // String inFilename = "E:/abdul_workspace/Hadoop/cloud-workspace/spotify-core/playlist_tracks_20160325.txt";
		//GzipUtil.compressGzipFile(inFilename);
		getFileNameFromFilePath(outFilename);
	}
	
	private static String getFileNameFromFilePath(String filePath) {
		System.out.println(">> file path >>"+ filePath);
		String a = filePath.substring(filePath
				.lastIndexOf('/')+1);
		System.out.println(" :; a :: " + a);
		return a;
	}
	
	public static String compressGzipFile(String inFilename) {
		
		String outFilename = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        outFilename= inFilename+".gz";
        
try {
            
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(
                                 new OutputStreamWriter(
                                     new GZIPOutputStream(new FileOutputStream(outFilename))
                                 ));

            //Construct the BufferedReader object
            bufferedReader = new BufferedReader(new FileReader(inFilename));
            
            String line = null;
            
            // from the input file to the GZIP output file
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //Close the BufferedWrter
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            //Close the BufferedReader
            if (bufferedReader != null ){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	return outFilename;
         
    }

}
