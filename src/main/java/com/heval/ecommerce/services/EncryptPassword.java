package com.heval.ecommerce.services;
import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.entity.User;
import org.springframework.boot.CommandLineRunner;


public class AdminSeederManual implements CommandLineRunner {
    public static void main(String[] args) {
        User user = new User();
        user.setName("Admin");
        user.setEmail("admin@example.com");
        user.setPassword("123456");
        user.setRole(UserRole.ADMIN);

        System.out.println("Insert manually in DB:");
        System.out.println(user);
    }
}
