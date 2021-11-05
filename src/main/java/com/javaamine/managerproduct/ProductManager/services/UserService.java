package com.javaamine.managerproduct.ProductManager.services;

import com.javaamine.managerproduct.ProductManager.model.Role;
import com.javaamine.managerproduct.ProductManager.model.User;
import com.javaamine.managerproduct.ProductManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<User> getEmployeesOfWeb(){
        return this.getAllUsers().stream().filter(user->!user.getRoles().contains(new Role("USER"))).collect(Collectors.toList());
    }
}
