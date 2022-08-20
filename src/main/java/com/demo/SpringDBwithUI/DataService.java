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

    public List<Product> findAllProducts(String filterText, Company company) {
        if ((filterText == null || filterText.isEmpty()) && company==null) {
            return (List<Product>) productRepository.findAll();
        }
        else if (company != null && filterText  != null) {
            return productRepository.findByNameStartsWithIgnoreCaseAndCompanyId(filterText, company.getId());
        }
        else if (filterText == null) {
            return productRepository.findByCompanyId(company.getId());
        }
        else {
            return productRepository.findByNameStartsWithIgnoreCase(filterText);
        }
    }

    public Optional<Product> findProductByID(Long id) {
        return productRepository.findById(id);
    }

    public boolean deleteProduct(Product product) {
        if (productRepository.findById(product.getId()).isEmpty()) {
            System.err.println("Product does not exist");
            return false;
        }
        productRepository.delete(product);
        return true;
    }

    public boolean saveProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null, cannot save product");
            return false;
        }
        if (productRepository.findByNameAndWeight(product.getName(), product.getWeight()).isPresent()) {
            System.err.println("Product already exists, cannot save product");
            return false;
        }
        productRepository.save(product);
        return true;
    }

    public boolean updateProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null, cannot save product");
            return false;
        }
        Optional<Product> p = productRepository.findByNameAndWeight(product.getName(), product.getWeight());
        if (p.isPresent() && p.get().getId()!= product.getId()) {
            System.err.println("Product already exists in the given quantity, cannot save product");
            return false;
        }
        productRepository.save(product);
        return true;
    }

    public List<Company> findAllCompanies() {
        return (List<Company>) companyRepository.findAll();
    }

    public boolean deleteCompany(Company company) {
        if (companyRepository.findByCompany(company.getCompany()).isEmpty()) {
            System.err.println("Company does not exist");
            return false;
        }
        companyRepository.delete(company);
        return true;
    }

    public boolean saveCompany(Company company) {
        if (company == null) {
            System.err.println("Company is null, cannot save company");
            return false;
        }
        else if (companyRepository.findByCompany(company.getCompany()).isPresent()) {
            System.err.println("Company already exists");
            return false;
        }
        companyRepository.save(company);
        return true;
    }

}
