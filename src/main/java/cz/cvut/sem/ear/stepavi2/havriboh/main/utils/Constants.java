package cz.cvut.sem.ear.stepavi2.havriboh.main.utils;


import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Role;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.USER;

    /**
     * Username login form parameter.
     */
    public static final String USERNAME_PARAM = "username";

    private Constants() {
        throw new AssertionError();
    }
}
