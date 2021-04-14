package bookstore.api.bookstore.configuration.security.session;

import bookstore.api.bookstore.persistence.entity.UserEntity;

import java.io.Serializable;

/**
 * @author Tatevik Mirzoyan
 * Created on 12-Apr-21
 */
public class SessionUser implements Serializable {

    public static final String SESSION_USER_KEY = "SESSION_USER";

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static SessionUser mapUserToSessionUser(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(entity.getId());
        sessionUser.setFirstName(entity.getFirstName());
        sessionUser.setLastName(entity.getLastName());
        sessionUser.setUsername(entity.getUsername());

        return sessionUser;
    }
}
