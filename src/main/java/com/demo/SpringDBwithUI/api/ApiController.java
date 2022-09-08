package com.demo.SpringDBwithUI.api;

import com.demo.SpringDBwithUI.data.Company;
import com.demo.SpringDBwithUI.data.DataService;
import com.demo.SpringDBwithUI.data.Product;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Controller class that handles all requests to the REST API. Implements GET, POST, PUT and DELETE mappings
 * for both Products and Companies for full CRUD functionality, and handles all exceptions through the ApiExceptionHandler class.
 */
@RestController
@RequestMapping(path="/api") // URL's start with /api (after Application path)
@Secured("ROLE_ADMIN")
public class ApiController {

    private final DataService dataService;

    /**
     * Constructor for ApiController. Constructor injection is used for the dependency.
     */
    public ApiController(DataService dataService) {
        this.dataService = dataService;
    }

    //The following mappings are for the Products table
    /**
     * Method which returns all products in the database, or all products meeting optional query criteria.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param name Optional product name parameter
     * @param company Optional company name parameter
     * @return List of products meeting query criteria, in Json representation.
     */
    @GetMapping(path="/products")
    public Iterable<Product> getAllProducts(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String company) {

        Company c = dataService.findCompanyByName(company).orElse(null);
        if (company!=null && company.isBlank()) company=null;
        if (name!=null && name.isBlank()) name=null;

        if (c == null && company != null) throw new NoSuchElementException("The company does not exist in the database");

        List<Product> products = dataService.findAllProducts(name, c);
        if (products.isEmpty()) throw new NoSuchElementException("No products found meeting query criteria");
        return products;
    }


    /**
     * Method which returns the product with the requested id.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param id The id of the requested product.
     * @return The Json representation of the requested product.
     */
    @GetMapping(value = "/products/{id}")
    public Product getByProductId(@PathVariable("id") Long id) {
        return dataService.findProductByID(id).get();
    }


    /**
     * Method which persists a new product to the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param p The product being requested for persistence in the database.
     * @return The Json representation of the newly saved product.
     */
    @PostMapping(path="/products")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Product addNewProduct(@RequestBody Product p) {

        String company = p.getCompany().getCompany();
        Optional <Company> c = dataService.findCompanyByName(company);

        if (c.isPresent()) p.setCompany(c.get());
        else throw new NoSuchElementException("Company does not exist in database, try one of the available companies");

        Product product =  dataService.saveProduct(p);
        if (product==null) throw new DuplicateEntryException("Product already exists in the given quantity, cannot save product");
        return product;
    }

    /**
     * Method which updates an existing product in the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param p The updated product being requested for persistence in the database.
     * @param id The id of the product being requested for update.
     * @return The Json representation of the newly updated product.
     */
    @PutMapping(path="/products/{id}")
    public Product updateProductById(@PathVariable("id") Long id, @RequestBody Product p) {

        Product product;
        String company = p.getCompany().getCompany();
        Optional <Company> c = dataService.findCompanyByName(company);
        if (c.isPresent()) {
            product = dataService.findProductByID(id).map(prod -> {
                prod.setName(p.getName());
                prod.setWeight(p.getWeight());
                prod.setAvailability(p.getAvailability());
                prod.setPrice(p.getPrice());
                prod.setCategory(p.getCategory());
                prod.setDescription(p.getDescription());
                prod.setCompany((c.get()));
                return prod;
            }).get();
        }
        else {
            throw new NoSuchElementException("Company does not exist in database, try one of the available companies");
        }

        product =  dataService.updateProduct(product);
        if (product==null) throw new DuplicateEntryException("Product already exists in the given quantity, cannot update product");
        return product;
    }


    /**
     * Method which deletes an existing product from the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param id The id of the product being requested for deletion.
     * @return Status indicating successful deletion of the requested product.
     */
    @DeleteMapping(path = "/products/{id}", produces = "application/json")
    public String deleteProduct(@PathVariable("id") Long id) {
        dataService.deleteProduct(dataService.findProductByID(id).get());
        return "\"status\": \"Product deleted successfully\"";
    }


    //The following mappings are for the Companies table
    /**
     * Method which returns all companies in the database, or all companies meeting optional query criteria.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param name Optional company name parameter
     * @return List of companies meeting query criteria, in Json representation.
     */
    @GetMapping(path="/companies")
    public Iterable<Company> getAllCompanies(@RequestParam(required = false) String name) {

        if (name==null || name.isBlank()) {
            return dataService.findAllCompanies();
        }
        Optional<Company> company = dataService.findCompanyByName(name);
        if (company.isEmpty()) throw new NoSuchElementException("There is no company with the requested name in the database");
        return List.of(company.get());
    }


    /**
     * Method which returns the company with the requested id.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param id The id of the requested company.
     * @return The Json representation of the requested company.
     */
    @GetMapping(value = "/companies/{id}")
    public Company getByCompanyId(@PathVariable("id") Long id) {
        return dataService.findCompanyById(id).get();
    }

    /**
     * Method which returns the number of products a requested company has in the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param id The id of the requested company.
     * @return The number of products the requested company has in the database.
     */
    @GetMapping(value = "/companies/{id}/productcount", produces = "application/json")
    public String getCompanyProductCount(@PathVariable("id") Long id) {
        return String.format("\"Number of products\": \"%d\"", dataService.countCompanyProducts(dataService.findCompanyById(id).get()));
    }


    /**
     * Method which persists a new company to the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param c The company being requested for persistence in the database.
     * @return The Json representation of the newly saved company.
     */
    @PostMapping(path="/companies") // Map ONLY POST Requests
    @ResponseStatus(value = HttpStatus.CREATED)
    public Company addNewCompany(@RequestBody Company c) {

        Company company =  dataService.saveCompany(c);
        if (company==null) throw new DuplicateEntryException("Company already exists in the database, considering updating it with PUT");
        return company;
    }


    /**
     * Method which updates an existing company in the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param c The updated company being requested for persistence in the database.
     * @param id The id of the company being requested for update.
     * @return The Json representation of the newly updated company.
     */
    @PutMapping(path="/companies/{id}") // Map ONLY POST Requests
    public Company updateCompanyById(@PathVariable("id") Long id, @RequestBody Company c) {

        Company company = dataService.findCompanyById(id).map(comp -> {
            comp.setCompany(c.getCompany());
            comp.setAddress(c.getAddress());
            comp.setEmail(c.getEmail());
            comp.setPhoneNumber(c.getPhoneNumber());
            comp.setTaxNumber(c.getTaxNumber());
            return comp;
        }).get();

        company =  dataService.updateCompany(company);
        if (company==null) throw new DuplicateEntryException("The requested company name already exists in the database, cannot update company");
        return company;
    }


    /**
     * Method which deletes an existing company from the database.
     * Returns an error (in Json) if an exception is thrown.
     *
     * @param id The id of the company being requested for deletion.
     * @return Status indicating successful deletion of the requested company.
     */
    @DeleteMapping(path = "/companies/{id}", produces = "application/json")
    public String deleteCompany(@PathVariable("id") Long id) {
        dataService.deleteCompany(dataService.findCompanyById(id).get());
        return "\"status\": \"Company deleted successfully\"";
    }
}