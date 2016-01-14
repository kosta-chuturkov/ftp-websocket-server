package ftp.core.common.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@MappedSuperclass
@XmlRootElement(namespace = "urn:ftp.core.common.model.roombar")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEntity", namespace = "urn:ftp.core.common.model")
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



