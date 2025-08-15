package com.example.demo.controller.admin;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class categoriescontroller {

    @Autowired
    categoriesrepository caterepo;

    @Autowired
    productrepository productrepo;

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
    
    @GetMapping("/apps-ecommerce-category")
    public String categories(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<Object[]> results = caterepo.findCategoriesWithTotalQuantity();
        List<categories> categoriesList = new ArrayList<>();

        for (Object[] result : results) {
            categories category = (categories) result[0];
            Long totalQuantity = (Long) result[1];
            category.setCategoryQuantity(totalQuantity != null ? totalQuantity.intValue() : 0); // Convert Long to int
            categoriesList.add(category);
        }

        model.addAttribute("categories", categoriesList);
        model.addAttribute("categoriesdto", new categoriesdto());
        return "admin/apps-ecommerce-category";
    }

    @PostMapping("/addCategory")
    public String saveCategory(@ModelAttribute("categoriesdto") categoriesdto categoriesdto, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", caterepo.findAll());
            return "admin/apps-ecommerce-category";
        }

        int categoryId = caterepo.findNextCategoryId();

        categories newCategory = new categories();
        newCategory.setCategoryId(categoryId);
        newCategory.setCategoryName(categoriesdto.getCategoryName());
        newCategory.setCategoryQuantity(categoriesdto.getCategoryQuantity());
        newCategory.setCategoryStatus(categoriesdto.getCategoryStatus());
        if (categoriesdto.getCategoryStatus().equalsIgnoreCase("Active")) {
            newCategory.setActive(true);
        } else {
            newCategory.setActive(false);
        }
        caterepo.save(newCategory);

        return "redirect:/admin/apps-ecommerce-category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            caterepo.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found or already deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the category.");
        }

        return "redirect:/admin/apps-ecommerce-category";
    }

    @PostMapping("/editCategory")
    public String saveEditedCategory(@ModelAttribute("categoriesdto") categoriesdto categoriesdto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-category";
        }

        Integer categoryId = categoriesdto.getCategoryId();
        categories existingCategory = caterepo.findById(categoryId).orElse(null);
        if (existingCategory == null) {
            result.addError(new FieldError("categoriesdto", "CategoryId", "Category not found!"));
            return "admin/apps-ecommerce-category";
        }

        String oldCategoryName = existingCategory.getCategoryName();

        if (categoriesdto.getCategoryStatus().equalsIgnoreCase("Block")) {
            existingCategory.setActive(false);
        } else {
            existingCategory.setActive(true);
        }

        existingCategory.setCategoryName(categoriesdto.getCategoryName());
        existingCategory.setCategoryStatus(categoriesdto.getCategoryStatus());

        caterepo.save(existingCategory);

        List<products> products = productrepo.findProductsByCategory(oldCategoryName);
        for (products product : products) {
            product.setProductCategory(categoriesdto.getCategoryName());
            product.setProductStatus(categoriesdto.getCategoryStatus());
        }
        productrepo.saveAll(products);

        return "redirect:/admin/apps-ecommerce-category";
    }
}
