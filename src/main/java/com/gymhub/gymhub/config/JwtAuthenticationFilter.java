package com.gymhub.gymhub.config;

import java.io.IOException;
import java.util.List;

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
    private UserDetailsService userDetailsService;

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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Apply filtering only if the request matches any of the protected URL patterns
        return protectedUrls.stream().noneMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter invoked for URL: " + request.getRequestURI());
        String jwt = getJwtFromRequest(request);
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            System.out.println(jwt);
            String username = tokenProvider.getUserNameFromJWT(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenciation = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenciation.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenciation);

        }
        else {
            System.out.println("Invalid or missing JWT for URL: " + request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);

    } 

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization ");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
