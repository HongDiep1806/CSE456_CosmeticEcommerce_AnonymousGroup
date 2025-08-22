package com.example.demo.otherfunction;

import com.example.demo.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonLoader {

    private List<Provinces> provinceList;

    public JsonLoader() throws IOException {
        this.provinceList = loadProvinces();
    }

    public List<Provinces> loadProvinces() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/static/provinces/db.json");
        JsonNode root = objectMapper.readTree(inputStream);
        List<Provinces> provinces = new ArrayList<>();
        for (JsonNode node : root.path("province")) {
            Provinces province = objectMapper.treeToValue(node, Provinces.class);
            provinces.add(province);
        }
        return provinces;
    }

    public List<Districts> loadDistricts(String provinceId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/static/provinces/db.json");
        JsonNode root = objectMapper.readTree(inputStream);
        List<Districts> districts = new ArrayList<>();
        for (JsonNode node : root.path("district")) {
            Districts district = objectMapper.treeToValue(node, Districts.class);
            if (district.getIdProvince().equals(provinceId)) {
                districts.add(district);
            }
        }
        return districts;
    }

    public String getProvinceNameById(String provinceId) {
        for (Provinces province : provinceList) {
            if (province.getIdProvince().equals(provinceId)) {
                return province.getName();
            }
        }
        return null; // Return null if not found
    }
}