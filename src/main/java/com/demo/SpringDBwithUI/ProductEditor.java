package com.demo.SpringDBwithUI;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@SpringComponent
@UIScope
public class ProductEditor extends FormLayout implements KeyNotifier {

    private final DataService dataService;

    /**
     * The currently edited product
     */
    private Product product;
    /* Fields to edit properties in Product entity */
    TextField name = new TextField("Name");
    NumberField weight = new NumberField("Weight (gr)");
    TextField availability = new TextField("Availability");
    NumberField price = new NumberField("Price (EUR)");
    TextField category = new TextField("Category");
    TextField description = new TextField("Description");
    ComboBox<Company> company = new ComboBox<>("Company");



    /* Action buttons */
    // TODO why more code?
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    Button cancel = new Button("Cancel");

    HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);

    Binder<Product> binder = new Binder<>(Product.class);
    //or BeanValidationBinder
    private ChangeHandler changeHandler;

    private boolean persisted;

    @Autowired
    public ProductEditor(DataService dataService) {
        this.dataService = dataService;
        reloadCompanyComboBox();
        add(name, weight, availability, price, category, description, company, actions);

        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        //setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editProduct(null));
        setVisible(false);

        persisted = false;
    }

    void delete() {
        Notification notification;
        if (dataService.deleteProduct(product)){
            notification = Notification.show("Successfully deleted product", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        else {
            notification = Notification.show("There was an error, could not delete product", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        changeHandler.onChange();
    }

    void save() {
        Notification notification;
        if (persisted) {
            if (dataService.updateProduct(product)) {
                notification = Notification.show("Successfully updated product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            else {
                notification = Notification.show("There was an error, could not update product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
        else {
            if (dataService.saveProduct(product)) {
                notification = Notification.show("Successfully saved new product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            else {
                notification = Notification.show("There was an error, could not save new product", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editProduct(Product p) {
        if (p == null) {
            setVisible(false);
            return;
        }
        persisted = p.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            product = dataService.findProductByID(p.getId()).get();
        }
        else {
            product = p;
        }
        delete.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(product);

        setVisible(true);

        // Focus first name initially
        name.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

    public void reloadCompanyComboBox() {
        company.setItems(dataService.findAllCompanies());
        company.setItemLabelGenerator(Company::getCompany);
    }

}