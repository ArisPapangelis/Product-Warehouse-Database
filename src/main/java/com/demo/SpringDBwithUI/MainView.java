package com.demo.SpringDBwithUI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

@Route("app")
public class MainView extends VerticalLayout {
    private final ProductRepository repository;
    private final ProductEditor editor;

    final Grid<Product> grid;
    final TextField filter;

    private final Button addNewBtn;

    public MainView(ProductRepository repository, ProductEditor editor) {
        this.repository = repository;
        this.editor = editor;
        this.grid = new Grid<>(Product.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create());

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        add(actions, grid, editor);

        grid.setHeight("500px");
        grid.setColumns("id", "name", "weight", "availability", "price", "category", "description");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        filter.setPlaceholder("Filter by product name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listProducts(e.getValue()));

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editProduct(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editProduct(new Product()));


        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listProducts(filter.getValue());
        });


        // Initialize listing
        listProducts(null);
    }

    private void listProducts(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems((Collection<Product>) repository.findAll());
        }
        else {
            grid.setItems(repository.findByNameStartsWithIgnoreCase(filterText));
        }
    }
}