package com.demo.SpringDBwithUI;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@SpringBootApplication
@RestController
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
public class SpringDBwithUiApplication implements CommandLineRunner, AppShellConfigurator {

	private final ProductRepository repository;
	private final DataService dataService;

	private static final Logger log = LoggerFactory.getLogger(SpringDBwithUiApplication.class);
	public SpringDBwithUiApplication(DataService dataService, ProductRepository repository) {
		this.repository = repository;
		this.dataService = dataService;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringDBwithUiApplication.class, args);
	}



	@Override
	public void run(String... args) throws Exception {
		Company company = new Company("Myrtali Organics", 12150502L, "6945212153", "info@myrtaliorganics.gr", "Konstantinou Palaiologou 3");
		dataService.saveCompany(company);
		dataService.saveProduct(new Product("Oregano", 30.0, "Available", 2.70, "Monovarietal", "100% Organic oregano from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Oregano", 100.0, "Not available", 7.00, "Monovarietal", "100% Organic oregano from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Thyme", 30.0, "Available", 2.70, "Monovarietal", "100% Organic thyme from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Mountain tea", 20.0, "Available", 2.80, "Monovarietal", "100% Organic mountain tea from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Eudora", 20.0, "Available", 5.50, "Blend", "100% Organic blend from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Basil", 30.0, "Available", 2.70, "Monovarietal", "100% Organic basil from the mountains of Epirus", company));

		company = new Company("Aris Pap", 12150342L, "6946126130", "aris.papagelis@gmail.com", "Meletiou Geografou 29");
		dataService.saveCompany(company);
		dataService.saveProduct(new Product("Coding", 100.0, "Available", 100.0, "Service", "Coding Services", company));



		// fetch all products
		log.info("Products found with findAll():");
		log.info("-------------------------------");
		for (Product product : repository.findAll()) {
			log.info(product.toString());
		}
		log.info("");

		// fetch an individual product by name and weight
		log.info("Product found with findByNameAndWeight('Oregano', 100.0):");
		log.info("--------------------------------");
		Optional<Product> product = repository.findByNameAndWeight("Oregano",100.0);
		if (product.isPresent()){
			log.info(product.get().toString());
		}
		else {
			log.info("Product not found");
		}


		log.info("");

		// fetch products by name
		log.info("Product found with findByName('Oregano'):");
		log.info("--------------------------------------------");
		for (Product blend : repository.findByName("Eudora")) {
			log.info(blend.toString());
		}
		log.info("");



	}


	/*

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	 */

}
