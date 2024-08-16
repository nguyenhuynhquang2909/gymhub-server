package com.gymhub.gymhub.components;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
@Setter
public class CookieManager {
    private Cookie[] cookies;

    public String getCookieValue(String name){
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
