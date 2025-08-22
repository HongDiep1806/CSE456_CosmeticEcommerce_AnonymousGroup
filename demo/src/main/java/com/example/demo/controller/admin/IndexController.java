package com.example.demo.controller.admin;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.*;
import com.example.demo.repository.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/admin")
public class IndexController {

    @Autowired
    AdminRepository adminrepo;

    @Autowired
    BlogRepository blogrepo;

    @Autowired
    ProductRepository productrepo;

    @Autowired
    OrderRepository orderrepo;

    @Autowired
    CustomerRepository customerrepo;

    @Autowired
    CategoriesRepository caterepo;

    @Autowired
    ProductImagesRepository productimagerepo;

    @Autowired
    CartRepository cartrepo;

    @Autowired
    OrderDetailRepository orderdetailsrepo;

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

    @GetMapping("index")
    public String dashboard(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        Long totalOrderAmount = orderrepo.findTotalOrderAmount();
        model.addAttribute("totalOrderAmount", totalOrderAmount);

        Long totalOrders = orderrepo.countAllOrders();
        model.addAttribute("totalOrders", totalOrders);

        Long totalCustomers = customerrepo.countAllCustomers();
        model.addAttribute("totalCustomers", totalCustomers);

        Pageable pageable = PageRequest.of(0, 5);
        List<Products> product = productrepo.findBestProducts(pageable);
        model.addAttribute("products", product);

        Pageable pageable1 = PageRequest.of(0, 5);
        List<Customers> customer = customerrepo.findTop5Customers(pageable1);
        model.addAttribute("customers", customer);

        Pageable pageable2 = PageRequest.of(0, 5);
        List<Orders> order = orderrepo.findTop50rders(pageable2);
        model.addAttribute("orders", order);

        return ("admin/index");
    }

}
