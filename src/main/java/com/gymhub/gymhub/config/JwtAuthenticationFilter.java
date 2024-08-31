package com.gymhub.gymhub.config;

import java.io.IOException;
import java.util.List;

import com.gymhub.gymhub.components.CookieManager;
import com.gymhub.gymhub.service.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    CookieManager cookieManager;

    private List<AntPathRequestMatcher> protectedUrls = List.of(
            new AntPathRequestMatcher("/auth/profile"),
            new AntPathRequestMatcher("/post/new/**"),
            new AntPathRequestMatcher("/post/update/**"),
            new AntPathRequestMatcher("/post/like/**"),
            new AntPathRequestMatcher("/post/report/**"),
            new AntPathRequestMatcher("/thread/new/**"),
            new AntPathRequestMatcher("/thread/like/**"),
            new AntPathRequestMatcher("/thread/report/**")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //String jwt = getJwtFromRequest(request);
        Cookie[] cookies = request.getCookies();
        cookieManager.setCookies(cookies);
        String jwt = cookieManager.getCookieValue("AuthenticationToken");
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUserNameFromJWT(jwt);
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("User authenticated: " + username);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
