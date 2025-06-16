package org.example.common.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JWTToken {
    private String access_token;
    private String token_type;
    private Long expires_in;
}
