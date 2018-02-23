package com.umg.ers.analytics.google.youtube;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtubereporting.YouTubeReporting;
import com.google.api.services.youtubereporting.YouTubeReporting.Media.Download;
import com.google.api.services.youtubereporting.model.Job;
import com.google.api.services.youtubereporting.model.ListJobsResponse;
import com.google.api.services.youtubereporting.model.ListReportsResponse;
import com.google.api.services.youtubereporting.model.Report;
import com.google.common.collect.Lists;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;

/**
 * This sample retrieves reports created by a specific job by:
 *
 * 1. Listing the jobs using the "jobs.list" method. 2. Retrieving reports using
 * the "reports.list" method.
 *
 * @author Ibrahim Ulukaya
 */
public class RetrieveReports {

	/**
	 * Define a global instance of a YouTube Reporting object, which will be
	 * used to make YouTube Reporting API requests.
	 */
	public static final String DEFAULT_DATE_PATTERN =  ".*([0-9]{4})([0-9]{2})([0-9]{2}).*";
	
	private static YouTubeReporting youtubeReporting;

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	// private static final String SERVICE_ACCOUNT_EMAIL =
	// "981009129543-compute@developer.gserviceaccount.com";
	private static final String SERVICE_ACCOUNT_EMAIL = "umg-you-tube-service@appspot.gserviceaccount.com";

	private static GoogleCredential gcred;

	private static final String CONTENT_OWNER = "ghgcd_xPPR_URSW5U_cwtQ";
	
	private static Connection c = null;
		
    private static Statement stmt = null;

	private static String start_date = "";

	private static String end_date = "";

	private static List<String> cleanupFileList = new ArrayList<String>();

	// UNCOMMENT THE FOLLOWING CODE FOR AWS DATA PIPELINE CONFIGURATIONS

	public static List<String> getCleanupFileList() {
		return cleanupFileList;
	}

	public static void setCleanupFileList(List<String> cleanupFileList) {
		RetrieveReports.cleanupFileList = cleanupFileList;
	}

	public static YoutubeModel youtubeModel;

	private static String CONVERT_TO_ZULU = "T00:00:00.000000Z";

	public static AWSUtil getaWSUtil() {
		return new AWSUtil();
	}

	// static block to load all the class levell data before invocation of
	// object
	static {
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/yt-analytics.readonly",
				"https://www.googleapis.com/auth/youtube.readonly",
				"https://www.googleapis.com/auth/yt-analytics-monetary.readonly");

		try {
			// Create a listener for automatic refresh OAuthAccessToken
			List<CredentialRefreshListener> list = new ArrayList<CredentialRefreshListener>();
			list.add(new CredentialRefreshListener() {
				public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
					System.out.println(" ::: tokenResponse ::: " + tokenResponse.toPrettyString());
				}

				public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse)
						throws IOException {
					System.err.println("Error!!!!!!!!!!!!!!!!!!: " + tokenErrorResponse.toPrettyString());
					credential.getRefreshToken();
				}
			});

			String accessToken = "notasecret";
			gcred = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY)
					.setServiceAccountId(SERVICE_ACCOUNT_EMAIL).setServiceAccountScopes(scopes)
					// .setServiceAccountPrivateKeyFromP12File(new
					// File("E:\\New folder\\youtubereports\\umg-you-tube-service-807d959a47e3.p12"))
					.setServiceAccountPrivateKeyFromP12File(new File("umg-you-tube-service-807d959a47e3.p12"))
					.setRefreshListeners(list).build();

			gcred.refreshToken();
			accessToken = gcred.getAccessToken();

		} catch (Exception e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// UNCOMMENT THE FOLLOWING CODE FOR AWS DATA PIPELINE CONFIGURATIONS
	private static void upload(String path2) throws InterruptedException {
		try {
			String fileNameFromPath = FileUtil.getFileNameFromFilePath(path2);
			// System.out.println("browseFileName " + fileNameFromPath);
			String s3OutputPath = getS3OutputPathPartition(fileNameFromPath, youtubeModel.getPartitionDate(),
					youtubeModel.getS3RawOutputPath() + "/" + fileNameFromPath);
			// System.out.println("s3PlaylistUriPath"+ s3OutputPath +">>>"+
			// path2);
			getaWSUtil().uploadToS3AndWWaitForCompletion(youtubeModel.getS3OutputBucketName(), s3OutputPath, path2);
			getCleanupFileList().add(youtubeModel.getWorkDir());
			FileUtil.cleanupFiles(getCleanupFileList().toArray(new String[0]));
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lists reporting jobs. (jobs.listJobs)
	 * 
	 * @return true if at least one reporting job exists
	 * @throws IOException
	 */
	private static List<Job> listReportingJobs() throws IOException {
		// Call the YouTube Reporting API's jobs.list method to retrieve
		// reporting jobs.
		ListJobsResponse jobsListResponse = youtubeReporting.jobs().list().setOnBehalfOfContentOwner(CONTENT_OWNER)
				.execute();
		List<Job> jobsList = jobsListResponse.getJobs();
		if (jobsList == null || jobsList.isEmpty()) {
			System.out.println("No jobs found.");
			// return false;
		} else {
			// Print information from the API response.
			System.out.println("\n================== Reporting Jobs ==================\n");
			for (Job job : jobsList) {
				// System.out.println("- Id: " + job.getId());
				// System.out.println("- Name: " + job.getName());
				System.out.println("- Report Type Id: " + job.getReportTypeId());
				System.out.println("\n-------------------------------------------------------------\n");
			}
		}
		return jobsList;
	}

	/**
	 * Lists reports created by a specific job. (reports.listJobsReports)
	 *
	 * @param jobId
	 *            The ID of the job.
	 * @throws IOException
	 */
	private static List<Report> retrieveReports(String jobId) throws IOException {
		// Call the YouTube Reporting API's reports.list method
		// to retrieve reports created by a job.
		ListReportsResponse reportsListResponse = youtubeReporting.jobs().reports().list(jobId)
				.setStartTimeAtOrAfter(start_date).setStartTimeBefore(end_date)
				.setOnBehalfOfContentOwner(CONTENT_OWNER).execute();
		List<Report> reportslist = reportsListResponse.getReports();

		List<Report> filteredRpt = new ArrayList<Report>();
		if (reportslist == null || reportslist.isEmpty()) {
			System.out.println("No reports found for : " + jobId);
			// return false;
		} else {
			// Print information from the API response.
			System.out.println("\n============= Reports for the job " + jobId + " =============\n");
			for (Report report : reportslist) {
				System.out.println("- Id: " + report.getId());
				System.out.println("- From: " + report.getStartTime());
				System.out.println("- To: " + report.getEndTime());
				System.out.println("- Download Url: " + report.getDownloadUrl());
				System.out.println("\n-------------------------------------------------------------\n");
				filteredRpt.add(reportslist.get(0));
				break;
			}
		}
		return filteredRpt;
	}

	// UNCOMMENT THE FOLLOWING CODE FOR AWS DATA PIPELINE CONFIGURATIONS
	/*
	 * Download the report specified by the URL. (media.download)
	 * 
	 * @param reportUrl The URL of the report to be downloaded.
	 * 
	 * @throws IOException
	 */

	private static boolean downloadReport(String reportUrl, String strFileName) throws IOException,
			InterruptedException {
		// Call the YouTube Reporting API's media.download method to download a
		// report.
		boolean status = false;
		System.out.println(reportUrl);
		try {
			Download request = youtubeReporting.media().download("");

			mkDir(youtubeModel.getWorkDir());
			String newStrFileName = youtubeModel.getWorkDir() + strFileName;
			FileOutputStream fop = new FileOutputStream(new File(newStrFileName));
			// Set the download type and add an event listener.
			MediaHttpDownloader downloader = request.getMediaHttpDownloader();
			// Thread.sleep(10000);
			downloader.download(new GenericUrl(reportUrl), fop);
			fop.flush();
			fop.close();
			upload(newStrFileName);
			status = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
		return status;
	}

	// ***** COMMENT THE FOLLOWING CODE FOR AWS DATA PIPELINE
	// CONFIGURATIONS*****
	/**
	 * Download the report specified by the URL. (media.download)
	 *
	 * @param reportUrl
	 *            The URL of the report to be downloaded.
	 * @throws IOException
	 */
	/*
	 * private static boolean downloadReport(String reportUrl,String
	 * strFileName) throws IOException { // Call the YouTube Reporting API's
	 * media.download method to download a report. Download request =
	 * youtubeReporting.media().download(""); FileOutputStream fop = new
	 * FileOutputStream(new File(strFileName));
	 * 
	 * // Set the download type and add an event listener. MediaHttpDownloader
	 * downloader = request.getMediaHttpDownloader(); downloader.download(new
	 * GenericUrl(reportUrl), fop); return true; }
	 */
	// UNCOMMENT THE FOLLOWING CODE FOR AWS DATA PIPELINE CONFIGURATIONS
	public static String getS3OutputPathPartition(final String fileName,final String dateString,final String baseOutputPath) {
		String pattenString = DEFAULT_DATE_PATTERN;
		Pattern pattern = Pattern.compile(pattenString);
		Matcher matcher = pattern.matcher(dateString);
		String partitionSeperator = null;
		if (matcher.find()) {
			partitionSeperator = baseOutputPath
					+ "/year="+ Integer.parseInt(matcher.group(1) )
					+ "/month=" + Integer.parseInt(matcher.group(2)) 
					+ "/day=" + Integer.parseInt(matcher.group(3)) 
					+"/"
					+ fileName;
		}
		System.out.println("partitionSeperator: " + partitionSeperator);
		return partitionSeperator;
	}
	
	public static void mkDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
	}

	/**
	 * Retrieve reports.
	 *
	 * @param args
	 *            command line args (not used).
	 * @throws InterruptedException
	 * @throws GoogleJsonResponseException
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws InterruptedException, GoogleJsonResponseException, ClassNotFoundException {
		int initial_counter = 0;
		int termination_counter = 0;
		if (args.length != 8) {
			System.out.println("Invalid number of arguments !!  Please pass 6 parameters.");
			System.exit(0);
		} else {
			int initial_counter_arg = Integer.parseInt(args[6]);
			int end_counter_arg = Integer.parseInt(args[7]);
			youtubeModel = new YoutubeModel(args[0], args[1], args[2], args[3], args[4], args[5], initial_counter_arg,
					end_counter_arg);
			System.out.println("::: Config work_dir :" + youtubeModel.getWorkDir());
			System.out.println("::: Config s3OutputBucketName :" + youtubeModel.getS3OutputBucketName());
			System.out.println("::: Config s3RawOutputPath :" + youtubeModel.getS3RawOutputPath());
			System.out.println("::: Config startDate :" + youtubeModel.getStartDate());
			System.out.println("::: Config endDate :" + youtubeModel.getEndDate());
			System.out.println("::: Config partitionDate :" + youtubeModel.getPartitionDate());
			System.out.println("::: Config startCounter :" + youtubeModel.getReportStartCounter());
			System.out.println("::: Config endCounter :" + youtubeModel.getReportEndCounter());

			start_date = youtubeModel.getStartDate() + CONVERT_TO_ZULU;
			end_date = youtubeModel.getEndDate() + CONVERT_TO_ZULU;

			initial_counter = youtubeModel.getReportStartCounter();
			termination_counter = youtubeModel.getReportEndCounter();

			System.out.println("::: zulu startDate :" + start_date);
			System.out.println("::: zulu endDate :" + end_date);
		}

		try {
			youtubeReporting = new YouTubeReporting.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, gcred)
					.setApplicationName("youtube-cmdline-retrievereports-sample").setHttpRequestInitializer(gcred)
					.build();

			List<Job> jobsList = listReportingJobs();
			System.out.println(" -------------------------- > " + jobsList.size());

		 	ArrayList<Integer> al = new ArrayList<Integer>();
        	Class.forName("org.postgresql.Driver");
			 c = DriverManager
			            .getConnection("jdbc:postgresql://umg-ers-datapipeline-qa-control-server.cut0lqq1k9hu.us-east-1.rds.amazonaws.com:5432/datapipeline",
			            "datapipeline_sv", "Yx0JB*JYd(Q41]wz");
			 
			 stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery( "SELECT * FROM datapipeline.youtube_reqiured_reports where enabled = true" );
		    

			 while (rs.next()) {
			        int id = rs.getInt("id");
			        System.out.println(id);
                     al.add(id);
			 }
			
			
			if (jobsList != null && jobsList.size() > 0) {
				int k = 0;
				for (int i = initial_counter; i < termination_counter; i++) {
					
					if(!(al.contains(i)))
            			continue;
					List<Report> retrievReports = retrieveReports(jobsList.get(i).getId());

					if (retrievReports != null && retrievReports.size() > 0) {

						for (int j = 0; j < retrievReports.size(); j++) {
							System.out.println("::::::::::::File Download Started::::::::");
							System.out.println(" ---------- > " + retrievReports.size() + " <--------");
							boolean bFlag = downloadReport(retrievReports.get(j).getDownloadUrl(), jobsList.get(i)
									.getReportTypeId());
							System.out.println("::::::::::::File Download Completed::::::::::::::::::"
									+ jobsList.get(i).getReportTypeId());
							System.out.println(" ::: Download file count ::: " + k);
							k++;
						}
					}
				}
				// UNCOMMENT THE FOLLOWING CODE FOR AWS DATA PIPELINE
				// CONFIGURATIONS

			}
		} catch (GoogleJsonResponseException e) {
			System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
					+ e.getDetails().getMessage());
			throw e;
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("######### Completed ##########");
			// FileUtil.cleanupFiles(youtubeModel.getWorkDir());
			// DeleteDirectoryExample.isFile(youtubeModel.getWorkDir());
			// DeleteDirectoryExample.isFile(youtubeModel.getWorkDir());
			System.out.println("######### Directory Cleared ##########");
			System.exit(0);
		}
	}
}