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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;


import javax.annotation.security.PermitAll;
import java.lang.annotation.Inherited;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * View which lists the info of all products in the database, and also includes the
 * product editor component to edit product information.
 */
@PermitAll
//@AnonymousAllowed
@Route("")
//@PageTitle("Products | P&I Demo") //Bug: Page secured with Spring security doesn't show page title when set through Vaadin PageTitle annotation, requires dynamic setting of title
public class MainView extends VerticalLayout implements BeforeLeaveObserver, HasDynamicTitle {

    private final SecurityService securityService;

    private final ProductEditor productEditor;
    private final DataService dataService;

    private final Grid<Product> grid;
    private final TextField filter;
    private final TextField numberOfProducts;

    private final Button addNewBtn;
    private final Button addNewCompanyBtn;
    private final Button toggleDarkModeBtn;
    private final Button logoutBtn;
    private final ComboBox<Company> companySelector;
    private final H1 header;
    //private H3 companyInfo;
    private final Grid<Company> companyInfo;
    private final RouterLink companiesLink;

    /**
     * Background task to periodically update the UI every few seconds, so as to keep in sync
     * with the REST API.
     */
    private TimerTask updateUI;
    private ScheduledExecutorService executor;
    private UI ui;


    /**
     * Constructor for MainView. Constructor injection is used for the two dependencies.
     */
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
        this.header = new H1("Product Warehouse Database");
        this.companyInfo = new Grid<>(Company.class);
        this.addNewCompanyBtn = new Button("New company");
        this.logoutBtn = new Button("Log out", VaadinIcon.SIGN_OUT.create(), event -> logout());
        this.companiesLink = new RouterLink("Full list of companies", CompaniesView.class);
        this.numberOfProducts = new TextField();

        // Build layout
        VerticalLayout headerLayout = new VerticalLayout(toggleDarkModeBtn,logoutBtn,header);
        headerLayout.setAlignSelf(Alignment.START, toggleDarkModeBtn);
        headerLayout.setAlignSelf(Alignment.END, logoutBtn);
        headerLayout.setAlignSelf(Alignment.CENTER, header);
        headerLayout.setSpacing(false);

        HorizontalLayout companies = new HorizontalLayout(companySelector, companiesLink);
        companies.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        //companies.setWidthFull();

        HorizontalLayout products = new HorizontalLayout(filter, numberOfProducts, addNewBtn);
        products.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        products.setWidth(100, Unit.PERCENTAGE);
        //products.setFlexGrow(2, numberOfProducts);

        HorizontalLayout gridAndEditor = new HorizontalLayout(grid, productEditor);
        gridAndEditor.setFlexGrow(3,grid);
        gridAndEditor.setFlexGrow(1,productEditor);
        gridAndEditor.setSizeFull();
        productEditor.setWidth(20,Unit.PERCENTAGE);

        add(headerLayout, companies, companyInfo, products, gridAndEditor);

        configureUIElements();

    }

    /**
     * Method which dynamically returns the title of the page.
     *
     * @return The title of the page at /app.
     */
    @Override
    public String getPageTitle() {
        return "Products | P&I Demo";
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        ui = attachEvent.getUI();
        //The implementation of updateUI is given here.
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

    /**
     * Method which is called when the user presses the log out button. Revokes access and returns the user to the log in screen.
     */
    private void logout() {
        stopTimerTask();
        securityService.logout();
    }

    /**
     * Static method which changes the application theme from light mode to dark mode and vice versa.
     *
     * @param event Button click event on the toggle dark mode button.
     */
    public static void toggleDarkMode(ClickEvent<Button> event) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }
    }

    /**
     * Updates the product list with a new database call depending on filter criteria.
     * Also updates the number of shown products.
     *
     * @param filterText The currently selected name filter
     * @param company The currently selected company filter
     */
    public void listProductsInGrid(String filterText, Company company) {
        List<Product> gridItems = dataService.findAllProducts(filterText,company);
        grid.setItems(gridItems);
        numberOfProducts.setValue("Number of products: " + gridItems.size());
        //numberOfProducts.setValue("Number of products: 999");
    }

    /**
     * Method called when the user filters by company. Shows the info of the company and selects only the products
     * that belong to the chosen company.
     *
     * @param event Company select event from the company filter combo box.
     */
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
        companySelector.blur();
    }

    /**
     * Method which initially configures all UI elements of the view.
     */
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
        numberOfProducts.setReadOnly(true);
        numberOfProducts.setWidth(12, Unit.PERCENTAGE);


        companySelector.setPlaceholder("Filter by company");
        companySelector.setItems(dataService.findAllCompanies());
        companySelector.setItemLabelGenerator(Company::getCompany);
        companySelector.setClearButtonVisible(true);
        companySelector.addFocusListener(comboBoxFocusEvent -> stopTimerTask());
        companySelector.addBlurListener(comboBoxBlurEvent -> startTimerTask());

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

    /**
     * Method which is called from product editor, which is a child component, whenever save, cancel or delete is pressed,
     * in order to update the UI based on database changes.
     */
    private void onEditorChange() {
        productEditor.setVisible(false);
        grid.asSingleSelect().clear();
        listProductsInGrid(filter.getValue(), companySelector.getValue());
        startTimerTask();
    }


    /**
     * Method which stops updating the UI whenever the current view is left.
     *
     * @param beforeLeaveEvent Event when leaving the current view
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        stopTimerTask();
    }

    /**
     * Method which stops the execution of the periodic task of updating the UI.
     */
    private void stopTimerTask() {
        executor.shutdown();
    }

    /**
     * Method which starts the execution of the periodic task of updating the UI every few seconds.
     */
    private void startTimerTask() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(updateUI, 1, 4, TimeUnit.SECONDS);
    }
}