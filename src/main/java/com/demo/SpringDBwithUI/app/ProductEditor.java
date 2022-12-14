package com.demo.SpringDBwithUI.app;

import com.demo.SpringDBwithUI.data.Company;
import com.demo.SpringDBwithUI.data.DataService;
import com.demo.SpringDBwithUI.data.Product;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Product editor component, used when a user would like to create a new product, update or delete
 * an existing product.
 */
@SpringComponent
@UIScope
public class ProductEditor extends FormLayout implements KeyNotifier {

    private final DataService dataService;


    //The currently edited product
    private Product product;

    // Fields to edit properties in Product entity
    private final TextField name;
    private final NumberField weight;
    private final TextField availability;
    private final NumberField price;
    private final TextField category;
    private final TextField description;
    private final ComboBox<Company> company;

    // Action buttons
    Button save;
    Button delete;
    Button cancel;

    Binder<Product> binder;
    private ChangeHandler changeHandler;

    /**
     * Functional interface used to execute the code passed from MainView whenever there is a product editor event.
     */
    @FunctionalInterface
    public interface ChangeHandler {
        void onChange();
    }

    /**
     * Boolean variable which shows whether the product editor was called to add a new product
     * or modify an existing product.
     */
    private boolean persisted;

    /**
     * Constructor for ProductEditor. Constructor injection is used for the dependency.
     */
    public ProductEditor(DataService dataService) {
        this.dataService = dataService;
        this.name = new TextField("Name");
        this.weight = new NumberField("Weight (gr)");
        this.availability = new TextField("Availability");
        this.price = new NumberField("Price (EUR)");
        this.category = new TextField("Category");
        this.description =  new TextField("Description");
        this.company = new ComboBox<>("Company");
        this.save = new Button("Save", VaadinIcon.CHECK.create());
        this.delete = new Button("Delete", VaadinIcon.TRASH.create());
        this.cancel = new Button("Cancel");
        this.binder = new BeanValidationBinder<>(Product.class);
        reloadCompanyComboBox();


        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        add(name, weight, availability, price, category, description, company, actions);

        //Bind using naming convention
        binder.bindInstanceFields(this);

        //Configure and style components
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        //Wire action buttons to save, delete and cancel
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> cancel());
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        setVisible(false);

        persisted = false;
    }


    /**
     * Method called when the user presses the delete button, after having selected an existing product.
     * Tries to delete the selected product from the database, and shows a success or failure notification
     * based on the result.
     * Finally, executes the code from MainView through the functional interface.
     */
    private void delete() {
        Notification notification;
        if (dataService.deleteProduct(product)){
            notification = Notification.show("Successfully deleted product", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        else {
            notification = Notification.show("Product does not exist, could not delete product", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        changeHandler.onChange();
    }

    /**
     * Method called when the user presses the save button, after having selected an existing product or pressing new product.
     * Tries to save a new product or update the selected product from the database, depending on the "persisted" variable,
     * and shows a success or failure notification based on the result.
     * Finally, executes the code from MainView through the functional interface.
     */
    private void save() {
        Notification notification;
        Product p;
        if (persisted) {
            p = dataService.updateProduct(product);
            if (p!=null) {
                notification = Notification.show("Successfully updated product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            else {
                notification = Notification.show("Product already exists in the given quantity, cannot update product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
        else {
            p = dataService.saveProduct(product);
            if (p!=null) {
                notification = Notification.show("Successfully saved new product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            else {
                notification = Notification.show("Product already exists, cannot save product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
        changeHandler.onChange();
    }

    /**
     * Method called when the user presses the cancel button, after having selected an existing product or pressing new product.
     * Cancels the action of opening the product editor and returns to MainView.
     * Finally, executes the code from MainView through the functional interface.
     */
    private void cancel() {
        editProduct(null);
        changeHandler.onChange();
    }


    /**
     * Method which is called when the user selects a product from the grid or presses the new product button.
     * Sets up the product editor component depending on the button which was pressed to call it,
     * and binds the editor fields to the product entity being edited.
     *
     * @param p The selected product for editing (either existing or new)
     */
    public void editProduct(Product p) {
        if (p == null) {
            setVisible(false);
            return;
        }
        persisted = p.getId() != null;
        if (persisted) {
            //Find fresh entity for editing
            product = dataService.findProductByID(p.getId()).get();
        }
        else {
            product = p;
        }
        delete.setVisible(persisted);

        //Bind customer properties to similarly named fields
        //Could also use annotation or "manual binding" or programmatically moving values from fields to entities before saving
        binder.setBean(product);

        setVisible(true);

        //Focus first name initially
        name.focus();
    }

    /**
     * Setter for the code to be executed from MainView through the ChangeHandler functional interface.
     *
     * @param h Code to be executed through the ChangeHandler onChange() method.
     */
    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

    /**
     * Sets the labels of the company combo box depending on the available companies in the database.
     * Custom companies are not allowed, a company has to already exist in the database to own a product.
     */
    public void reloadCompanyComboBox() {
        company.setItems(dataService.findAllCompanies());
        company.setItemLabelGenerator(Company::getCompany);
    }

}