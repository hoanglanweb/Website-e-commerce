package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.Brand;

/**
 * Repository quản lý truy vấn dữ liệu thương hiệu.
 * Spring Data JPA tự động triển khai các phương thức truy vấn.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    // Tìm thương hiệu theo slug.
    Optional<Brand> findBySlug(String slug);

    // Kiểm tra slug đã tồn tại hay chưa.
    boolean existsBySlug(String slug);

    // Tìm thương hiệu theo tên, không phân biệt chữ hoa và chữ thường.
    List<Brand> findByBrandNameContainingIgnoreCase(String brandName);

    // Lấy danh sách thương hiệu theo trạng thái.
    List<Brand> findByStatus(Boolean status);

    // Lấy danh sách thương hiệu theo trạng thái và sắp xếp tên tăng dần.
    List<Brand> findByStatusOrderByBrandNameAsc(Boolean status);

    // Lấy toàn bộ thương hiệu và sắp xếp theo tên tăng dần.
    List<Brand> findAllByOrderByBrandNameAsc();
}
