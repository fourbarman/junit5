package com.fourbarman.junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {

    private UserDao userDao;
    private Map<Integer, Boolean> answers = new HashMap<>();

    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * If we didn't init UserDaoSpy (answers will not be initiated) then use userDao#delete()
     *
     * @param userId Id.
     * @return Integer.
     */
    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId, userDao.delete(userId));
    }
}
