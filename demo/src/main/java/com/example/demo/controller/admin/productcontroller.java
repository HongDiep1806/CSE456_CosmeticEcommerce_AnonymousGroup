package com.example.demo.controller.admin;

import com.example.demo.model.productsdto;
import com.example.demo.model.products;
import com.example.demo.repository.adminrepository;
import com.example.demo.service.ProductService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class productcontroller {

    @Autowired private ProductService productService;
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

    // ===== LIST =====
    @GetMapping("apps-ecommerce-products")
    public String products(Model model, RedirectAttributes ra, HttpSession session) {
        if (!isLoggedIn(session)) {
            ra.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("productIdsInCart", productService.getProductIdsInCart());
        return "admin/apps-ecommerce-products";
    }

    // ===== ADD (GET) =====
    @GetMapping("apps-ecommerce-add-product")
    public String addproduct(Model model, RedirectAttributes ra, HttpSession session) {
        if (!isLoggedIn(session)) {
            ra.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        model.addAttribute("categories", productService.getActiveCategories());

        productsdto dto = new productsdto();
        dto.setProductId(productService.getNextProductId());
        model.addAttribute("productsdto", dto);
        return "admin/apps-ecommerce-add-product";
    }

    // ===== ADD (POST) =====
    @PostMapping("apps-ecommerce-add-product")
    public String saveProduct(@ModelAttribute("productsdto") productsdto dto, BindingResult result, Model model) {
        products saved = productService.create(dto, result);
        if (saved == null) {
            model.addAttribute("categories", productService.getActiveCategories());
            return "admin/apps-ecommerce-add-product";
        }
        return "redirect:/admin/apps-ecommerce-products";
    }

    // ===== keep mapping cũ để tương thích view =====
    @GetMapping("/set-current-product-id/{id}")
    public String setCurrentProductId(@PathVariable("id") int id, HttpSession session) {
        session.setAttribute("currentProductId", id);
        return "redirect:/admin/apps-ecommerce-edit-product";
    }

    // ===== EDIT (GET) =====
    @GetMapping("/apps-ecommerce-edit-product")
    public String showEditForm(HttpSession session, Model model, RedirectAttributes ra) {
        if (!isLoggedIn(session)) {
            ra.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        Integer productId = (Integer) session.getAttribute("currentProductId");
        if (productId == null) {
            model.addAttribute("errorMessage", "No product selected!");
            return "admin/apps-ecommerce-edit-product";
        }

        productsdto dto = productService.buildEditDto(productId);
        if (dto == null) {
            model.addAttribute("errorMessage", "Product not found!");
            return "admin/apps-ecommerce-edit-product";
        }

        model.addAttribute("categories", productService.getActiveCategories());
        model.addAttribute("productsdto", dto);
        model.addAttribute("existingImage", productService.getExistingMainImageUrl(productId));
        model.addAttribute("galleryImageUrls", productService.getGalleryImageUrls(productId));
        return "admin/apps-ecommerce-edit-product";
    }

    // ===== EDIT (POST) =====
    @PostMapping("/apps-ecommerce-edit-product")
    public String saveEditedProduct(@ModelAttribute("productsdto") productsdto dto,
                                    @RequestParam(value = "imagesToDelete", required = false) String imagesToDelete,
                                    BindingResult result, Model model) {
        products saved = productService.update(dto, imagesToDelete, result);
        if (saved == null) {
            model.addAttribute("categories", productService.getActiveCategories());
            model.addAttribute("existingImage", productService.getExistingMainImageUrl(dto.getProductId()));
            model.addAttribute("galleryImageUrls", productService.getGalleryImageUrls(dto.getProductId()));
            return "admin/apps-ecommerce-edit-product";
        }
        return "redirect:/admin/apps-ecommerce-products";
    }

    // ===== DELETE =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes ra) {
        try {
            boolean ok = productService.delete(id);
            if (!ok) {
                ra.addFlashAttribute("errorMessage", "Product is in a cart and cannot be deleted.");
            } else {
                ra.addFlashAttribute("successMessage", "Product deleted successfully!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "An error occurred while deleting the product.");
        }
        return "redirect:/admin/apps-ecommerce-products";
    }

    // ===== remove preview như flow cũ =====
    @PostMapping("/removeImagePreview")
    public String removeImagePreview(@RequestParam("index") int index, productsdto dto, Model model) {
        if (dto.getProductOtherImages() != null && index >= 0 && index < dto.getProductOtherImages().size()) {
            dto.getProductOtherImages().remove(index);
        }
        model.addAttribute("productsdto", dto);
        return "redirect:/admin/apps-ecommerce-add-product";
    }
}
