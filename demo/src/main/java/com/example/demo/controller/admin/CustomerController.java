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
public class CustomerController {

    @Autowired
    CustomerRepository customerrepo;

    @Autowired
    ProductRepository productrepo;

    @Autowired
    OrderRepository orderrepo;

    @Autowired
    AdminRepository adminrepo;

    @Autowired
    BlogRepository blogrepo;

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

        List<Customers> customers = (List<Customers>) customerrepo.findAll();
        model.addAttribute("customers", customers);
        return ("admin/apps-ecommerce-customers");
    }

    @PostMapping("/editCustomer")
    public String editCustomer(@ModelAttribute("customersdto") CustomersDto customersdto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-customers";
        }
        Integer customerId = customersdto.getCustomerId();
        Customers existingCustomer = customerrepo.findById(customerId).orElse(null);
        if (existingCustomer == null) {
            result.addError(new FieldError("CustomersDto", "CustomerId", "Customer not found!"));
            return "admin/apps-ecommerce-customers";
        }
        existingCustomer.setCustomerStatus(customersdto.getCustomerStatus());
        customerrepo.save(existingCustomer);

        return "redirect:/admin/apps-ecommerce-customers";
    }

    @GetMapping("pages-profile-settings")
    public String setting() {
        return ("Admin/pages-profile-settings");
    }

}