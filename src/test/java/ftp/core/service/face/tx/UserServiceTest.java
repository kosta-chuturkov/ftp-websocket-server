package ftp.core.service.face.tx;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ftp.core.base.AbstractTest;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.repository.projections.UploadedFilesProjection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Kosta_Chuturkov on 10/17/2016.
 */
@Ignore
public class
UserServiceTest extends AbstractTest {

  @Autowired
  private UserService userService;

  @Autowired
  private FileService fileService;

  @Test
  public void testGetUserByNickName() throws Exception {
    //when
    User admin = this.userService.findUserByNickName("admin");

    //then
    assertThat(admin, is(notNullValue()));
    assertThat(admin.getEmail(), is(User.getCurrent().getEmail()));
    assertThat(admin.getNickName(), is(User.getCurrent().getNickName()));
  }

  @Test
  public void testGetUserByNickLike() {
    //given
    String badmin = "badmin";
    User user1 = this.userService
        .registerUser("badmin1@gmail.com", badmin, "admin1234", "admin1234");
    assertThat(user1, is(notNullValue()));

    String sen2 = "sen2";
    User user2 = this.userService.registerUser("sen2@gmail.com", sen2, "admin1234", "admin1234");
    assertThat(user2, is(notNullValue()));

    String ladadmin3 = "ladadmin3";
    User user3 = this.userService
        .registerUser("ladadmin3@gmail.com", ladadmin3, "admin1234", "admin1234");
    assertThat(user3, is(notNullValue()));

    //when
    List<NickNameProjection> usersContainingAdmString = this.userService.getUserByNickLike("adm");

    //then
    assertThat(usersContainingAdmString.size(), is(equalTo(3)));//including the admin user
    usersContainingAdmString
        .containsAll(Lists.newArrayList(badmin, ladadmin3, User.getCurrent().getNickName()));
  }


  @Test
  public void testAddFileToUser() {
    //given
    String fileNameEscaped = "DSC_4536.jpg";
    String downloadHash = "996993eb4b291410b98a50d201ff96bb034018c6e4cea45b57832caefc9a80ed";
    String deleteHash = "4e9437adf20a4da01e935484cdbb4cb0ea79f0f14807305f89cc0eaa67c35e88";
    long fileSize = 2940415l;
    User currentUser = User.getCurrent();
    final File file = new File.Builder()
        .withName(fileNameEscaped)
        .withTimestamp(new Date())
        .withDownloadHash(downloadHash)
        .withDeleteHash(deleteHash)
        .withFileSize(fileSize)
        .withCreator(currentUser)
        .withFileType(File.FileType.PRIVATE)
        .build();
    File savedFile = this.fileService.save(file);
    assertThat(savedFile, is(notNullValue()));

    //when
    //then
    UploadedFilesProjection uploadedFilesProjection = this.userService
        .findUploadedFilesByUserId(User.getCurrent().getId());
    Set<File> uploadedFiles = uploadedFilesProjection.getUploadedFiles();
    assertThat(uploadedFiles.size(), is(equalTo(1)));
    File uploadedFile = Iterables.get(uploadedFiles, 0);
    assertThat(uploadedFile.getId(), is(savedFile.getId()));
    assertThat(uploadedFile.getName(), is(savedFile.getName()));
    assertThat(uploadedFile.getCreator().getEmail(), is(savedFile.getCreator().getEmail()));
    assertThat(uploadedFile.getDeleteHash(), is(savedFile.getDeleteHash()));
    assertThat(uploadedFile.getDownloadHash(), is(savedFile.getDownloadHash()));
    assertThat(uploadedFile.getFileSize(), is(savedFile.getFileSize()));
    assertThat(uploadedFile.getFileType(), is(savedFile.getFileType()));


  }

  @Override
  protected void makeRequestsAs() {
    super.makeRequestsAdminUser();
  }
}