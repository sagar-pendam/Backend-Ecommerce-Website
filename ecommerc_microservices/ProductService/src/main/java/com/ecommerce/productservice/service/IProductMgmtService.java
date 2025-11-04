package com.ecommerce.productservice.service;

import com.ecommerce.productservice.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductMgmtService {
	public Product saveProduct(Product product);
	public List<Product> getAllProducts();
	Optional<Product> getByCode(String code);
    public String saveAllProducts(List<Product> products);
}
