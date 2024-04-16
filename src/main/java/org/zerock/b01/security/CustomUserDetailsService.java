package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.security.dto.MemberSecurityDTO;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor // 690 passwordEncoder.encode시 주석 처리 // 728 재사용
public class CustomUserDetailsService implements UserDetailsService {
    // UserDetailsService의 구현체로 loadUserByUsername 메서드를 재정의하여 사용함.
    // 실제 로그인 처리를 담당하는 곳으로 MemberRepository를 주입 받아 memberSecurityDTO로 반환 처리
    // 728 제거    private PasswordEncoder passwordEncoder; // 689 CustomSecurityConfig에서 생성함 패스워드 암호화

    // 728 제거 public CustomUserDetailsService() { // 689 추가 생성자 실행시 암호화 처리함.
    // 728 제거        this.passwordEncoder = new BCryptPasswordEncoder();
    // 728 제거    }

    private final MemberRepository memberRepository; // 728 추가

    @Override // 688 추가
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // loadUserByUsername은 UserDetailsService의 단 하나의 메서드로 실제 인증 처리할 때 호출 됨
        log.info("loadUserByUsername 메서드 실행 ");
        log.info("loadUserByUsername.로그온 사용자의 이름 :" + username);
        // 690 제거 return null;

        // UserDetails는 사용자 인증과 관련된 정보를 저장하는 역할
        // UserDetails타입 객체를 이용해서 패스워드를 검사하고, 사용자 권한을 확인하는 방식으로 동작함.
        // getAuthorites() 메소드는 사용자가 가진 인가(Authority) 정보를 반환 해야 함.
            // 728체거       UserDetails userDetails = User.builder() // 690 추가 (로그인 테슽용)
            //                .username("USER1")
            //                //.password("1111")
            //                .password(passwordEncoder.encode("1111"))
            //                .authorities("ROLE_USER")  //@PreAuthorize("hasRole('USER')")
            //                .build();
        // password가 인코딩 되지 않아 오류 발생 -> CustomSecurityConfig에 메서드 추가

        // 729 제거 return userDetails;

        Optional<Member> result = memberRepository.getWithRoles(username);

        if(result.isEmpty()) {  // 해당 사용자가 없다면

            throw new UsernameNotFoundException("username not found");
        }

        Member member = result.get(); // 결과를 가져와 Member 엔티티 객체에 담는다.

        MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO( // Member 객체에 있는 값을 MemberSecurityDTO에 넣는다.
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                false,
                member.getRoleSet().stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())).collect(Collectors.toList()) //룰을 가져와 콜렉션 처리함.
        );

        log.info("memberSecurityDTO---------");
        log.info(memberSecurityDTO);

        return memberSecurityDTO;  // 엔티티 객체를 가져와 dto로 리턴한다.


    }
}
