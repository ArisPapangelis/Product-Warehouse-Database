package com.demo.SpringDBwithUI.views;

import com.demo.SpringDBwithUI.Company;
import com.demo.SpringDBwithUI.DataService;
import com.demo.SpringDBwithUI.Product;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;


import java.util.ArrayList;
import java.util.List;

@Route("app")
@PageTitle("Home | P&I Demo")
public class MainView extends VerticalLayout {

    private final ProductEditor productEditor;
    private final DataService dataService;

    private final Grid<Product> grid;
    private final TextField filter;

    private final Button addNewBtn;
    private final Button addNewCompanyBtn;
    private final Button toggleDarkModeBtn;
    private final ComboBox<Company> companySelector;
    private final H1 header;
    //private H3 companyInfo;
    private final Grid<Company> companyInfo;
    private final RouterLink companiesLink;

    public MainView(DataService dataService) {
        addClassName("main-view");
        setSizeFull();

        this.dataService = dataService;

        this.productEditor = new ProductEditor(dataService);
        this.grid = new Grid<>(Product.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create());
        this.toggleDarkModeBtn = new Button("Toggle dark mode", MainView::toggleDarkMode);
        this.companySelector = new ComboBox<>(this::selectCompany);
        this.header = new H1("Products Database");
        this.companyInfo = new Grid<>(Company.class);
        this.addNewCompanyBtn = new Button("New company");
        this.companiesLink = new RouterLink("Full list of companies", CompaniesView.class);


        // Build layout
        VerticalLayout headerLayout = new VerticalLayout(toggleDarkModeBtn,header);
        headerLayout.setAlignSelf(Alignment.END, toggleDarkModeBtn);
        headerLayout.setAlignSelf(Alignment.CENTER, header);
        headerLayout.setSpacing(false);

        HorizontalLayout companies = new HorizontalLayout(companySelector, companiesLink);
        companies.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);

        HorizontalLayout gridAndEditor = new HorizontalLayout(grid, productEditor);
        gridAndEditor.setFlexGrow(3,grid);
        gridAndEditor.setFlexGrow(1,productEditor);
        gridAndEditor.setSizeFull();
        productEditor.setWidth(20,Unit.PERCENTAGE);

        add(headerLayout,companies, companyInfo, actions,gridAndEditor);

        configureUIElements();

    }

    public static void toggleDarkMode(ClickEvent<Button> event) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }
    }

    private void listProductsInGrid(String filterText, Company company) {
        List<Product> gridItems = dataService.findAllProducts(filterText,company);
        grid.setItems(gridItems);
        //numberOfProducts = gridItems.size();
    }

    private void selectCompany(AbstractField.ComponentValueChangeEvent<ComboBox<Company>,Company> event) {
        if (companySelector.isEmpty()) {
            companyInfo.setItems(new ArrayList<>());
            companyInfo.setVisible(false);
        }
        else {
            companyInfo.setItems(event.getValue());
            companyInfo.setVisible(true);
        }
        listProductsInGrid(filter.getValue(), event.getValue());
    }

    private void configureUIElements() {
        //Configure grid
        //grid.setHeight("500px");
        grid.setSizeFull();
        grid.setColumns("id", "name", "weight", "availability", "price", "category", "description");
        grid.addColumn(product -> product.getCompany().getCompany()).setHeader("Company");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        grid.getColumnByKey("description").setFlexGrow(2);
        grid.getColumnByKey("price").setHeader("Price (EUR)");
        grid.getColumnByKey("weight").setHeader("Weight (gr)");

        filter.setPlaceholder("Filter by product name");

        companySelector.setPlaceholder("Filter by company");
        companySelector.setItems(dataService.findAllCompanies());
        companySelector.setItemLabelGenerator(Company::getCompany);
        companySelector.setClearButtonVisible(true);

        companiesLink.setHighlightCondition(HighlightConditions.sameLocation());

        companyInfo.setColumns("company", "taxNumber", "phoneNumber", "email", "address");
        companyInfo.setAllRowsVisible(true);
        companyInfo.setVisible(false);

        /*
        Lambda function too long, so added ComponentValueChangeEvent handler to constructor of ComboBox
        companySelector.addValueChangeListener(e -> {
            if (companySelector.isEmpty()) {
                companyInfo.setItems(new ArrayList<>());
                companyInfo.setVisible(false);
            }
            else {
                companyInfo.setItems(e.getValue());
                companyInfo.setVisible(true);
            }
            listProductsInGrid(filter.getValue(), e.getValue());
        });
         */

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listProductsInGrid(e.getValue(), companySelector.getValue()));

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> productEditor.editProduct(e.getValue()));
        //grid.addItemClickListener(e -> productEditor.editProduct(e.getItem()));

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            productEditor.editProduct(new Product());
        });

        // Listen changes made by the editor, refresh data from backend
        //productEditor.
        productEditor.setChangeHandler(this::onEditorChange);




        // Initialize listing
        listProductsInGrid(null, null);
    }

    private void onEditorChange() {
        productEditor.setVisible(false);
        grid.asSingleSelect().clear();
        listProductsInGrid(filter.getValue(), companySelector.getValue());
    }


}