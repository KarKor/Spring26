package com.umcsuser.current.repositories;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user);
    boolean add(User user);
    boolean remove(String login);
    List<String> logins();
}