package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.*;
import com.example.demo.repository.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class customercontroller {

    @Autowired
    customerrepository customerrepo;

    @Autowired
    productrepository productrepo;

    @Autowired
    orderrepository orderrepo;

    @Autowired
    adminrepository adminrepo;

    @Autowired
    blogrepository blogrepo;

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

    @GetMapping("apps-ecommerce-customers")
    public String customers(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }

        List<customers> customers = (List<customers>) customerrepo.findAll();
        model.addAttribute("customers", customers);
        return ("admin/apps-ecommerce-customers");
    }

    @PostMapping("/editCustomer")
    public String editCustomer(@ModelAttribute("customersdto") customersdto customersdto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-customers";
        }
        Integer customerId = customersdto.getCustomerId();
        customers existingCustomer = customerrepo.findById(customerId).orElse(null);
        if (existingCustomer == null) {
            result.addError(new FieldError("customersdto", "CustomerId", "Customer not found!"));
            return "admin/apps-ecommerce-customers";
        }
        existingCustomer.setCustomerStatus(customersdto.getCustomerStatus());
        customerrepo.save(existingCustomer);

        return "redirect:/admin/apps-ecommerce-customers";
    }

    @GetMapping("pages-profile-settings")
    public String setting() {
        return ("admin/pages-profile-settings");
    }

}