package com.fourbarman.junit.service;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test

    void usersEmptyIfNoUserAdded() {
        var userService = new UserService();
        var users = userService.getAll();
        assertFalse(users.isEmpty(), () -> "User list should be empty");
    }

}
