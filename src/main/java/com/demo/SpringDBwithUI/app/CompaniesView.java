package com.demo.SpringDBwithUI.app;

import com.demo.SpringDBwithUI.data.Company;
import com.demo.SpringDBwithUI.data.DataService;
import com.demo.SpringDBwithUI.security.SecurityService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import javax.annotation.security.PermitAll;
import java.util.TimerTask;

/**
 * View which lists the info of all companies in the database, as well as the number of
 * products each company has
 */
@PermitAll
//@AnonymousAllowed
@Route("/companies")
//@PageTitle("Companies | P&I Demo") //Bug: Page secured with Spring security doesn't show page title when set through Vaadin PageTitle annotation, requires dynamic setting of title
public class CompaniesView extends VerticalLayout implements HasDynamicTitle {

    private final DataService dataService;
    private final SecurityService securityService;

    private final H1 header;

    private final Button toggleDarkModeBtn;
    private final Button logoutBtn;

    private final Grid<Company> companyGrid;

    private final RouterLink productsLink;

    /**
     * Constructor for CompaniesView. Constructor injection is used for the two dependencies.
     */
    public CompaniesView(DataService dataService, SecurityService securityService) {

        setSizeFull();
        this.dataService = dataService;
        this.securityService = securityService;

        this.toggleDarkModeBtn = new Button("Toggle dark mode", MainView::toggleDarkMode);
        this.logoutBtn = new Button("Log out", VaadinIcon.SIGN_OUT.create(), event -> securityService.logout());
        this.header = new H1("List of Suppliers");
        this.companyGrid = new Grid<>(Company.class);
        this.productsLink = new RouterLink("<- Go back to list of products", MainView.class);

        VerticalLayout headerLayout = new VerticalLayout(toggleDarkModeBtn,logoutBtn,header,productsLink);
        headerLayout.setAlignSelf(Alignment.START, toggleDarkModeBtn);
        headerLayout.setAlignSelf(Alignment.END, logoutBtn);
        headerLayout.setAlignSelf(Alignment.CENTER, header);
        headerLayout.setAlignSelf(Alignment.START,productsLink);
        headerLayout.setSpacing(false);

        add(headerLayout, companyGrid);

        companyGrid.setSizeFull();
        companyGrid.setColumns("company", "taxNumber", "phoneNumber", "email", "address");
        companyGrid.addColumn(dataService::countCompanyProducts).setHeader("Number of products");
        //companyGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        companyGrid.setItems(dataService.findAllCompanies());
        productsLink.setHighlightCondition(HighlightConditions.sameLocation());

        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
    }

    /**
     * Method which dynamically returns the title of the page.
     *
     * @return The title of the page at /app/companies.
     */
    @Override
    public String getPageTitle() {
        return "Companies | P&I Demo";
    }
}
