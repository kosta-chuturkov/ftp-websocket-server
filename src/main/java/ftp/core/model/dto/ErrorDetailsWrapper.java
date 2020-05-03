package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class ErrorDetailsWrapper implements Serializable {
    @JsonProperty
    List<ErrorDetails> errors = Lists.newArrayList();

    public void addError(ErrorDetails error) {
        this.errors.add(error);
    }
}
