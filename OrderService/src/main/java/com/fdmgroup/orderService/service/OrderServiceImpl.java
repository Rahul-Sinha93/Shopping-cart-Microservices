package com.fdmgroup.orderService.service;

import com.fdmgroup.orderService.entity.Order;
import com.fdmgroup.orderService.exception.CustomException;
import com.fdmgroup.orderService.external.client.PaymentService;
import com.fdmgroup.orderService.external.client.ProductService;
import com.fdmgroup.orderService.external.request.PaymentRequest;
import com.fdmgroup.orderService.external.response.PaymentResponse;
import com.fdmgroup.orderService.external.response.ProductResponse;
import com.fdmgroup.orderService.model.OrderRequest;
import com.fdmgroup.orderService.model.OrderResponse;
import com.fdmgroup.orderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Product Service -> Block Products( Reduce the Quantity)
        log.info("Reducing the quantity {} of product with id {}", orderRequest.getQuantity(), orderRequest.getProductId() );
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        //Order Entity -> Save the data with Status Order Created
        log.info("Placoing the Order Request: {}", orderRequest);
        Order order = Order.builder()
                        .amount(orderRequest.getTotalAmount())
                        .orderStatus("CREATED")
                        .productId(orderRequest.getProductId())
                        .orderDate(Instant.now())
                        .quantity(orderRequest.getQuantity())
                        .build();
            order = orderRepository.save(order);


        //Payment Service -> Payments -> Success -> complete, Else -> Cancelled
        log.info("Calling the payment service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully. Changing the order status to PLACED");
            orderStatus = "PLACED";
        }catch (Exception e){
            log.error("Error occured in payment. Changing the order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Placed successfully with id: {}", order.getId());
        return  order.getId();

    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Getting the order with id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException("Order not found for the order Id:"+ orderId,"NOT_FOUND",404));
        log.info("Invoking Product service to fetch the product details for the id: {}", order.getProductId());

        ProductResponse productResponse = restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+ order.getProductId(), ProductResponse.class);

        log.info("Getting Payment Details from the PaymentDetails Service using the order Id");

        PaymentResponse paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+ order.getId(), PaymentResponse.class);

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .price(productResponse.getPrice())
                .quantity(order.getQuantity())
                .build();

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .amount(paymentResponse.getAmount())
                .paymentDate(paymentResponse.getPaymentDate())
                .status(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();


        OrderResponse orderResponse = OrderResponse.builder()
                .amount(order.getAmount())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .orderId(order.getId())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        log.info("Successfully got the order details for id: {}", orderId);
        return orderResponse;
    }
}
