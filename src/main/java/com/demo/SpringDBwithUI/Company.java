package com.demo.SpringDBwithUI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "Companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Field can't be empty")
    private String company;
    @Min(value = 0, message = "Must be a positive number")
    @NotNull(message = "Field can't be empty")
    private Long taxNumber;
    @NotBlank(message = "Field can't be empty")
    private String phoneNumber;
    @Email(message = "Must be a valid email")
    @NotBlank(message = "Field can't be empty")
    private String email;
    @NotBlank(message = "Field can't be empty")
    private String address;

    @OneToMany(mappedBy = "company")
    @JsonIgnoreProperties({"company"})
    private List<Product> products;



    public Company() {
    }

    public Company(String company, Long taxNumber, String phoneNumber, String email, String address) {
        this.company = company;
        this.taxNumber = taxNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Company{" +
                "company='" + company + '\'' +
                ", taxNumber=" + taxNumber +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Long getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(Long taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}