package com.javaamine.managerproduct.ProductManager.controllers;

import com.javaamine.managerproduct.ProductManager.model.Product;
import com.javaamine.managerproduct.ProductManager.model.Role;
import com.javaamine.managerproduct.ProductManager.model.User;
import com.javaamine.managerproduct.ProductManager.repository.RoleRepository;
import com.javaamine.managerproduct.ProductManager.repository.UserRepository;
import com.javaamine.managerproduct.ProductManager.services.ProductService;
import com.javaamine.managerproduct.ProductManager.services.UserService;
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
    private UserService userService;

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
    public String error403() {
        return "403";
    }

    @GetMapping("/login")
    public String home() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signup_form";
    }

    @PostMapping("/process_register")
    @Transactional
    public String processRegister(User user, Model model) {
        if (userRepository.getUserByUsername(user.getUsername()) != null) {
            model.addAttribute("errorMessage", "Login Failed");
            return "signup_form";
        }
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.getRoleByName("USER");
        roles.add(role);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRoles(roles);
        userRepository.save(user);
       /*
            @ManyMany adds the primary automaticly to users_roles
       Query q=en.createNativeQuery("INSERT INTO saledb.users_roles (user_id, role_id)  VALUES (?, ?)");
        q.setParameter(1,user.getId());
        q.setParameter(2,role.getId());
        q.executeUpdate();*/
        return "register_success";
    }

    @GetMapping("/configure")
    public String getAllUsers(Model model) {
        boolean enable = true;
        List<User> usersEmployees = userService.getEmployeesOfWeb();
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("index", enable);
        return "administration";
    }

    @GetMapping("/configure/newEmployee")
    public String showNewEmployeePage(Model model) {
        Role role = new Role();
        model.addAttribute("role", role);
        return "new_employee";
    }

    @PostMapping("/configure/save")
    public String saveNewEmployee(@ModelAttribute("role") Role role,Model model) {
        System.out.println(role);
        User user=userService.saveUser(role);
        logger.info("user /configure/save "+user.getId());
        model.addAttribute("user",user);
        return "reset_password_username";
    }

    @PostMapping("/configure/resetUsernameAndPassword")
    public String resetUserNameAndPassword(User user,Model model) {
        logger.info("/configure/resetUsernameAndPassword "+ user);
        if (userRepository.getUserByUsername(user.getUsername()) != null) {
            model.addAttribute("errorMessage", "This username is already used");
            return "reset_password_username";
        }
        User userDb=userRepository.getById(user.getId());
        userDb.setUsername(user.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        userDb.setPassword(encodedPassword);
        userRepository.save(userDb);
        return "redirect:/login";
    }

    @PostMapping("/configure/edit")
    public String editEmployee(@ModelAttribute("user") User user, @ModelAttribute("role") Role role) {
        System.out.println(user.getUsername());
        System.out.println(role);
        //  userService.editUser(user,role);
        return "redirect:/configure";
    }

    @RequestMapping("/configure/edit/{id}")
    public ModelAndView showEditUserPage(@PathVariable(name = "id") Long id, Model model) {
        ModelAndView mav = new ModelAndView("edit_user");
        User user = userService.getUser(id);
        mav.addObject("user", user);
        /*Role role=new Role();
        model.addAttribute("role", role);*/
        return mav;
    }

    @RequestMapping("/configure/delete/{id}")
    public String deleteUserOrEmployee(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/configure";
    }

    @RequestMapping("/changePasswordPage")
    public String changePasswordPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        return "forget_password";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String editPassword(@ModelAttribute("user")  User user, Model model) {
        User userDb = userRepository.getUserByUsername(user.getUsername());



        if (userDb == null) {
            model.addAttribute("usernameNotFound", "This username is not found !! try again");
            return "forget_password";
        }
        logger.info(user.toString());
        logger.info(userDb.toString());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        userDb.setPassword(encodedPassword);
        logger.info(userDb.toString());
        userRepository.save(userDb);
        return "redirect: /login";
    }


}
