package ftp.core.model.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Entity<T> {

  @Id
  @GeneratedValue(generator = "pooled")
  @GenericGenerator(name = "pooled", strategy = "enhanced-table", parameters = {
      @Parameter(name = "value_column_name", value = "sequence_next_hi_value"),
      @Parameter(name = "prefer_entity_table_as_segment_value", value = "true"),
      @Parameter(name = "optimizer", value = "pooled-lo"),
      @Parameter(name = "increment_size", value = "100")})
  private T id;

  @Override
  public T getId() {
    return this.id;
  }

  public void setId(final T id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractEntity)) {
      return false;
    }
    AbstractEntity<?> that = (AbstractEntity<?>) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getId());
  }
}



