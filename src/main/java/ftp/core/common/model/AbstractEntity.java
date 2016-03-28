package ftp.core.common.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class AbstractEntity<T> implements Entity<T> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	private T id;

	@Override
	public T getId() {
		return this.id;
	}

	public void setId(final T id) {
		this.id = id;
	}

}



