package com.demo.SpringDBwithUI.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * View which models the signup screen of the application.
 */
@AnonymousAllowed
@Route("signup")
@PageTitle("Signup | P&I Demo")
public class SignupView extends VerticalLayout {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager users;

    private final Button signupBtn;

    private final TextField signUpUsername;
    private final PasswordField signUpPassword;

    private final Button loginBtn;


    /**
     * Constructor for SignupView. Constructor injection is used for the two dependencies.
     */
    public SignupView(PasswordEncoder passwordEncoder, UserDetailsManager users) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        this.signupBtn = new Button("Sign up", VaadinIcon.PLUS.create());
        this.signUpUsername = new TextField("Username", "Username");
        this.signUpPassword = new PasswordField("Password", "Password");
        this.loginBtn = new Button("Log in", VaadinIcon.SIGN_IN.create());

        signupBtn.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY);
        signUpPassword.setClearButtonVisible(true);
        loginBtn.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_SUCCESS);

        loginBtn.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(LoginView.class));
        //UI.getCurrent().navigate("app")
        //UI.getCurrent().getPage().setLocation("/app");
        signupBtn.addClickListener(buttonClickEvent -> signUp());

        add(new H1("Product Warehouse Database"), new H2("Sign up"), signUpUsername, signUpPassword, signupBtn, new H3("If you have already signed up, you can log in below"), loginBtn);
        this.passwordEncoder = passwordEncoder;
        this.users = users;
    }


    /**
     * Method called when the "Sign up" button is pressed. The current username and password(after hashing) in the corresponding fields are
     * used to create a new user. Edge cases for already existing users or empty fields are also checked.
     */
    private void signUp() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        String username = signUpUsername.getValue();
        String password = signUpPassword.getValue();

        try {
            if (username.isBlank() || password.isBlank()) throw new IllegalAccessException();
            UserDetails user = new User(username, passwordEncoder.encode(password), authorities);
            users.createUser(user);
            Notification notification = Notification.show("Successfully created user: " + username, 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (DuplicateKeyException exception) {
            Notification notification = Notification.show("User already exists: " + username, 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (IllegalAccessException exception) {
            Notification notification = Notification.show("Cannot create user with empty username or password", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            signUpUsername.clear();
            signUpPassword.clear();
        }
    }

}
