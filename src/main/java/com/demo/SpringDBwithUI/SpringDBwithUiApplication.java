package com.demo.SpringDBwithUI;

import com.demo.SpringDBwithUI.data.Company;
import com.demo.SpringDBwithUI.data.DataService;
import com.demo.SpringDBwithUI.data.Product;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
@Push
public class SpringDBwithUiApplication implements CommandLineRunner, AppShellConfigurator {


	private final DataService dataService;

	private static final Logger log = LoggerFactory.getLogger(SpringDBwithUiApplication.class);
	public SpringDBwithUiApplication(DataService dataService) {
		this.dataService = dataService;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringDBwithUiApplication.class, args);
	}



	@Override
	public void run(String... args) throws Exception {

		Company company = new Company("Myrtali Organics", 121504673L, "6945212153", "info@myrtaliorganics.gr", "Konstantinou Palaiologou 3");
		dataService.saveCompany(company);
		dataService.saveProduct(new Product("Oregano", 30.0, "Available", 2.70, "Herbs", "100% Organic oregano from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Oregano", 100.0, "Not available", 7.00, "Herbs", "100% Organic oregano from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Thyme", 30.0, "Available", 2.70, "Herbs", "100% Organic thyme from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Mountain tea", 20.0, "Available", 2.80, "Herbs", "100% Organic mountain tea from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Eudora", 20.0, "Available", 5.50, "Herbs", "100% Organic herbal blend from the mountains of Epirus", company));
		dataService.saveProduct(new Product("Basil", 30.0, "Available", 2.70, "Herbs", "100% Organic basil from the mountains of Epirus", company));

		company = new Company("BioProtein", 121503425L, "6946126131", "info@bioprotein.gr", "Meletiou Geografou 30");
		dataService.saveCompany(company);
		dataService.saveProduct(new Product("Chocolate Protein Bar", 225.0, "Not available", 3.60, "Fitness", "Organic protein bar with 20g of protein per bar", company));
		dataService.saveProduct(new Product("Vanilla Protein Powder", 1000.0, "Available", 30.00, "Fitness", "Organic protein powder with 25g of protein per scoop", company));

		company = new Company("BeeHappy", 121502365L, "6936356821", "info@beehappy.gr", "Zagoriou 19");
		dataService.saveCompany(company);
		dataService.saveProduct(new Product("Honey", 500.0, "Available", 18.0, "Spreads", "Organic honey from pine trees", company));

		company = new Company("OliveSpring", 121503653L, "6948352325", "info@olivespring.gr", "Leoforos Dodonis 46");
		dataService.saveCompany(company);


	}


}
