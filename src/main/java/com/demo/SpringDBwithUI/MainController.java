package com.demo.SpringDBwithUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController // This means that this class is a Rest Controller
@RequestMapping(path="/api") // This means URL's start with /app (after Application path)
public class MainController {

    private final DataService dataService;

    public MainController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping(path="/products/add") // Map ONLY POST Requests
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
        p.setCompany(dataService.findCompanyByName(company));
        dataService.saveProduct(p);
        return "Saved";
        //Sample Post: curl localhost:8080/app/add -d name=Chamomile -d weight=20.0 -d availability=Unavailable -d price=2.7 -d category=Monovarietal -d description=100%25%20organic%20chamomile%20from%20the%20mountains%20of%20Epirus
        //http://localhost:8080/api/products/add?name=Dorida&weight=30&availability=Unavailable&price=2.7&category=Herbs&description=100%25%20organic%20herbal%20blend%20from%20the%20mountains%20of%20Epirus&company=Myrtali%20Organics
    }

    @GetMapping(path="/products")
    public Iterable<Product> getAllProducts() {
        // This returns a JSON or XML with the users
        return dataService.findAllProducts(null, null);
    }

    @GetMapping(path="/companies")
    public Iterable<Company> getAllCompanies() {
        // This returns a JSON or XML with the users
        return dataService.findAllCompanies();
    }
}