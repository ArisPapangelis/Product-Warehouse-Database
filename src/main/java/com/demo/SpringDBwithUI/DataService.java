package com.demo.SpringDBwithUI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class DataService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    public DataService(ProductRepository productRepository, CompanyRepository companyRepository) {
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
    }

    public List<Product> findAllProducts(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return (List<Product>) productRepository.findAll();
        } else {
            return productRepository.findByNameStartsWithIgnoreCase(filterText);
        }
    }

    public Optional<Product> findProductByID(Long id) {
        return productRepository.findById(id);
    }

    public void deleteProduct(Product product) {
        if (productRepository.findById(product.getId()).isEmpty()) {
            System.err.println("Product does not exist");
            return;
        }
        productRepository.delete(product);
    }

    public void saveProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null, cannot save product");
            return;
        }
        productRepository.save(product);
    }

    public List<Company> findAllCompanies() {
        return (List<Company>) companyRepository.findAll();
    }

    public void deleteCompany(Company company) {
        if (companyRepository.findById(company.getId()).isEmpty()) {
            System.err.println("Company does not exist");
            return;
        }
        companyRepository.delete(company);
    }

    public void saveCompany(Company company) {
        if (company == null) {
            System.err.println("Company is null, cannot save company");
            return;
        }
        companyRepository.save(company);
    }

}
