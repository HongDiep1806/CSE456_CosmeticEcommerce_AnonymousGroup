package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table (name = "admins")
public class admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10, nullable = false)
    private int AdminId;

    @Column(name = "email", length = 50, nullable = false)
    private String AdminEmail;

    @Column(name = "name", length = 50, nullable = false)
    private String AdminName;

    @Column(name = "password", length = 50, nullable = false)
    private String AdminPassword;

    @Column(name = "address", length = 255)
    private String AdminAddress;

    @Column(name = "city", length = 50)
    private String AdminCity;

    @Column(name = "province", length = 50)
    private String AdminProvince;

    @Column(name = "phone", length = 50, nullable = false)
    private String AdminPhone;

    @Column(name = "status", length = 50)
    private String AdminStatus;

    public int getAdminId() {
        return AdminId;
    }

    public void setAdminId(int adminId) {
        AdminId = adminId;
    }

    public String getAdminEmail() {
        return AdminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        AdminEmail = adminEmail;
    }

    public String getAdminName() {
        return AdminName;
    }

    public void setAdminName(String adminName) {
        AdminName = adminName;
    }

    public String getAdminPassword() {
        return AdminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        AdminPassword = adminPassword;
    }

    public String getAdminAddress() {
        return AdminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        AdminAddress = adminAddress;
    }

    public String getAdminCity() {
        return AdminCity;
    }

    public void setAdminCity(String adminCity) {
        AdminCity = adminCity;
    }

    public String getAdminProvince() {
        return AdminProvince;
    }

    public void setAdminProvince(String adminProvince) {
        AdminProvince = adminProvince;
    }

    public String getAdminPhone() {
        return AdminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        AdminPhone = adminPhone;
    }

    public String getAdminStatus() {
        return AdminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        AdminStatus = adminStatus;
    }

}
