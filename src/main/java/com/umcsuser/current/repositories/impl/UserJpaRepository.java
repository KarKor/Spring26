package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class UserJpaRepository implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public User save(User user) {
        return entityManager.merge(user);
    }

    @Override
    public void deleteById(String id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        TypedQuery<User> query = entityManager.createQuery(
                "FROM User u WHERE u.login = :login", User.class);

        query.setParameter("login", login);

        return query.getResultStream().findFirst();
    }
}