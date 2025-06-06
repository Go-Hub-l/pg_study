package com.example.pgstudy;

import java.util.List;

public interface UserMapper {
    void insertUser(User user);
    List<User> selectAllUsers();
    void updateUser(User user);
    void deleteUserByName(String name);
} 