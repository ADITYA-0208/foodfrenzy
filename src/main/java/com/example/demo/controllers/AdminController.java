package com.example.demo.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.count.*;
import com.example.demo.entities.*;
import com.example.demo.loginCredentials.*;
import com.example.demo.services.*;
import jakarta.validation.Valid;

@Controller
public class AdminController {

    @Autowired
    private UserServices services;
    @Autowired
    private AdminServices adminServices;
    @Autowired
    private ProductServices productServices;
    @Autowired
    private OrderServices orderServices;

    private String email;
    private User user;

    @PostMapping("/adminLogin")
    public String adminLogin(@ModelAttribute("adminLogin") AdminLogin login, Model model) {
        email = login.getEmail();
        String password = login.getPassword();
        if (adminServices.validateAdminCredentials(email, password)) {
            return "redirect:/admin/services";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "Login";
        }
    }

    @PostMapping("/userLogin")
    public String userLogin(@ModelAttribute("userLogin") UserLogin login, Model model) {
        email = login.getUserEmail();
        String password = login.getUserPassword();
        if (services.validateLoginCredentials(email, password)) {
            user = services.getUserByEmail(email);
            List<Orders> orders = orderServices.getOrdersForUser(user);
            model.addAttribute("orders", orders);
            model.addAttribute("name", user.getUname());
            return "BuyProduct";
        } else {
            model.addAttribute("error2", "Invalid email or password");
            return "Login";
        }
    }

    @PostMapping("/product/search")
    public String searchHandler(@RequestParam("productName") String name, Model model) {
        Product product = productServices.getProductByName(name);
        if (product == null) {
            model.addAttribute("message", "SORRY...! Product Unavailable");
        } else {
            model.addAttribute("product", product);
        }
        List<Orders> orders = orderServices.getOrdersForUser(user);
        model.addAttribute("orders", orders);
        return "BuyProduct";
    }

    @GetMapping("/admin/services")
    public String adminServices(Model model) {
        List<User> users = services.getAllUser();
        List<Admin> admins = adminServices.getAll();
        List<Product> products = productServices.getAllProducts();
        List<Orders> orders = orderServices.getOrders();
        model.addAttribute("users", users);
        model.addAttribute("admins", admins);
        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        return "Admin_Page";
    }

    @GetMapping("/addAdmin")
    public String addAdminPage() {
        return "Add_Admin";
    }

    @PostMapping("/addingAdmin")
    public String addAdmin(@ModelAttribute Admin admin) {
        adminServices.addAdmin(admin);
        return "redirect:/admin/services";
    }

    @GetMapping("/updateAdmin/{adminId}")
    public String updateAdminPage(@PathVariable("adminId") int id, Model model) {
        Admin admin = adminServices.getAdmin(id);
        model.addAttribute("admin", admin);
        return "Update_Admin";
    }

    @PostMapping("/updatingAdmin/{id}")
    public String updateAdmin(@ModelAttribute Admin admin, @PathVariable("id") int id) {
        adminServices.update(admin, id);
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteAdmin/{id}")
    public String deleteAdmin(@PathVariable("id") int id) {
        adminServices.delete(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/addProduct")
    public String addProductPage() {
        return "Add_Product";
    }

    @PostMapping("/addingProduct")
    public String addProduct(@ModelAttribute Product product) {
        productServices.addProduct(product);
        return "redirect:/admin/services";
    }

    @GetMapping("/updateProduct/{productId}")
    public String updateProductPage(@PathVariable("productId") int id, Model model) {
        Product product = productServices.getProduct(id);
        model.addAttribute("product", product);
        return "Update_Product";
    }

    @PostMapping("/updatingProduct/{id}")
    public String updateProduct(@ModelAttribute Product product, @PathVariable("id") int id) {
        productServices.update(product, id);
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productServices.delete(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/addUser")
    public String addUserPage() {
        return "Add_User";
    }

    @PostMapping("/addingUser")
    public String addUser(@ModelAttribute User user) {
        services.addUser(user);
        return "redirect:/admin/services";
    }

    @GetMapping("/updateUser/{userId}")
    public String updateUserPage(@PathVariable("userId") int id, Model model) {
        User user = services.getUser(id);
        model.addAttribute("user", user);
        return "Update_User";
    }

    
    @PostMapping("/product/order")
    public String orderHandler(@ModelAttribute Orders order, Model model) {
        double totalAmount = Logic.countTotal(order.getoPrice(), order.getoQuantity());
        order.setTotalAmmout(totalAmount);
        order.setUser(user);
        order.setOrderDate(new Date());
        orderServices.saveOrder(order);
        model.addAttribute("amount", totalAmount);
        return "Order_success";
    }

    @GetMapping("/product/back")
    public String backToBuyProductPage(Model model) {
        List<Orders> orders = orderServices.getOrdersForUser(user);
        model.addAttribute("orders", orders);
        return "BuyProduct";
    }
}
