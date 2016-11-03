package ftp.core.controller;

import ftp.core.base.AbstractTest;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.StorageService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;

import javax.annotation.Resource;
import java.io.FileInputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Kosta_Chuturkov on 10/27/2016.
 */
public class FileManagementControllerTest extends AbstractTest {

    @Resource
    private FileManagementController fileManagementController;

    @Resource
    private ResourceLoader resourceLoader;
    
    @Autowired
    private StorageService storageService;

    @Test
    public void testProfilePicUpdate() throws Exception {
        //given
        org.springframework.core.io.Resource defaultPic = this.resourceLoader.getResource(ServerConstants.DEFAULT_PROFILE_PICTURE);
        when(this.storageService.loadProfilePicture(Matchers.anyString())).thenReturn(new FileSystemResource(defaultPic.getFile()));
        FileSystemResource profilePicBefore = this.fileManagementController.getProfilePic("admin");
        org.springframework.core.io.Resource updatePic = this.resourceLoader.getResource("classpath:resources/kitty.jpg");
        when(this.storageService.loadProfilePicture(Matchers.anyString())).thenReturn(new FileSystemResource(updatePic.getFile()));
        //when
        this.fileManagementController.updateProfilePicture(new MockMultipartFile("kitty.jpg", "kitty.jpg", "",
                IOUtils.toByteArray(new FileInputStream(this.resourceLoader.getResource("classpath:resources/kitty.jpg").getFile()))));

        //then
        FileSystemResource profilePicAfter = this.fileManagementController.getProfilePic("admin");
        assertThat(profilePicAfter.getFile().getName(), is(not(profilePicBefore.getFile().getName())));
    }

    @Test
    public void testUploadFile() throws Exception {

    }

    @Test
    public void testDeleteFiles() throws Exception {

    }

    @Test
    public void testDownloadFile() throws Exception {

    }

    @Test
    public void testGetProfilePic() throws Exception {

    }

    @Test
    public void testGetSharedFiles() throws Exception {

    }

    @Test
    public void testGetPrivateFiles() throws Exception {

    }

    @Test
    public void testGetUploadedFiles() throws Exception {

    }

    @Override
    protected void makeRequestsAs() {
        super.makeRequestsAdminUser();
    }
}