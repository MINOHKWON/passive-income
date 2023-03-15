package io.passive.api.model;

import org.springframework.security.access.annotation.Secured;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserVO {
    private String userId;
    private String userNm;
    private String pw;
}
