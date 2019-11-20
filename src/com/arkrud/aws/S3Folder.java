package com.arkrud.aws;

import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Bucket;

public class S3Folder {
	private String folderName;
	private AWSAccount folderAccount;
	private String folderPath;
	private Bucket bucket;
	private Owner folderOwner;

	public S3Folder() {
		super();
	}

	public S3Folder(String folderName, Bucket bucket, AWSAccount folderAccount, String folderPath) {
		super();
		this.folderName = folderName;
		this.bucket = bucket;
		this.folderAccount = folderAccount;
		this.folderPath = folderPath;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public AWSAccount getFolderAccount() {
		return folderAccount;
	}

	public void setFolderAccount(AWSAccount folderAccount) {
		this.folderAccount = folderAccount;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

	public Owner getFolderOwner() {
		return folderOwner;
	}

	public void setFolderOwner(Owner owner) {
		this.folderOwner = owner;
	}
}
