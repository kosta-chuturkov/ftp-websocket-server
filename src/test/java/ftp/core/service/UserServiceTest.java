package ftp.core.service;

import org.junit.Assert;
import org.junit.Test;

import ftp.core.common.model.User;

public class UserServiceTest extends AbstractTest {

	@Test
	public void testCreateUser() {
		User current = User.getCurrent();
		Assert.assertTrue(current != null);
	}

}
