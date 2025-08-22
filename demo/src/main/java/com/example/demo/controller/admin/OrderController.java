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
public class OrderController {

    @Autowired
    OrderRepository orderrepo;

    @Autowired
    OrderDetailRepository orderdetailsrepo;

    @Autowired
    ProductRepository productrepo;

    @Autowired
    CustomerRepository customerrepo;

    @Autowired
    AdminRepository adminrepo;

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
        Orders order = orderrepo.findById(id).orElse(null);
        Customers customer = customerrepo.findById(order.getCustomer().getCustomerId()).orElse(null);

        List<OrderDetails> orderDetailsList = orderdetailsrepo.findByOrderId(id);
        List<OrderDetailsDto> orderDetailsDTOs = new ArrayList<>();
        for (OrderDetails orderDetail : orderDetailsList) {
            Products product = productrepo.findById(orderDetail.getProductId()).orElse(null);
            if (product != null) {
                OrderDetailsDto dto = new OrderDetailsDto();
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
        List<Orders> order = (List<Orders>) orderrepo.findAll();
        model.addAttribute("orders", order);
        return ("admin/apps-ecommerce-orders");
    }

    @PostMapping("/editOrder")
    public String saveEditedCategory(@ModelAttribute("ordersdto") OrdersDto ordersdto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-orders";
        }

        Integer orderId = ordersdto.getOrderId();
        Orders existingOrder = orderrepo.findById(orderId).orElse(null);
        if (existingOrder == null) {
            result.addError(new FieldError("OrdersDto", "OrderId", "Order not found!"));
            return "admin/apps-ecommerce-orders";
        }

        existingOrder.setOrderStatus(ordersdto.getOrderStatus());

        orderrepo.save(existingOrder);

        return "redirect:/admin/apps-ecommerce-orders";
    }

}
