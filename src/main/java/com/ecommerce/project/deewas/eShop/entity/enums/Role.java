package com.ecommerce.project.deewas.eShop.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * How They Work Together:
 * Permissions: The Permission enum defines specific actions like reading, creating, updating, and deleting for different roles (ADMIN, MANAGER).
 * Roles: The Role enum links a set of permissions to each role. For example, the ADMIN role gets both ADMIN and MANAGER permissions.
 * Authorities for Security: The getAuthorities() method converts each permission into a form that Spring Security can understand (SimpleGrantedAuthority), and adds the role itself as an authority (e.g., "ROLE_ADMIN").
 *
 * Permissions represent what actions a role can perform.
 * Roles group together those permissions.
 * The getAuthorities() method prepares everything so the security system knows what a user with a certain role can do.
 */


public enum Role {
    USER(Collections.emptySet()),
    MANAGER(
            Set.of(
                    Permission.MANAGER_READ,
                    Permission.MANAGER_CREATE,
                    Permission.MANAGER_UPDATE,
                    Permission.MANAGER_DELETE
            )
    ),
    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_CREATE,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_DELETE,

                    Permission.MANAGER_READ,
                    Permission.MANAGER_CREATE,
                    Permission.MANAGER_UPDATE,
                    Permission.MANAGER_DELETE
            )
    );

    private final Set<Permission> permissions;   // linking a set of permission to the role

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    // Get the granted authorities for the role. This method returns a list of SimpleGrantedAuthority objects that represent the permissions and the role itself as a granted authority.
    // SimpleGrantedAuthority is needed to make the permissions and the role available to spring security for authorization purposes when a user is authenticated.
    public List<SimpleGrantedAuthority> getAuthorities() {  //This method converts each permission in the permissions set to a SimpleGrantedAuthority object, which is used in Spring Security to handle authorization (who can access what).
        ArrayList<SimpleGrantedAuthority> grantedAuthorities;
        grantedAuthorities = new ArrayList<>(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name())) //transform each permission to simpleGrantedAuthority
                .toList());
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));  // it's a common convention in Spring Security to use "ROLE_" prefix. i.e. ROLE_ADMIN is treated as a role, ADMIN_READ might be treated as a permission.
        return grantedAuthorities;
    }
}
