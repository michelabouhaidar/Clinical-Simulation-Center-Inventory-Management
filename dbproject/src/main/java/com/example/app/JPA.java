package com.example.app;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JPA {
    private static EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("dbprojectPU");
    }

    private JPA() {}

    public static EntityManager em() { return emf.createEntityManager(); }
}
