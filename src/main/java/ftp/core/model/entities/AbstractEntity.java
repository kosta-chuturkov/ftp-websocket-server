package ftp.core.model.entities;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Entity<T> {

  private static final long serialVersionUID = 1L;
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

}



