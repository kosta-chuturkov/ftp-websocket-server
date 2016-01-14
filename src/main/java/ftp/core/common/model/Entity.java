package ftp.core.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "urn:ftp.core.common.model.roombar")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Entity", namespace = "urn:ftp.core.common.model")
public interface Entity {

	public abstract long getId();

}
