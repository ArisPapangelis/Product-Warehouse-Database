package com.demo.SpringDBwithUI.api;

import com.demo.SpringDBwithUI.Company;
import com.demo.SpringDBwithUI.DataService;
import com.demo.SpringDBwithUI.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController // This means that this class is a Rest Controller
@RequestMapping(path="/api") // This means URL's start with /app (after Application path)
public class ApiController {

    private final DataService dataService;

    public ApiController(DataService dataService) {
        this.dataService = dataService;
    }

    //The following mappings are for the Products table
    @GetMapping(path="/products")
    public Iterable<Product> getAllProducts(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String company) {
        // This returns a JSON or XML with the users

        Company c = dataService.findCompanyByName(company).orElse(null);
        if (company!=null && company.isBlank()) company=null;
        if (name!=null && name.isBlank()) name=null;

        if (c == null && company != null) throw new NoSuchElementException("The company does not exist in the database");

        List<Product> products = dataService.findAllProducts(name, c);
        if (products.isEmpty()) throw new NoSuchElementException("No products found meeting query criteria");
        return products;
    }

    @GetMapping(value = "/products/{id}")
    public Product getByProductId(@PathVariable("id") Long id) {
        return dataService.findProductByID(id).get();
    }



    @PostMapping(path="/products") // Map ONLY POST Requests
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


    @PutMapping(path="/products/{id}") // Map ONLY POST Requests
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

    @DeleteMapping(path = "/products/{id}", produces = "application/json")
    public String deleteProduct(@PathVariable("id") Long id) {
        dataService.deleteProduct(dataService.findProductByID(id).get());
        return "\"status\": \"Product deleted successfully\"";
    }


    //The following mappings are for the Companies table
    @GetMapping(path="/companies")
    public Iterable<Company> getAllCompanies(@RequestParam(required = false) String name) {

        if (name==null || name.isBlank()) {
            return dataService.findAllCompanies();
        }
        Optional<Company> company = dataService.findCompanyByName(name);
        if (company.isEmpty()) throw new NoSuchElementException("There is no company with the requested name in the database");
        return List.of(company.get());
    }

    @GetMapping(value = "/companies/{id}")
    public Company getByCompanyId(@PathVariable("id") Long id) {
        return dataService.findCompanyById(id).get();
    }

    @GetMapping(value = "/companies/{id}/productcount", produces = "application/json")
    public String getCompanyProductCount(@PathVariable("id") Long id) {
        return String.format("\"Number of products\": \"%d\"", dataService.countCompanyProducts(dataService.findCompanyById(id).get()));
    }


    @PostMapping(path="/companies") // Map ONLY POST Requests
    @ResponseStatus(value = HttpStatus.CREATED)
    public Company addNewCompany(@RequestBody Company c) {

        Company company =  dataService.saveCompany(c);
        if (company==null) throw new DuplicateEntryException("Company already exists in the database, considering updating it with PUT");
        return company;
    }


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

    @DeleteMapping(path = "/companies/{id}", produces = "application/json")
    public String deleteCompany(@PathVariable("id") Long id) {
        dataService.deleteCompany(dataService.findCompanyById(id).get());
        return "\"status\": \"Company deleted successfully\"";
    }

}