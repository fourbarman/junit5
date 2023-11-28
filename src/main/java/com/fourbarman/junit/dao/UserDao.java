package com.fourbarman.junit.dao;

import lombok.SneakyThrows;

import java.sql.DriverManager;

/**
 * Sample class to emulate db connection.
 */
public class UserDao {
    @SneakyThrows
    public boolean delete(Integer userId) {
        try (var connection = DriverManager.getConnection("url", "username", "password")) {
            return true;
        }
    }
}
