package com.demo.SpringDBwithUI.security;

import com.demo.SpringDBwithUI.api.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;


/**
 * Class that handles the authentication exception thrown when a user submits wrong
 * or non-existent credentials through the REST API.
 */
@Component
public class BadCredentialsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        ApiError bodyOfResponse = new ApiError(LocalDateTime.now().toString(), HttpStatus.UNAUTHORIZED, exception.toString(), "User credentials do not exist in database");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, bodyOfResponse);
        out.flush();
    }


}