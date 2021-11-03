package com.javaamine.managerproduct.ProductManager.controllers;

import com.javaamine.managerproduct.ProductManager.model.Product;
import com.javaamine.managerproduct.ProductManager.model.Role;
import com.javaamine.managerproduct.ProductManager.model.User;
import com.javaamine.managerproduct.ProductManager.repository.RoleRepository;
import com.javaamine.managerproduct.ProductManager.repository.UserRepository;
import com.javaamine.managerproduct.ProductManager.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
public class AppController {
    Logger logger = LoggerFactory.getLogger(AppController.class);
    @Autowired
    private ProductService service;

    @Autowired
    private EntityManager en;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping("/")
    public String viewHomePage(Model model) {
        List<Product> listProducts = service.listAll();
        model.addAttribute("listProducts", listProducts);

        return "index";
    }

    @RequestMapping("/new")
    public String showNewProductPage(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);

        return "new_product";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("product") Product product) {
        service.save(product);

        return "redirect:/";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_product");
        Product product = service.get(id);
        mav.addObject("product", product);

        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") int id) {
        service.delete(id);
        return "redirect:/";
    }

    @GetMapping("/403")
    public String error403(){
        return "403";
    }
    @GetMapping("/login")
    public String home(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }

    @PostMapping("/process_register")
    @Transactional
    public String processRegister(User user) {

        Set<Role> roles = new HashSet<>();
        List<Role> roleList= roleRepository.findAll();
        Role role=null;
        for(int i=0;i<roleList.size();i++){
            if(roleList.get(i).getName().equals("USER")){
                role=roleList.get(i);
                break;  
            }
        }
        System.out.println(role);
        roles.add(role);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRoles(roles);
        userRepository.save(user);
        Query q=en.createNativeQuery("INSERT INTO saledb.users_roles (user_id, role_id)  VALUES (?, ?)");
        q.setParameter(1,user.getId());
        q.setParameter(2,1);
        q.executeUpdate();
        return "register_success";
    }



}
