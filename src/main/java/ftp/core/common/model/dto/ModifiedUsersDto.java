package ftp.core.common.model.dto;

/**
 * Created by kosta on 1.6.2016 г..
 */
public class ModifiedUsersDto {

    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ModifiedUsersDto that = (ModifiedUsersDto) o;

        return this.name != null ? this.name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }
}
