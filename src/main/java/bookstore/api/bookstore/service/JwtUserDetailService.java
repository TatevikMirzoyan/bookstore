package bookstore.api.bookstore.service;

import bookstore.api.bookstore.configuration.security.session.CurrentUser;
import bookstore.api.bookstore.persistence.entity.UserEntity;
import bookstore.api.bookstore.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Optional;

import static bookstore.api.bookstore.configuration.security.session.SessionUser.SESSION_USER_KEY;


/**
 * @author Tatevik Mirzoyan
 * Created on 12-Apr-21
 */
@Service
public class JwtUserDetailService implements UserDetailsService {

    final UserRepository userRepository;

    @Autowired
    public JwtUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = getUserEntityByUsername(username.toLowerCase()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with name=%s was not found", username)));
        storeSessionUser(user);
        return new User(username, user.getPassword(), new ArrayList<>());
    }

    private void storeSessionUser(UserEntity user) {
        CurrentUser currentUser = new CurrentUser(user);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        servletRequestAttributes.getRequest().getSession().setAttribute(SESSION_USER_KEY, currentUser.getUser());
    }

    private Optional<UserEntity> getUserEntityByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username);
        return Optional.ofNullable(user);
    }
}
