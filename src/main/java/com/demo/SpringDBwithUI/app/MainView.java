package com.demo.SpringDBwithUI.app;

import com.demo.SpringDBwithUI.data.Company;
import com.demo.SpringDBwithUI.data.DataService;
import com.demo.SpringDBwithUI.data.Product;
import com.demo.SpringDBwithUI.security.SecurityService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.Lumo;


import javax.annotation.security.PermitAll;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@PermitAll
@Route("")
@PageTitle("Products | P&I Demo")
public class MainView extends VerticalLayout implements BeforeLeaveObserver {

    private final SecurityService securityService;

    private final ProductEditor productEditor;
    private final DataService dataService;

    private final Grid<Product> grid;
    private final TextField filter;

    private final Button addNewBtn;
    private final Button addNewCompanyBtn;
    private final Button toggleDarkModeBtn;
    private final Button logoutBtn;
    private final ComboBox<Company> companySelector;
    private final H1 header;
    //private H3 companyInfo;
    private final Grid<Company> companyInfo;
    private final RouterLink companiesLink;

    private TimerTask updateUI;
    private ScheduledExecutorService executor;
    private UI ui;
    public MainView(DataService dataService, SecurityService securityService) {
        addClassName("main-view");
        setSizeFull();

        this.dataService = dataService;
        this.securityService = securityService;

        this.productEditor = new ProductEditor(dataService);
        this.grid = new Grid<>(Product.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create());
        this.toggleDarkModeBtn = new Button("Toggle dark mode", MainView::toggleDarkMode);
        this.companySelector = new ComboBox<>(this::selectCompany);
        this.header = new H1("Products Database");
        this.companyInfo = new Grid<>(Company.class);
        this.addNewCompanyBtn = new Button("New company");
        this.logoutBtn = new Button("Log out", VaadinIcon.SIGN_OUT.create(), event -> logout());
        this.companiesLink = new RouterLink("Full list of companies", CompaniesView.class);



        // Build layout
        VerticalLayout headerLayout = new VerticalLayout(toggleDarkModeBtn,logoutBtn,header);
        headerLayout.setAlignSelf(Alignment.START, toggleDarkModeBtn);
        headerLayout.setAlignSelf(Alignment.END, logoutBtn);
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        ui = attachEvent.getUI();
        this.updateUI = new TimerTask() {
            @Override
            public void run() {
                ui.access(() -> {
                    Company company = companySelector.getValue();
                    companySelector.setItems(dataService.findAllCompanies());
                    companySelector.setItemLabelGenerator(Company::getCompany);
                    companySelector.setValue(company);
                    productEditor.reloadCompanyComboBox();
                    listProductsInGrid(filter.getValue(), companySelector.getValue());
                    ui.push();
                });
            }
        };
        startTimerTask();
    }

    private void logout() {
        stopTimerTask();
        securityService.logout();
    }

    public static void toggleDarkMode(ClickEvent<Button> event) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }
    }

    public void listProductsInGrid(String filterText, Company company) {
        List<Product> gridItems = dataService.findAllProducts(filterText,company);
        grid.setItems(gridItems);
        //numberOfProducts = gridItems.size();
    }

    private void selectCompany(AbstractField.ComponentValueChangeEvent<ComboBox<Company>,Company> event) {
        stopTimerTask();
        if (companySelector.isEmpty()) {
            companyInfo.setItems(new ArrayList<>());
            companyInfo.setVisible(false);
        }
        else {
            companyInfo.setItems(event.getValue());
            companyInfo.setVisible(true);
        }
        listProductsInGrid(filter.getValue(), event.getValue());
        startTimerTask();
    }

    private void configureUIElements() {
        //Configure grid
        //grid.setHeight("500px");
        grid.setSizeFull();
        grid.setColumns("name", "weight", "availability", "price", "category", "description");
        grid.addColumn(product -> product.getCompany().getCompany()).setHeader("Company");
        //grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
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

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listProductsInGrid(e.getValue(), companySelector.getValue()));

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
                stopTimerTask();
                productEditor.editProduct(e.getValue());
        });
        //grid.addItemClickListener(e -> productEditor.editProduct(e.getItem()));

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> {
            stopTimerTask();
            grid.asSingleSelect().clear();
            productEditor.editProduct(new Product());
        });

        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

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
        startTimerTask();
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        stopTimerTask();
    }

    private void stopTimerTask() {
        executor.shutdown();
    }

    private void startTimerTask() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(updateUI, 1, 4, TimeUnit.SECONDS);
    }
}