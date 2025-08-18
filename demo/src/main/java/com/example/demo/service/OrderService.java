package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired private orderrepository orderrepo;
    @Autowired private orderdetailrepository orderdetailsrepo;
    @Autowired private productrepository productrepo;
    @Autowired private customerrepository customerrepo;

    // ViewModel gộp dữ liệu cho trang order details
    public static class OrderDetailsVM {
        private customers customer;
        private orders order;
        private List<orderdetailsdto> items;

        public OrderDetailsVM(customers customer, orders order, List<orderdetailsdto> items) {
            this.customer = customer;
            this.order = order;
            this.items = items;
        }
        public customers getCustomer() { return customer; }
        public orders getOrder() { return order; }
        public List<orderdetailsdto> getItems() { return items; }
    }

    /** Lấy dữ liệu hiển thị chi tiết đơn hàng (customer, order, list item DTO) */
    public OrderDetailsVM buildOrderDetails(int orderId) {
        orders order = orderrepo.findById(orderId).orElse(null);
        if (order == null) return null;

        customers customer = customerrepo.findById(order.getCustomer().getCustomerId()).orElse(null);

        List<orderdetails> detailEntities = orderdetailsrepo.findByOrderId(orderId);
        List<orderdetailsdto> detailDtos = new ArrayList<>();
        for (orderdetails d : detailEntities) {
            products p = productrepo.findById(d.getProductId()).orElse(null);
            if (p != null) {
                orderdetailsdto dto = new orderdetailsdto();
                dto.setProductId(p.getProductId());
                dto.setProductName(p.getProductName());
                dto.setProductMainImage(p.getProductMainImage());
                dto.setQuantity(d.getProductQuantity());
                dto.setProductPrice(p.getProductPrice());
                dto.setTotalPrice(d.getProductPrice() * d.getProductQuantity());
                detailDtos.add(dto);
            }
        }

        return new OrderDetailsVM(customer, order, detailDtos);
    }

    /** Danh sách tất cả orders */
    public List<orders> getAllOrders() {
        return (List<orders>) orderrepo.findAll();
    }

    /** Cập nhật trạng thái đơn hàng */
    public boolean updateOrderStatus(ordersdto dto, BindingResult result) {
        Integer orderId = dto.getOrderId();
        orders existing = orderrepo.findById(orderId).orElse(null);
        if (existing == null) {
            result.addError(new FieldError("ordersdto", "OrderId", "Order not found!"));
            return false;
        }
        existing.setOrderStatus(dto.getOrderStatus());
        orderrepo.save(existing);
        return true;
    }
}
