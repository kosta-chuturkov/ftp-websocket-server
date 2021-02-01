package ftp.core.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "file_shared_to_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fileId", "userId"})
})
public class FileSharedToUser extends AbstractEntity<Long> implements Serializable {
    @Column(nullable = false)
    private Long fileId;

    @Column(nullable = false)
    private Long userId;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public FileSharedToUser() {
    }

    public FileSharedToUser(File file, User user) {
        this.fileId = file.getId();
        this.userId = user.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FileSharedToUser that = (FileSharedToUser) o;
        return Objects.equals(fileId, that.fileId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileId, userId);
    }
}
