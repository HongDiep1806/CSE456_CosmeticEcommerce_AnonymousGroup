package com.example.demo.controller.admin;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.*;
import com.example.demo.repository.*;

import jakarta.servlet.http.*;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    CategoriesRepository caterepo;

    @Autowired
    ProductRepository productrepo;

    @Autowired
    ProductImagesRepository productimagerepo;

    @Autowired
    CartRepository cartrepo;

    @Autowired
    AdminRepository adminrepo;

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

    @GetMapping("apps-ecommerce-products")
    public String products(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<Products> productList = (List<Products>) productrepo.findAll();

        List<Integer> productIdsInCart = new ArrayList<>();
        cartrepo.findAll().forEach(cart -> {
            productIdsInCart.add(cart.getProduct().getProductId());
        });
        model.addAttribute("productIdsInCart", productIdsInCart);
        model.addAttribute("products", productList);
        return "admin/apps-ecommerce-products";
    }

    @GetMapping("apps-ecommerce-add-product")
    public String addproduct(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<Categories> categories = caterepo.findAllByIsActiveTrue();
        model.addAttribute("categories", categories);

        ProductsDto productsdto = new ProductsDto();
        int nextProductId = productrepo.findNextProductId();
        productsdto.setProductId(nextProductId);

        model.addAttribute("productsdto", productsdto);
        return ("admin/apps-ecommerce-add-product");
    }

    @PostMapping("apps-ecommerce-add-product")
    public String saveProduct(@ModelAttribute("productsdto") ProductsDto productsdto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-add-product";
        }

        if (productsdto.getProductMainImage().isEmpty()) {
            result.addError(new FieldError("ProductsDto", "ProductMainImage", "ProductMainImage is required"));
            return "admin/apps-ecommerce-add-product";
        }

        // Thư mục productimages khi app đang chạy (localhost)
        String uploadDir = new File("uploads/productimages").getAbsolutePath();
        Path uploadPath = Paths.get(uploadDir);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            result.addError(new FieldError("ProductsDto", "ProductMainImage", "Unable to create upload directory."));
            return "admin/apps-ecommerce-add-product";
        }

        // ==== Lưu ảnh chính ====
        MultipartFile image = productsdto.getProductMainImage();
        String storageFilename = image.getOriginalFilename();

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(storageFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            result.addError(new FieldError("ProductsDto", "ProductMainImage", "Unable to save the main image."));
            return "admin/apps-ecommerce-add-product";
        }

        // Lưu thông tin sản phẩm
        Products pro = new Products();
        pro.setProductId(productsdto.getProductId());
        pro.setProductName(productsdto.getProductName());
        pro.setProductPrice(productsdto.getProductPrice());
        pro.setProductDescription(productsdto.getProductDescription());
        pro.setProductCategory(productsdto.getProductCategory());
        pro.setProductQuantity(productsdto.getProductQuantity());
        pro.setProductMainImage(storageFilename);
        pro.setProductStatus(productsdto.getProductStatus());

        Products savedProduct = productrepo.save(pro);

        // ==== Lưu gallery images ====
        List<MultipartFile> galleryImages = productsdto.getProductOtherImages();
        if (galleryImages != null && !galleryImages.isEmpty()) {
            for (MultipartFile galleryImage : galleryImages) {
                if (galleryImage.isEmpty()) continue;

                String galleryImageFilename = galleryImage.getOriginalFilename();

                try (InputStream inputStream = galleryImage.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(galleryImageFilename), StandardCopyOption.REPLACE_EXISTING);

                    ProductOtherImages productImage = new ProductOtherImages();
                    productImage.setProduct(savedProduct);
                    productImage.setProductImage(galleryImageFilename);
                    productimagerepo.save(productImage);
                } catch (IOException e) {
                    System.out.println("Error saving gallery image: " + e.getMessage());
                }
            }
        }

        return "redirect:/admin/apps-ecommerce-products";
    }


    @GetMapping("/set-current-product-id/{id}")
    public String setCurrentProductId(@PathVariable("id") int id, HttpSession session) {
        session.setAttribute("currentProductId", id);
        return "redirect:/admin/apps-ecommerce-edit-product";
    }

    @GetMapping("/apps-ecommerce-edit-product")
    public String showEditForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Integer adminId = (Integer) session.getAttribute("loginAdmin");
        Integer superId = (Integer) session.getAttribute("loginSuper");

        if (adminId == null && superId == null) {
            redirectAttributes.addFlashAttribute("loginRequired", "Please log in to view this page.");
            return "redirect:/admin/auth-signin-basic";
        }
        List<Categories> categories = caterepo.findAllByIsActiveTrue();
        model.addAttribute("categories", categories);
        Integer productId = (Integer) session.getAttribute("currentProductId");
        if (productId == null) {
            model.addAttribute("errorMessage", "No product selected!");
            return "admin/apps-ecommerce-edit-product";
        }

        Products product = productrepo.findById(productId).orElse(null);
        if (product == null) {
            model.addAttribute("errorMessage", "Product not found!");
            return "admin/apps-ecommerce-edit-product";
        }

        ProductsDto productsDto = new ProductsDto();
        productsDto.setProductId(product.getProductId());
        productsDto.setProductName(product.getProductName());
        productsDto.setProductPrice(product.getProductPrice());
        productsDto.setProductDescription(product.getProductDescription());
        productsDto.setProductCategory(product.getProductCategory());
        productsDto.setProductQuantity(product.getProductQuantity());
        productsDto.setProductStatus(product.getProductStatus());
        productsDto.setCreateTime(product.getCreateTime());

        model.addAttribute("productsdto", productsDto);
        model.addAttribute("existingImage", "/productimages/" + product.getProductMainImage());

        List<ProductOtherImages> galleryImages = productimagerepo.findByProduct(product);
        List<String> galleryImageUrls = galleryImages.stream()
                .map(image -> "/productimages/" + image.getProductImage())
                .collect(Collectors.toList());
        model.addAttribute("galleryImageUrls", galleryImageUrls);

        return "admin/apps-ecommerce-edit-product";
    }

    @PostMapping("/apps-ecommerce-edit-product")
    public String saveEditedProduct(@ModelAttribute("productsdto") ProductsDto productsdto,
            @RequestParam(value = "imagesToDelete", required = false) String imagesToDelete,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return "admin/apps-ecommerce-edit-product";
        }

        Products pro = productrepo.findById(productsdto.getProductId()).orElse(null);
        if (pro == null) {
            result.addError(new FieldError("ProductsDto", "productId", "Product not found!"));
            return "admin/apps-ecommerce-edit-product";
        }

        String uploadDir = new File("uploads/productimages").getAbsolutePath();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Handle main product image
        MultipartFile image = productsdto.getProductMainImage();
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
                pro.setProductMainImage(storageFilename);
            } catch (IOException e) {
                result.addError(
                        new FieldError("ProductsDto", "ProductMainImage", "Unable to save the image. Try again."));
                return "admin/apps-ecommerce-edit-product";
            }
        }

        List<ProductOtherImages> existingGalleryImages = productimagerepo.findByProduct(pro);

        // Handle images to delete
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            String[] imagesToDeleteArray = imagesToDelete.split(",");
            for (String imageToDelete : imagesToDeleteArray) {
                // Extract just the filename
                String fileName = Paths.get(imageToDelete).getFileName().toString(); // Extract filename

                ProductOtherImages imageEntity = existingGalleryImages.stream()
                        .filter(img -> img.getProductImage().equals(fileName))
                        .findFirst()
                        .orElse(null);
                if (imageEntity != null) {
                    productimagerepo.delete(imageEntity);
                    try {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Handle new gallery images
        List<MultipartFile> galleryImages = productsdto.getProductOtherImages();
        if (galleryImages != null) {
            for (MultipartFile galleryImage : galleryImages) {
                if (galleryImage != null && !galleryImage.isEmpty()) {
                    String galleryImageFilename = galleryImage.getOriginalFilename();
                    Path targetPath = uploadPath.resolve(galleryImageFilename);

                    if (Files.exists(targetPath)) {
                        ProductOtherImages newImage = new ProductOtherImages();
                        newImage.setProduct(pro);
                        newImage.setProductImage(galleryImageFilename);
                        productimagerepo.save(newImage);
                        continue; 
                    }

                    try {
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                            System.out.println("Created directory: " + uploadPath);
                        }

                        try (InputStream inputStream = galleryImage.getInputStream()) {
                            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        ProductOtherImages newImage = new ProductOtherImages();
                        newImage.setProduct(pro); 
                        newImage.setProductImage(galleryImageFilename); 
                        productimagerepo.save(newImage);
                    } catch (IOException e) {
                        result.addError(new FieldError("ProductsDto", "productOtherImages",
                                "Unable to save the new gallery image. Try again."));
                        return "admin/apps-ecommerce-edit-product";
                    }
                }
            }
        }

        pro.setProductName(productsdto.getProductName());
        pro.setProductPrice(productsdto.getProductPrice());
        pro.setProductDescription(productsdto.getProductDescription());
        pro.setProductCategory(productsdto.getProductCategory());
        pro.setProductQuantity(productsdto.getProductQuantity());
        pro.setProductStatus(productsdto.getProductStatus());
        pro.setCreateTime(productsdto.getCreateTime());

        productrepo.save(pro);

        return "redirect:/admin/apps-ecommerce-products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            boolean isInCart = cartrepo.existsByProductId(id);
            if (isInCart) {
                redirectAttributes.addFlashAttribute("errorMessage", "Product is in a cart and cannot be deleted.");
                return "redirect:/admin/apps-ecommerce-products";
            }

            Products product = productrepo.findById(id).orElse(null);
            List<ProductOtherImages> p = productimagerepo.findByProduct(product);
            for (ProductOtherImages image : p) {
                productimagerepo.delete(image);
            }
            productrepo.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found or already deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the product.");
        }

        return "redirect:/admin/apps-ecommerce-products";
    }

    @PostMapping("/removeImagePreview")
    public String removeImagePreview(@RequestParam("index") int index,
            ProductsDto productsdto,
            Model model) {
        productsdto.getProductOtherImages().remove(index);
        model.addAttribute("productsdto", productsdto);
        return "redirect:/admin/apps-ecommerce-add-product";
    }
}
