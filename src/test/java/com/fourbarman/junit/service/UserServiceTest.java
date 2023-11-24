package com.fourbarman.junit.service;

import com.fourbarman.junit.dto.User;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

/**
 * 1. TestInstance.Lifecycle.PER_METHOD
 * New class instance for every @Test method. @AfterAll and @BeforeAll should be static. Default behavior.
 * 2. TestInstance.Lifecycle.PER_CLASS
 * One class instance for every @Test method. @AfterAll and @BeforeAll can not be static.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {

    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("Before all:");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each:" + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test usersEmptyIfNoUserAdded:" + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void userSizeIFUserAdded() {
        System.out.println("Test userSizeIFUserAdded:" + this);
        userService.add(new User());
        userService.add(new User());

        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each:" + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all:");
    }

}
