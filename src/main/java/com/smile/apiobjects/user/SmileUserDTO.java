package com.smile.apiobjects.user;

import lombok.Builder;

@Builder
public record SmileUserDTO(Integer id,
                           String username,
                           String nickname,
                           String email,
                           SmileRole roles,
                           Boolean enabled) {
}
