package ftp.core.service.face.tx;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import ftp.core.base.AbstractTest;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.repository.projections.UploadedFilesProjection;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Kosta_Chuturkov on 11/2/2016.
 */
public class FileServiceTest extends AbstractTest {

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Test
    public void testSaveFile() throws Exception {
        //given
        User currentUser = User.getCurrent();
        String downloadHash = "996993eb4b291410b98a50d201ff96bb034018c6e4cea45b57832caefc9a80ed";
        String deleteHash = "4e9437adf20a4da01e935484cdbb4cb0ea79f0f14807305f89cc0eaa67c35e88";
        final File fileToBeSaved = new File.Builder()
                .withName("someName.jpg")
                .withTimestamp(new Date())
                .withDownloadHash(downloadHash)
                .withDeleteHash(deleteHash)
                .withFileSize(34324231L)
                .withCreator(currentUser)
                .withSharedWithUsers(Sets.newHashSet())
                .withFileType(File.FileType.PRIVATE)
                .build();
        //when
        this.fileService.saveFile(fileToBeSaved);

        //then
        UploadedFilesProjection uploadedFilesByUserId = this.userService.findUploadedFilesByUserId(currentUser.getId());
        assertThat(uploadedFilesByUserId, is(notNullValue()));

        Set<File> uploadedFiles = uploadedFilesByUserId.getUploadedFiles();
        assertThat(uploadedFiles.size(), is(1));

        File uploadedFile = Iterables.get(uploadedFiles, 0);
        assertThat(uploadedFile.getId(), is(fileToBeSaved.getId()));
        assertThat(uploadedFile.getName(), is(fileToBeSaved.getName()));
        assertThat(uploadedFile.getCreator().getEmail(), is(fileToBeSaved.getCreator().getEmail()));
        assertThat(uploadedFile.getDeleteHash(), is(fileToBeSaved.getDeleteHash()));
        assertThat(uploadedFile.getDownloadHash(), is(fileToBeSaved.getDownloadHash()));
        assertThat(uploadedFile.getFileSize(), is(fileToBeSaved.getFileSize()));
        assertThat(uploadedFile.getFileType(), is(fileToBeSaved.getFileType()));


    }

    @Override
    protected void makeRequestsAs() {
        super.makeRequestsAdminUser();
    }
}