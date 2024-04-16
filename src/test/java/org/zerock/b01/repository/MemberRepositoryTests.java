package org.zerock.b01.repository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMembers(){

        IntStream.rangeClosed(1,100).forEach(i -> {
            // 100명의 사용자 추가 기본적으로 user룰 90번 이상에는 admin 룰 추가
            Member member = Member.builder()
                    .mid("member"+i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email"+i+"@aaa.bbb")
                    .build();

            member.addRole(MemberRole.USER);

            if(i >= 90){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);

        });

    }
//
    @Test  // 725 추가
    public void testRead() {

        Optional<Member> result = memberRepository.getWithRoles("member100");

        Member member = result.orElseThrow();

        log.info(member);
        log.info(member.getRoleSet());

        member.getRoleSet().forEach(memberRole -> log.info(memberRole.name()));
        //Hibernate:
        //    select
        //        m1_0.mid,
        //        m1_0.del,
        //        m1_0.email,
        //        m1_0.moddate,
        //        m1_0.mpw,
        //        m1_0.regdate,
        //        r1_0.member_mid,
        //        r1_0.role_set,
        //        m1_0.social
        //    from
        //        member m1_0
        //    left join
        //        member_role_set r1_0
        //            on m1_0.mid=r1_0.member_mid
        //    where
        //        m1_0.mid=?
        //        and m1_0.social=0
        //2024-04-16T12:54:58.146+09:00  INFO 8352 --- [    Test worker] o.z.b.repository.MemberRepositoryTests   : Member(mid=member100, mpw=$2a$10$7Fys9UNXVpQ91SXJnDJygOrX78C3zxs8zBF3ZhHmEAFV7jlh6360y, email=email100@aaa.bbb, del=false, social=false)
        //2024-04-16T12:54:58.147+09:00  INFO 8352 --- [    Test worker] o.z.b.repository.MemberRepositoryTests   : [USER, ADMIN]
        //2024-04-16T12:54:58.149+09:00  INFO 8352 --- [    Test worker] o.z.b.repository.MemberRepositoryTests   : USER
        //2024-04-16T12:54:58.150+09:00  INFO 8352 --- [    Test worker] o.z.b.repository.MemberRepositoryTests   : ADMIN

    }

    @Test // 762 추가 테스트 소셜로그인 암호 변경 체크용
    @Commit
    public void testUpdate() {

        String mid ="lonen@nate.com";
        String mpw = passwordEncoder.encode("54321");

        memberRepository.updatePassword(mpw,mid);
        //Hibernate:
        //    update
        //        member
        //    set
        //        mpw=?
        //    where
        //        mid=?
    }

}
