package com.gymhub.gymhub.config;

import java.io.IOException;
import java.util.List;

import com.gymhub.gymhub.service.CustomUserDetailsService;
import com.gymhub.gymhub.service.MemberService;
import com.gymhub.gymhub.service.ModService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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


    private final MemberService memberService;
    private final ModService modService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(MemberService memberService, ModService modService) {
        this.memberService = memberService;
        this.modService = modService;
        //accept both memberService and modService as bean
    }

    private List<AntPathRequestMatcher> protectedUrls = List.of(
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
        String jwt = getJwtFromRequest(request);
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUserNameFromJWT(jwt);

            UserDetails userDetails = null;

            // Determine which service to use based on some condition
            if (isModeratorRequest(request)) {
                userDetails = customUserDetailsService.loadUserByUsername(username);
            } else {
                userDetails = customUserDetailsService.loadUserByUsername(username);
            }

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
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

    private boolean isModeratorRequest(HttpServletRequest request) {
        // You can implement your logic here to determine whether this request is related to a moderator
        // For example, check if the request URL contains "/mod" or some other condition
        return request.getRequestURI().startsWith("/mod");
    }
}
