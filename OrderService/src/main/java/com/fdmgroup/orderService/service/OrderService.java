package com.fdmgroup.orderService.service;

import com.fdmgroup.orderService.model.OrderRequest;
import com.fdmgroup.orderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
