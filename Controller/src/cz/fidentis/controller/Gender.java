/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.controller;

/**
 *
 * @author Marek Zuzi
 */
public enum Gender {

    MALE("Male"),
    FEMALE("Female");

    private final String str;

    Gender(String s) {
        this.str = s;
    }

    @Override
    public String toString() {
        return str;
    }
}
