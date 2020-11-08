package com.emysilva.etransaction.service.impl;

import com.emysilva.etransaction.model.User;
import com.emysilva.etransaction.model.security.UserRole;
import com.emysilva.etransaction.repository.UserRepository;
import com.emysilva.etransaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean checkUserExists(String username, String email) {
        return checkUsernameExists(username) || checkEmailExists(email);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return findByUsername(username) != null ;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public User createUser(User user, Set<UserRole> userRoles) {
        return null;
    }

    @Override
    public List<User> findUserList() {
        return userRepository.findAll();
    }

    @Override
    public void enableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }
}
