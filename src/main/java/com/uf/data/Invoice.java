package com.uf.data;

import java.util.List;

public class Invoice {
    public String customer;
    public List<Performance> performances;


    public Invoice() {
    }

    public Invoice(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }

}
