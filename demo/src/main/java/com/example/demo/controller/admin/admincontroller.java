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
import com.example.demo.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class admincontroller {

    private final AdminService adminService;

    public admincontroller(AdminService adminService) {
        this.adminService = adminService;
    }

    @ModelAttribute("loggedInAdminName")
    public String getLoggedInAdminName(HttpSession session) {
        return adminService.getLoggedInAdminName(session);
    }

    @GetMapping("/auth-signin-basic")
    public String dashboard() {
        adminService.createSuperAdminIfNotExists();
        return "admin/auth-signin-basic";
    }

    @PostMapping("/login/success")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginSubmit(@RequestParam("AdminEmail") String email,
            @RequestParam("AdminPassword") String password,
            HttpSession session) {
        return adminService.handleLogin(email, password, session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/auth-signin-basic";
    }

    @GetMapping("/apps-ecommerce-sellers")
    public String admin(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!adminService.isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }

        model.addAttribute("provinces", adminService.loadProvinces());
        model.addAttribute("adminsdto", new adminsdto());
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/apps-ecommerce-sellers";
    }

    @PostMapping("/addAdmin")
    public String saveAdmin(@RequestParam(value = "adminProvince", required = false) String province,
            @ModelAttribute("adminsdto") adminsdto adminsdto,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("admins", adminService.getAllAdmins());
            return "admin/apps-ecommerce-sellers";
        }
        adminService.addAdmin(province, adminsdto);
        return "redirect:/admin/apps-ecommerce-sellers";
    }

    @GetMapping("/districts/{provinceId}")
    @ResponseBody
    public List<districts> getDistricts(@PathVariable String provinceId) {
        return adminService.loadDistricts(provinceId);
    }

    @GetMapping("/deleteAdmin/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        adminService.deleteAdmin(id, redirectAttributes);
        return "redirect:/admin/apps-ecommerce-sellers";
    }

    @PostMapping("/editAdmin")
    public String saveEditedAdmin(@ModelAttribute("adminsdto") adminsdto adminsdto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-sellers";
        }
        adminService.updateAdminStatus(adminsdto, result);
        return "redirect:/admin/apps-ecommerce-sellers";
    }
}
