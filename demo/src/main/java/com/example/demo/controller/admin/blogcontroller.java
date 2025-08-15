package com.example.demo.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class blogcontroller {

    @Autowired
    adminrepository adminrepo;

    @Autowired
    blogrepository blogrepo;

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

    @GetMapping("apps-ecommerce-blog")
    public String blog(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<blogs> blogs = (List<blogs>) blogrepo.findAll();
        model.addAttribute("blogs", blogs);
        return ("admin/apps-ecommerce-blog");
    }

    @GetMapping("apps-ecommerce-create-blog")
    public String addproduct(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<blogs> blogs = (List<blogs>) blogrepo.findAll();
        model.addAttribute("blogs", blogs);

        blogsdto blogsdto = new blogsdto();
        int nextBlogId = blogrepo.findNextBlogId();
        blogsdto.setBlogId(nextBlogId);
        model.addAttribute("blogsdto", blogsdto);
        return ("admin/apps-ecommerce-create-blog");
    }

    @PostMapping("apps-ecommerce-create-blog/save")
    public String saveProduct(@ModelAttribute("blogsdto") blogsdto blogsdto, BindingResult result,
            HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (result.hasErrors()) {
            return "admin/apps-ecommerce-create-blog";
        }

        if (blogsdto.getBlogImage().isEmpty()) {
            result.addError(new FieldError("blogsdto", "BlogImage", "BlogImage is required"));
            return "admin/apps-ecommerce-create-blog";
        }

        MultipartFile image = blogsdto.getBlogImage();
        String storagefilename = image.getOriginalFilename();

        String uploaddir = "C:\\Users\\Admin\\Downloads\\Cosmetic\\projectB_cse311\\demo\\src\\main\\resources\\static\\blogimages";
        Path uploadpath = Paths.get(uploaddir);

        try {

            if (!Files.exists(uploadpath)) {
                Files.createDirectories(uploadpath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Path targetPath = uploadpath.resolve(storagefilename);

                System.out.println("Target File Path: " + targetPath.toString());
                if (!Files.exists(targetPath)) {
                    Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.out.println("File already exists: " + targetPath.toString());
                }
            }

        } catch (IOException e) {

            System.out.println("Error occurred while saving image: " + e.getMessage());
            result.addError(new FieldError("blogsdto", "ProductMainImage", "Unable to save the image. Try again."));
            return "admin/apps-ecommerce-create-blog";
        }

        blogs bl = new blogs();
        bl.setBlogId(blogsdto.getBlogId());
        bl.setBlogTitle(blogsdto.getBlogTitle());
        bl.setBlogDescription(blogsdto.getBlogDescription());
        bl.setBlogStatus(blogsdto.getBlogStatus());
        bl.setBlogCreateDate(blogsdto.getBlogCreateDate());
        if (adminId != null) {
            bl.setBlogPostBy(adminrepo.findById(adminId).get().getAdminName());
        } else if (superId != null) {
            bl.setBlogPostBy(adminrepo.findById(superId).get().getAdminName());
        }
        bl.setBlogtag(blogsdto.getBlogtag());
        bl.setBlogImage(storagefilename);

        blogrepo.save(bl);

        return "redirect:/admin/apps-ecommerce-blog";
    }

    @GetMapping("/set-current-blog-id/{id}")
    public String setCurrentBlogId(@PathVariable("id") int id, HttpSession session) {
        session.setAttribute("currentBlogId", id);
        return "redirect:/admin/apps-ecommerce-edit-blog";
    }

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

        blogs blog = blogrepo.findById(blogId).orElse(null);
        if (blog == null) {
            model.addAttribute("errorMessage", "Blog not found!");
            return "admin/apps-ecommerce-edit-blog";
        }

        blogsdto blogsdto = new blogsdto();
        blogsdto.setBlogId(blog.getBlogId());
        blogsdto.setBlogTitle(blog.getBlogTitle());
        blogsdto.setBlogDescription(blog.getBlogDescription());
        blogsdto.setBlogStatus(blog.getBlogStatus());
        blogsdto.setBlogCreateDate(blog.getBlogCreateDate());
        blogsdto.setBlogPostBy(blog.getBlogPostBy());
        blogsdto.setBlogtag(blog.getBlogtag());

        model.addAttribute("blogsdto", blogsdto);
        model.addAttribute("existingImage", "/blogimages/" + blog.getBlogImage());

        return "admin/apps-ecommerce-edit-blog";
    }

    @PostMapping("/apps-ecommerce-edit-blog")
    public String saveEditedBlog(@ModelAttribute("blogsdto") blogsdto blogsdto, BindingResult result,
            HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (result.hasErrors()) {
            return "admin/apps-ecommerce-edit-blog";
        }

        blogs bl = blogrepo.findById(blogsdto.getBlogId()).orElse(null);
        if (bl == null) {
            result.addError(new FieldError("blogsdto", "blogId", "Blog not found!"));
            return "admin/apps-ecommerce-edit-blog";
        }

        String uploadDir = "C:\\Users\\Admin\\Downloads\\Cosmetic\\projectB_cse311\\demo\\src\\main\\resources\\static\\blogimages";
        Path uploadPath = Paths.get(uploadDir);

        MultipartFile image = blogsdto.getBlogImage();
        if (image != null && !image.isEmpty()) {
            String storageFilename = image.getOriginalFilename();
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = image.getInputStream()) {
                    Path targetPath = uploadPath.resolve(storageFilename);
                    Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                bl.setBlogImage(storageFilename);
            } catch (IOException e) {
                result.addError(
                        new FieldError("blogsdto", "BlogImage", "Unable to save the image. Try again."));
                return "admin/apps-ecommerce-edit-blog";
            }
        } else {
            bl.setBlogImage(bl.getBlogImage());
        }

        bl.setBlogTitle(blogsdto.getBlogTitle());
        bl.setBlogDescription(blogsdto.getBlogDescription());
        bl.setBlogStatus(blogsdto.getBlogStatus());
        bl.setBlogCreateDate(blogsdto.getBlogCreateDate());
        if (adminId != null) {
            bl.setBlogPostBy(adminrepo.findById(adminId).get().getAdminName());
        } else if (superId != null) {
            bl.setBlogPostBy(adminrepo.findById(superId).get().getAdminName());
        }
        bl.setBlogtag(blogsdto.getBlogtag());

        blogrepo.save(bl);

        return "redirect:/admin/apps-ecommerce-blog";
    }

    @GetMapping("/deleteblog/{id}")
    public String deleteblog(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            blogrepo.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Blog deleted successfully!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Blog not found or already deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the blog.");
        }

        return "redirect:/admin/apps-ecommerce-blog";
    }

}