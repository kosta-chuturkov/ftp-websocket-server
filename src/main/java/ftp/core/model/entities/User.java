package ftp.core.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import ftp.core.security.Authorities;
import java.util.Collection;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User extends AbstractEntity<Long> implements UserDetails {

  @NotNull
  @NotEmpty
  @Column(name = "nickname", length = 32)
  private String nickName;

  @NotNull
  @NotEmpty
  @Column(name = "email", length = 32)
  private String email;

  @NotNull
  @NotEmpty
  @JsonIgnore
  @Column(name = "pass", length = 64)
  private String password;

  @Column(name = "remaining_storage")
  private long remainingStorage;

  @Column(name = "token")
  @JsonIgnore
  private Long token;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private Set<File> uploadedFiles = Sets.newHashSet();

  @JsonIgnore
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  private Set<Authority> authorities;

  @JsonIgnore
  @Column(name = "account_non_expired")
  private Boolean accountNonExpired = Boolean.TRUE;

  @JsonIgnore
  @Column(name = "account_non_locked")
  private Boolean accountNonLocked = Boolean.TRUE;

  @JsonIgnore
  @Column(name = "credentials_non_expired")
  private Boolean credentialsNonExpired = Boolean.TRUE;

  @JsonIgnore
  @Column(name = "enabled")
  private Boolean enabled = Boolean.TRUE;

  public User() {
    this.authorities = Sets.newHashSet();
  }

  private User(Builder builder) {
    setNickName(builder.nickName);
    setEmail(builder.email);
    setPassword(builder.password);
    setRemainingStorage(builder.remainingStorage);
    setToken(builder.token);
    setUploadedFiles(builder.uploadedFiles);
    setAuthorities(builder.authorities);
    this.accountNonExpired = builder.accountNonExpired;
    this.accountNonLocked = builder.accountNonLocked;
    this.credentialsNonExpired = builder.credentialsNonExpired;
    this.enabled = builder.enabled;
    this.authorities = Sets.newHashSet();
  }


  public static User getCurrent() {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    final Authentication authentication = securityContext.getAuthentication();
    String userName;
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails) {
        final User springSecurityUser = (User) authentication.getPrincipal();
        return springSecurityUser;
      } else if (authentication.getPrincipal() instanceof String) {
        userName = (String) authentication.getPrincipal();
        throw new RuntimeException(
            "Request now allowed as: [" + userName + "]. You must login first.");
      }
    }
    return null;
  }

  public static boolean isCurrentUserInRole(final String authority) {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    final Authentication authentication = securityContext.getAuthentication();
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails) {
        final UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
        return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(authority));
      }
    }
    return false;
  }

  public static boolean isAuthenticated() {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication != null) {
      final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
      if (authorities != null) {
        for (final GrantedAuthority authority : authorities) {
          if (authority.getAuthority().equals(Authorities.ANONYMOUS)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public void addAuthority(Authority authority) {
    if (!this.authorities.contains(authority)) {
      this.authorities.add(authority);
    }
  }

  public String getNickName() {
    return this.nickName;
  }

  public void setNickName(final String nickName) {
    this.nickName = nickName;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  @Override
  public String getUsername() {
    return getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  public void setAccountNonExpired(final boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  public void setAccountNonLocked(final boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  public void setCredentialsNonExpired(final boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public long getRemainingStorage() {
    return this.remainingStorage;
  }

  public void setRemainingStorage(final long remainingStorage) {
    this.remainingStorage = remainingStorage;
  }

  public Long getToken() {
    return this.token;
  }

  public void setToken(final Long token) {
    this.token = token;
  }

  public Set<File> getUploadedFiles() {
    return this.uploadedFiles;
  }

  public void setUploadedFiles(final Set<File> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
  }

  @Override
  public Set<Authority> getAuthorities() {
    return this.authorities;
  }

  public void setAuthorities(final Set<Authority> authorities) {
    this.authorities = authorities;
  }

  public boolean addUploadedFile(final File file) {
    if (!this.uploadedFiles.contains(file)) {
      return this.uploadedFiles.add(file);
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
    result = prime * result + ((this.nickName == null) ? 0 : this.nickName.hashCode());
    result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final User other = (User) obj;
    if (this.email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!this.email.equals(other.email)) {
      return false;
    }
    if (this.nickName == null) {
      if (other.nickName != null) {
        return false;
      }
    } else if (!this.nickName.equals(other.nickName)) {
      return false;
    }
    if (this.password == null) {
      if (other.password != null) {
        return false;
      }
    } else if (!this.password.equals(other.password)) {
      return false;
    }
    return true;
  }

  public static final class Builder {

    private String nickName;
    private String email;
    private String password;
    private long remainingStorage;
    private Long token;
    private Set<File> uploadedFiles;
    private Set<Authority> authorities;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;

    public Builder() {
    }

    public Builder(User copy) {
      this.nickName = copy.nickName;
      this.email = copy.email;
      this.password = copy.password;
      this.remainingStorage = copy.remainingStorage;
      this.token = copy.token;
      this.uploadedFiles = copy.uploadedFiles;
      this.authorities = copy.authorities;
      this.accountNonExpired = copy.accountNonExpired;
      this.accountNonLocked = copy.accountNonLocked;
      this.credentialsNonExpired = copy.credentialsNonExpired;
      this.enabled = copy.enabled;
    }

    public Builder withNickName(String val) {
      this.nickName = val;
      return this;
    }

    public Builder withEmail(String val) {
      this.email = val;
      return this;
    }

    public Builder withPassword(String val) {
      this.password = val;
      return this;
    }

    public Builder withRemainingStorage(long val) {
      this.remainingStorage = val;
      return this;
    }

    public Builder withToken(Long val) {
      this.token = val;
      return this;
    }

    public Builder withUploadedFiles(Set<File> val) {
      this.uploadedFiles = val;
      return this;
    }

    public Builder withAuthorities(Set<Authority> val) {
      this.authorities = val;
      return this;
    }

    public Builder withAccountNonExpired(Boolean val) {
      this.accountNonExpired = val;
      return this;
    }

    public Builder withAccountNonLocked(Boolean val) {
      this.accountNonLocked = val;
      return this;
    }

    public Builder withCredentialsNonExpired(Boolean val) {
      this.credentialsNonExpired = val;
      return this;
    }

    public Builder withEnabled(Boolean val) {
      this.enabled = val;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }
}
