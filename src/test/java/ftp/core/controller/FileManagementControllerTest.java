package ftp.core.controller;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ftp.core.base.AbstractTest;
import ftp.core.model.dto.*;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.service.face.StorageService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * Created by Kosta_Chuturkov on 10/27/2016.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FileManagementControllerTest extends AbstractTest {

    @Resource
    private FileManagementController fileManagementController;

    @Resource
    private ResourceLoader resourceLoader;

    @Autowired
    private StorageService storageService;

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Resource
    private Gson gson;

    @Test
    public void testProfilePicUpdate() throws Exception {
        //given
        FileSystemResource profilePicBefore = this.fileManagementController.getProfilePic("admin");
        mockStorageServiceToReturnUpdatedPic();

        //when
        this.fileManagementController.updateProfilePicture(new MockMultipartFile("kitty.jpg", "kitty.jpg", "",
                fileContentToByteArray(getResourceAsFile("classpath:resources/kitty.jpg"))));

        //then
        verify(this.storageService, times(1)).storeProfilePicture(any(InputStream.class), anyString());
        FileSystemResource profilePicAfter = this.fileManagementController.getProfilePic("admin");
        verify(this.storageService, times(3)).loadProfilePicture(Matchers.anyString());
        assertThat(profilePicAfter.getFile().getName(), is(not(profilePicBefore.getFile().getName())));
    }

    private void mockStorageServiceToReturnUpdatedPic() throws IOException {
        org.springframework.core.io.Resource updatePic = this.resourceLoader.getResource("classpath:resources/kitty.jpg");
        when(this.storageService.loadProfilePicture(Matchers.anyString())).thenReturn(new FileSystemResource(updatePic.getFile()));
    }

    @Test
    public void testUploadFile() throws Exception {
        //given
        byte[] content = fileContentToByteArray(getResourceAsFile("classpath:resources/kitty.jpg"));

        //when
        long contentSize = content.length;
        long remainingStorageBefore = User.getCurrent().getRemainingStorage();
        String fileName = "kitty.jpg";
        UploadedFilesDto<JsonFileDto> response = this.fileManagementController.uploadFile(new MockMultipartFile(fileName, fileName, "",
                content), null);
        //then
        assertThat(response, is(notNullValue()));
        ArgumentCaptor<InputStream> inputStreamArgumentCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<String> newFileNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> destinationFolderCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.storageService).store(inputStreamArgumentCaptor.capture(), newFileNameCaptor.capture(), destinationFolderCaptor.capture());
        assertThat(newFileNameCaptor.getValue(), CoreMatchers.endsWith(fileName));
        assertThat(destinationFolderCaptor.getValue(), is(User.getCurrent().getEmail()));
        assertThat(content, is(IOUtils.toByteArray(inputStreamArgumentCaptor.getValue())));

        List<JsonFileDto> files = response.getFiles();
        assertThat(files.size(), is(1));
        JsonFileDto uploadedFile = files.get(0);
        assertThat(uploadedFile.getName(), is(fileName));
        assertThat(Integer.parseInt(uploadedFile.getSize()), is(content.length));

        String url = uploadedFile.getUrl();
        String downloadHash = url.substring(url.lastIndexOf("/") + 1);
        File fileByDownloadHash = this.fileService.getFileByDownloadHash(downloadHash);
        assertThat(fileByDownloadHash, is(notNullValue()));
        assertThat(fileByDownloadHash.getFileSize().intValue(), is(content.length));
        assertThat(fileByDownloadHash.getName(), is(fileName));
        assertThat(fileByDownloadHash.getFileType(), is(File.FileType.PRIVATE));
        assertThat(fileByDownloadHash.getCreator().getEmail(), is(User.getCurrent().getEmail()));

        User one = this.userService.findOne(User.getCurrent().getId());
        assertThat(one.getRemainingStorage(), is((remainingStorageBefore - contentSize)));
    }

    @Test
    public void testUploadAndRetrieveFile() throws Exception {
        //given
        java.io.File file = getResourceAsFile("classpath:resources/kitty.jpg");
        byte[] contentToUpload = fileContentToByteArray(file);
        String downloadHash = uploadedFileAndGetDownloadHash(contentToUpload, "kitty.jpg");
        when(this.storageService.loadAsResource(Matchers.anyString(), anyString())).thenReturn(new FileSystemResource(file));

        //when
        FileSystemResource fileSystemResource = this.fileManagementController.downloadFile(downloadHash);

        //then
        assertThat(IOUtils.toByteArray(fileSystemResource.getInputStream()), is(contentToUpload));

    }

    private String uploadedFileAndGetDownloadHash(byte[] content, String fileName) {
        UploadedFilesDto<JsonFileDto> response = this.fileManagementController.uploadFile(new MockMultipartFile(fileName, fileName, "",
                content), null);
        List<JsonFileDto> files = response.getFiles();
        JsonFileDto uploadedFile = files.get(0);
        String url = uploadedFile.getUrl();
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private String uploadedFileAndGetDeleteHash(byte[] content, String fileName) {
        UploadedFilesDto<JsonFileDto> response = this.fileManagementController.uploadFile(new MockMultipartFile(fileName, fileName, "",
                content), null);
        List<JsonFileDto> files = response.getFiles();
        JsonFileDto uploadedFile = files.get(0);
        String url = uploadedFile.getDeleteUrl();
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Test
    public void testDeleteFiles() throws Exception {
        //given
        String fileName = "kitty.jpg";
        java.io.File file = getResourceAsFile("classpath:resources/kitty.jpg");
        byte[] contentToUpload = fileContentToByteArray(file);
        long contentSize = contentToUpload.length;
        long remainingStorageBefore = User.getCurrent().getRemainingStorage();
        String deleteHash = uploadedFileAndGetDeleteHash(contentToUpload, "kitty.jpg");
        when(this.storageService.loadAsResource(Matchers.anyString(), anyString())).thenReturn(new FileSystemResource(file));

        //when
        DeletedFilesDto response = this.fileManagementController.deleteFiles(deleteHash);
        //then
        verify(this.storageService, times(1)).deleteResource(anyString(), anyString());
        assertThat(response.getFiles().get(fileName).toString(), is("true"));

        File persistentFile = this.fileService.findByDeleteHash(deleteHash, User.getCurrent().getNickName());
        assertThat(persistentFile, is(nullValue()));

        User one = this.userService.findOne(User.getCurrent().getId());
        assertThat(one.getRemainingStorage(), is((remainingStorageBefore + contentSize)));

    }

    private byte[] fileContentToByteArray(java.io.File file) throws IOException {
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    private java.io.File getResourceAsFile(String location) throws IOException {
        return this.resourceLoader.getResource(location).getFile();
    }

    @Test
    public void testGetSharedFiles() throws Exception {
        //given
        String badmin = "badmin";
        User user1 = this.userService.registerUser("badmin1@gmail.com", badmin, "admin1234", "admin1234");
        assertThat(user1, is(notNullValue()));

        String sen2 = "sen2";
        User user2 = this.userService.registerUser("sen2@gmail.com", sen2, "admin1234", "admin1234");
        assertThat(user2, is(notNullValue()));


        String ladadmin3 = "ladadmin3";
        User user3 = this.userService.registerUser("ladadmin3@gmail.com", ladadmin3, "admin1234", "admin1234");
        assertThat(user3, is(notNullValue()));

        String jpgFileName = "kitty.jpg";
        String mp3FileName = "sample.mp3";
        String tarFileName = "sample.tar.gz";

        Set<String> users = Sets.newHashSet();
        byte[] sampleJpg = fileContentToByteArray(getResourceAsFile("classpath:resources/" + jpgFileName));
        byte[] sampleMp3File = fileContentToByteArray(getResourceAsFile("classpath:resources/" + mp3FileName));
        byte[] sampleTarFile = fileContentToByteArray(getResourceAsFile("classpath:resources/" + tarFileName));
       // fileManagementController.uploadFile(new MockMultipartFile(jpgFileName, sampleJpg), )


        //when


    }

    @Test
    public void testGetPrivateFiles() throws Exception {
        //given
        String jpgFileName = "kitty.jpg";
        String mp3FileName = "sample.mp3";
        String tarFileName = "sample.tar.gz";

        byte[] sampleJpg = fileContentToByteArray(getResourceAsFile("classpath:resources/" + jpgFileName));
        byte[] sampleMp3File = fileContentToByteArray(getResourceAsFile("classpath:resources/" + mp3FileName));
        byte[] sampleTarFile = fileContentToByteArray(getResourceAsFile("classpath:resources/" + tarFileName));
        uploadedFileAndGetDownloadHash(sampleJpg, jpgFileName);
        uploadedFileAndGetDownloadHash(sampleMp3File, mp3FileName);
        uploadedFileAndGetDownloadHash(sampleTarFile, tarFileName);

        //when
        List<PrivateFileWithMeDto> privateFiles = fileManagementController.getPrivateFiles(0, 10);

        //then
        assertThat(privateFiles, is(notNullValue()));
        assertThat(privateFiles.size(), is(3));
    }

    @Test
    public void testGetUploadedFiles() throws Exception {

    }

    @Override
    protected void makeRequestsAs() {
        super.makeRequestsAdminUser();
    }
}