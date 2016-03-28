package ftp.core.common.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "files")
public class File extends AbstractEntity<Long> {

	public static final int PUBLIC_FILE = 1;

	public static final int PRIVATE_FILE = 2;

	public static final int SHARED_FILE = 3;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "files_to_users", joinColumns = @JoinColumn(name = "fileID"), inverseJoinColumns = @JoinColumn(name = "userId"))
	private final List<User> sharedWithUsers = Lists.newArrayList();
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User creator;
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

	public boolean addUser(final User user) {
		if (!this.sharedWithUsers.contains(user)) {
			return this.sharedWithUsers.add(user);
		}
		return false;
	}

	public User getCreator() {
		return this.creator;
	}

	public void setCreator(final User creator) {
		this.creator = creator;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDownloadHash() {
		return this.downloadHash;
	}

	public void setDownloadHash(final String downloadHash) {
		this.downloadHash = downloadHash;
	}

	public String getDeleteHash() {
		return this.deleteHash;
	}

	public void setDeleteHash(final String deleteHash) {
		this.deleteHash = deleteHash;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(final long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public FileType getFileType() {
		return this.fileType;
	}

	public void setFileType(final FileType fileType) {
		this.fileType = fileType;
	}

	public List<User> getSharedWithUsers() {
		return this.sharedWithUsers;
	}

	public void setSharedWithUsers(final List<User> sharedWithUsers) {
		this.sharedWithUsers.clear();
		if (sharedWithUsers != null) {
			this.sharedWithUsers.addAll(sharedWithUsers);
		}
	}

	public enum FileType {
		SHARED(SHARED_FILE), PRIVATE(PRIVATE_FILE), PUBLIC(PUBLIC_FILE);

		public static Map<Integer, FileType> mapping = Maps.newHashMap();

		public static List<FileType> ALL = Lists.newArrayList(SHARED, PUBLIC, PRIVATE);

		static {
			mapping.put(SHARED_FILE, SHARED);
			mapping.put(PRIVATE_FILE, PRIVATE);
			mapping.put(PUBLIC_FILE, PUBLIC);
		}

		private int type;

		private FileType(final int type) {
			this.type = type;
		}

		public static FileType getById(final int id) {
			return mapping.get(id);
		}

		public int getType() {
			return this.type;
		}

		public void setType(final int type) {
			this.type = type;
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		File file = (File) o;

		if (fileSize != file.fileSize) return false;
		if (sharedWithUsers != null ? !sharedWithUsers.equals(file.sharedWithUsers) : file.sharedWithUsers != null)
			return false;
		if (creator != null ? !creator.equals(file.creator) : file.creator != null) return false;
		if (name != null ? !name.equals(file.name) : file.name != null) return false;
		if (downloadHash != null ? !downloadHash.equals(file.downloadHash) : file.downloadHash != null) return false;
		if (deleteHash != null ? !deleteHash.equals(file.deleteHash) : file.deleteHash != null) return false;
		if (timestamp != null ? !timestamp.equals(file.timestamp) : file.timestamp != null) return false;
		return fileType == file.fileType;

	}

	@Override
	public int hashCode() {
		int result = sharedWithUsers != null ? sharedWithUsers.hashCode() : 0;
		result = 31 * result + (creator != null ? creator.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (downloadHash != null ? downloadHash.hashCode() : 0);
		result = 31 * result + (deleteHash != null ? deleteHash.hashCode() : 0);
		result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
		result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
		result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
		return result;
	}
}
