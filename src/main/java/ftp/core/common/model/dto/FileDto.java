package ftp.core.common.model.dto;

import java.util.Date;

import ftp.core.common.model.File.FileType;

public class FileDto {

	private String sharingUserName;

	private String name;

	private String downloadHash;

	private String deleteHash;

	private long size;

	private String timestamp;

	private FileType fileType;
	
	public FileDto(){
		
	}

	public FileDto(String sharingUserName, String name, String downloadHash, String deleteHash, long size,
			String timestamp, FileType modifier) {
		this.sharingUserName = sharingUserName;
		this.name = name;
		this.downloadHash = downloadHash;
		this.deleteHash = deleteHash;
		this.size = size;
		this.timestamp = timestamp;
		this.fileType = modifier;
	}



	public String getSharingUserName() {
		return sharingUserName;
	}

	public void setSharingUserName(String sharingUserName) {
		this.sharingUserName = sharingUserName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDownloadHash() {
		return downloadHash;
	}

	public void setDownloadHash(String downloadHash) {
		this.downloadHash = downloadHash;
	}

	public String getDeleteHash() {
		return deleteHash;
	}

	public void setDeleteHash(String deleteHash) {
		this.deleteHash = deleteHash;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType modifier) {
		this.fileType = modifier;
	}

}
