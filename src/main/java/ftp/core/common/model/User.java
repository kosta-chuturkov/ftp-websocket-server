package ftp.core.common.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.collect.Lists;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

	@NotNull
	@NotEmpty
	@Column(name = "nickname")
	private String nickName;

	@NotNull
	@NotEmpty
	@Column(name = "email")
	private String email;

	@NotNull
	@NotEmpty
	@Column(name = "pass")
	private String password;

	@Column(name = "remaining_storage")
	private long remainingStorage;

	@Column(name = "token")
	private BigDecimal token;

	@OneToMany
	@JoinColumn(name = "user_id")
	private List<File> uploadedFiles = Lists.newArrayList();

	public User() {
	}

	private static ThreadLocal<User> current = new ThreadLocal<User>();

	public User(String nickName, String email, String password, long remainingStorage, BigDecimal token) {
		super();
		this.nickName = nickName;
		this.email = email;
		this.password = password;
		this.remainingStorage = remainingStorage;
		this.token = token;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getRemainingStorage() {
		return remainingStorage;
	}

	public void setRemainingStorage(long remainingStorage) {
		this.remainingStorage = remainingStorage;
	}

	public BigDecimal getToken() {
		return token;
	}

	public void setToken(BigDecimal token) {
		this.token = token;
	}

	public static User getCurrent() {
		return current.get();
	}

	public static void setCurrent(User current) {
		User.current.set(current);
	}

	public List<File> getUploadedFiles() {
		return uploadedFiles;
	}

	public void setUploadedFiles(List<File> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (nickName == null) {
			if (other.nickName != null)
				return false;
		} else if (!nickName.equals(other.nickName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

}
