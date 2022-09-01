package com.demo.SpringDBwithUI.app;

import com.demo.SpringDBwithUI.data.Company;
import com.demo.SpringDBwithUI.data.DataService;
import com.demo.SpringDBwithUI.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.security.PermitAll;

@PermitAll
@Route("/companies")
@PageTitle("Companies | P&I Demo")
public class CompaniesView extends VerticalLayout {

    private final DataService dataService;
    private final SecurityService securityService;

    private final H1 header;

    private final Button toggleDarkModeBtn;
    private final Button logoutBtn;

    private final Grid<Company> companyGrid;

    private final RouterLink productsLink;

    public CompaniesView(DataService dataService, SecurityService securityService) {

        setSizeFull();
        this.dataService = dataService;
        this.securityService = securityService;

        this.toggleDarkModeBtn = new Button("Toggle dark mode", MainView::toggleDarkMode);
        this.logoutBtn = new Button("Log out", VaadinIcon.SIGN_OUT.create(), event -> securityService.logout());
        this.header = new H1("List of Companies");
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
}
