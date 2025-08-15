package com.example.demo.controller.admin;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.*;
import com.example.demo.otherfunction.JsonLoader;
import com.example.demo.otherfunction.encryption;
import com.example.demo.repository.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class admincontroller {

    @Autowired
    adminrepository adminrepo;

    @Autowired
    JsonLoader jsonLoader;

    @ModelAttribute("loggedInAdminName")
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

    @GetMapping("/auth-signin-basic")
    public String dashboard() {
        if (adminrepo.existsByEmail("yenhopie28@gmail.com") == false) {
            admin admin = new admin();
            admin.setAdminName("Liora Master");
            admin.setAdminEmail("yenhopie28@gmail.com");
            String hashPassword = encryption.encrypt("yenhopie28");
            admin.setAdminPassword(hashPassword);
            admin.setAdminPhone("0123456789");
            admin.setAdminProvince("Tỉnh Bình Dương");
            admin.setAdminCity("Thành phố Thủ Dầu Một");
            admin.setAdminAddress("Nam Kỳ Khởi Nghĩa, Định Hòa");
            admin.setAdminStatus("Active");
            adminrepo.save(admin);
        }
        return "admin/auth-signin-basic";
    }

    @PostMapping("/login/success")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginSubmit(@RequestParam("AdminEmail") String email,
            @RequestParam("AdminPassword") String password, HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        admin ad = adminrepo.findByAdminEmail(email);

        if (ad == null) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (ad.getAdminStatus().equalsIgnoreCase("block")) {
            response.put("success", false);
            response.put("message", "Your account is blocked. Please contact support.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (!encryption.matches(password, ad.getAdminPassword())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (ad.getAdminEmail().equals("yenhopie28@gmail.com")) {
            session.setAttribute("loginSuper", ad.getAdminId());
        } else {
            session.setAttribute("loginAdmin", ad.getAdminId());
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/auth-signin-basic";
    }

    @GetMapping("apps-ecommerce-sellers")
    public String admin(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }

        List<provinces> provincesList;
        try {
            provincesList = jsonLoader.loadProvinces();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        model.addAttribute("provinces", provincesList);

        List<admin> admins = (List<admin>) adminrepo.findAll();
        model.addAttribute("adminsdto", new adminsdto());
        model.addAttribute("admins", admins);
        return "admin/apps-ecommerce-sellers";
    }

    @PostMapping("/addAdmin")
    public String saveAdmin(@RequestParam(value = "adminProvince", required = false) String province,
            @ModelAttribute("adminsdto") adminsdto adminsdto, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("admins", adminrepo.findAll());
            return "admin/apps-ecommerce-sellers";
        }

        int adminId = adminrepo.findNextAdminId();

        String provinceName = jsonLoader.getProvinceNameById(province);

        admin newAdmin = new admin();
        newAdmin.setAdminId(adminId);
        newAdmin.setAdminName(adminsdto.getAdminName());
        newAdmin.setAdminEmail(adminsdto.getAdminEmail());
        String hashPassword = encryption.encrypt(adminsdto.getAdminPassword());
        newAdmin.setAdminPassword(hashPassword);
        newAdmin.setAdminPhone(adminsdto.getAdminPhone());
        newAdmin.setAdminAddress(adminsdto.getAdminAddress());
        newAdmin.setAdminCity(adminsdto.getAdminCity());
        newAdmin.setAdminProvince(provinceName);
        newAdmin.setAdminStatus(adminsdto.getAdminStatus());
        adminrepo.save(newAdmin);

        return "redirect:/admin/apps-ecommerce-sellers";
    }

    @GetMapping("/districts/{provinceId}")
    @ResponseBody
    public List<districts> getDistricts(@PathVariable String provinceId) {
        try {
            return jsonLoader.loadDistricts(provinceId);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/deleteAdmin/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            adminrepo.deleteById(id);
            ;
            redirectAttributes.addFlashAttribute("successMessage", "Admin deleted successfully!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin not found or already deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the Admin.");
        }

        return "redirect:/admin/apps-ecommerce-sellers";
    }

    @PostMapping("/editAdmin")
    public String saveEditedCategory(@ModelAttribute("adminsdto") adminsdto adminsdto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-sellers";
        }

        Integer adminId = adminsdto.getAdminId();
        admin existingAdmin = adminrepo.findById(adminId).orElse(null);
        if (existingAdmin == null) {
            result.addError(new FieldError("adminsdto", "AdminId", "Admin not found!"));
            return "admin/apps-ecommerce-sellers";
        }

        existingAdmin.setAdminStatus(adminsdto.getAdminStatus());

        adminrepo.save(existingAdmin);

        return "redirect:/admin/apps-ecommerce-sellers";
    }
}