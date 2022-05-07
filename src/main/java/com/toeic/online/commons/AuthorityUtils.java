package com.toeic.online.commons;

import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Authority;
import com.toeic.online.domain.User;
import com.toeic.online.security.AuthoritiesConstants;
import com.toeic.online.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class AuthorityUtils {

    @Autowired
    private UserService userService;

    private static final Authority AUTH_HT = new Authority(AuthoritiesConstants.HT);
    private static final Authority AUTH_GVCN = new Authority(AuthoritiesConstants.GVCN);
    private static final Authority AUTH_ADMIN = new Authority(AuthoritiesConstants.ADMIN);
    private static final Authority AUTH_HP = new Authority(AuthoritiesConstants.HP);
    private static final Authority AUTH_GVBM = new Authority(AuthoritiesConstants.GVBM);
    private static final Authority AUTH_TK = new Authority(AuthoritiesConstants.TK);

    public Boolean checkAuthStudentAction() {
        Optional<User> userOpt = this.userService.getUserWithAuthorities();
        User user = userOpt.get();

        Set<Authority> authorities = user.getAuthorities();

        return authorities.contains(AUTH_GVCN) || authorities.contains(AUTH_HT) || authorities.contains(AUTH_ADMIN);
    }

    public Boolean checkAuthTeacherAction(){
        Optional<User> userOpt = this.userService.getUserWithAuthorities();
        User user = userOpt.get();
        Set<Authority> authorities = user.getAuthorities();
        return authorities.contains(AUTH_HT) || authorities.contains(AUTH_ADMIN);
    }

    public Boolean checkCNorBM() {
        Optional<User> userOpt = this.userService.getUserWithAuthorities();
        User user = userOpt.get();

        Set<Authority> userAuthorities = user.getAuthorities();

        List<String> authorities = this.userService.getAuthorities();

        for (String auth : authorities) {
            Authority AUTH = new Authority(auth);
            if ( !auth.equals(AuthoritiesConstants.GVBM) && !auth.equals(AuthoritiesConstants.GVCN) && userAuthorities.contains(AUTH)) {
                return false;
            }
        }

        return true;
    }

    public Boolean checkTK() {
        Optional<User> userOpt = this.userService.getUserWithAuthorities();
        User user = userOpt.get();

        Set<Authority> authorities = user.getAuthorities();

        return authorities.contains(AUTH_TK) && (!authorities.contains(AUTH_HT) && !authorities.contains(AUTH_HP) && !authorities.contains(AUTH_ADMIN));
    }
}
