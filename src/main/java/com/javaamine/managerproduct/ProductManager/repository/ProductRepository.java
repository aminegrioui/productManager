package com.javaamine.managerproduct.ProductManager.repository;

import com.javaamine.managerproduct.ProductManager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
