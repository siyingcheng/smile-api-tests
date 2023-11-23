package com.smile.apiobjects.user;

import com.simon.core.apidriver.auth.ILogin;
import lombok.Getter;

import static com.smile.apiobjects.user.IUser.DEFAULT_PASSWORD;

@Getter
public enum SmileUsers implements ILogin {
    ADMIN(1, "admin", DEFAULT_PASSWORD, SmileRole.ROLE_ADMIN, "admin@example.com", true),
    INVALID(2, "invalid", DEFAULT_PASSWORD, SmileRole.ROLE_USER, "invalid@example.com", false),
    OWEN(202, "owen", DEFAULT_PASSWORD, SmileRole.ROLE_USER, "owen0999@example.com", true);

    private final Integer id;
    private final String username;
    private final String password;
    private final SmileRole role;
    private final String email;
    private final Boolean enabled;

    SmileUsers(Integer id, String username, String password, SmileRole role, String email, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.enabled = enabled;
    }
}
