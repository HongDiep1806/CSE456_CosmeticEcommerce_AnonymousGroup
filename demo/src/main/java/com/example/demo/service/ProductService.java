package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired private productrepository productrepo;
    @Autowired private productimagesrepository productimagerepo;
    @Autowired private cartrepository cartrepo;
    @Autowired private categoriesrepository caterepo;

    private final Path uploadPath = Paths.get(new java.io.File("uploads/productimages").getAbsolutePath());

    private void ensureUploadDir(BindingResult result, String fieldName) {
        try {
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        } catch (Exception e) {
            result.addError(new FieldError("productsdto", fieldName, "Unable to create upload directory."));
        }
    }

    private String saveFile(MultipartFile file, BindingResult result, String fieldName) {
        if (file == null || file.isEmpty()) return null;
        try {
            ensureUploadDir(result, fieldName);
            if (result.hasErrors()) return null;
            String filename = file.getOriginalFilename();
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (Exception e) {
            result.addError(new FieldError("productsdto", fieldName, "Unable to save the image."));
            return null;
        }
    }

    // ====== Query data cho Controller ======
    public List<products> getAllProducts() {
        return (List<products>) productrepo.findAll();
    }

    public List<Integer> getProductIdsInCart() {
        List<Integer> ids = new ArrayList<>();
        cartrepo.findAll().forEach(c -> ids.add(c.getProduct().getProductId()));
        return ids;
    }

    public List<categories> getActiveCategories() {
        return caterepo.findAllByIsActiveTrue();
    }

    public int getNextProductId() {
        return productrepo.findNextProductId();
    }

    // ====== Tạo mới sản phẩm ======
    public products create(productsdto dto, BindingResult result) {
        if (result.hasErrors()) return null;

        if (dto.getProductMainImage() == null || dto.getProductMainImage().isEmpty()) {
            result.addError(new FieldError("productsdto", "ProductMainImage", "ProductMainImage is required"));
            return null;
        }

        String mainImg = saveFile(dto.getProductMainImage(), result, "ProductMainImage");
        if (mainImg == null) return null;

        products pro = new products();
        pro.setProductId(dto.getProductId());
        pro.setProductName(dto.getProductName());
        pro.setProductPrice(dto.getProductPrice());
        pro.setProductDescription(dto.getProductDescription());
        pro.setProductCategory(dto.getProductCategory());
        pro.setProductQuantity(dto.getProductQuantity());
        pro.setProductMainImage(mainImg);
        pro.setProductStatus(dto.getProductStatus());

        products saved = productrepo.save(pro);

        List<MultipartFile> gallery = dto.getProductOtherImages();
        if (gallery != null && !gallery.isEmpty()) {
            for (MultipartFile g : gallery) {
                if (g == null || g.isEmpty()) continue;
                String file = saveFile(g, result, "productOtherImages");
                if (file == null) continue;
                productotherimages img = new productotherimages();
                img.setProduct(saved);
                img.setProductImage(file);
                productimagerepo.save(img);
            }
        }
        return saved;
    }

    // ====== Build dữ liệu cho trang Edit ======
    public productsdto buildEditDto(int productId) {
        products p = productrepo.findById(productId).orElse(null);
        if (p == null) return null;
        productsdto dto = new productsdto();
        dto.setProductId(p.getProductId());
        dto.setProductName(p.getProductName());
        dto.setProductPrice(p.getProductPrice());
        dto.setProductDescription(p.getProductDescription());
        dto.setProductCategory(p.getProductCategory());
        dto.setProductQuantity(p.getProductQuantity());
        dto.setProductStatus(p.getProductStatus());
        dto.setCreateTime(p.getCreateTime());
        return dto;
    }

    public String getExistingMainImageUrl(int productId) {
        products p = productrepo.findById(productId).orElse(null);
        return (p == null || p.getProductMainImage() == null) ? null : "/productimages/" + p.getProductMainImage();
    }

    public List<String> getGalleryImageUrls(int productId) {
        products p = productrepo.findById(productId).orElse(null);
        if (p == null) return List.of();
        return productimagerepo.findByProduct(p)
                .stream().map(i -> "/productimages/" + i.getProductImage()).collect(Collectors.toList());
    }

    // ====== Cập nhật sản phẩm ======
    public products update(productsdto dto, String imagesToDelete, BindingResult result) {
        if (result.hasErrors()) return null;

        products pro = productrepo.findById(dto.getProductId()).orElse(null);
        if (pro == null) {
            result.addError(new FieldError("productsdto", "productId", "Product not found!"));
            return null;
        }

        // ảnh chính
        if (dto.getProductMainImage() != null && !dto.getProductMainImage().isEmpty()) {
            String main = saveFile(dto.getProductMainImage(), result, "ProductMainImage");
            if (main == null) return null;
            pro.setProductMainImage(main);
        }

        // xóa ảnh gallery
        if (imagesToDelete != null && !imagesToDelete.isBlank()) {
            List<productotherimages> cur = productimagerepo.findByProduct(pro);
            for (String token : imagesToDelete.split(",")) {
                String fileName = Paths.get(token.trim()).getFileName().toString();
                productotherimages found = cur.stream()
                        .filter(i -> fileName.equals(i.getProductImage())).findFirst().orElse(null);
                if (found != null) {
                    productimagerepo.delete(found);
                    try { Files.deleteIfExists(uploadPath.resolve(fileName)); } catch (Exception ignored) {}
                }
            }
        }

        // thêm ảnh gallery
        if (dto.getProductOtherImages() != null) {
            for (MultipartFile g : dto.getProductOtherImages()) {
                if (g == null || g.isEmpty()) continue;
                String file = saveFile(g, result, "productOtherImages");
                if (file == null) continue;
                productotherimages img = new productotherimages();
                img.setProduct(pro);
                img.setProductImage(file);
                productimagerepo.save(img);
            }
        }

        // update các field còn lại
        pro.setProductName(dto.getProductName());
        pro.setProductPrice(dto.getProductPrice());
        pro.setProductDescription(dto.getProductDescription());
        pro.setProductCategory(dto.getProductCategory());
        pro.setProductQuantity(dto.getProductQuantity());
        pro.setProductStatus(dto.getProductStatus());
        pro.setCreateTime(dto.getCreateTime());

        return productrepo.save(pro);
    }

    // ====== Xóa sản phẩm ======
    public boolean delete(int productId) throws Exception {
        if (cartrepo.existsByProductId(productId)) return false;

        products product = productrepo.findById(productId).orElse(null);
        if (product == null) throw new EmptyResultDataAccessException(1);

        // xóa gallery DB + file
        List<productotherimages> pimgs = productimagerepo.findByProduct(product);
        for (productotherimages img : pimgs) {
            productimagerepo.delete(img);
            try { Files.deleteIfExists(uploadPath.resolve(img.getProductImage())); } catch (Exception ignored) {}
        }
        // xóa ảnh chính
        try { Files.deleteIfExists(uploadPath.resolve(product.getProductMainImage())); } catch (Exception ignored) {}

        productrepo.deleteById(productId);
        return true;
    }
}
