package com.toeic.online.security.jwt;

import com.toeic.online.service.dto.AdminUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String token;
    private final AdminUserDTO currentUser;

}
