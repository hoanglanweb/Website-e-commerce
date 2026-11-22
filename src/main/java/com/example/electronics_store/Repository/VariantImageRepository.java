
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.VariantImage;

/**
 * Repository quản lý hình ảnh của từng phiên bản sản phẩm.
 */
@Repository
public interface VariantImageRepository
                extends JpaRepository<VariantImage, Long> {

        // Lấy tất cả hình ảnh của một phiên bản sản phẩm.
        List<VariantImage> findByVariantId(Long variantId);

        // Lấy hình ảnh theo thứ tự hiển thị tăng dần.
        List<VariantImage> findByVariantIdOrderByDisplayOrderAsc(
                        Long variantId);

        // Lấy ảnh đại diện của một phiên bản sản phẩm.
        Optional<VariantImage> findByVariantIdAndPrimaryTrue(
                        Long variantId);

        // Kiểm tra phiên bản sản phẩm đã có ảnh đại diện hay chưa.
        boolean existsByVariantIdAndPrimaryTrue(Long variantId);

        // Tìm hình ảnh theo URL.
        Optional<VariantImage> findByImageUrl(String imageUrl);

        // Tìm hình ảnh có URL chứa từ khóa.
        List<VariantImage> findByImageUrlContaining(String keyword);

        // Đếm số hình ảnh của một phiên bản sản phẩm.
        long countByVariantId(Long variantId);

        // Xóa toàn bộ hình ảnh của một phiên bản sản phẩm.
        void deleteByVariantId(Long variantId);

        // Xóa hình ảnh theo ID phiên bản và ID hình ảnh.
        void deleteByVariantIdAndId(Long variantId, Long id);

        // Lấy danh sách ảnh của 1 biến thể (ưu tiên ảnh chính lên đầu)
        List<VariantImage> findByVariantIdOrderByPrimaryDescDisplayOrderAsc(Long variantId);
}
