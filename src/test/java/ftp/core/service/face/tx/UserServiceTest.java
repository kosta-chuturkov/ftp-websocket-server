package ftp.core.service.face.tx;

import ftp.core.AbstractTest;
import ftp.core.controller.FileManagementController;
import ftp.core.model.entities.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        User user = super.user;

        //when
        User admin = this.userService.getUserByNickName("admin");

        //then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getEmail(), is(user.getEmail()));
        assertThat(admin.getNickName(), is(user.getNickName()));
    }

    @Test
    public void testMock() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(post("/").with(csrf().asHeader())).andReturn();
        System.out.println(mvcResult.toString());
    }

    @Test
    public void testLogin() throws Exception {
//        when().
//                get("/lotto/{id}", 5).
//                then().
//                statusCode(200).
//                body("lotto.lottoId", equalTo(5));
    }
}