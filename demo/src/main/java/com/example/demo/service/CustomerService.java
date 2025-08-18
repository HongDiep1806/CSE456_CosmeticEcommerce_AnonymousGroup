package com.example.demo.service;

import com.example.demo.model.customers;
import com.example.demo.model.customersdto;
import com.example.demo.repository.customerrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private customerrepository customerrepo;

    /** Lấy toàn bộ khách hàng cho trang list */
    public List<customers> getAllCustomers() {
        return (List<customers>) customerrepo.findAll();
    }

    /** Cập nhật status khách hàng từ DTO */
    public boolean updateCustomerStatus(customersdto dto, BindingResult result) {
        if (result.hasErrors()) return false;

        Integer customerId = dto.getCustomerId();
        customers existing = customerrepo.findById(customerId).orElse(null);
        if (existing == null) {
            result.addError(new FieldError("customersdto", "CustomerId", "Customer not found!"));
            return false;
        }

        existing.setCustomerStatus(dto.getCustomerStatus());
        customerrepo.save(existing);
        return true;
    }
}
