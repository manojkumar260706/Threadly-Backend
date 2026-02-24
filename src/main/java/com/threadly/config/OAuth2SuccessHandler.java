package com.threadly.config;

import com.threadly.entity.User;
import com.threadly.repository.UserRepository;
import com.threadly.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken tokenAuth = (OAuth2AuthenticationToken) authentication;
        String provider = tokenAuth.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String username, email;

        if ("google".equals(provider)) {
            email = oAuth2User.getAttribute("email");
            username = email.split("@")[0];
        } else if ("github".equals(provider)) {
            email = oAuth2User.getAttribute("email"); // may be null sometimes 👀
            if (email == null) {
                email = oAuth2User.getAttribute("login") + "@github.com";
            }
            username = oAuth2User.getAttribute("login");
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }

        String finalEmail = email;
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(finalEmail);
            newUser.setUsername(finalEmail.split("@")[0]);
            newUser.setPassword(null);
            newUser.setProvider(provider.toUpperCase());
            return (User) userRepository.save(newUser);
        });

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        String token = jwtService.createToken(claims, user.getUsername());
        response.sendRedirect(frontendUrl + "/oauth/callback?token=" + token);
    }
}
