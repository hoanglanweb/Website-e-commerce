
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductDescription;

/**
 * Repository quản lý nội dung mô tả sản phẩm.
 */
@Repository
public interface ProductDescriptionRepository
        extends JpaRepository<ProductDescription, Long> {

    // Tìm nội dung mô tả theo ID sản phẩm.
    Optional<ProductDescription> findByProductId(Long productId);

    // Kiểm tra sản phẩm đã có nội dung mô tả hay chưa.
    boolean existsByProductId(Long productId);

    // Tìm các nội dung mô tả có tiêu đề giới thiệu chứa từ khóa.
    List<ProductDescription> findByIntroductionTitleContainingIgnoreCase(
            String keyword
    );

    // Tìm nội dung mô tả có mô tả ngắn chứa từ khóa.
    List<ProductDescription> findByShortDescriptionContainingIgnoreCase(
            String keyword
    );

    // Lấy tất cả nội dung mô tả theo thời gian cập nhật mới nhất.
    List<ProductDescription> findAllByOrderByUpdatedAtDesc();

    // Lấy tất cả nội dung mô tả theo thời gian tạo mới nhất.
    List<ProductDescription> findAllByOrderByCreatedAtDesc();

    // Xóa nội dung mô tả theo ID sản phẩm.
    void deleteByProductId(Long productId);
}
