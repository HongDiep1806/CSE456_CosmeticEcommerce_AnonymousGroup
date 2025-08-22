package com.example.demo.controller.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.*;
import com.example.demo.otherfunction.ExportCSVService;
import com.example.demo.repository.*;


import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class ExportController {

    @Autowired
    private ExportCSVService exportService;

    @Autowired
    private CustomerRepository customerrepo;

    @Autowired
    private ProductRepository productrepo;

    @Autowired
    private CategoriesRepository caterepo;

    @Autowired
    private BlogRepository blogrepo;

    @Autowired
    private AdminRepository adminrepo;

    @Autowired
    private OrderRepository orderrepo;

    @GetMapping("/export/customers/csv")
    public void exportCSVcustomer(HttpServletResponse response) throws IOException {
        List<Customers> customers = (List<Customers>) customerrepo.findAll();
        exportService.exportToCSVforcustomer(response, customers);
    }


    @GetMapping("/export/products/csv")
    public void exportCSVproduct(HttpServletResponse response) throws IOException {
        List<Products> pro = (List<Products>) productrepo.findAll();
        exportService.exportToCSVforproduct(response, pro);
    }

    @GetMapping("/export/categories/csv")
    public void exportCSVcategory(HttpServletResponse response) throws IOException {
        List<Categories> cate = (List<Categories>) caterepo.findAll();
        exportService.exportToCSVforcategory(response, cate);
    }

    @GetMapping("/export/blogs/csv")
    public void exportCSVblog(HttpServletResponse response) throws IOException {
        List<Blogs> bl = (List<Blogs>) blogrepo.findAll();
        exportService.exportToCSVforblog(response, bl);
    }

    @GetMapping("/export/admins/csv")
    public void exportCSVadmin(HttpServletResponse response) throws IOException {
        List<Admin> ad = (List<Admin>) adminrepo.findAll();
        exportService.exportToCSVforadmin(response, ad);
    }

    @GetMapping("/export/orders/csv")
    public void exportCSVorder(HttpServletResponse response) throws IOException {
        List<Orders> od = (List<Orders>) orderrepo.findAll();
        exportService.exportToCSVfororder(response, od);
    }
}
