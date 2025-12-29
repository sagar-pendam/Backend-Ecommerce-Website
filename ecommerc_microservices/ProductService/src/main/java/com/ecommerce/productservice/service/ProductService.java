package com.ecommerce.productservice.service;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.productservice.exception.ProductNotFound;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.respository.ProductRepository;
import com.ecommerce.productservice.rest.ProductController;


@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    public Product saveProduct(Product product) {
    	 logger.debug("Saving single product: {}", product.getProductCode());
        return repo.save(product);
    }

    public List<Product> getAllProducts() {
    	logger.debug("Fetching all products");
        return repo.findAll();
    }

    public Product getByCode(String code) {

        logger.debug("Searching product by code {}", code);
      Optional<Product> product = repo.findByProductCode(code);
      if (product.isPresent()) {
          return product.get();
      }
      else{
          throw new ProductNotFound("Product not found with code :"+code);
      }
    }

    public String saveAllProducts(List<Product> products)
    {
    	logger.debug("Saving {} products", products.size());
        List<Product> products1 = repo.saveAll(products);
        return "All products saved successfully";
    }
}
