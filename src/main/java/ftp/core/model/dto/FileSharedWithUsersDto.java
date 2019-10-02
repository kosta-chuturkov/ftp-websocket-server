package ftp.core.model.dto;

import java.util.Set;

/**
 * Created by Kosta_Chuturkov on 3/30/2016.
 */
public interface FileSharedWithUsersDto extends PersonalFileDto {
    Set<String> getSharedToUsers();
}
