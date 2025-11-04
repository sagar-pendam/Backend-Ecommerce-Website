package com.ecommerce.productservice.service;
import java.util.List;
import java.util.Optional;

import com.ecommerce.productservice.exception.ProductNotFound;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.respository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    public Product saveProduct(Product product) {
        return repo.save(product);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getByCode(String code) {
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
        List<Product> products1 = repo.saveAll(products);
        return "All products saved successfully";
    }
}
