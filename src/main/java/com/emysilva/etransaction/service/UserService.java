package com.emysilva.etransaction.service;

import com.emysilva.etransaction.model.User;
import com.emysilva.etransaction.model.security.UserRole;

import java.util.List;
import java.util.Set;

public interface UserService {
    User findByUsername(String username);

    User findByEmail(String email);

    boolean checkUserExists(String username, String email);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);

    User createUser(User user, Set<UserRole> userRoles);

    List<User> findUserList();

    void enableUser (String username);

    void disableUser (String username);
}
