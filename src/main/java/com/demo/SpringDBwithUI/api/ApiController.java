package com.demo.SpringDBwithUI.api;

import com.demo.SpringDBwithUI.Company;
import com.demo.SpringDBwithUI.DataService;
import com.demo.SpringDBwithUI.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController // This means that this class is a Rest Controller
@RequestMapping(path="/api") // This means URL's start with /app (after Application path)
public class ApiController {

    private final DataService dataService;

    public ApiController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(path="/products")
    public Iterable<Product> getAllProducts() {
        // This returns a JSON or XML with the users
        return dataService.findAllProducts(null, null);
    }

    @GetMapping(value = "/products/{id}")
    public Product findById(@PathVariable("id") Long id) {
        return dataService.findProductByID(id).get();
    }

    @PutMapping(value = "/products/{id}")
    public Product updateById(@PathVariable("id") Long id, @RequestParam String name
            , @RequestParam Double weight, @RequestParam String availability
            , @RequestParam Double price, @RequestParam String category
            , @RequestParam String description, @RequestParam String company) {

        Product product = dataService.findProductByID(id).get();
        product.setName(name);
        product.setWeight(weight);
        product.setAvailability(availability);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription(description);

        Optional <Company> c = dataService.findCompanyByName(company);
        if (c.isPresent()) {
            product.setCompany(c.get());
        }
        else {
            throw new NoSuchElementException("Company does not exist in database, try one of the available companies");
        }

        dataService.updateProduct(product);
        return product;
    }


    @PostMapping(path="/products", produces = "application/json") // Map ONLY POST Requests
    @ResponseStatus(value = HttpStatus.CREATED)
    public String addNewProduct (@RequestParam String name
            , @RequestParam Double weight, @RequestParam String availability
            , @RequestParam Double price, @RequestParam String category
            , @RequestParam String description, @RequestParam String company) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        Product p = new Product();
        p.setName(name);
        p.setWeight(weight);
        p.setAvailability(availability);
        p.setPrice(price);
        p.setCategory(category);
        p.setDescription(description);
        Optional <Company> c = dataService.findCompanyByName(company);
        if (c.isPresent()) {
            p.setCompany(c.get());
        }
        else {
            throw new NoSuchElementException("Company does not exist in database, try one of the available companies");
        }

        dataService.saveProduct(p);
        return "{\"status\": \"Successfully saved new product\"}";
        //Sample Post: curl localhost:8080/app/add -d name=Chamomile -d weight=20.0 -d availability=Unavailable -d price=2.7 -d category=Monovarietal -d description=100%25%20organic%20chamomile%20from%20the%20mountains%20of%20Epirus
        //http://localhost:8080/api/products/add?name=Dorida&weight=30&availability=Unavailable&price=2.7&category=Herbs&description=100%25%20organic%20herbal%20blend%20from%20the%20mountains%20of%20Epirus&company=Myrtali%20Organics
    }




    /*

    @PostMapping(path="/products/add") // Map ONLY POST Requests
    public String addNewProduct(@RequestBody Product p) {
        dataService.saveProduct(p);
        return "{\"status\": \"Successfully saved new product\"}";
    }

    */

    @GetMapping(path="/companies")
    public Iterable<Company> getAllCompanies() {
        // This returns a JSON or XML with the users
        return dataService.findAllCompanies();
    }
}