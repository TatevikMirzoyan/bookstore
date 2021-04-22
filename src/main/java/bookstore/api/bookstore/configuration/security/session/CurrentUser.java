package bookstore.api.bookstore.configuration.security.session;

import bookstore.api.bookstore.persistence.entity.UserEntity;

/**
 * @author Tatevik Mirzoyan
 * Created on 12-Apr-21
 */
public class CurrentUser {

    private static final long serialVersionUID = 1L;
    private final SessionUser user;

    public CurrentUser(UserEntity user) {
        this.user = SessionUser.mapUserToSessionUser(user);
    }

    public SessionUser getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String toString() {
        return "CurrentUser{" + "user=" + user + "} " + super.toString();
    }
}
