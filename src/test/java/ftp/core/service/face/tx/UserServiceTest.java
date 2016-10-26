package ftp.core.service.face.tx;

import com.jayway.restassured.response.Response;
import ftp.core.AbstractTest;
import ftp.core.constants.APIAliases;
import ftp.core.controller.FileManagementController;
import ftp.core.model.entities.User;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static com.jayway.restassured.RestAssured.given;
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

    @Ignore
    @Test
    public void testLogin() throws Exception {
        Response response = given().auth().preemptive().basic("admin","admin1234").get("/api/");
        response.then().log().all();
        String token = response.cookie("XSRF-TOKEN");

        given()
                .cookie("XSRF-TOKEN", token)
                .header("X-XSRF-TOKEN", token)
                .param("email", "test@abv.bg")
                .param("pswd", "pass12345")
                .param("password_repeated", "pass12345")
                .param("nickname", "test")
                .when()
                .post(APIAliases.REGISTRATION_ALIAS)
                .then()
                .statusCode(201);
    }
}