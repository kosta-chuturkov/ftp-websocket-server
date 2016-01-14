package ftp.core.common.model;

public abstract class AbstractNamedEntity extends AbstractEntity implements NamedEntity {

	private String name;

	public AbstractNamedEntity() {

	}

	public AbstractNamedEntity( final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AbstractNamedEntity [name=" + name + "]";
	}
}
