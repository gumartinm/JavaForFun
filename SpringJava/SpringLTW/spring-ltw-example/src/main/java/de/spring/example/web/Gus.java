package de.spring.example.web;

import de.spring.example.annotation.initTransactional;

public class Gus {

    @initTransactional
    public void prueba() {
        System.out.println("GUSOOK");
    }
}
