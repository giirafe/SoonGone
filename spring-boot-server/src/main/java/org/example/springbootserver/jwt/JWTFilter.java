package org.example.springbootserver.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springbootserver.auth.dto.CustomOAuth2User;
import org.example.springbootserver.auth.dto.Token2OAuthDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String authorization = null;
        Cookie[] cookies = request.getCookies();

        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        if (path.equals("/") || path.equals("/oauth2Verify")) {
            filterChain.doFilter(request, response);
            return; // Skip the rest of the filter
        }

        if(cookies ==  null) {
            System.out.println("User has no Cookies Set !~!!!!!!!!!!!!!!!!");
            filterChain.doFilter(request, response);
            return; // Return like authorization == null below
        }

        for (Cookie cookie : cookies) {

//            System.out.println(cookie.getName());
            if (cookie.getName().equals("Authorization")) {

                authorization = cookie.getValue();
            }
            System.out.println("Authorization: " + authorization);
            System.out.println(jwtUtil.getUsername(authorization));
        }

        //Authorization 헤더 검증
        if (authorization == null) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰
        String token = authorization;

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰에서 username과 role 획득
        String userSocialIdentifier = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userDTO를 생성하여 값 set
        Token2OAuthDTO token2OAuthDTO = new Token2OAuthDTO();
        token2OAuthDTO.setSocialUserIdentifier(userSocialIdentifier);
        token2OAuthDTO.setName(userSocialIdentifier);
        token2OAuthDTO.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(token2OAuthDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
