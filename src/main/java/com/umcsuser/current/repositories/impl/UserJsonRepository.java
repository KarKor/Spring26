package com.umcsuser.current.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.current.db.JsonFileStorage;
import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserJsonRepository implements UserRepository {
    private final JsonFileStorage<User> storage;

    public UserJsonRepository(String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<User>>(){}.getType());
    }

    @Override
    public List<User> findAll() { return storage.load(); }

    @Override
    public Optional<User> findById(String id) {
        return findAll().stream().filter(u -> u.getID().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return findAll().stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    @Override
    public User save(User user) {
        List<User> users = findAll();
        users.removeIf(u -> u.getID().equals(user.getID()));
        users.add(user);
        storage.save(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        List<User> users = findAll();
        users.removeIf(u -> u.getID().equals(id));
        storage.save(users);
    }
}