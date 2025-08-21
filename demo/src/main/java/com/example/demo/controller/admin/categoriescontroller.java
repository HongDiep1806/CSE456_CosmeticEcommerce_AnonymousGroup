
package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.categories;
import com.example.demo.model.categoriesdto;
import com.example.demo.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class categoriescontroller {

    @Autowired
    private CategoryService categoriesService;

    // get logged-in admin/super name
    @ModelAttribute("loggedInAdminName")
    public String getLoggedInAdminName(HttpSession session) {
        return categoriesService.getLoggedInAdminName(session);
    }

    // view all categories page
    @GetMapping("/apps-ecommerce-category")
    public String categories(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (!categoriesService.isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<categories> categoriesList = categoriesService.getAllCategoriesWithQuantity();
        model.addAttribute("categories", categoriesList);
        model.addAttribute("categoriesdto", new categoriesdto());
        return "admin/apps-ecommerce-category";
    }

    // add new category
    @PostMapping("/addCategory")
    public String saveCategory(@ModelAttribute("categoriesdto") categoriesdto categoriesdto,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoriesService.findAllCategories());
            return "admin/apps-ecommerce-category";
        }
        categoriesService.addCategory(categoriesdto);
        return "redirect:/admin/apps-ecommerce-category";
    }

    // delete category by id
    @GetMapping("/deleteCategory/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            categoriesService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found or already deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the category.");
        }
        return "redirect:/admin/apps-ecommerce-category";
    }

    // edit existing category
    @PostMapping("/editCategory")
    public String saveEditedCategory(@ModelAttribute("categoriesdto") categoriesdto categoriesdto,
            BindingResult result) {
        categories existingCategory = categoriesService.findCategoryById(categoriesdto.getCategoryId());
        if (existingCategory == null) {
            result.addError(new FieldError("categoriesdto", "CategoryId", "Category not found!"));
            return "admin/apps-ecommerce-category";
        }

        categoriesService.updateCategoryAndProducts(existingCategory, categoriesdto);
        return "redirect:/admin/apps-ecommerce-category";
    }
}
