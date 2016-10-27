package ftp.core.service.face.tx;

import ftp.core.AbstractTest;
import ftp.core.controller.FileManagementController;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.entities.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Kosta_Chuturkov on 10/17/2016.
 */
public class UserServiceTest extends AbstractTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FileManagementController fileManagementController;

    @Test
    public void testGetUserByNickName() throws Exception {
        //given
        super.makeRequestsAdminUser();

        //when
        User admin = this.userService.getUserByNickName("admin");

        //then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getEmail(), is(User.getCurrent().getEmail()));
        assertThat(admin.getNickName(), is(User.getCurrent().getNickName()));
    }

    @Test
    public void makeRequest() throws Exception {
        super.makeRequestsAsAnonymousUser();
        //when
        List<DataTransferObject> privateFiles = this.fileManagementController.getPrivateFiles(1, 10);

        //then
        assertThat(privateFiles,is(notNullValue()));
    }

    @Override
    protected void makeRequestsAs() {
        super.makeRequestsAdminUser();
    }
}