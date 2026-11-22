package com.example.electronics_store.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.electronics_store.Model.RegisterDTO;
import com.example.electronics_store.Model.User;
import com.example.electronics_store.Repository.UserRepository;
import com.example.electronics_store.Service.AuthService;

@Controller
@ControllerAdvice
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // Tự động cung cấp tên người dùng đang đăng nhập cho mọi trang HTML
    @ModelAttribute("currentUserFullName")
    public String populateCurrentUser(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByEmail(principal.getName())
                .map(User::getFullName)
                .orElse(principal.getName());
    }

    @GetMapping("/login")
    public String showLoginPage(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "Auth/Login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterDTO());
        }
        return "Auth/Register";
    }

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("registerForm") RegisterDTO registerForm,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registerUser(registerForm);
            redirectAttributes.addFlashAttribute("registerSuccess", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            redirectAttributes.addFlashAttribute("registerForm", registerForm);
            return "redirect:/register";
        }
    }
}