package com.ptit.datn.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ptit.datn.domain.User;
import com.ptit.datn.dto.GooglePojo;
import com.ptit.datn.dto.TokenDto;
import com.ptit.datn.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class GoogleService {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    String GOOGLE_USER_INFO_URI;

    public TokenDto loginWithGoogle(String googleToken) throws IOException {
        GooglePojo googlePojo = getUserInfo(googleToken);
        if (googlePojo == null || !googlePojo.isVerifiedEmail()) {
            throw new IOException("Invalid Google token or email not verified");
        }
        String email = googlePojo.getEmail();
        // check exist user by email
        User user = userService.getUserByEmail(email);
        if (user == null) {
            // create new user
            user = new User();
            user.setEmail(email);
            user.setFullName(googlePojo.getFullName());
            user.setPassword("google-auth"); // Set a default password or handle it as needed
            user.setLogin(email); // Use email as login
            user.setActivated(true); // Activate the user by default
            user = userService.createUserForSSOLogin(user);
            String token = tokenProvider.createToken(user);
            TokenDto tokenDto = TokenDto.builder()
                .token(token).type("Bearer").build();
            return tokenDto;
        }
        // User exists, generate token
        String token = tokenProvider.createToken(user);
        TokenDto tokenDto = TokenDto.builder()
                .token(token).type("Bearer").build();
        return tokenDto;
    }

    public GooglePojo getUserInfo(String accessToken) throws IOException {
        URL url =new URL(GOOGLE_USER_INFO_URI);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return parseUserInfo(response.toString());
    }
    private GooglePojo parseUserInfo(String jsonResponse) {
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
        GooglePojo userInfo = new GooglePojo();
        userInfo.setEmail(jsonObject.get("email").getAsString());
        userInfo.setFullName(jsonObject.get("name").getAsString());
        userInfo.setVerifiedEmail(jsonObject.get("email_verified").getAsBoolean());
        return userInfo;
    }

}
