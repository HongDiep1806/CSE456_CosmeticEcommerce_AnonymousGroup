package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class districts {
    @JsonProperty("idProvince")
    private String idProvince;

    @JsonProperty("idDistrict")
    private String idDistrict;

    @JsonProperty("name")
    private String name;

    // Getters and Setters
    public String getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(String idProvince) {
        this.idProvince = idProvince;
    }

    public String getIdDistrict() {
        return idDistrict;
    }

    public void setIdDistrict(String idDistrict) {
        this.idDistrict = idDistrict;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}