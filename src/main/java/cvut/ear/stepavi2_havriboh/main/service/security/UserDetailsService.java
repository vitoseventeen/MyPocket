package cvut.ear.stepavi2_havriboh.main.service.security;

import cvut.ear.stepavi2_havriboh.main.dao.UserDao;
import cvut.ear.stepavi2_havriboh.main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public UserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new cvut.ear.stepavi2_havriboh.main.security.model.UserDetails(user.get());
    }
}