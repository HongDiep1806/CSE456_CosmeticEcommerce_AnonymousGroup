package com.example.demo.controller.admin;

import com.example.demo.model.orders;
import com.example.demo.model.ordersdto;
import com.example.demo.repository.adminrepository;
import com.example.demo.service.OrderService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("admin")
public class ordercontroller {

    @Autowired private OrderService orderService;
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

    // ============= DETAILS =============
    @GetMapping("apps-ecommerce-order-details/{id}")
    public String orderdetail(@PathVariable("id") int id,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (!isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }

        OrderService.OrderDetailsVM vm = orderService.buildOrderDetails(id);
        if (vm == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found!");
            return "redirect:/admin/apps-ecommerce-orders";
        }

        model.addAttribute("customers", vm.getCustomer());
        model.addAttribute("orders", vm.getOrder());
        model.addAttribute("orderDetails", vm.getItems());
        return "admin/apps-ecommerce-order-details";
    }

    // ============= LIST =============
    @GetMapping("apps-ecommerce-orders")
    public String orders(Model model,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {
        if (!isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<orders> orderList = orderService.getAllOrders();
        model.addAttribute("orders", orderList);
        return "admin/apps-ecommerce-orders";
    }

    // ============= UPDATE STATUS =============
    @PostMapping("/editOrder")
    public String saveEditedOrder(@ModelAttribute("ordersdto") ordersdto ordersdto,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        boolean ok = orderService.updateOrderStatus(ordersdto, result);
        if (!ok || result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot update order.");
            return "redirect:/admin/apps-ecommerce-orders";
        }
        return "redirect:/admin/apps-ecommerce-orders";
    }
}
