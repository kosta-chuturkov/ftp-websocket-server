package ftp.core.model.entities;

import java.io.Serializable;

public interface Entity<T extends Serializable> extends Serializable{

  T getId();

}
