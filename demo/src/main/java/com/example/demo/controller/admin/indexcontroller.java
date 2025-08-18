package com.example.demo.controller.admin;

import com.example.demo.model.customers;
import com.example.demo.model.orders;
import com.example.demo.model.products;
import com.example.demo.repository.adminrepository;
import com.example.demo.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class indexcontroller {

    @Autowired private adminrepository adminrepo;
    @Autowired private DashboardService dashboardService;

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

    @GetMapping("index")
    public String dashboard(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }

        // Số liệu tổng hợp
        Long totalOrderAmount = dashboardService.getTotalOrderAmount();
        Long totalOrders      = dashboardService.getTotalOrders();
        Long totalCustomers   = dashboardService.getTotalCustomers();

        // Danh sách hiển thị
        List<products>  products  = dashboardService.getBestProducts(5);
        List<customers> customers = dashboardService.getTopCustomers(5);
        List<orders>    orders    = dashboardService.getRecentOrders(5);

        // Đưa vào model
        model.addAttribute("totalOrderAmount", totalOrderAmount);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("products", products);
        model.addAttribute("customers", customers);
        model.addAttribute("orders", orders);

        return "admin/index";
    }
}
