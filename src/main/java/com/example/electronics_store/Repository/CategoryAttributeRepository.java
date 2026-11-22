
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.CategoryAttribute;

/**
 * Repository quản lý cấu hình thuộc tính theo danh mục sản phẩm.
 */
@Repository
public interface CategoryAttributeRepository
        extends JpaRepository<CategoryAttribute, Long> {

    // Lấy danh sách cấu hình thuộc tính của một danh mục.
    List<CategoryAttribute> findByCategoryId(Long categoryId);

    // Lấy cấu hình thuộc tính theo danh mục và trạng thái.
    List<CategoryAttribute> findByCategoryIdAndStatus(
            Long categoryId,
            Boolean status
    );

    // Tìm cấu hình của một thuộc tính trong một danh mục.
    Optional<CategoryAttribute> findByCategoryIdAndAttributeId(
            Long categoryId,
            Long attributeId
    );

    // Kiểm tra thuộc tính đã được cấu hình cho danh mục hay chưa.
    boolean existsByCategoryIdAndAttributeId(
            Long categoryId,
            Long attributeId
    );

    // Lấy các thuộc tính được sử dụng làm tùy chọn phiên bản.
    List<CategoryAttribute> findByCategoryIdAndVariantOption(
            Long categoryId,
            Boolean isVariantOption
    );

    // Lấy các thuộc tính bắt buộc của danh mục.
    List<CategoryAttribute> findByCategoryIdAndRequired(
            Long categoryId,
            Boolean isRequired
    );

    // Lấy các thuộc tính có thể dùng để lọc sản phẩm.
    List<CategoryAttribute> findByCategoryIdAndFilterable(
            Long categoryId,
            Boolean isFilterable
    );

    // Lấy các thuộc tính đang hiển thị của danh mục.
    List<CategoryAttribute> findByCategoryIdAndVisible(
            Long categoryId,
            Boolean isVisible
    );

    // Lọc cấu hình theo danh mục và nhóm thuộc tính.
    List<CategoryAttribute> findByCategoryIdAndGroupName(
            Long categoryId,
            String groupName
    );

    // Lấy cấu hình đang hoạt động và sắp xếp theo thứ tự hiển thị.
    List<CategoryAttribute> findByCategoryIdAndStatusOrderByDisplayOrderAsc(
            Long categoryId,
            Boolean status
    );

    // Lấy tất cả cấu hình theo trạng thái.
    List<CategoryAttribute> findByStatus(Boolean status);
}
