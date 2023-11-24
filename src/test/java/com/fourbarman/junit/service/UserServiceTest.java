package com.fourbarman.junit.service;

import com.fourbarman.junit.dto.User;
import org.junit.jupiter.api.*;


import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 1. TestInstance.Lifecycle.PER_METHOD
 * New class instance for every @Test method. @AfterAll and @BeforeAll should be static. Default behavior.
 * 2. TestInstance.Lifecycle.PER_CLASS
 * One class instance for every @Test method. @AfterAll and @BeforeAll can not be static.
 *
 * Tags.
 * If needed to start tests with Tags by terminal, i.e.:
 * "mvn clean test -Dgroups=login"
 * Or
 * "mvn clean test -DexcludedGroups=login"
 */
@Tag("fast")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
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
    @DisplayName("users empty if no users added")
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test usersEmptyIfNoUserAdded:" + this);
        var users = userService.getAll();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("users size = 2 if two User added")
    void userSizeIFUserAdded() {
        System.out.println("Test userSizeIFUserAdded:" + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();
        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }
    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each:" + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all:");
    }
    @Nested
    @Tag("login")
    @DisplayName("Test user login functionality")
    class LoginTest {
        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
        }

        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> {
                        var e = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
                        assertThat(e.getMessage()).isEqualTo("Username or password is null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }

        @Test
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
            assertTrue(maybeUser.isEmpty());
        }


        @Test
        void loginFailIfUserDoesNotExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());
            assertTrue(maybeUser.isEmpty());
        }
    }
}
