package com.javaamine.managerproduct.ProductManager.repository;

import com.javaamine.managerproduct.ProductManager.model.Role;
import com.javaamine.managerproduct.ProductManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {

    List<Role> findByName(String name);
}
