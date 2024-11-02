package com.ecommerce.project.deewas.eShop.entity.enums;


/**
 * Purpose: This enum defines the specific actions or permissions that can be granted, such as reading, updating, creating, or deleting something for both ADMIN and MANAGER roles
 * Each of these constants is associated with a string value (like "admin:read", "admin:update") that describes the permission in a more human-readable way.
 * ADMIN_READ and ADMIN_UPDATE are constants of the enum, and "admin:read" or "admin:update" are strings that represent the permission as a label.
 * These strings are stored in a private field permission, which is initialized through the constructor of the enum.
 */

public enum Permission {

//    ADMIN_READ is the enum constant. "admin:read" is the value passed to the constructor to initialize the "permission" field for the ADMIN_READ enum constant.

    ADMIN_READ("admin:read"),  // This is an enum constant that takes a single string argument "admin:read". The string "admin:read" is the value passed to the constructor and assigned to the permission field.
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete"),
    ;

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission; // will return value like "admin:read", and if returned this.name(), it would return enum constants like ADMIN_READ
    }

}

// System.out.println(Permission.ADMIN_READ);   OUTPUT: ADMIN_READ
// System.out.println(Permission.ADMIN_READ.name());   OUTPUT: ADMIN_READ
// System.out.println(Permission.ADMIN_READ.getPermission());  OUTPUT: "admin:read"
// System.out.println(Permission.ADMIN_READ("hello"));   OUTPUT: Error
