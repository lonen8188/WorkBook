package org.zerock.b01.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
import org.zerock.b01.security.handler.Custom403Handler;
import org.zerock.b01.security.handler.CustomSocialLoginSuccessHandler;


import javax.sql.DataSource;

@Configuration // 시큐리티에서 user에 암호화된 pw를 콘솔에 보여줌.
//2024-04-16T10:13:03.075+09:00  WARN 2212 --- [  restartedMain] .s.s.UserDetailsServiceAutoConfiguration :
//Using generated security password: bda2cc6b-9c41-407b-a4fa-a9b0a9f06ef0
//This generated password is for development use only. Your security configuration must be updated before running your application in production.
//2024-04-16T10:13:03.238+09:00  INFO 2212 --- [  restartedMain] o.s.s.web.DefaultSecurityFilterChain     :  DefaultSecurityFilterChain을 반환해준다 즉 우리가 Bean으로 등록하는 SecurityFilterChain은 결국 DefaultSecrutiyFilterChain이었다
// Will secure any request with [org.springframework.security.web.session.DisableEncodeUrlFilter@3a7149cb,  : 세션 ID가 URL에 포함되는 것을 막기 위해 HttpServletResponse를 사용해서 URL이 인코딩 되는 것을 막기 위한 필터이다.
// org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@6fdd8f7b, : SpringSecurityContextHolder는 기본적으로 ThreadLocal 기반으로 동작하는데, 비동기와 관련된 기능을 쓸 때에도 SecurityContext를 사용할 수 있도록 만들어주는 필터이다.
// org.springframework.security.web.context.SecurityContextHolderFilter@7dd9f279, : SecurityContext가 없으면 만들어주는 필터이다. SecurityContext는 Authentication 객체를 보관하는 인터페이스이다. SecurityContext를 통해 한 요청에 대해서 어떤 필터에서도 같은 Authentication 객체를 사용할 수 있다.
// org.springframework.security.web.header.HeaderWriterFilter@39713148, : 응답에 Security와 관련된 헤더 값을 설정해주는 필터이다
// org.springframework.security.web.csrf.CsrfFilter@46160e36, : CSRF 공격을 방어하기 위한 설정을 하는 필터이다.
// org.springframework.security.web.authentication.logout.LogoutFilter@28b2bd69, : 로그아웃 요청을 처리하는 필터이다.  아래에 DefaultLogoutPageGeneratingFilter가 로그아웃 기본 페이지를 생성한다.
// org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter@1e98f693, username과 password를 쓰는 form 기반 인증을 처리하는 필터이다. //AuthenticationManager를 통한 인증을 실행한다.//성공하면 Authentication 객체를 SecurityHolder에 저장한 후 AuthenticationSuccessHandler를 실행한다. //실패하면 AuthenticationFailureHandler를 실행한다.
// org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter@27a4d812,  : 로그인 기본 페이지를 생성하는 필터이다.
// org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter@794e9ef0,  : 로그아웃 기본 페이지를 생성하는 필터이다.
// org.springframework.security.web.authentication.www.BasicAuthenticationFilter@93b8846, :HTTP header에 인증 값을 담아 보내는 BASIC 인증을 처리하는 필터이다.
// org.springframework.security.web.savedrequest.RequestCacheAwareFilter@f5e605, : 인증 처리 후 원래의 Request 정보로 재구성하는 필터이다.
// org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@2de21626,  : 서블릿 API 보안 메서드를 구현하는 요청 래퍼로 서블릿 요청을 채우는 필터이다.
// org.springframework.security.web.authentication.AnonymousAuthenticationFilter@4332ec9a, : 이 필터에 올 때까지 사용자가 인증되지 않았다면, 이 요청은 익명의 사용자가 보낸 것으로 판단할 수 있다. 이 익명 사용자에 관한 처리를 하는 필터이다.
// org.springframework.security.web.access.ExceptionTranslationFilter@3f49049d,  : 필터 처리 과정에서 인증 예외 또는 인가 예외가 발생한 경우 해당 예외를 잡아서 처리하는 필터이다.
// org.springframework.security.web.access.intercept.AuthorizationFilter@37ff1cbc] : HttpServletRequest에게 인증(authorization)을 제공한다. 이것은 Security Filter들의 하나인 FilterChainProxy안에 삽입되어있다
// SessionManagementFilter : 세션 생성 전략을 설정하는 필터이다. 최대 동시 접속 세션을 설정하고, 유효하지 않은 세션으로 접근했을 때의 처리, 세션 변조 공격 방지 등의 처리를 담당한다.
// ExceptionTranslationFilter : 필터 처리 과정에서 인증 예외 또는 인가 예외가 발생한 경우 해당 예외를 잡아서 처리하는 필터이다.
// FilterSecurityInterceptor  : 인가를 결정하는 AccessDicisionManager에게 접근 권한이 있는지 확인하고 처리하는 필터이다. 앞 필터들을 통과할 때 인증 또는 인가에 문제가 있으면 ExceptionTranslationFilter로 예외를 던진다.
@Log4j2
//@EnableMethodSecurity
@RequiredArgsConstructor
// @EnableGlobalMethodSecurity 시큐리티 6에서 차단
@EnableMethodSecurity(prePostEnabled = true) // 시큐리트 6에서 추가 https://jake-seo-dev.tistory.com/82
// @PreAuthorize(사전권한체크), @PostAuthorize(사후권한체크), @PreFilter, @PostFilter 를 기본으로 사용할 수 있게 해준다.
// 게시물 목록은 로그인 여부에 관계 없이 볼 수 있지만 게시물의 글쓰기는 로그인한 사용자만 접근 등...
public class CustomSecurityConfig {

    private final DataSource dataSource; // 704 추가 자동 로그인 및 db 연동용 import javax.sql.DataSource; 톰켓 10이지만 이것만 9버전
    private final CustomUserDetailsService userDetailsService; // 704 추가

    @Bean // 680 추가 -> 강제 로그인 페이지 안함.
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
        // 모든 사용자가 모든 경로에 접근할 수 있음.
        log.info("----------------CustomSecurityConfig.filterChain() 메서드 실행 ----------------------");
        log.info("--------------강제로 로그인 하지 않음--------------");
        log.info("--------------모든 사용자가 모든 경로에 접근 할 수 있음.---------");
        log.info("--------------application.properties파일에 로그 출력 레벨 추가---------");
        //logging.level.org.springframework.security.web= debug
        //logging.level.org.zerock.security = debug
        //logging.level.org.springframework.security=trace

        // 6.1 버전에서 제외 됨 (스프링 3.0이후 버전에서는 사용 안됨)
        // http.formLogin().loginPage("/member/login");
        // http.formLogin(Customizer.withDefaults());
        // 람다식으로 사용할 것을 권고 함. 아래로 변경
        http.formLogin(form -> {
            form.loginPage("/member/login"); // 694 추가 로그인 폼 추가 .loginPage("/member/login");
            // member/login.html을 찾음.
        });  // CustomUserDetailsService 클래스로 UserDeTailsService 구현체로 생성

        // 699 추가 토큰 에러 403에러 초기 해결용 (csrf는 get방식을 제외한 코드에서 토큰을 발행함)
        // http.csrf().disable() 6.1 버전에서 제외 됨 (스프링 3.0이후 버전에서는 사용 안됨) 람다식으로 사용할 것을 권고 함. 아래로 변경
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()); // csrf 토튼 비활성화

        //
//        http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/logout"));
//
        // 704 추가 람다식으로 변경
        http.rememberMe(httpSecurityRememberMeConfigurer -> {

            httpSecurityRememberMeConfigurer.key("12345678")
                    .tokenRepository(persistentTokenRepository())  // 하단에 메서드 추가
                    .userDetailsService(userDetailsService)
                    .tokenValiditySeconds(60*60*24*30);

        });

        // 718 추가 403 오류 처리
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {

            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
        });

        // 차단됨 http.oauth2Login().loginPage("/member/login");
        // 746 추가
        http.oauth2Login( httpSecurityOAuth2LoginConfigurer -> {
            httpSecurityOAuth2LoginConfigurer.loginPage("/member/login");
            httpSecurityOAuth2LoginConfigurer.successHandler(authenticationSuccessHandler()); // 761 추가 소설로그인 암호 강제 변경
        });


        return http.build();
    }
    @Bean // 683 추가
    public WebSecurityCustomizer webSecurityCustomizer() {
        // /css와 같이 정적 자원들에 대한 시큐리티 적용 제외
        log.info("------------CustomSecurityConfig.WebSecurityCustomizer() 메서드 실행 -------------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }

    @Bean // 689 임시 패스워드 암호화
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }


    @Bean // 704 추가 토큰 발생용 (쿠키 값을 인코딩하기 위한 키, 필요한 정보를 저장하는 TokenRepository를 지정
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean // 718 추가 권한이 없는 사용자 처리용
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    @Bean // 760 추가 소설 회원가입 후 암호 설정
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }


//

    ////    @Bean
////    public UserDetailsService userDetailsService(){
////        return new CustomUserDetailsService(passwordEncoder());
////    }
//

//
//
//
//


//


}
