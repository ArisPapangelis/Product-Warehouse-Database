package com.demo.SpringDBwithUI.data;

import org.springframework.stereotype.Service;

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
            return productRepository.findByNameStartsWithIgnoreCaseAndCompany(filterText, company);
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

    public Product saveProduct(Product product) {

        Optional<Product> p = productRepository.findByNameIgnoreCaseAndWeightAndCompany(product.getName(), product.getWeight(), product.getCompany());
        if (p.isPresent() && p.get().getCompany().getCompany().equalsIgnoreCase(product.getCompany().getCompany())) {
            System.err.println("Product already exists, cannot save product");
            return null;
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {

        Optional<Product> p = productRepository.findByNameIgnoreCaseAndWeightAndCompany(product.getName(), product.getWeight(), product.getCompany());
        if (p.isPresent() && p.get().getId()!= product.getId() && p.get().getCompany().getCompany().equalsIgnoreCase(product.getCompany().getCompany())) {
            System.err.println("Product already exists in the given quantity, cannot save product");
            return null;
        }
        return productRepository.save(product);
    }

    public List<Company> findAllCompanies() {
        return (List<Company>) companyRepository.findAll();
    }

    public Optional<Company> findCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Optional <Company> findCompanyByName(String name) {
        return companyRepository.findByCompanyIgnoreCase(name);
    }



    public boolean deleteCompany(Company company) {
        if (companyRepository.findById(company.getId()).isEmpty()) {
            System.err.println("Company does not exist");
            return false;
        }
        companyRepository.delete(company);
        return true;
    }

    public Company saveCompany(Company company) {
        Optional<Company> c = companyRepository.findByCompanyIgnoreCase(company.getCompany());
        if (c.isPresent()) {
            System.err.println("Company already exists, cannot save company");
            return null;
        }
        return companyRepository.save(company);
    }

    public Company updateCompany(Company company) {
        Optional<Company> c = companyRepository.findByCompanyIgnoreCase(company.getCompany());
        if (c.isPresent() && c.get().getId()!= company.getId()) {
            System.err.println("Company already exists, cannot update company");
            return null;
        }
        return companyRepository.save(company);
    }

    public int countCompanyProducts(Company company) {
        return productRepository.countProductsByCompany(company);
    }
}