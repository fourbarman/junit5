package com.fourbarman.junit.service;

import com.fourbarman.junit.dao.UserDao;
import com.fourbarman.junit.dto.User;
import com.fourbarman.junit.extension.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mockito;


import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 1. TestInstance.Lifecycle.PER_METHOD
 * New class instance for every @Test method. @AfterAll and @BeforeAll should be static. Default behavior.
 * 2. TestInstance.Lifecycle.PER_CLASS
 * One class instance for every @Test method. @AfterAll and @BeforeAll can not be static.
 * <p>
 * Tags.
 * If needed to start tests with Tags by terminal, i.e.:
 * "mvn clean test -Dgroups=login"
 * Or
 * "mvn clean test -DexcludedGroups=login"
 */
@Tag("fast")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        //ThrowableExtension.class
})
public class UserServiceTest extends TestBase {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserDao userDao;
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void init() {
        System.out.println("Before all:");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each:" + this);
        this.userDao = Mockito.mock(UserDao.class);
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUSer() {
        userService.add(IVAN);
        //works great with Spy.
        Mockito.doReturn(true).when(userDao).delete(Mockito.any());
        //this variant allows to chain return statement from Mock ".thenReturn().thenReturn()..."
        Mockito.when(userDao.delete(Mockito.any())).thenReturn(true);
        boolean deleteResult = userService.delete(IVAN.getId());
        assertThat(deleteResult).isTrue();
    }

    @Test
    @DisplayName("users empty if no users added")
    void usersEmptyIfNoUserAdded() throws IOException {
        if (true) {
            //throw new IOException(); this will throw IOException as implemented in ThrowableExtension!
            throw new RuntimeException();//this should pass!
        }
        System.out.println("Test usersEmptyIfNoUserAdded:" + this);
        var users = userService.getAll();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("users size = 2 if two User added")
    void userSizeIFUserAdded(UserService userService) {
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
        //        @Test
        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        // injected RepetitionInfo - can be used as info about repetitions of test
        void loginSuccessIfUserExists(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
        }

        @Test
            //Timeouts great for acceptance tests, and sometimes - for integration tests.
            //@Timeout(value = 200L, unit = TimeUnit.MILLISECONDS) the same, but asserts are better to read & understand.
        void checkLoginFunctionalityPerformance() {
            Optional<User> maybeUser = assertTimeout(Duration.ofMillis(200L), () -> {
                Thread.sleep(100L);
                return userService.login(IVAN.getUsername(), IVAN.getPassword());
            });
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

        @ParameterizedTest(name = "{arguments} test")
        //only for 1 argument
        //@NullSource
        //@EmptySource
        //@ValueSource(strings = {
        //        "Ivan", "Petr"
        //})
        //@ArgumentsSource()
        //MethodSource(fullPathToClass#staticMethod)
        //Params from .csv file
        //@CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
        //Params without csv file
//        @CsvSource({
//                "Ivan,123",
//                "Petr,111"
//        })
        @MethodSource("com.fourbarman.junit.service.UserServiceTest#getArgumentsForLoginTest")
        @DisplayName("login param test")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);

            Optional<User> maybeUser = userService.login(username, password);

            assertThat(maybeUser).isEqualTo(user);
        }
    }

    //Method for MethodSource.
    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );
    }
}
