package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.otherfunction.JsonLoader;
import com.example.demo.otherfunction.encryption;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private customerrepository customerrepo;
    @Autowired private categoriesrepository caterepo;
    @Autowired private productrepository productrepo;
    @Autowired private productimagesrepository productimagerepo;
    @Autowired private cartrepository cartrepo;
    @Autowired private blogrepository blogrepo;
    @Autowired private orderrepository orderrepo;
    @Autowired private orderdetailrepository orderdetailsrepo;

    @Autowired private JsonLoader jsonLoader;               // loader tỉnh/thành
    @Autowired private SendEmailService sendEmailService;   // gửi email reset

    /* ======= Helpers chung ======= */

    public static class CartSummary {
        public final List<cartsdto> items;
        public final int count;
        public final double total;
        public CartSummary(List<cartsdto> items, double total) {
            this.items = items;
            this.count = items.size();
            this.total = total;
        }
    }

    public CartSummary buildCartSummary(Integer customerId) {
        if (customerId == null) return new CartSummary(new ArrayList<>(), 0.0);
        List<carts> cartItems = cartrepo.findByCustomerId(customerId);
        List<cartsdto> dtos = new ArrayList<>();
        double total = 0.0;
        for (carts c : cartItems) {
            products p = c.getProduct();
            if (p != null) {
                double line = p.getProductPrice() * c.getQuantity();
                total += line;
                cartsdto dto = new cartsdto();
                dto.setProductId(p.getProductId());
                dto.setProductName(p.getProductName());
                dto.setProductImage(p.getProductMainImage());
                dto.setProductPrice(p.getProductPrice());
                dto.setQuantity(c.getQuantity());
                dto.setTotalPrice(line);
                dtos.add(dto);
            }
        }
        return new CartSummary(dtos, total);
    }

    /* Home / Index */

    public void fillIndex(Model model) {
        List<categories> categories = (List<categories>) caterepo.findAll();
        model.addAttribute("categories", categories);

        Pageable top4 = PageRequest.of(0, 4);
        List<products> productsTop = productrepo.findTop5Products(top4).stream()
                .filter(p -> !"block".equalsIgnoreCase(p.getProductStatus()))
                .collect(Collectors.toList());
        model.addAttribute("products", productsTop);
        List<products> best = productrepo.findBestProducts(top4).stream()
                .filter(p -> !"block".equalsIgnoreCase(p.getProductStatus()))
                .collect(Collectors.toList());
        model.addAttribute("products1", best);

        int defaultCategoryId = categories.isEmpty() ? 0 : categories.get(12).getCategoryId(); // giữ logic cũ
        int defaultCategoryId2 = categories.isEmpty() ? 0 : categories.get(7).getCategoryId();
        List<products> toner = productrepo.findProductsByCategoryId(defaultCategoryId, top4).stream()
                .filter(p -> !"block".equalsIgnoreCase(p.getProductStatus()))
                .collect(Collectors.toList());
        List<products> lipstick = productrepo.findProductsByCategoryId(defaultCategoryId2, top4).stream()
                .filter(p -> !"block".equalsIgnoreCase(p.getProductStatus()))
                .collect(Collectors.toList());
        model.addAttribute("productsToner", toner);
        model.addAttribute("productsLipstick", lipstick);

        List<blogs> blogs = ((List<blogs>) blogrepo.findAll()).stream()
                .filter(b -> !"hidden".equalsIgnoreCase(b.getBlogStatus()))
                .collect(Collectors.toList());
        model.addAttribute("blogs", blogs);
    }

    /* Auth / Register / Login / Forgot */

    public customers prepareRegisterModel() {
        customers c = new customers();
        c.setCustomerId(customerrepo.findNextCustomerId());
        return c;
    }

    public String register(customers customer, String confirmPassword, String cemail, Model model) {
        if (customerrepo.findByCemail(cemail) != null) {
            model.addAttribute("error", "Email already exists.");
            return "user/register";
        }
        if (!Objects.equals(customer.getCustomerPassword(), confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "user/register";
        }
        customer.setCustomerPassword(encryption.encrypt(customer.getCustomerPassword()));
        customer.setCustomerStatus("Active");
        customerrepo.save(customer);
        return "redirect:/user/login";
    }

    public ResponseEntity<Map<String, Object>> login(String email, String password, HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        customers c = customerrepo.findByCemail(email);
        if (c == null) {
            resp.put("success", false);
            resp.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
        if ("block".equalsIgnoreCase(c.getCustomerStatus())) {
            resp.put("success", false);
            resp.put("message", "Your account is blocked. Please contact support.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
        }
        if (!encryption.matches(password, c.getCustomerPassword())) {
            resp.put("success", false);
            resp.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
        session.setAttribute("loginCustomer", c.getCustomerId());
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    public String sendResetCode(String cemail, HttpSession session, Model model) {
        customers customer = customerrepo.findByCemail(cemail);
        if (customer == null) {
            model.addAttribute("error", "Email not found.");
            return "user/forgot-password";
        }
        String code = sendEmailService.generateResetCode();
        session.setAttribute("resetCode", code);
        session.setAttribute("customerId", customer.getCustomerId());
        sendEmailService.sendEmail(cemail, "Your password reset code is: " + code, "Password Reset Code");
        return "redirect:/user/forgot-password/verify";
    }

    public String verifyResetCode(String code, HttpSession session, Model model) {
        String sessionCode = (String) session.getAttribute("resetCode");
        if (sessionCode != null && sessionCode.equals(code)) {
            return "user/reset-password";
        } else {
            model.addAttribute("error", "Invalid reset code.");
            return "user/verify-code";
        }
    }

    public String resetPasswordFromVerify(String newPassword, String confirmPassword, HttpSession session, Model model) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        customers customer = customerrepo.findById(customerId).orElse(null);
        if (customer == null) return "redirect:/user/login";

        if (!Objects.equals(newPassword, confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "user/reset-password";
        }
        customer.setCustomerPassword(encryption.encrypt(newPassword));
        customerrepo.save(customer);
        return "redirect:/user/login";
    }

    /* Cart */

    public String deleteCartItem(Integer productId, Integer customerId, RedirectAttributes ra) {
        if (customerId == null) {
            ra.addFlashAttribute("loginRequired", "Please log in to modify your cart.");
            return "redirect:/user/login";
        }
        CartId cartId = new CartId(customerId, productId);
        try {
            cartrepo.deleteById(cartId);
            ra.addFlashAttribute("successMessage", "Item removed from cart successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to remove item from cart.");
        }
        return "redirect:/user/cart";
    }

    public ResponseEntity<Map<String, Object>> addToCart(int productId, int quantity, Integer customerId) {
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/user/login")).build();
        }
        customers customer = customerrepo.findById(customerId).orElse(null);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Customer not found."));
        }
        products product = productrepo.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found."));
        }
        if (quantity > product.getProductQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Requested quantity exceeds available stock. Available: " + product.getProductQuantity()));
        }

        carts existing = cartrepo.findByCustomerIdAndProductId(customerId, productId);
        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (newQty > product.getProductQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Total quantity in cart exceeds available stock. Available: " + product.getProductQuantity()));
            }
            existing.setQuantity(newQty);
            cartrepo.save(existing);
        } else {
            carts c = new carts();
            c.setId(new CartId(customerId, productId));
            c.setCustomer(customer);
            c.setProduct(product);
            c.setQuantity(quantity);
            cartrepo.save(c);
        }

        CartSummary summary = buildCartSummary(customerId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("cartItemCount", summary.count);
        resp.put("cartItems", summary.items);
        resp.put("total", summary.total);
        return ResponseEntity.ok(resp);
    }

    public String updateCart(Map<String, String> params, Integer customerId, RedirectAttributes ra) {
        if (customerId == null) {
            ra.addFlashAttribute("loginRequired", "Please log in to update your cart.");
            return "redirect:/user/login";
        }
        List<carts> cartItems = cartrepo.findByCustomerId(customerId);
        boolean exceeded = false;
        for (carts item : cartItems) {
            String key = "quantity-" + item.getProduct().getProductId();
            String val = params.get(key);
            if (val != null) {
                int req = Integer.parseInt(val);
                int avail = item.getProduct().getProductQuantity();
                if (req > avail) {
                    exceeded = true;
                    ra.addFlashAttribute("errorMessage",
                            "Quantity for product " + item.getProduct().getProductName() +
                                    " exceeds available stock (" + avail + " available).");
                } else if (req > 0) {
                    item.setQuantity(req);
                    cartrepo.save(item);
                }
            }
        }
        return "redirect:/user/cart";
    }

    /* Checkout */

    public String showCheckout(HttpSession session, Model model) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        if (customerId == null) return "redirect:/user/login";

        customers customer = customerrepo.findById(customerId).orElse(null);
        if (customer == null) return "redirect:/user/login";
        model.addAttribute("customers", customer);

        List<carts> cartItems = cartrepo.findByCustomerId(customerId);
        if (cartItems.isEmpty()) {
            model.addAttribute("emptyCart", true);
            model.addAttribute("checkoutSuccess", false);
            return "user/checkout";
        }

        CartSummary summary = buildCartSummary(customerId);
        List<cartsdto> checkoutItems = summary.items.stream().map(i -> {
            cartsdto dto = new cartsdto();
            dto.setProductName(i.getProductName());
            dto.setQuantity(i.getQuantity());
            dto.setTotalPrice(i.getTotalPrice());
            return dto;
        }).collect(Collectors.toList());

        try {
            model.addAttribute("provinces", jsonLoader.loadProvinces());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        model.addAttribute("total", summary.total);
        model.addAttribute("cartItems2", checkoutItems);
        model.addAttribute("emptyCart", false);
        model.addAttribute("checkoutSuccess", false);
        return "user/checkout";
    }

    public List<districts> getDistricts(String provinceId) {
        try {
            return jsonLoader.loadDistricts(provinceId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String processCheckout(HttpSession session, String address, String city, String provinceId, String note) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        if (customerId == null) return "redirect:/user/login";

        List<carts> cartItems = cartrepo.findByCustomerId(customerId);
        if (cartItems.isEmpty()) {
            session.setAttribute("emptyCart", true);
            return "redirect:/user/checkout";
        }

        double total = 0.0;
        for (carts c : cartItems) {
            products p = c.getProduct();
            if (p != null) total += p.getProductPrice() * c.getQuantity();
        }

        String provinceName = jsonLoader.getProvinceNameById(provinceId);

        orders order = new orders();
        order.setCustomer(customerrepo.findById(customerId).orElse(null));
        order.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
        order.setOrderStatus("Pending");
        order.setOrderAmount(total);
        order.setOrderPaymentMethod("COD");
        order.setOrderNote(note != null ? note : "");
        order.setOrderAddress(address);
        order.setOrderCity(city);
        order.setOrderProvince(provinceName);
        orderrepo.save(order);

        for (carts c : cartItems) {
            products p = c.getProduct();
            if (p != null) {
                orderdetails od = new orderdetails();
                od.setOrder(order);
                od.setProductId(p.getProductId());
                od.setProductPrice(p.getProductPrice());
                od.setProductQuantity(c.getQuantity());
                od.setCoupon(null);
                orderdetailsrepo.save(od);

                int newQty = p.getProductQuantity() - c.getQuantity();
                p.setProductQuantity(Math.max(newQty, 0));
                productrepo.save(p);
            }
        }
        cartrepo.deleteAllByCustomerId(customerId);

        session.setAttribute("checkoutSuccess", true);
        return "redirect:/user/my-account";
    }

    //Account - Order/

    public String myAccount(HttpSession session, Model model) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        if (customerId == null) return "redirect:/user/login";

        List<orders> orders = orderrepo.findOrdersByCustomerId(customerId);
        model.addAttribute("orders", orders);

        customers customer = customerrepo.findById(customerId).orElse(null);
        model.addAttribute("customer", customer);

        try {
            model.addAttribute("provinces", jsonLoader.loadProvinces());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "user/my-account";
    }

    public String updateAccount(Integer customerId, String newPassword, String confirmPassword,
                                String provinceId, String city, customers updated, Model model) {
        if (customerId == null) return "redirect:/user/login";
        customers c = customerrepo.findById(customerId).orElse(null);
        if (c == null) return "redirect:/user/login";

        c.setCustomerName(updated.getCustomerName());
        c.setCustomerAddress(updated.getCustomerAddress());
        if (provinceId != null) {
            String provinceName = jsonLoader.getProvinceNameById(provinceId);
            c.setCustomerProvince(provinceName);
        }
        if (city != null) c.setCustomerCity(city);

        if (newPassword != null && !newPassword.isEmpty()) {
            if (!Objects.equals(newPassword, confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                // vẫn lưu các field khác
            } else {
                c.setCustomerPassword(encryption.encrypt(newPassword));
            }
        }
        customerrepo.save(c);
        return "redirect:/user/my-account";
    }

    public String orderDetails(int id, HttpSession session, Model model) {
        Integer customerId = (Integer) session.getAttribute("loginCustomer");
        if (customerId == null) return "redirect:/user/login";
        orders order = orderrepo.findById(id).orElse(null);
        if (order == null) return "redirect:/user/my-account";

        List<orderdetails> list = orderdetailsrepo.findByOrderId(id);
        List<orderdetailsdto> dtos = new ArrayList<>();
        for (orderdetails d : list) {
            products p = productrepo.findById(d.getProductId()).orElse(null);
            if (p != null) {
                orderdetailsdto dto = new orderdetailsdto();
                dto.setProductId(p.getProductId());
                dto.setProductName(p.getProductName());
                dto.setProductMainImage(p.getProductMainImage());
                dto.setQuantity(d.getProductQuantity());
                dto.setProductPrice(p.getProductPrice());
                dto.setTotalPrice(d.getProductPrice() * d.getProductQuantity());
                dtos.add(dto);
            }
        }
        model.addAttribute("customer", customerrepo.findById(customerId).orElse(null));
        model.addAttribute("orders", order);
        model.addAttribute("orderDetails", dtos);
        return "user/order-details";
    }

    /* Blogs / Products / Shop */

    public void fillBlogDetail(int id, Model model) {
        blogs blog = blogrepo.findById(id).orElse(null);
        List<blogs> recent = blogrepo.findTop3ByIdNot(id);
        model.addAttribute("blogs", blog);
        model.addAttribute("recentBlogs", recent);
    }

    public void fillBlogList(int page, Model model) {
        int size = 9;
        Pageable pageable = PageRequest.of(page, size);
        Page<blogs> blogPage = blogrepo.findAll(pageable);
        List<blogs> visible = blogPage.getContent().stream()
                .filter(b -> !"hidden".equalsIgnoreCase(b.getBlogStatus()))
                .collect(Collectors.toList());
        model.addAttribute("blogs", visible);
        model.addAttribute("currentPage", blogPage.getNumber());
        model.addAttribute("totalPages", blogPage.getTotalPages());
    }

    public void fillProductDetail(int id, Model model) {
        products p = productrepo.findById(id).orElse(null);
        List<productotherimages> gallery = productimagerepo.findByProduct(p);
        List<String> galleryUrls = gallery.stream()
                .map(i -> "/productimages/" + i.getProductImage())
                .collect(Collectors.toList());
        List<products> related = productrepo.findProductsByCategoryExcludingId(
                p.getProductCategory(), p.getProductId());
        model.addAttribute("products", p);
        model.addAttribute("galleryImageUrls", galleryUrls);
        model.addAttribute("relatedProducts", related);
    }

    public void fillShop(String categoryName, Double minPrice, Double maxPrice, int page, Model model) {
        int size = 8;
        List<categories> categories = (List<categories>) caterepo.findAll();
        model.addAttribute("categories", categories);

        Pageable byIdDesc = PageRequest.of(page, size, Sort.by("ProductId").descending());
        Pageable byPriceDescBig = PageRequest.of(page, 100, Sort.by("ProductPrice").descending());
        Page<products> productPage;

        if (minPrice != null && maxPrice != null) {
            if (categoryName != null && !categoryName.isEmpty()) {
                productPage = productrepo.findProductsByCategoryAndPriceRange(categoryName, minPrice, maxPrice, byPriceDescBig);
            } else {
                productPage = productrepo.findProductsByPriceRange(minPrice, maxPrice, byPriceDescBig);
            }
        } else if (categoryName != null && !categoryName.isEmpty()) {
            productPage = productrepo.findProductsByCategory(categoryName, byIdDesc);
        } else {
            productPage = productrepo.findAllProducts(byIdDesc);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categoryName", categoryName);
    }
}
