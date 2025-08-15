package com.example.demo.model;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

public class blogsdto {

    private int BlogId;

    private String BlogTitle;

    private String BlogDescription;

    private String BlogStatus;

    private Date BlogCreateDate;

    private String BlogPostBy;

    private String Blogtag;

    private MultipartFile BlogImage;

    public int getBlogId() {
        return BlogId;
    }

    public void setBlogId(int blogId) {
        BlogId = blogId;
    }

    public String getBlogTitle() {
        return BlogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        BlogTitle = blogTitle;
    }

    public String getBlogDescription() {
        return BlogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        BlogDescription = blogDescription;
    }

    public String getBlogStatus() {
        return BlogStatus;
    }

    public void setBlogStatus(String blogStatus) {
        BlogStatus = blogStatus;
    }

    public Date getBlogCreateDate() {
        return BlogCreateDate;
    }

    public void setBlogCreateDate(Date blogCreateDate) {
        BlogCreateDate = blogCreateDate;
    }

    public String getBlogPostBy() {
        return BlogPostBy;
    }

    public void setBlogPostBy(String blogPostBy) {
        BlogPostBy = blogPostBy;
    }

    public String getBlogtag() {
        return Blogtag;
    }

    public void setBlogtag(String blogtag) {
        Blogtag = blogtag;
    }

    public MultipartFile getBlogImage() {
        return BlogImage;
    }

    public void setBlogImage(MultipartFile blogImage) {
        BlogImage = blogImage;
    }

}
