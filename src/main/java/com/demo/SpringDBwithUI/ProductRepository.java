package com.demo.SpringDBwithUI;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    //Product findById(int id);
    List<Product> findByName(String name);
    List<Product> findByNameAndWeight(String name, double weight);

}
