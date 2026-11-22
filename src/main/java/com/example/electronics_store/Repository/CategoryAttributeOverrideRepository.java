
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.CategoryAttributeOverride;

/**
 * Repository quản lý cấu hình ghi đè thuộc tính theo danh mục.
 */
@Repository
public interface CategoryAttributeOverrideRepository
        extends JpaRepository<CategoryAttributeOverride, Long> {

    // Lấy tất cả cấu hình ghi đè của một danh mục.
    List<CategoryAttributeOverride> findByCategoryId(Long categoryId);

    // Tìm cấu hình ghi đè theo danh mục và thuộc tính.
    Optional<CategoryAttributeOverride> findByCategoryIdAndAttributeId(
            Long categoryId,
            Long attributeId
    );

    // Kiểm tra cấu hình ghi đè đã tồn tại hay chưa.
    boolean existsByCategoryIdAndAttributeId(
            Long categoryId,
            Long attributeId
    );

    // Lấy các cấu hình đang được bật.
    List<CategoryAttributeOverride> findByCategoryIdAndEnabled(
            Long categoryId,
            Boolean enabled
    );

    // Lấy các cấu hình đang bật và sắp xếp theo thứ tự hiển thị.
    List<CategoryAttributeOverride>
    findByCategoryIdAndEnabledOrderByDisplayOrderAsc(
            Long categoryId,
            Boolean enabled
    );

    // Lấy cấu hình theo danh mục và tên nhóm.
    List<CategoryAttributeOverride> findByCategoryIdAndGroupName(
            Long categoryId,
            String groupName
    );

    // Lấy tất cả cấu hình ghi đè của một thuộc tính.
    List<CategoryAttributeOverride> findByAttributeId(Long attributeId);

    // Lấy tất cả cấu hình đang được bật.
    List<CategoryAttributeOverride> findByEnabled(Boolean enabled);

    // Lấy toàn bộ cấu hình và sắp xếp theo thời gian cập nhật mới nhất.
    List<CategoryAttributeOverride> findAllByOrderByUpdatedAtDesc();
}
