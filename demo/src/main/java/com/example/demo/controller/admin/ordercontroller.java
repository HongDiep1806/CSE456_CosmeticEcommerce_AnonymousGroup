package com.example.demo.controller.admin;

import java.util.ArrayList;
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
@RequestMapping("admin")
public class ordercontroller {

    @Autowired
    orderrepository orderrepo;

    @Autowired
    orderdetailrepository orderdetailsrepo;

    @Autowired
    productrepository productrepo;

    @Autowired
    customerrepository customerrepo;

    @Autowired
    adminrepository adminrepo;

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
    
    @GetMapping("apps-ecommerce-order-details/{id}")
    public String orderdetail(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        orders order = orderrepo.findById(id).orElse(null);
        customers customer = customerrepo.findById(order.getCustomer().getCustomerId()).orElse(null);

        List<orderdetails> orderDetailsList = orderdetailsrepo.findByOrderId(id);
        List<orderdetailsdto> orderDetailsDTOs = new ArrayList<>();
        for (orderdetails orderDetail : orderDetailsList) {
            products product = productrepo.findById(orderDetail.getProductId()).orElse(null);
            if (product != null) {
                orderdetailsdto dto = new orderdetailsdto();
                dto.setProductId(product.getProductId());
                dto.setProductName(product.getProductName());
                dto.setProductMainImage(product.getProductMainImage());
                dto.setQuantity(orderDetail.getProductQuantity());
                dto.setProductPrice(product.getProductPrice());
                dto.setTotalPrice(orderDetail.getProductPrice() * orderDetail.getProductQuantity());
                orderDetailsDTOs.add(dto);
            }
        }

        model.addAttribute("customers", customer);
        model.addAttribute("orders", order);
        model.addAttribute("orderDetails", orderDetailsDTOs);
        return ("admin/apps-ecommerce-order-details");
    }

    @GetMapping("apps-ecommerce-orders")
    public String orders(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<orders> order = (List<orders>) orderrepo.findAll();
        model.addAttribute("orders", order);
        return ("admin/apps-ecommerce-orders");
    }

    @PostMapping("/editOrder")
    public String saveEditedCategory(@ModelAttribute("ordersdto") ordersdto ordersdto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-orders";
        }

        Integer orderId = ordersdto.getOrderId();
        orders existingOrder = orderrepo.findById(orderId).orElse(null);
        if (existingOrder == null) {
            result.addError(new FieldError("ordersdto", "OrderId", "Order not found!"));
            return "admin/apps-ecommerce-orders";
        }

        existingOrder.setOrderStatus(ordersdto.getOrderStatus());

        orderrepo.save(existingOrder);

        return "redirect:/admin/apps-ecommerce-orders";
    }

}
