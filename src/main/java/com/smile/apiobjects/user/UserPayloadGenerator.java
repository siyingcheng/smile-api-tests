package com.smile.apiobjects.user;

import com.smile.core.apidriver.IPayload;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.json.JSONObject;

@Builder
@Accessors(chain = true)
@Data
public class UserPayloadGenerator implements IPayload {
    private Integer id;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private SmileRole roles;
    private Boolean enabled;


    public static UserPayloadGenerator of(SmileUserDTO userDTO) {
        return UserPayloadGenerator.builder()
                .id(userDTO.id())
                .username(userDTO.username())
                .nickname(userDTO.nickname())
                .email(userDTO.email())
                .roles(userDTO.roles())
                .enabled(userDTO.enabled())
                .build();
    }

    @Override
    public String buildPayload() {
        return new JSONObject(this).toString();
    }
}
