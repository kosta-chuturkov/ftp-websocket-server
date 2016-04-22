package ftp.core.common.model;

import java.io.Serializable;

public interface Entity<T extends Serializable> {

	T getId();

}
