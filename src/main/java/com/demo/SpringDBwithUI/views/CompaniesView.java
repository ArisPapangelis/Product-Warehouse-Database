package com.demo.SpringDBwithUI.views;

import com.demo.SpringDBwithUI.Company;
import com.demo.SpringDBwithUI.DataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("app/companies")
@PageTitle("Companies | P&I Demo")
public class CompaniesView extends VerticalLayout {

    private final DataService dataService;

    private final H1 header;

    private final Button toggleDarkModeBtn;

    private final Grid<Company> companyGrid;

    private final RouterLink productsLink;

    public CompaniesView(DataService dataService) {

        setSizeFull();
        this.dataService = dataService;

        this.toggleDarkModeBtn = new Button("Toggle dark mode", MainView::toggleDarkMode);
        this.header = new H1("List of Companies");
        this.companyGrid = new Grid<>(Company.class);
        this.productsLink = new RouterLink("<- Go back to list of products", MainView.class);

        VerticalLayout headerLayout = new VerticalLayout(toggleDarkModeBtn,header,productsLink);
        headerLayout.setAlignSelf(Alignment.END, toggleDarkModeBtn);
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
    }
}
