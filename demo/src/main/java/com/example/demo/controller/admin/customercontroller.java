package com.example.demo.controller.admin;

import com.example.demo.model.customers;
import com.example.demo.model.customersdto;
import com.example.demo.repository.adminrepository;
import com.example.demo.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class customercontroller {

    @Autowired private CustomerService customerService;
    @Autowired private adminrepository adminrepo;

    @ModelAttribute("loggedInAdminName")
    public String getLoggedInAdminName(HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");
        if (adminId != null) return adminrepo.findById(adminId).get().getAdminName();
        if (superId != null) return adminrepo.findById(superId).get().getAdminName();
        return null;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginAdmin") != null || session.getAttribute("loginSuper") != null;
    }

    // ===== LIST CUSTOMERS =====
    @GetMapping("apps-ecommerce-customers")
    public String customers(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<customers> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "admin/apps-ecommerce-customers";
    }

    // ===== UPDATE CUSTOMER STATUS =====
    @PostMapping("/editCustomer")
    public String editCustomer(@ModelAttribute("customersdto") customersdto customersdto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        boolean ok = customerService.updateCustomerStatus(customersdto, result);
        if (!ok || result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot update customer.");
        }
        return "redirect:/admin/apps-ecommerce-customers";
    }

    // ===== PROFILE SETTINGS (static page) =====
    @GetMapping("pages-profile-settings")
    public String setting() {
        return "admin/pages-profile-settings";
    }
}
