package ftp.core.repository.projections;

import ftp.core.model.entities.File;

import java.util.Set;

/**
 * Created by Kosta_Chuturkov on 11/2/2016.
 */
public interface UploadedFilesProjection {
    Set<File> getUploadedFiles();
}
