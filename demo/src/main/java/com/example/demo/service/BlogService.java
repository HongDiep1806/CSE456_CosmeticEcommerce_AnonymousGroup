package com.example.demo.service;

import com.example.demo.model.blogs;
import com.example.demo.model.blogsdto;
import com.example.demo.repository.adminrepository;
import com.example.demo.repository.blogrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    @Autowired
    public blogrepository blogrepo;
    @Autowired
    public adminrepository adminrepo;
    public List<blogs> findAll(){
        return(List<blogs>) blogrepo.findAll();
    }
    public int findNextBlogId(){
        return blogrepo.findNextBlogId();
    }
    public blogs saveNewBlog(blogsdto dto, Integer adminId, Integer superId, String storageFilename) {
        blogs bl = new blogs();
        bl.setBlogId(dto.getBlogId());
        bl.setBlogTitle(dto.getBlogTitle());
        bl.setBlogDescription(dto.getBlogDescription());
        bl.setBlogStatus(dto.getBlogStatus());
        bl.setBlogCreateDate(dto.getBlogCreateDate());
        bl.setBlogtag(dto.getBlogtag());
        bl.setBlogImage(storageFilename);
        if (adminId != null) {
            bl.setBlogPostBy(adminrepo.findById(adminId).orElseThrow().getAdminName());
        } else if (superId != null) {
            bl.setBlogPostBy(adminrepo.findById(superId).orElseThrow().getAdminName());
        }

        return blogrepo.save(bl);
    }
    public blogsdto getBlogDtoById(Integer blogId) {
        blogs blog = blogrepo.findById(blogId).orElse(null);
        if (blog == null) {
            return null;
        }

        blogsdto dto = new blogsdto();
        dto.setBlogId(blog.getBlogId());
        dto.setBlogTitle(blog.getBlogTitle());
        dto.setBlogDescription(blog.getBlogDescription());
        dto.setBlogStatus(blog.getBlogStatus());
        dto.setBlogCreateDate(blog.getBlogCreateDate());
        dto.setBlogPostBy(blog.getBlogPostBy());
        dto.setBlogtag(blog.getBlogtag());

        return dto;
    }
    public String getBlogImageFilename(Integer blogId) {
        return blogrepo.findById(blogId)
                .map(blogs::getBlogImage)
                .orElse(null);
    }
    public boolean updateBlog(blogsdto dto, Integer adminId, Integer superId, BindingResult result) {
        Optional<blogs> optionalBlog = blogrepo.findById(dto.getBlogId());
        if (optionalBlog.isEmpty()) {
            result.addError(new FieldError("blogsdto", "blogId", "Blog not found!"));
            return false;
        }

        blogs bl = optionalBlog.get();

        MultipartFile image = dto.getBlogImage();
        if (image != null && !image.isEmpty()) {
            String uploadDir = new File("uploads/blogimages").getAbsolutePath();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String storageFilename = image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(storageFilename), StandardCopyOption.REPLACE_EXISTING);
                    bl.setBlogImage(storageFilename); // chỉ set nếu upload thành công
                }

            } catch (IOException e) {
                result.addError(new FieldError("blogsdto", "BlogImage", "Unable to save the image. Try again."));
                return false;
            }
        }
        bl.setBlogTitle(dto.getBlogTitle());
        bl.setBlogDescription(dto.getBlogDescription());
        bl.setBlogStatus(dto.getBlogStatus());
        bl.setBlogCreateDate(dto.getBlogCreateDate());
        bl.setBlogtag(dto.getBlogtag());

        if (adminId != null) {
            bl.setBlogPostBy(adminrepo.findById(adminId).orElseThrow().getAdminName());
        } else if (superId != null) {
            bl.setBlogPostBy(adminrepo.findById(superId).orElseThrow().getAdminName());
        }

        blogrepo.save(bl);
        return true;
    }
    public boolean deleteBlogById(int id) {
        Optional<blogs> optionalBlog = blogrepo.findById(id);

        if (optionalBlog.isEmpty()) {
            return false;
        }

        blogs blog = optionalBlog.get();
        if (blog.getBlogImage() != null && !blog.getBlogImage().isBlank()) {
            String imagePath = new File("uploads/blogimages", blog.getBlogImage()).getAbsolutePath();
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    System.err.println("Không thể xóa ảnh: " + imagePath);
                }
            }
        }
        blogrepo.deleteById(id);
        return true;
    }


}
