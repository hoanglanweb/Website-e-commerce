package com.example.electronics_store.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.electronics_store.Model.RegisterDTO;
import com.example.electronics_store.Model.User;
import com.example.electronics_store.Repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(RegisterDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập họ và tên.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập địa chỉ email.");
        }

        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email này đã được sử dụng. Vui lòng chọn email khác.");
        }

        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }

        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("CUSTOMER");
        user.setStatus(true);

        userRepository.save(user);
    }
}