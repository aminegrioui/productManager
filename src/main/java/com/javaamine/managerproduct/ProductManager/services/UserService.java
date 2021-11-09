package com.javaamine.managerproduct.ProductManager.services;

import com.javaamine.managerproduct.ProductManager.controllers.AppController;
import com.javaamine.managerproduct.ProductManager.model.Role;
import com.javaamine.managerproduct.ProductManager.model.User;
import com.javaamine.managerproduct.ProductManager.repository.RoleRepository;
import com.javaamine.managerproduct.ProductManager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private EntityManager en;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getEmployeesOfWeb() {
        return this.getAllUsers().stream().filter(user -> !user.getRoles().contains(new Role("USER"))).collect(Collectors.toList());
    }

    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    public void deleteUser(Long id) {
       User user=this.getUser(id);
       Set<Role> roles=user.getRoles();


        userRepository.deleteById(id);
    }

    public User saveUser(Role role) {
        logger.info("Your new Username: " + genarateUserName());
        logger.info("role from view " + role.getName());
        User user = new User();
        Set<Role> roles = user.getRoles();
        logger.info("initial roles " + roles);
        Role roleDb = roleRepository.getRoleByName(role.getName());
        logger.info("Role aus DB" + roleDb);
        roles.add(roleDb);
        logger.info("end roles " + roles);
        user.setEnabled(true);
        user.setUsername(genarateUserName());
        String password = genaratePassword();
        logger.info("Your new Password: " + password);
        user.setPassword(encodedPassword(password));
        user.setRoles(roles);
        logger.info("end User " + user);
        return userRepository.save(user);
    }

    public User editUser(User user, String roleValue) {
        logger.info("Your new Username", user.getUsername());
        logger.info("Your new Password", user.getPassword());
        User userFromDB = this.getUser(user.getId());
        Set<Role> roles = user.getRoles();
        Role role = roleRepository.getRoleByName(roleValue);
        roles.add(role);
        user.setEnabled(true);
        userFromDB.setUsername(user.getUsername());
        userFromDB.setPassword(this.encodedPassword(user.getPassword()));
        userFromDB.setRoles(roles);
        return userRepository.save(user);
    }


    private String genarateUserName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private String genaratePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String encodedPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        return encodedPassword;
    }
}
