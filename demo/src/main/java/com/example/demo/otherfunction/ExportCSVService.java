package com.example.demo.otherfunction;

import com.example.demo.model.*;

import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ExportCSVService {

    public void exportToCSVforcustomer(HttpServletResponse response, List<Customers> customers) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Customers.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Customer ID,Name,Email,Phone,Address,City,Province");

        // Write data rows
        for (Customers customer : customers) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                    customer.getCustomerId(),
                    customer.getCustomerName(),
                    customer.getCemail(),
                    customer.getCustomerPhone(),
                    customer.getCustomerAddress(),
                    customer.getCustomerCity(),
                    customer.getCustomerProvince());
        }

        writer.flush();
        writer.close();
    }

    public void exportToCSVforproduct(HttpServletResponse response, List<Products> product) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Products.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Product ID,Name,Category,Description,Price,Quantity");

        // Write data rows
        for (Products pro : product) {
            writer.printf("%d,%s,%s,%s,%s,%s%n",
                    pro.getProductId(),
                    pro.getProductName(),
                    pro.getProductCategory(),
                    pro.getProductDescription(),
                    pro.getProductPrice(),
                    pro.getProductQuantity());
        }

        writer.flush();
        writer.close();
    }

    public void exportToCSVforcategory(HttpServletResponse response, List<Categories> categories) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Categories.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Category ID,Name,Quantity");

        // Write data rows
        for (Categories cate : categories) {
            writer.printf("%d,%s,%s%n",
                    cate.getCategoryId(), cate.getCategoryName(), cate.getCategoryQuantity());
        }

        writer.flush();
        writer.close();
    }

    public void exportToCSVforblog(HttpServletResponse response, List<Blogs> blog) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Blogs.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Blog ID,Title, Description, Created Date, Post By, Tag");

        // Write data rows
        for (Blogs bl : blog) {
            writer.printf("%d,%s,%s,%s,%s,%s%n",
                    bl.getBlogId(),
                    bl.getBlogTitle(),
                    bl.getBlogDescription(),
                    bl.getBlogCreateDate(),
                    bl.getBlogPostBy(),
                    bl.getBlogtag());
        }

        writer.flush();
        writer.close();
    }

    public void exportToCSVforadmin(HttpServletResponse response, List<Admin> admin) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"admins.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Admin ID,Admin Email, Admin Name, Admin Address, Admin City, Admin Province, Admin Phone");

        // Write data rows
        for (Admin ad : admin) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                    ad.getAdminId(),
                    ad.getAdminEmail(),
                    ad.getAdminName(),
                    ad.getAdminAddress(),
                    ad.getAdminCity(),
                    ad.getAdminProvince(),
                    ad.getAdminPhone());
        }

        writer.flush();
        writer.close();
    }


    public void exportToCSVfororder(HttpServletResponse response, List<Orders> order) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Orders.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV header
        writer.println("Order ID,Customer Name, Order Date, Address, City, Province, Note, Payment Method, Total Price");

        // Write data rows
        for (Orders od : order) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    od.getOrderId(),
                    od.getCustomer().getCustomerName(),
                    od.getOrderDate(),
                    od.getOrderAddress(),
                    od.getOrderCity(),
                    od.getOrderProvince(),
                    od.getOrderNote(),
                    od.getOrderPaymentMethod(),
                    od.getOrderAmount());
        }

        writer.flush();
        writer.close();
    }
}
