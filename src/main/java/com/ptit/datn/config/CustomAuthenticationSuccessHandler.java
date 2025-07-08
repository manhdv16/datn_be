package com.ptit.datn.config;

import com.ptit.datn.service.GoogleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Inject any required services here, e.g., OAuth2AuthorizedClientService, GoogleService, etc.
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private  GoogleService googleService;
    private String apiPortalUrl = "http://localhost:3000";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken auth2Token = (OAuth2AuthenticationToken) authentication;

            // Get the access token from the OAuth2AuthorizedClientService
            OAuth2AuthorizedClient oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(
                    auth2Token.getAuthorizedClientRegistrationId(),
                    auth2Token.getName()
            );
            if (oAuth2AuthorizedClient != null) {
                String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();

                // Call your service to handle the login with Google
                var internalToken = googleService.loginWithGoogle(accessToken);

                // Redirect to the frontend with the token
                response.sendRedirect( apiPortalUrl + "/login?token=" + internalToken.getToken());
            } else {
                response.sendRedirect(apiPortalUrl + "/login");
            }
        }
    }
}
