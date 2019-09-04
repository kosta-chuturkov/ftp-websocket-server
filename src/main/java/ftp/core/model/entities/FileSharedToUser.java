package ftp.core.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "file_shared_to_user", uniqueConstraints={
        @UniqueConstraint(columnNames = {"fileId", "userId"})
})
public class FileSharedToUser extends AbstractEntity<Long> implements Serializable {
    @Column(name = "fileId", nullable = false)
    private File file;

    @Column(name = "userId", nullable = false)
    private User user;

    public FileSharedToUser() {
    }

    public FileSharedToUser(File file, User user) {
        this.file = file;
        this.user = user;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FileSharedToUser that = (FileSharedToUser) o;
        return Objects.equals(file, that.file) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file, user);
    }
}
