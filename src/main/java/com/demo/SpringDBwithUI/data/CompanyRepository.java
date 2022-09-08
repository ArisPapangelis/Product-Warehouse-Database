package com.demo.SpringDBwithUI.data;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Includes repository search methods for the Companies table.
 */
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Optional<Company> findByCompanyIgnoreCase(String company);

}
