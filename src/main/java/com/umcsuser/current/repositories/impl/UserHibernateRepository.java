package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.User;
import org.hibernate.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class UserHibernateRepository {

    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    public List<User> findAll() {
        return session.createQuery("FROM User", User.class).list();
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(session.get(User.class, id));
    }

    public User save(User user) {
        return session.merge(user);
    }

    public void deleteById(String id) {
        User user = session.get(User.class, id);

        if (user != null) {
            session.remove(user);
        }
    }

    public Optional<User> findByLogin(String login) {
        org.hibernate.query.Query<User> query = session.createQuery(
                "FROM User u WHERE u.login = :login", User.class);

        query.setParameter("login", login);

        return query.uniqueResultOptional();
    }
}