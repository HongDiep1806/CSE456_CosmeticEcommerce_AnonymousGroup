package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class provinces {
    @JsonProperty("idProvince")
    private String idProvince;

    @JsonProperty("name")
    private String name;

    // Getters and Setters
    public String getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(String idProvince) {
        this.idProvince = idProvince;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}