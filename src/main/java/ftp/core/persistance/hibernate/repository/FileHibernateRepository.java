package ftp.core.persistance.hibernate.repository;

import ftp.core.common.model.File;
import ftp.core.persistance.face.generic.repository.GenericHibernateRepository;
import ftp.core.persistance.face.repository.FileRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileHibernateRepository extends GenericHibernateRepository<File, Long> implements FileRepository {

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
        query.setParameter("creatorNickName", creatorNickName);
        return (File) query.uniqueResult();
    }

    @Override
    public List<File> getSharedFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
        final Query query = getCurrentSession().getNamedQuery("File.getSharedFilesForUser");
        query.setParameter("userNickName", userNickName);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return (List<File>) query.list();
    }

    @Override
    public List<File> getPrivateFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
        final Query query = getCurrentSession().getNamedQuery("File.getPrivateFilesForUser");
        query.setParameter("userNickName", userNickName);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return (List<File>) query.list();
    }

    @Override
    public List<Long> getSharedFilesWithUsers(final Long userId, final int firstResult, final int maxResults) {
        final Query query = getCurrentSession().getNamedQuery("File.getSharedFilesWithUsers");
        query.setParameter("userId", userId);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return (List<Long>) query.list();
    }

    @Override
    public File findWithSharedUsers(final Long fileId) {
        final Query query = getCurrentSession().getNamedQuery("File.getSharedWithUsers");
        query.setParameter("fileId", fileId);
        return (File) query.uniqueResult();
    }

}
