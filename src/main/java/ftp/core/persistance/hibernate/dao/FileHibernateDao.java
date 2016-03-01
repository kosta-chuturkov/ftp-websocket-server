package ftp.core.persistance.hibernate.dao;

import ftp.core.common.model.File;
import ftp.core.persistance.face.dao.FileDao;
import ftp.core.persistance.face.generic.dao.GenericHibernateDao;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileHibernateDao extends GenericHibernateDao<File, Long> implements FileDao {

	@Override
	public File getFileByDownloadHash(final String downloadHash) {
		final Query query = getCurrentSession().getNamedQuery("File.getFileByDownloadHash");
		query.setParameter("downloadHash", downloadHash);
		return (File) query.uniqueResult();
	}

	@Override
	public void deleteFile(final String deleteHash, final String creatorNickName) {
		final Query query = getCurrentSession().getNamedQuery("File.deleteFile");
		query.setParameter("deleteHash", deleteHash);
		query.setParameter("creatorNickName", creatorNickName);
		query.executeUpdate();
	}

	@Override
	public File findByDeleteHash(final String deleteHash, final String creatorNickName) {
		final Query query = getCurrentSession().getNamedQuery("File.findByDeleteHash");
		query.setParameter("deleteHash", deleteHash);
		return (File) query.uniqueResult();
	}

	@Override
	public List<File> getSharedFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
		final Query query = getCurrentSession().getNamedQuery("File.getSharedFilesForUser");
		query.setParameter("userNickName", userNickName);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return (List<File>)query.list();
	}

	@Override
	public List<File> getPrivateFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
		final Query query = getCurrentSession().getNamedQuery("File.getPrivateFilesForUser");
		query.setParameter("userNickName", userNickName);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return (List<File>)query.list();
	}

	@Override
	public List<File> getUploadedFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
		final Query query = getCurrentSession().getNamedQuery("File.getUploadedFilesForUser");
		query.setParameter("userNickName", userNickName);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return (List<File>)query.list();
	}

}
