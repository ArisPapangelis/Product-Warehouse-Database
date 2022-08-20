package com.demo.SpringDBwithUI;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Collection;

@Route("app")
@PageTitle("Home | P&I Demo")
public class MainView extends VerticalLayout {

    private final ProductEditor editor;
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

    public MainView(DataService dataService) {
        addClassName("main-view");
        setSizeFull();

        this.dataService = dataService;

        this.editor = new ProductEditor(dataService);
        this.grid = new Grid<>(Product.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create());
        this.toggleDarkModeBtn = new Button("Toggle dark mode");
        this.companySelector = new ComboBox<>();
        this.header = new H1("Products Database");
        //this.companyInfo = new H3();
        this.companyInfo = new Grid<>(Company.class);
        this.addNewCompanyBtn = new Button("New company");


        // build layout
        VerticalLayout headerLayout = new VerticalLayout(toggleDarkModeBtn,header);
        headerLayout.setAlignSelf(Alignment.END, toggleDarkModeBtn);
        headerLayout.setAlignSelf(Alignment.CENTER, header);
        headerLayout.setSpacing(false);



        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);

        HorizontalLayout gridAndEditor = new HorizontalLayout(grid, editor);
        gridAndEditor.setFlexGrow(3,grid);
        gridAndEditor.setFlexGrow(1,editor);
        gridAndEditor.setSizeFull();
        editor.setWidth(20,Unit.PERCENTAGE);

        add(headerLayout,companySelector, companyInfo, actions,gridAndEditor);

        configureUIElements();

    }

    private void toggleDarkMode() {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }
    }

    private void listProductsInGrid(String filterText, Company company) {
        grid.setItems(dataService.findAllProducts(filterText,company));
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
        //companyInfo.setReadOnly(true);
        //companyInfo.setWidthFull();
        /*
        companyInfo.setWhiteSpace(HasText.WhiteSpace.PRE);
        companySelector.addValueChangeListener(e -> {
            if (companySelector.isEmpty()) companyInfo.setText("");
            else companyInfo.setText(e.getValue().toHtml());
        });

         */
        companyInfo.setColumns("company", "taxNumber", "phoneNumber", "email", "address");
        companyInfo.setAllRowsVisible(true);
        companyInfo.setVisible(false);
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

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listProductsInGrid(e.getValue(), companySelector.getValue()));

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editProduct(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editProduct(new Product()));
        toggleDarkModeBtn.addClickListener(e -> toggleDarkMode());


        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listProductsInGrid(filter.getValue(), companySelector.getValue());
        });

        // Initialize listing
        listProductsInGrid(null, null);
    }
}