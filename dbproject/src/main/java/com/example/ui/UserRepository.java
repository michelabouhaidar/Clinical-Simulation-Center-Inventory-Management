package com.example.ui;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.example.domain.User;

public class UserRepository {
    public User findByUsername(EntityManager em, String username) {
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class);
            q.setParameter("u", username);
            return q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void save(EntityManager em, User u) {
        if (em.contains(u) || u.getId() != null && em.find(User.class, u.getId()) != null) {
            em.merge(u);
        } else {
            em.persist(u);
        }
    }
}
