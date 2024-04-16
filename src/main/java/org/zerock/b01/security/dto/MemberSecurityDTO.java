package org.zerock.b01.security.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User implements OAuth2User { // 754 추가 implements OAuth2User

    //import org.springframework.security.core.userdetails.User;
    // 도메인으로 회원은 특별한 점은 없지만 시큐리트를 이용하는 경우 회원 dto는 해당 api에 맞게 작성되어야 함.
    // 스프링 스큐리티에서는 UserDetails라는 타입을 이용함
    // 필드
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;

    // private String username;
    
    private Map<String, Object> props ; // 소셜 로그인 정보 754 추가


    //생성자 -> extends User 에서 받음
    // public MemberSecurityDTO(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    // public MemberSecurityDTO(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
    public MemberSecurityDTO(String username, String password, String email, boolean del, boolean social, Collection<? extends GrantedAuthority> authorities) {
    //                              이름             암호              이메일          탈퇴,         소셜                         권한
        super(username, password, authorities);

        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.del = del;
        this.social = social;
       // this.username = username;

    }

    // 754 추가
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.mid;
    }

    public String getUsername() {
        return this.mid;
    }


}
