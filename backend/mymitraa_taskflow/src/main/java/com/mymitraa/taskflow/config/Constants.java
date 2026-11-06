package com.mymitraa.taskflow.config;

/**
 * Global Constants for MyMitraa TaskFlow backend.
 */
public final class Constants {

    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String DATABASE_NAME = "mymitraa_taskflow";

    // Security & Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    // System account
    public static final String SYSTEM_ACCOUNT = "system";

    private Constants() {
        // utility class
    }
}
