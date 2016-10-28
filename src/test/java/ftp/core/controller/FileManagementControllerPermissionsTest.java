package ftp.core.controller;

import ftp.core.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Kosta_Chuturkov on 10/27/2016.
 */
public class FileManagementControllerPermissionsTest extends AbstractTest {

    @Resource
    private FileManagementController fileManagementController;

    @Test
    public void profilePicUpdateprofilePicUpdateNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.updateProfilePicture(null));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }

    @Test
    public void uploadFileNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.uploadFile(null, null));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }

    @Test
    public void deleteFilesNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.deleteFiles(null));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }

    @Test
    public void downloadFileNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.downloadFile(null));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }


    @Test
    public void getSharedFilesNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.getSharedFiles(null, null));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }

    @Test
    public void getUploadedFilesNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.getUploadedFiles(null, null));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }

    @Test
    public void getPrivateFilesNoPerm() throws IOException {
        //when
        Throwable throwable = invokeProtectedMethodAsAnonymousUser((t) -> this.fileManagementController.getPrivateFiles(1, 10));

        //then
        assertThat(throwable.getMessage(), is("Access is denied"));
    }

    private <T> Throwable invokeProtectedMethodAsAnonymousUser(Consumer<T> funcToInvoke) throws IOException {
        try {
            funcToInvoke.accept(null);
            Assert.fail("Should fail...");
        } catch (AccessDeniedException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void makeRequestsAs() {
        super.makeRequestsAsAnonymousUser();
    }
}