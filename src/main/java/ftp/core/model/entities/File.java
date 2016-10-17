package ftp.core.model.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NamedQueries(value = {
        @NamedQuery(name="File.getFileByDownloadHash",query ="select file\n" +
                "\t\t\t\tfrom File file\n" +
                "\t\t\t\tjoin fetch file.creator\n" +
                "\t\t\t\twhere file.downloadHash=:downloadHash"),
        @NamedQuery(name="File.getSharedWithUsers",query ="select file\n" +
                "\t\t\t\tfrom File file\n" +
                "\t\t\t\tleft join fetch file.sharedWithUsers\n" +
                "\t\t\t\twhere file.id=:fileId"),
        @NamedQuery(name="File.deleteFile",query ="delete \n" +
                "\t\t\t\tfrom File file\n" +
                "\t\t\t\twhere file.deleteHash=:deleteHash\n" +
                "\t\t\t\tand file.creator.nickName=:creatorNickName"),
        @NamedQuery(name="File.findByDeleteHash",query ="select file \n" +
                "\t\t\t\tfrom File file\n" +
                "\t\t\t\twhere file.deleteHash=:deleteHash\n" +
                "\t\t\t\tand file.creator.nickName = :creatorNickName"),
        @NamedQuery(name="File.getSharedFilesForUser",query ="select file \n" +
                "\t\t\t\tfrom File file\n" +
                "\t\t\t\tleft outer join fetch file.sharedWithUsers swu\n" +
                "\t\t\t\twhere :userNickName in(swu)\n" +
                "\t\t\t\tand file.fileType = ftp.core.model.entities.File$FileType.SHARED"),
        @NamedQuery(name="File.getPrivateFilesForUser",query ="select file \n" +
                "\t\t\t\tfrom File file\n" +
                "\t\t\t    left join file.creator crtr\n" +
                "\t\t\t    where crtr.nickName=:userNickName\n" +
                "\t\t\t\tand file.fileType = ftp.core.model.entities.File$FileType.PRIVATE"),
        @NamedQuery(name="File.getSharedFilesWithUsers",query ="select fls.id\n" +
                "\t\t\t    from User usr\n" +
                "\t\t\t    left outer join usr.uploadedFiles fls\n" +
                "\t\t\t    where fls.fileType = ftp.core.model.entities.File$FileType.SHARED\n" +
                "\t\t\t    and usr.id=:userId")
})
@Entity
@Table(name = "files")
public class File extends AbstractEntity<Long> {

    public static final int PUBLIC_FILE = 1;

    public static final int PRIVATE_FILE = 2;

    public static final int SHARED_FILE = 3;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_shared_to_users", joinColumns = @JoinColumn(name = "file_id"))
    @Column(name = "nickname", length = 32)
    private final Set<String> sharedWithUsers = Sets.newHashSet();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User creator;

    @NotNull
    @NotEmpty
    @Column(name = "name")
    private String name;

    @NotNull
    @NotEmpty
    @Column(name = "download_hash", unique = true, length = 64)
    private String downloadHash;

    @NotNull
    @NotEmpty
    @Column(name = "delete_hash", unique = true, length = 64)
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

    private File(Builder builder) {
        setCreator(builder.creator);
        setName(builder.name);
        setDownloadHash(builder.downloadHash);
        setDeleteHash(builder.deleteHash);
        setFileSize(builder.fileSize);
        setTimestamp(builder.timestamp);
        setFileType(builder.fileType);
    }

    public boolean addUser(final String user) {
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

    public Set<String> getSharedWithUsers() {
        return this.sharedWithUsers;
    }

    public void setSharedWithUsers(final Set<String> sharedWithUsers) {
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

    public static final class Builder {
        private User creator;
        private String name;
        private String downloadHash;
        private String deleteHash;
        private long fileSize;
        private Date timestamp;
        private FileType fileType;

        public Builder() {
        }

        public Builder(File copy) {
            this.creator = copy.creator;
            this.name = copy.name;
            this.downloadHash = copy.downloadHash;
            this.deleteHash = copy.deleteHash;
            this.fileSize = copy.fileSize;
            this.timestamp = copy.timestamp;
            this.fileType = copy.fileType;
        }

        public Builder withCreator(User val) {
            this.creator = val;
            return this;
        }

        public Builder withName(String val) {
            this.name = val;
            return this;
        }

        public Builder withDownloadHash(String val) {
            this.downloadHash = val;
            return this;
        }

        public Builder withDeleteHash(String val) {
            this.deleteHash = val;
            return this;
        }

        public Builder withFileSize(long val) {
            this.fileSize = val;
            return this;
        }

        public Builder withTimestamp(Date val) {
            this.timestamp = val;
            return this;
        }

        public Builder withFileType(FileType val) {
            this.fileType = val;
            return this;
        }

        public File build() {
            return new File(this);
        }
    }
}
