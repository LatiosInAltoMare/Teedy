package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * User registration request entity.
 *
 * @author jtremeaux
 */
@Entity
@Table(name = "T_USER_REGISTER_REQUEST")
public class UserRegisterRequest {
    /**
     * Request ID.
     */
    @Id
    @Column(name = "URR_ID_C", length = 36)
    private String id;

    /**
     * Email address.
     */
    @Column(name = "URR_EMAIL_C", nullable = false, length = 100)
    private String email;

    /**
     * Requested username.
     */
    @Column(name = "URR_USERNAME_C", nullable = false, length = 50)
    private String username;

    /**
     * Request status (PENDING, APPROVED, REJECTED).
     */
    @Column(name = "URR_STATUS_C", nullable = false, length = 20)
    private String status;

    /**
     * Creation date.
     */
    @Column(name = "URR_CREATEDATE_D", nullable = false)
    private Date createDate;

    /**
     * Processing date (when approved or rejected).
     */
    @Column(name = "URR_PROCESSDATE_D")
    private Date processDate;

    public String getId() {
        return id;
    }

    public UserRegisterRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRegisterRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRegisterRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserRegisterRequest setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserRegisterRequest setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public UserRegisterRequest setProcessDate(Date processDate) {
        this.processDate = processDate;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("email", email)
                .add("status", status)
                .toString();
    }
}