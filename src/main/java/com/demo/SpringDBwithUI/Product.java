package com.demo.SpringDBwithUI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Field can't be empty")
    private String name;
    @Min(value = 0, message = "Must be a positive number")
    @NotNull(message = "Field can't be empty")
    private Double weight;
    @NotBlank(message = "Field can't be empty")
    private String availability;
    @Min(value = 0, message = "Must be a positive number")
    @NotNull(message = "Field can't be empty")
    private Double price;
    @NotBlank(message = "Field can't be empty")
    private String category;
    @NotBlank(message = "Field can't be empty")
    private String description;

    /*
    @ManyToOne
    @JoinColumn(name = "companyID")
    @NotNull
    @JsonIgnoreProperties({"products"})
    private Company company;

     */

    protected  Product() {

    }
    public Product(String name, Double weight, String availability, Double price, String category, String description) {
        this.name = name;
        this.weight = weight;
        this.availability = availability;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", availability='" + availability + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
