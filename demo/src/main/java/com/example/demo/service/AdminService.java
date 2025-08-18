package com.example.demo.service;

import jakarta.servlet.http.HttpSession;

// import org.apache.tomcat.util.net.openssl.ciphers.Encryption;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.model.*;
import com.example.demo.repository.adminrepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.demo.otherfunction.JsonLoader;
import com.example.demo.otherfunction.encryption;

@Service
public class AdminService {

    private final adminrepository adminrepo;
    private final encryption encryption;
    private final JsonLoader jsonLoader;

    public AdminService(adminrepository adminrepo, encryption encryption, JsonLoader jsonLoader) {
        this.adminrepo = adminrepo;
        this.encryption = encryption;
        this.jsonLoader = jsonLoader;
    }

    //get name of logged in admin to show in the header
    public String getLoggedInAdminName(HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId != null) {
            return adminrepo.findById(adminId).get().getAdminName();
        } else if (superId != null) {
            return adminrepo.findById(superId).get().getAdminName();
        } else {
            return null;
        }
    }

    //if super admin does not exist, create one with default information
    public void createSuperAdminIfNotExists() {
        if (!adminrepo.existsByEmail("yenhopie28@gmail.com")) {
            admin admin = new admin();
            admin.setAdminName("Liora Master");
            admin.setAdminEmail("yenhopie28@gmail.com");
            admin.setAdminPassword(encryption.encrypt("yenhopie28"));
            admin.setAdminPhone("0123456789");
            admin.setAdminProvince("Tỉnh Bình Dương");
            admin.setAdminCity("Thành phố Thủ Dầu Một");
            admin.setAdminAddress("Nam Kỳ Khởi Nghĩa, Định Hòa");
            admin.setAdminStatus("Active");
            adminrepo.save(admin);
        }
    }

    //handle login
    public ResponseEntity<Map<String, Object>> handleLogin(String email, String password, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        admin ad = adminrepo.findByAdminEmail(email);

        if (ad == null || !encryption.matches(password, ad.getAdminPassword())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if ("block".equalsIgnoreCase(ad.getAdminStatus())) {
            response.put("success", false);
            response.put("message", "Your account is blocked. Please contact support.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if ("yenhopie28@gmail.com".equals(ad.getAdminEmail())) {
            session.setAttribute("loginSuper", ad.getAdminId());
        } else {
            session.setAttribute("loginAdmin", ad.getAdminId());
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    // Check if the admin is logged in
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginAdmin") != null || session.getAttribute("loginSuper") != null;
    }

    //get all provinces from JSON file
    public List<provinces> loadProvinces() {
        try {
            return jsonLoader.loadProvinces();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //get all admins from the database
    public List<admin> getAllAdmins() {
        return (List<admin>) adminrepo.findAll();
    }

    //add a new admin
    public void addAdmin(String province, adminsdto dto) {
        int adminId = adminrepo.findNextAdminId();
        String provinceName = jsonLoader.getProvinceNameById(province);

        admin newAdmin = new admin();
        newAdmin.setAdminId(adminId);
        newAdmin.setAdminName(dto.getAdminName());
        newAdmin.setAdminEmail(dto.getAdminEmail());
        newAdmin.setAdminPassword(encryption.encrypt(dto.getAdminPassword()));
        newAdmin.setAdminPhone(dto.getAdminPhone());
        newAdmin.setAdminAddress(dto.getAdminAddress());
        newAdmin.setAdminCity(dto.getAdminCity());
        newAdmin.setAdminProvince(provinceName);
        newAdmin.setAdminStatus(dto.getAdminStatus());

        adminrepo.save(newAdmin);
    }

    //load districts based on selected province
    // This method uses the JsonLoader to fetch districts from a JSON file based on the selected
    public List<districts> loadDistricts(String provinceId) {
        try {
            return jsonLoader.loadDistricts(provinceId);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //delete an admin
    public void deleteAdmin(int id, RedirectAttributes redirectAttributes) {
        try {
            adminrepo.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Admin deleted successfully!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin not found or already deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the Admin.");
        }
    }

    //edit an admin
    public void updateAdminStatus(adminsdto dto, BindingResult result) {
        Integer adminId = dto.getAdminId();
        admin existingAdmin = adminrepo.findById(adminId).orElse(null);

        if (existingAdmin == null) {
            result.addError(new FieldError("adminsdto", "AdminId", "Admin not found!"));
            return;
        }

        existingAdmin.setAdminStatus(dto.getAdminStatus());
        adminrepo.save(existingAdmin);
    }
}
