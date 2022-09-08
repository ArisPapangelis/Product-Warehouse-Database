package com.demo.SpringDBwithUI.security;


import com.demo.SpringDBwithUI.app.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View which models the login screen of the application.
 */
@Route("login")
@PageTitle("Login | P&I Demo")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login;

    private final Button signupBtn;

    /**
     * Constructor for LoginView.
     */
    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        this.login = new LoginForm();
        this.signupBtn = new Button("Sign up", VaadinIcon.PLUS.create());

        signupBtn.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_SUCCESS);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        login.addLoginListener(loginEvent -> UI.getCurrent().navigate(MainView.class));
        //UI.getCurrent().navigate("app")
        //UI.getCurrent().getPage().setLocation("/app");

        signupBtn.addClickListener(buttonClickEvent -> UI.getCurrent().navigate(SignupView.class));

        add(new H1("Product Warehouse Database"), login, new H3("If you are using the app for the first time, you can sign up below"), signupBtn);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
