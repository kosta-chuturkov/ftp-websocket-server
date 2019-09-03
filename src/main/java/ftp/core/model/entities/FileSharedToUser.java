package ftp.core.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "file_shared_to_user")
public class FileSharedToUser extends AbstractEntity<Long> implements Serializable {
    @Column(name = "fileId", nullable = false)
    private File file;

    @Column(name = "userId", nullable = false)
    private User user;

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
}
