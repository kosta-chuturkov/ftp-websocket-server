package ftp.core.common.model;

import com.google.common.collect.Sets;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends AbstractEntity<Long> {

    private static final ThreadLocal<User> current = new ThreadLocal<>();
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
    private Set<File> uploadedFiles = Sets.newHashSet();

    public User() {

    }

    public User(final String nickName, final String email, final String password, final long remainingStorage, final BigDecimal token) {
        super();
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.remainingStorage = remainingStorage;
        this.token = token;
    }

    public static User getCurrent() {
        return current.get();
    }

    public static void setCurrent(final User current) {
        User.current.set(current);
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public long getRemainingStorage() {
        return this.remainingStorage;
    }

    public void setRemainingStorage(final long remainingStorage) {
        this.remainingStorage = remainingStorage;
    }

    public BigDecimal getToken() {
        return this.token;
    }

    public void setToken(final BigDecimal token) {
        this.token = token;
    }

    public Set<File> getUploadedFiles() {
        return this.uploadedFiles;
    }

    public void setUploadedFiles(final Set<File> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public boolean addUploadedFile(final File file) {
        if (!this.uploadedFiles.contains(file)) {
            return this.uploadedFiles.add(file);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
        result = prime * result + ((this.nickName == null) ? 0 : this.nickName.hashCode());
        result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final User other = (User) obj;
        if (this.email == null) {
            if (other.email != null)
                return false;
        } else if (!this.email.equals(other.email))
            return false;
        if (this.nickName == null) {
            if (other.nickName != null)
                return false;
        } else if (!this.nickName.equals(other.nickName))
            return false;
        if (this.password == null) {
            if (other.password != null)
                return false;
        } else if (!this.password.equals(other.password))
            return false;
        return true;
    }

}
