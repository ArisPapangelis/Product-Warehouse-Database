package com.demo.SpringDBwithUI.security;

import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Service which handles security related functions.
 */
@Component
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/login";

    /**
     * Method which returns the current authenticated user as a UserDetails object.
     *
     * @return The UserDetails of the current authenticated user.
     */
    public UserDetails getAuthenticatedUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        // Anonymous or no authentication.
        return null;
    }

    /**
     * Method which is called when the user presses the "Log out" button. Returns the user to the login screen after logout.
     */
    public void logout() {
        //UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        //System.err.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}