package ftp.core.common.model;

import javax.persistence.*;


@MappedSuperclass
public abstract class AbstractEntity implements Entity {

	@Id
	@SequenceGenerator(sequenceName="HIBERNATE_SEQUENCE", name = "HIBERNATE_SEQUENCE")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HIBERNATE_SEQUENCE")
	@Access(AccessType.PROPERTY)
	@Column(name = "id", nullable = false)
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return super.toString() + "id=" + getId();
	}

}



