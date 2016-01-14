package ftp.core.common.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "files")
public class File extends AbstractEntity {

	public static final int PUBLIC_FILE = 1;

	public static final int PRIVATE_FILE = 2;

	public static final int SHARED_FILE = 3;

	public enum FileType {
		SHARED(SHARED_FILE), PRIVATE(PRIVATE_FILE), PUBLIC(PUBLIC_FILE);

		public static Map<Integer, FileType> mapping = Maps.newHashMap();

		public static List<FileType> ALL = Lists.newArrayList(SHARED, PUBLIC, PRIVATE);

		static {
			mapping.put(SHARED_FILE, SHARED);
			mapping.put(PRIVATE_FILE, PRIVATE);
			mapping.put(PUBLIC_FILE, PUBLIC);
		}

		private FileType(int type) {
			this.type = type;
		}

		private int type;

		public int getType() {
			return type;
		}

		public static FileType getById(int id) {
			return mapping.get(id);
		}

		public void setType(int type) {
			this.type = type;
		}

	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User creator;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "files_to_users", joinColumns = @JoinColumn(name = "fileID") , inverseJoinColumns = @JoinColumn(name = "userId") )
	private List<User> sharedWithUsers = Lists.newArrayList();

	@NotNull
	@NotEmpty
	@Column(name = "name")
	private String name;

	@NotNull
	@NotEmpty
	@Column(name = "download_hash", unique = true)
	private String downloadHash;

	@NotNull
	@NotEmpty
	@Column(name = "delete_hash", unique = true)
	private String deleteHash;

	@Column(name = "file_size")
	private long fileSize;

	@Column(name = "timestamp")
	@Type(type = "timestamp")
	private Date timestamp;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "filetype")
	private FileType fileType;

	public File() {

	}

	public boolean addUser(User user) {
		if (!sharedWithUsers.contains(user)) {
			return sharedWithUsers.add(user);
		}
		return false;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
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

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public List<User> getSharedWithUsers() {
		return sharedWithUsers;
	}

	public void setSharedWithUsers(List<User> sharedWithUsers) {
		this.sharedWithUsers.clear();
		if (sharedWithUsers != null) {
			this.sharedWithUsers.addAll(sharedWithUsers);
		}
	}
}
