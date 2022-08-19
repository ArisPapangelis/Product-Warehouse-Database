package com.demo.SpringDBwithUI;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

    //Product findById(int id);
    List<Product> findByName(String name);
    List<Product> findByNameStartsWithIgnoreCase(String name);
    List<Product> findByNameAndWeight(String name, double weight);


}
