package ftp.core.persistance.hibernate.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Component;

import ftp.core.common.model.File;
import ftp.core.common.model.File.FileType;
import ftp.core.persistance.face.dao.FileDao;
import ftp.core.persistance.face.generic.dao.GenericHibernateDao;

@Component
public class FileHibernateDao extends GenericHibernateDao<File, Long> implements FileDao {

	@Override
	public File getFileByDownloadHash(String downloadHash) {
		Query query = getCurrentSession().getNamedQuery("File.getFileByDownloadHash");
		query.setParameter("downloadHash", downloadHash);
		return (File) query.uniqueResult();
	}

	@Override
	public void deleteFile(String deleteHash, String creatorNickName) {
		Query query = getCurrentSession().getNamedQuery("File.deleteFile");
		query.setParameter("deleteHash", deleteHash);
		query.setParameter("creatorNickName", creatorNickName);
		query.executeUpdate();
	}

	@Override
	public File findByDeleteHash(String deleteHash, String creatorNickName) {
		Query query = getCurrentSession().getNamedQuery("File.findByDeleteHash");
		query.setParameter("deleteHash", deleteHash);
		return (File) query.uniqueResult();
	}

	@Override
	public List<File> getSharedFilesForUser(String userNickName, int firstResult, int maxResults) {
		Query query = getCurrentSession().getNamedQuery("File.getSharedFilesForUser");
		query.setParameter("userNickName", userNickName);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return (List<File>)query.list();
	}

	@Override
	public List<File> getPrivateFilesForUser(String userNickName, int firstResult, int maxResults) {
		Query query = getCurrentSession().getNamedQuery("File.getPrivateFilesForUser");
		query.setParameter("userNickName", userNickName);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return (List<File>)query.list();
	}

	@Override
	public List<File> getUploadedFilesForUser(String userNickName, int firstResult, int maxResults) {
		Query query = getCurrentSession().getNamedQuery("File.getUploadedFilesForUser");
		query.setParameter("userNickName", userNickName);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		return (List<File>)query.list();
	}

}
