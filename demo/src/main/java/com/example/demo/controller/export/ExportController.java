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
    private customerrepository customerrepo;

    @Autowired
    private productrepository productrepo;

    @Autowired
    private categoriesrepository caterepo;

    @Autowired
    private blogrepository blogrepo;

    @Autowired
    private adminrepository adminrepo;

    @Autowired
    private orderrepository orderrepo;

    @GetMapping("/export/customers/csv")
    public void exportCSVcustomer(HttpServletResponse response) throws IOException {
        List<customers> customers = (List<customers>) customerrepo.findAll();
        exportService.exportToCSVforcustomer(response, customers);
    }


    @GetMapping("/export/products/csv")
    public void exportCSVproduct(HttpServletResponse response) throws IOException {
        List<products> pro = (List<products>) productrepo.findAll();
        exportService.exportToCSVforproduct(response, pro);
    }

    @GetMapping("/export/categories/csv")
    public void exportCSVcategory(HttpServletResponse response) throws IOException {
        List<categories> cate = (List<categories>) caterepo.findAll();
        exportService.exportToCSVforcategory(response, cate);
    }

    @GetMapping("/export/blogs/csv")
    public void exportCSVblog(HttpServletResponse response) throws IOException {
        List<blogs> bl = (List<blogs>) blogrepo.findAll();
        exportService.exportToCSVforblog(response, bl);
    }

    @GetMapping("/export/admins/csv")
    public void exportCSVadmin(HttpServletResponse response) throws IOException {
        List<admin> ad = (List<admin>) adminrepo.findAll();
        exportService.exportToCSVforadmin(response, ad);
    }

    @GetMapping("/export/orders/csv")
    public void exportCSVorder(HttpServletResponse response) throws IOException {
        List<orders> od = (List<orders>) orderrepo.findAll();
        exportService.exportToCSVfororder(response, od);
    }
}
