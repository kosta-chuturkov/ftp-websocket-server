package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class RegistrationRequest implements Serializable {
    @JsonProperty
    private String email;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private String password;
    @JsonProperty("password_repeated")
    private String passwordRepeated;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String email, String nickname, String password, String passwordRepeated) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.passwordRepeated = passwordRepeated;
    }

    public String getPasswordRepeated() {
        return passwordRepeated;
    }

    public void setPasswordRepeated(String passwordRepeated) {
        this.passwordRepeated = passwordRepeated;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
