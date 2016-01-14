package ftp.core.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "urn:ftp.core.common.model")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamedEntity", namespace = "urn:ftp.core.common.model")
public interface NamedEntity extends Entity {

	public String getName();

	public void setName(String name);

}
