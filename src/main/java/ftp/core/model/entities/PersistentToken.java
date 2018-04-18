package ftp.core.model.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Persistent tokens are used by Spring Security to automatically log in users.
 */
@Entity
@Table(name = "persistent_token")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PersistentToken implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern("d MMMM yyyy");

  private static final int MAX_USER_AGENT_LEN = 255;

  @Id
  private String series;

  @JsonIgnore
  @NotNull
  @Column(name = "token_value", nullable = false)
  private String tokenValue;

  @JsonIgnore
  @Column(name = "token_date")
  private LocalDate tokenDate;

  // an IPV6 address max length is 39 characters
  @Size(min = 0, max = 39)
  @Column(name = "ip_address", length = 39)
  private String ipAddress;

  @Column(name = "user_agent")
  private String userAgent;

  @JsonIgnore
  @ManyToOne
  private User user;

  public String getSeries() {
    return this.series;
  }

  public void setSeries(final String series) {
    this.series = series;
  }

  public String getTokenValue() {
    return this.tokenValue;
  }

  public void setTokenValue(final String tokenValue) {
    this.tokenValue = tokenValue;
  }

  public LocalDate getTokenDate() {
    return this.tokenDate;
  }

  public void setTokenDate(final LocalDate tokenDate) {
    this.tokenDate = tokenDate;
  }

  @JsonGetter
  public String getFormattedTokenDate() {
    return DATE_TIME_FORMATTER.format(this.tokenDate);
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public void setUserAgent(final String userAgent) {
    if (userAgent.length() >= MAX_USER_AGENT_LEN) {
      this.userAgent = userAgent.substring(0, MAX_USER_AGENT_LEN - 1);
    } else {
      this.userAgent = userAgent;
    }
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(final User user) {
    this.user = user;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final PersistentToken that = (PersistentToken) o;

    if (!this.series.equals(that.series)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return this.series.hashCode();
  }

  @Override
  public String toString() {
    return "PersistentToken{" + "series='" + this.series + '\'' + ", tokenValue='" + this.tokenValue
        + '\''
        + ", tokenDate=" + this.tokenDate + ", ipAddress='" + this.ipAddress + '\''
        + ", userAgent='"
        + this.userAgent + '\'' + "}";
  }
}
