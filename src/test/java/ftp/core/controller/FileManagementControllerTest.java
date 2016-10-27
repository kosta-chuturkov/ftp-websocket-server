package ftp.core.controller;

import ftp.core.AbstractTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by Kosta_Chuturkov on 10/27/2016.
 */
public class FileManagementControllerTest extends AbstractTest {

    @Resource
    private FileManagementController fileManagementController;

    @Test
    public void testProfilePicUpdate() throws Exception {

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