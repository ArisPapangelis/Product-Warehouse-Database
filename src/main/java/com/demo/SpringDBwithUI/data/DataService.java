package com.demo.SpringDBwithUI.data;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service used for accessing the persistence layer. Includes the business logic of the application.
 */
@Service
public class DataService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    /**
     * Constructor for DataService. Constructor injection is used for the two dependencies.
     */
    public DataService(ProductRepository productRepository, CompanyRepository companyRepository) {
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * Method which returns a list of all products meeting filter criteria.
     *
     * @param filterText The currently selected product name filter
     * @param company The currently selected company name filter
     * @return The list of products meeting filter criteria
     */
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

    /**
     * Method which returns a product based on its ID (optional - might not exist)
     *
     * @param id The id of the requested product
     * @return The product with the requested id (might not exist)
     */
    public Optional<Product> findProductByID(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Method which deletes a requested product, if that product actually exists.
     *
     * @param product The product being requested for deletion from the database.
     * @return The boolean result of the delete action - true or false.
     */
    public boolean deleteProduct(Product product) {
        if (productRepository.findById(product.getId()).isEmpty()) {
            System.err.println("Product does not exist");
            return false;
        }
        productRepository.delete(product);
        return true;
    }

    /**
     * Method which saves a new product to the database, as long as the product doesn't already exist.
     * A product already exists if it has the same name, weight, and company.
     *
     * @param product The product being requested for persistence in the database.
     * @return The newly created product.
     */
    public Product saveProduct(Product product) {

        Optional<Product> p = productRepository.findByNameIgnoreCaseAndWeightAndCompany(product.getName(), product.getWeight(), product.getCompany());
        if (p.isPresent() && p.get().getCompany().getCompany().equalsIgnoreCase(product.getCompany().getCompany())) {
            System.err.println("Product already exists, cannot save product");
            return null;
        }
        return productRepository.save(product);
    }

    /**
     * Method which updates an existing product in the database, as long as the updated product doesn't already exist.
     * A product already exists if it has the same name, weight, and company.
     *
     * @param product The product being requested for update in the database.
     * @return The newly updated product.
     */
    public Product updateProduct(Product product) {

        Optional<Product> p = productRepository.findByNameIgnoreCaseAndWeightAndCompany(product.getName(), product.getWeight(), product.getCompany());
        if (p.isPresent() && p.get().getId()!= product.getId() && p.get().getCompany().getCompany().equalsIgnoreCase(product.getCompany().getCompany())) {
            System.err.println("Product already exists in the given quantity, cannot save product");
            return null;
        }
        return productRepository.save(product);
    }

    /**
     * Method which returns a list of all companies in the database.
     *
     * @return List of all companies
     */
    public List<Company> findAllCompanies() {
        return (List<Company>) companyRepository.findAll();
    }

    /**
     * Method which returns a company based on its ID (optional - might not exist)
     *
     * @param id The id of the requested company
     * @return The company with the requested id (might not exist)
     */
    public Optional<Company> findCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    /**
     * Method which returns a company based on its name (optional - might not exist)
     *
     * @param name The name of the requested company
     * @return The company with the requested name (might not exist)
     */
    public Optional <Company> findCompanyByName(String name) {
        return companyRepository.findByCompanyIgnoreCase(name);
    }


    /**
     * Method which deletes a requested company, if that company actually exists.
     *
     * @param company The company being requested for deletion from the database.
     * @return The boolean result of the delete action - true or false.
     */
    public boolean deleteCompany(Company company) {
        if (companyRepository.findById(company.getId()).isEmpty()) {
            System.err.println("Company does not exist");
            return false;
        }
        companyRepository.delete(company);
        return true;
    }

    /**
     * Method which saves a new company to the database, as long as the company doesn't already exist.
     * A company already exists if it has the same name as another company.
     *
     * @param company The company being requested for persistence in the database.
     * @return The newly created company.
     */
    public Company saveCompany(Company company) {
        Optional<Company> c = companyRepository.findByCompanyIgnoreCase(company.getCompany());
        if (c.isPresent()) {
            System.err.println("Company already exists, cannot save company");
            return null;
        }
        return companyRepository.save(company);
    }

    /**
     * Method which updates an existing company in the database, as long as the updated company doesn't already exist.
     * A company already exists if it has the same name as another company.
     *
     * @param company The company being requested for update in the database.
     * @return The newly updated company.
     */
    public Company updateCompany(Company company) {
        Optional<Company> c = companyRepository.findByCompanyIgnoreCase(company.getCompany());
        if (c.isPresent() && c.get().getId()!= company.getId()) {
            System.err.println("Company already exists, cannot update company");
            return null;
        }
        return companyRepository.save(company);
    }

    /**
     * Method which returns the number of products a requested company has.
     *
     * @param company The company whose product count should be returned.
     * @return The number of products the requested company has.
     */
    public int countCompanyProducts(Company company) {
        return productRepository.countProductsByCompany(company);
    }
}
