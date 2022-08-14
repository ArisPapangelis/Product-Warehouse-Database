package com.demo.SpringDBwithUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
public class SpringDBwithUiApplication implements CommandLineRunner {


	public static void main(String[] args) {
		SpringApplication.run(SpringDBwithUiApplication.class, args);
	}

	@Autowired
	private ProductRepository repository;

	private static final Logger log = LoggerFactory.getLogger(SpringDBwithUiApplication.class);

	@Override
	public void run(String... args) throws Exception {
		repository.save(new Product("Oregano", 30.0, "Available", 2.70, "Monovarietal", "100% Organic oregano from the mountains of Epirus"));
		repository.save(new Product("Oregano", 100.0, "Not available", 7.00, "Monovarietal", "100% Organic oregano from the mountains of Epirus"));
		repository.save(new Product("Thyme", 30.0, "Available", 2.70, "Monovarietal", "100% Organic thyme from the mountains of Epirus"));
		repository.save(new Product("Mountain tea", 20.0, "Available", 2.80, "Monovarietal", "100% Organic mountain tea from the mountains of Epirus"));
		repository.save(new Product("Eudora", 20.0, "Available", 5.50, "Blend", "100% Organic blend from the mountains of Epirus"));
		repository.save(new Product("Basil", 30.0, "Available", 2.70, "Monovarietal", "100% Organic basil from the mountains of Epirus"));



		// fetch all products
		log.info("Products found with findAll():");
		log.info("-------------------------------");
		for (Product product : repository.findAll()) {
			log.info(product.toString());
		}
		log.info("");

		// fetch an individual product by ID
		Product product = repository.findById(3);
		log.info("Product found with findById(3):");
		log.info("--------------------------------");
		log.info(product.toString());
		log.info("");

		// fetch an individual product by name and weight
		product = repository.findByNameAndWeight("Oregano",100.0);
		log.info("Product found with findByNameAndWeight('Oregano', 100.0):");
		log.info("--------------------------------");
		log.info(product.toString());
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
