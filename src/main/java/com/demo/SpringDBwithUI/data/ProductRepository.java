package com.demo.SpringDBwithUI.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@RepositoryRestResource(collectionResourceRel = "products", path="products")

/**
 * Includes repository search methods for the Products table.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findByName(String name);
    List<Product> findByNameStartsWithIgnoreCase(String name);
    Optional<Product> findByNameIgnoreCaseAndWeightAndCompany(String name, double weight, Company company);

    List<Product> findByNameStartsWithIgnoreCaseAndCompany(String name, Company company);

    List<Product> findByCompanyId(long id);

    int countProductsByCompany(Company company);

}
