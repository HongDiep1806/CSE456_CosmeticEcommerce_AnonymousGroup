package com.example.demo.controller.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.example.demo.service.AdminService;
import com.example.demo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.blogs;
import com.example.demo.model.blogsdto;
import com.example.demo.repository.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class blogcontroller {

    @Autowired
    AdminService adminService;

    @Autowired
    blogrepository blogrepo;
    @Autowired
    public BlogService blogService;

    @ModelAttribute("loggedInAdminName")
    public String getLoggedInAdminName(HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId != null) {
            return adminService.findById(adminId).getAdminName();
        } else if (superId != null) {
            return adminService.findById(superId).getAdminName();
        } else {
            return null;
        }
    }
    //done
    @GetMapping("apps-ecommerce-blog")
    public String blog(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<blogs> blogs = blogService.findAll();
        model.addAttribute("blogs", blogs);
        return ("admin/apps-ecommerce-blog");
    }
    //done
    @GetMapping("apps-ecommerce-create-blog")
    public String addproduct(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<blogs> blogs = blogService.findAll();
        model.addAttribute("blogs", blogs);
        blogsdto blogsdto = new blogsdto();
        int nextBlogId = blogService.findNextBlogId();
        blogsdto.setBlogId(nextBlogId);
        model.addAttribute("blogsdto", blogsdto);
        return ("admin/apps-ecommerce-create-blog");
    }

    //done
    @PostMapping("apps-ecommerce-create-blog/save")
    public String saveProduct(@ModelAttribute("blogsdto") blogsdto blogsdto,
                              BindingResult result,
                              HttpSession session) {

        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        // Validate form
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-create-blog";
        }
        if (blogsdto.getBlogImage() == null || blogsdto.getBlogImage().isEmpty()) {
            result.addError(new FieldError("blogsdto", "BlogImage", "BlogImage is required"));
            return "admin/apps-ecommerce-create-blog";
        }

        // Thư mục uploads tương đối
        String uploadDir = new File("uploads/blogimages").getAbsolutePath();
        Path uploadPath = Paths.get(uploadDir);

        // Tạo thư mục nếu chưa có
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            result.addError(new FieldError("blogsdto", "BlogImage", "Unable to create upload directory."));
            return "admin/apps-ecommerce-create-blog";
        }

        // Lưu file ảnh
        MultipartFile image = blogsdto.getBlogImage();
        String storageFilename = image.getOriginalFilename();

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(storageFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            result.addError(new FieldError("blogsdto", "BlogImage", "Unable to save the image."));
            return "admin/apps-ecommerce-create-blog";
        }

        blogService.saveNewBlog(blogsdto, adminId, superId, storageFilename);

        return "redirect:/admin/apps-ecommerce-blog";
    }


    //done
    @GetMapping("/set-current-blog-id/{id}")
    public String setCurrentBlogId(@PathVariable("id") int id, HttpSession session) {
        session.setAttribute("currentBlogId", id);
        return "redirect:/admin/apps-ecommerce-edit-blog";
    }

    //done
    @GetMapping("/apps-ecommerce-edit-blog")
    public String showEditForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }

        Integer blogId = (Integer) session.getAttribute("currentBlogId");
        if (blogId == null) {
            model.addAttribute("errorMessage", "No blog selected!");
            return "admin/apps-ecommerce-edit-blog";
        }

        blogsdto blogsdto = blogService.getBlogDtoById(blogId);
        if (blogsdto == null) {
            model.addAttribute("errorMessage", "Blog not found!");
            return "admin/apps-ecommerce-edit-blog";
        }

        // Add attributes to view
        model.addAttribute("blogsdto", blogsdto);
        model.addAttribute("existingImage", "/blogimages/" + blogService.getBlogImageFilename(blogId)); // có thể thêm hàm riêng nếu cần

        return "admin/apps-ecommerce-edit-blog";
    }
    //done
    @PostMapping("/apps-ecommerce-edit-blog")
    public String saveEditedBlog(@ModelAttribute("blogsdto") blogsdto blogsdto,
                                 BindingResult result,
                                 HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (result.hasErrors()) {
            return "admin/apps-ecommerce-edit-blog";
        }

        boolean success = blogService.updateBlog(blogsdto, adminId, superId, result);
        if (!success) {
            return "admin/apps-ecommerce-edit-blog";
        }

        return "redirect:/admin/apps-ecommerce-blog";
    }

    @GetMapping("/deleteblog/{id}")
    public String deleteblog(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        boolean deleted = blogService.deleteBlogById(id);

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Blog deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Blog not found or already deleted.");
        }

        return "redirect:/admin/apps-ecommerce-blog";
    }
}