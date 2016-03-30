package ftp.core.common.model.dto;

import ftp.core.common.model.File;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Kosta_Chuturkov on 3/30/2016.
 */
public class FileWithSharedUsersDto extends MainPageFileDto {

    private final Set<String> sharedToUsers = new TreeSet<>();

    public FileWithSharedUsersDto(final String sharingUserName, final String name, final String downloadHash, final String deleteHash, final long size, final String timestamp, final File.FileType modifier) {
        super(sharingUserName, name, downloadHash, deleteHash, size, timestamp, modifier);
    }

    public FileWithSharedUsersDto() {
        super();
    }

    public Set<String> getSharedToUsers() {
        return this.sharedToUsers;
    }

    public boolean addSharedUser(final String name) {
        if (!this.sharedToUsers.contains(name)) {
            return this.sharedToUsers.add(name);
        }
        return false;
    }
}
