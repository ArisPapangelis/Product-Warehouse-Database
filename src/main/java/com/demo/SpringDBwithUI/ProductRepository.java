package com.demo.SpringDBwithUI;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    List<Product> findByName(String name);
    Product findById(int id);
    Product findByNameAndWeight(String name, double weight);

}
