package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.categories;
import com.example.demo.model.categoriesdto;
import com.example.demo.model.products;
import com.example.demo.repository.adminrepository;
import com.example.demo.repository.categoriesrepository;
import com.example.demo.repository.productrepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CategoryService {

    @Autowired
    private categoriesrepository caterepo;

    @Autowired
    private productrepository productrepo;

    @Autowired
    private adminrepository adminrepo;

    // get logged-in admin/super name
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

    // check if admin/super is logged in
    public boolean isAdminLoggedIn(HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");
        return (adminId != null || superId != null);
    }

    // get all categories with total quantity
    public List<categories> getAllCategoriesWithQuantity() {
        List<Object[]> results = caterepo.findCategoriesWithTotalQuantity();
        List<categories> categoriesList = new ArrayList<>();

        for (Object[] result : results) {
            categories category = (categories) result[0];
            Long totalQuantity = (Long) result[1];
            category.setCategoryQuantity(totalQuantity != null ? totalQuantity.intValue() : 0);
            categoriesList.add(category);
        }
        return categoriesList;
    }

    // get all categories
    public List<categories> findAllCategories() {
        return (List<categories>) caterepo.findAll();
    }

    // add new category
    public void addCategory(categoriesdto categoriesdto) {
        int categoryId = caterepo.findNextCategoryId();
        categories newCategory = new categories();
        newCategory.setCategoryId(categoryId);
        newCategory.setCategoryName(categoriesdto.getCategoryName());
        newCategory.setCategoryQuantity(categoriesdto.getCategoryQuantity());
        newCategory.setCategoryStatus(categoriesdto.getCategoryStatus());
        newCategory.setActive(categoriesdto.getCategoryStatus().equalsIgnoreCase("Active"));
        caterepo.save(newCategory);
    }

    // delete category by id
    // if the category has products, they cannot be deleted
    public void deleteCategory(int id) {
        caterepo.deleteById(id);
    }

    // find category by id
    public categories findCategoryById(Integer id) {
        return caterepo.findById(id).orElse(null);
    }

    // update category and its products
    public void updateCategoryAndProducts(categories existingCategory, categoriesdto categoriesdto) {
        String oldCategoryName = existingCategory.getCategoryName();

        existingCategory.setActive(!categoriesdto.getCategoryStatus().equalsIgnoreCase("Block"));
        existingCategory.setCategoryName(categoriesdto.getCategoryName());
        existingCategory.setCategoryStatus(categoriesdto.getCategoryStatus());
        caterepo.save(existingCategory);

        List<products> products = productrepo.findProductsByCategory(oldCategoryName);
        for (products product : products) {
            product.setProductCategory(categoriesdto.getCategoryName());
            product.setProductStatus(categoriesdto.getCategoryStatus());
        }
        productrepo.saveAll(products);
    }
}
