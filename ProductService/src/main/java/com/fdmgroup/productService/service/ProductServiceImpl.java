package com.fdmgroup.productService.service;

import com.fdmgroup.productService.entity.Product;
import com.fdmgroup.productService.exception.ProductServiceCustomException;
import com.fdmgroup.productService.model.ProductRequest;
import com.fdmgroup.productService.model.ProductResponse;
import com.fdmgroup.productService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product Added successfully");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Getting the Product with Product Id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(
                                 ()-> new ProductServiceCustomException("Product with given Id not found", "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {

        log.info("Reducing the quantity {} for the Product Id {}",quantity,productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductServiceCustomException("Product with the given id not found", "PRODUCT_NOT_FOUND"));

        if(product.getQuantity() < quantity){
            throw new ProductServiceCustomException(
                    "Product does not have sufficient quantity",
                    "INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        log.info("Product Quantity Updated Successfully");
    }
}
