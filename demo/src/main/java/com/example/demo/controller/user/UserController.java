package com.example.demo.controller.user;

import com.example.demo.model.customers;
import com.example.demo.model.districts;
import com.example.demo.service.UserService;
import com.example.demo.service.UserService.CartSummary;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        CartSummary s = userService.buildCartSummary(customerId);
        model.addAttribute("cartItems", s.items);
        model.addAttribute("cartItemCount", s.count);
        model.addAttribute("total", s.total);
    }

    /* Trang chủ */
    @GetMapping("/index")
    public String index(Model model) {
        userService.fillIndex(model);
        return "/user/index";
    }

    /* ====== Register / Login / Logout ====== */
    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("customers", userService.prepareRegisterModel());
        return "user/register";
    }

    @PostMapping("/register/save")
    public String saveCustomer(@ModelAttribute customers customer,
                               @RequestParam("confirmPassword") String confirmPassword,
                               @RequestParam("cemail") String cemail,
                               Model model) {
        return userService.register(customer, confirmPassword, cemail, model);
    }

    @GetMapping("/login")
    public String login(Model model) { return "user/login"; }

    @PostMapping("/login/success")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginSubmit(@RequestParam("cemail") String email,
                                                           @RequestParam("CustomerPassword") String password,
                                                           HttpSession session) {
        return userService.login(email, password, session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }

    /*  Forgot password flow */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() { return "user/forgot-password"; }

    @PostMapping("/forgot-password/send-code")
    public String sendResetCode(@RequestParam("email") String cemail, HttpSession session, Model model) {
        return userService.sendResetCode(cemail, session, model);
    }

    @GetMapping("/forgot-password/verify")
    public String verifyCodePage() { return "user/verify-code"; }

    @PostMapping("/forgot-password/verify-code")
    public String verifyResetCode(@RequestParam("code") String code, HttpSession session, Model model) {
        return userService.verifyResetCode(code, session, model);
    }

    @GetMapping("/forgot-password/reset")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("cemail", email);
        return "user/reset-password";
    }

    @PostMapping("/forgot-password/reset")
    public String resetPassword(@RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                HttpSession session, Model model) {
        return userService.resetPasswordFromVerify(newPassword, confirmPassword, session, model);
    }

    /* Static pages */
    @GetMapping("404")   public String error() { return "user/404"; }
    @GetMapping("about") public String about() { return "user/about"; }
    @GetMapping("contact") public String contact() { return "user/contact"; }
    @GetMapping("faq") public String faq() { return "user/faq"; }

    /* Blogs */
    @GetMapping("blog-details/{id}")
    public String blogdetail(@PathVariable("id") int id, Model model) {
        userService.fillBlogDetail(id, model);
        return "user/blog-details";
    }

    @GetMapping("/blog")
    public String blog(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        userService.fillBlogList(page, model);
        return "user/blog";
    }

    /* Cart */
    @GetMapping("cart")
    public String cart(HttpSession session, Model model, RedirectAttributes ra) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        if (customerId == null) {
            ra.addFlashAttribute("loginRequired", "Please log in to view your cart.");
            return "redirect:/user/login";
        }
        return "user/cart";
    }

    @GetMapping("/cart/delete/{productId}")
    public String deleteCartItem(@PathVariable Integer productId, HttpSession session, RedirectAttributes ra) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        return userService.deleteCartItem(productId, customerId, ra);
    }

    @PostMapping("cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productId") int productId,
                                                         @RequestParam("quantity") int quantity,
                                                         HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        return userService.addToCart(productId, quantity, customerId);
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Map<String, String> params, HttpSession session,
                             RedirectAttributes ra) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        return userService.updateCart(params, customerId, ra);
    }

    /* ====== Checkout ====== */
    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        return userService.showCheckout(session, model);
    }

    @GetMapping("/districts/{provinceId}")
    @ResponseBody
    public List<districts> getDistricts(@PathVariable String provinceId) {
        return userService.getDistricts(provinceId);
    }

    @PostMapping("/checkout/confirmation")
    public String processCheckout(HttpSession session, Model model,
                                  @RequestParam("orderAddress") String address,
                                  @RequestParam("orderCity") String city,
                                  @RequestParam("orderProvince") String province,
                                  @RequestParam(value = "orderNote", required = false) String note) {
        return userService.processCheckout(session, address, city, province, note);
    }

    @GetMapping("/checkout/clearCheckoutSuccess")
    public String clearCheckoutSuccess(HttpSession session, Model model) {
        Boolean checkoutSuccess = (Boolean) session.getAttribute("checkoutSuccess");
        if (checkoutSuccess != null && checkoutSuccess) {
            model.addAttribute("checkoutSuccess", true);
            session.removeAttribute("checkoutSuccess");
        }
        return "user/my-account";
    }

    /* Account / Orders */
    @GetMapping("my-account")
    public String myaccount(HttpSession session, Model model) {
        return userService.myAccount(session, model);
    }

    @PostMapping("/my-account")
    public String updateAccount(@RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                @RequestParam(value = "orderProvince", required = false) String province,
                                @RequestParam(value = "orderCity", required = false) String city,
                                @ModelAttribute("customer") customers updatedCustomer,
                                HttpSession session, Model model) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        return userService.updateAccount(customerId, newPassword, confirmPassword, province, city, updatedCustomer, model);
    }

    @GetMapping("order-details/{id}")
    public String orderDetail(@PathVariable("id") int id, Model model, HttpSession session) {
        return userService.orderDetails(id, session, model);
    }

    /* Product / Shop */
    @GetMapping("product-details/{id}")
    public String productDetail(@PathVariable("id") int id, Model model) {
        userService.fillProductDetail(id, model);
        return "user/product-details";
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(value = "category", required = false) String categoryName,
                       @RequestParam(value = "minPrice", required = false) Double minPrice,
                       @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        userService.fillShop(categoryName, minPrice, maxPrice, page, model);
        return "user/shop";
    }
}