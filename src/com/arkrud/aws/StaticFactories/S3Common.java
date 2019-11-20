package com.arkrud.aws.StaticFactories;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.S3Folder;

public class S3Common {
	// Connect to AWS
	public static AmazonS3 connectToS3(AWSCredentials credentials) {
		AmazonS3 s3 = new AmazonS3Client(credentials);
		return s3;
	}

	// Retrieve AWS S3 buckets for account
	public static ArrayList<Bucket> retrieveS3Buckets(AmazonS3 s3) {
		ArrayList<Bucket> s3Buckets = new ArrayList<Bucket>();
		try {
			// System.out.println("Listing buckets");
			for (Bucket bucket : s3.listBuckets()) {
				s3Buckets.add(bucket);
				// System.out.println(" - " + bucket.getName());
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return s3Buckets;
	}

	// Create S3 Bucket
	public static void createS3Buckets(AmazonS3 s3, String bucketName) {
		try {
			if (!(s3.doesBucketExist(bucketName))) {
				// Note that CreateBucketRequest does not specify region. So bucket is created in the region specified in the client.
				s3.createBucket(new CreateBucketRequest(bucketName));
			}
			// Get location.
			// String bucketLocation = s3.getBucketLocation(new GetBucketLocationRequest(bucketName));
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " + "means your request made it " + "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " + "means the client encountered " + "an internal error while trying to " + "communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	// Delete S3 Bucket
	public static void deleteS3Buckets(AmazonS3 s3, String bucketName) {
		try {
			ObjectListing objectListing = s3.listObjects(bucketName);
			while (true) {
				for (Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext();) {
					S3ObjectSummary objectSummary = (S3ObjectSummary) iterator.next();
					s3.deleteObject(bucketName, objectSummary.getKey());
				}
				if (objectListing.isTruncated()) {
					objectListing = s3.listNextBatchOfObjects(objectListing);
				} else {
					break;
				}
			}
			;
			VersionListing list = s3.listVersions(new ListVersionsRequest().withBucketName(bucketName));
			for (Iterator<?> iterator = list.getVersionSummaries().iterator(); iterator.hasNext();) {
				S3VersionSummary s = (S3VersionSummary) iterator.next();
				s3.deleteVersion(bucketName, s.getKey(), s.getVersionId());
			}
			s3.deleteBucket(bucketName);
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " + "means your request made it " + "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " + "means the client encountered " + "an internal error while trying to " + "communicate with S3, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	// Retrieve S3 Folders from Bucket
	public static ArrayList<S3Folder> retrieveS3BucketFolders(AmazonS3 s3, Bucket bucket) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket.getName());
		ObjectListing objectListing;
		ArrayList<S3Folder> s3folders = new ArrayList<S3Folder>();
		ArrayList<String> list = new ArrayList<String>();
		do {
			objectListing = s3.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				if (!list.contains(objectSummary.getKey().split("/")[0])) {
					S3Folder s3folder = new S3Folder();
					if (objectSummary.getKey().contains("/")) {
						list.add(objectSummary.getKey().split("/")[0]);
						s3folder.setFolderOwner(objectSummary.getOwner());
						s3folder.setFolderName(objectSummary.getKey().split("/")[0]);
						s3folder.setBucket(bucket);
						s3folders.add(s3folder);
					}
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());
		return s3folders;
	}

	// Retrieve objects from Folder in the S3 Bucket
	public static ArrayList<ArrayList<Object>> listTheObjectsInBucket(AWSAccount accountName, Bucket bucketName, String prefix) {
		Owner owner;
		ArrayList<ArrayList<Object>> s3objectInFolder = new ArrayList<ArrayList<Object>>();
		Hashtable<String, String> subFolders = new Hashtable<String, String>();
		String folderName;
		if (prefix.split("/").length > 1) {
			folderName = prefix.split("/")[prefix.split("/").length - 1];
		} else {
			folderName = prefix.split("/")[0];
		}
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName.getName()).withPrefix(prefix);
		ObjectListing objectListing;
		AmazonS3 s3 = connectToS3(AwsCommon.getAWSCredentials(accountName.getAccountAlias()));
		do {
			objectListing = s3.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				ArrayList<Object> s3ObjectData = new ArrayList<Object>();
				int folderPathPosition = 0;
				int pathLenght = objectSummary.getKey().split("/").length;
				int x = 0;
				while (x < pathLenght) {
					if (objectSummary.getKey().split("/")[x].contains(folderName)) {
						folderPathPosition = x;
						break;
					}
					x++;
				}
				if (pathLenght - folderPathPosition == 2) {
					s3ObjectData.add(objectSummary);
					s3ObjectData.add(Long.toString(objectSummary.getSize()));
					owner = objectSummary.getOwner();
					s3ObjectData.add(owner);
					s3objectInFolder.add(s3ObjectData);
				} else if (pathLenght - folderPathPosition > 2) {
					int y = 0;
					String subFolderPath = "";
					while (y < folderPathPosition + 2) {
						subFolderPath = subFolderPath + objectSummary.getKey().split("/")[y] + "/";
						y++;
					}
					subFolders.put(objectSummary.getKey().split("/")[folderPathPosition + 1], subFolderPath);
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());
		Enumeration<String> enumKey = subFolders.keys();
		while (enumKey.hasMoreElements()) {
			ArrayList<Object> s3SubFoldersData = new ArrayList<Object>();
			String folder = enumKey.nextElement();
			String folderPath = subFolders.get(folder);
			S3Folder subFolder = new S3Folder(folder, bucketName, accountName, folderPath);
			s3SubFoldersData.add(subFolder);
			s3SubFoldersData.add("-");
			s3SubFoldersData.add("N/A");
			s3objectInFolder.add(s3SubFoldersData);
		}
		return s3objectInFolder;
	}

	// Retrieve objects from Folder in the S3 Bucket
	public static ArrayList<ArrayList<Object>> listObjectsInBucket(String accountName, String bucketName, String prefix) {
		String owner = "";
		ArrayList<ArrayList<Object>> s3objectInFolder = new ArrayList<ArrayList<Object>>();
		String folderName;
		if (prefix.split("/").length > 1) {
			folderName = prefix.split("/")[prefix.split("/").length - 1];
		} else {
			folderName = prefix.split("/")[0];
		}
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
		ObjectListing objectListing;
		AmazonS3 s3 = connectToS3(AwsCommon.getAWSCredentials(accountName));
		do {
			objectListing = s3.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				ArrayList<Object> s3ObjectData = new ArrayList<Object>();
				int folderPathPosition = 0;
				int pathLenght = objectSummary.getKey().split("/").length;
				int x = 0;
				while (x < pathLenght) {
					if (objectSummary.getKey().split("/")[x].contains(folderName)) {
						folderPathPosition = x;
						break;
					}
					x++;
				}
				if (pathLenght - folderPathPosition == 2) {
					s3ObjectData.add(objectSummary.getKey());
					s3ObjectData.add(Long.toString(objectSummary.getSize()));
					owner = objectSummary.getOwner().getDisplayName();
					s3ObjectData.add(owner);
					s3objectInFolder.add(s3ObjectData);
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());
		return s3objectInFolder;
	}

	// Upload local folder to S3 Bucket Folder
	public static void uploadFileToS3Folder(String profileName, String bucketName, String folderName, File fileName) {
		TransferManager tm = new TransferManager(new ProfileCredentialsProvider(profileName));
		PutObjectRequest request = new PutObjectRequest(bucketName, folderName + fileName.getName(), fileName);
		request.setGeneralProgressListener(new ProgressListener() {
			@Override
			public void progressChanged(ProgressEvent progressEvent) {
				System.out.println("Transferred bytes: " + progressEvent.getBytesTransferred());
			}
		});
		if (fileName.isDirectory()) {
			MultipleFileUpload upload = tm.uploadDirectory(bucketName, folderName, fileName, true);
			try {
				try {
					upload.waitForCompletion();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (AmazonClientException amazonClientException) {
				System.out.println("Unable to upload file, upload aborted.");
				amazonClientException.printStackTrace();
			}
		} else {
			Upload upload = tm.upload(request);
			try {
				try {
					upload.waitForCompletion();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (AmazonClientException amazonClientException) {
				System.out.println("Unable to upload file, upload aborted.");
				amazonClientException.printStackTrace();
			}
		}
	}

	// Delete Folder in S3 bucket
	public static void deleteFolderInBacket(AmazonS3 s3, String bucketName, String folderPrefix) {
		if (s3.doesBucketExist(bucketName)) {
			ObjectListing objects = s3.listObjects(bucketName, folderPrefix);
			for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
				s3.deleteObject(bucketName, objectSummary.getKey());
			}
		}
	}

	// Create folder in S3 bucket
	public static void createFolder(AmazonS3 s3, String bucketName, String folderName) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + "/", emptyContent, metadata);
		// send request to S3 to create folder
		s3.putObject(putObjectRequest);
	}

	// Create sub-folder in S3 Folder
	public static void createSubFolder(AmazonS3 s3, String bucketName, String parentFolder, String folderName) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, parentFolder + folderName + "/", emptyContent, metadata);
		// send request to S3 to create folder
		s3.putObject(putObjectRequest);
	}

	// Store AWS accounts properties in Vector to populate informational tables
	public static ArrayList<ArrayList<Object>> prepareTheTableData(S3Folder s3Folder) {
		return S3Common.listTheObjectsInBucket(s3Folder.getFolderAccount(), s3Folder.getBucket(), s3Folder.getFolderPath());
	}

	// Delete object from S3 Bucket Folder
	public static void deletes3Object(AmazonS3 s3, String bucketName, String folderName) {
		try {
			s3.deleteObject(new DeleteObjectRequest(bucketName, folderName));
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public static void returnBucketStructure(AWSAccount theAWSAccount, Bucket bucket, String prefix, DefaultMutableTreeNode s3Bucket) {
		AmazonS3 s3 = S3Common.connectToS3(AwsCommon.getAWSCredentials(theAWSAccount.getAccountAlias()));
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket.getName()).withDelimiter("/").withPrefix(prefix);
		ObjectListing objectListing = s3.listObjects(listObjectsRequest);
		Iterator<String> oli = objectListing.getCommonPrefixes().iterator();
		while (oli.hasNext()) {
			String folder = oli.next();
			S3Folder s3Folder = new S3Folder();
			s3Folder.setFolderPath(folder);
			s3Folder.setBucket(bucket);
			s3Folder.setFolderAccount(theAWSAccount);
			if (folder.split("/").length > 0) {
				s3Folder.setFolderName(folder.split("/")[folder.split("/").length - 1]);
			}
			DefaultMutableTreeNode s3FolderBucket = new DefaultMutableTreeNode(s3Folder);
			s3Bucket.add(s3FolderBucket);
			returnBucketStructure(theAWSAccount, bucket, folder, s3FolderBucket);
		}
	}
}
