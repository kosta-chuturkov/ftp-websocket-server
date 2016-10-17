package ftp.core.service.face.tx;

import ftp.core.base.AbstractTest;
import ftp.core.model.entities.User;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Kosta_Chuturkov on 10/17/2016.
 */
@Ignore
public class UserServiceTest extends AbstractTest{

    @Autowired
    private UserService userService;

    @Test
    public void testGetUserByNickName() throws Exception {
        //given
        User user = super.user;

        //when
        User admin = this.userService.getUserByNickName("admin");

        //then
        assertThat(admin,is(notNullValue()));
        assertThat(admin.getEmail(),is(user.getEmail()));
        assertThat(admin.getNickName(),is(user.getNickName()));
    }

    @Test
    public void testGetUserByEmail() throws Exception {

    }
}