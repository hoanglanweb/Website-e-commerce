
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductImage;

/**
 * Repository quản lý hình ảnh sản phẩm.
 */
@Repository
public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {

    // Lấy tất cả hình ảnh của một sản phẩm.
    List<ProductImage> findByProductId(Long productId);

    // Lấy hình ảnh theo thứ tự hiển thị tăng dần.
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(
            Long productId
    );

    // Lấy ảnh đại diện của sản phẩm.
    Optional<ProductImage> findByProductIdAndPrimaryTrue(
            Long productId
    );

    // Kiểm tra sản phẩm đã có ảnh đại diện hay chưa.
    boolean existsByProductIdAndPrimaryTrue(Long productId);

    // Tìm hình ảnh theo URL.
    Optional<ProductImage> findByImageUrl(String imageUrl);

    // Tìm hình ảnh có URL chứa từ khóa.
    List<ProductImage> findByImageUrlContaining(String keyword);

    // Đếm số hình ảnh của một sản phẩm.
    long countByProductId(Long productId);

    // Xóa toàn bộ hình ảnh của một sản phẩm.
    void deleteByProductId(Long productId);

    // Xóa hình ảnh theo ID sản phẩm và ID hình ảnh.
    void deleteByProductIdAndId(Long productId, Long id);
}
