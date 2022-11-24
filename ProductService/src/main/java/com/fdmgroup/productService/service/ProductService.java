package com.fdmgroup.productService.service;

import com.fdmgroup.productService.model.ProductRequest;
import com.fdmgroup.productService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
