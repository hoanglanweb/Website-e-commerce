
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.Category;

/**
 * Repository quản lý truy vấn dữ liệu danh mục sản phẩm.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Tìm danh mục theo slug.
    Optional<Category> findBySlug(String slug);

    // Kiểm tra slug đã tồn tại hay chưa.
    boolean existsBySlug(String slug);

    // Tìm danh mục theo tên, không phân biệt chữ hoa và chữ thường.
    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);

    // Lấy danh sách danh mục theo trạng thái.
    List<Category> findByStatus(Boolean status);

    // Lấy danh sách danh mục theo trạng thái, sắp xếp theo tên.
    List<Category> findByStatusOrderByCategoryNameAsc(Boolean status);

    // Lấy danh sách danh mục gốc (không có danh mục cha).
    List<Category> findByParentIsNull();

    // Lấy danh sách danh mục con theo ID danh mục cha.
    List<Category> findByParentId(Long parentId);

    // Lấy danh sách danh mục con theo ID danh mục cha và trạng thái.
    List<Category> findByParentIdAndStatus(Long parentId, Boolean status);

    // Lấy danh sách danh mục gốc đang hoạt động.
    List<Category> findByParentIsNullAndStatus(Boolean status);

    // Lấy toàn bộ danh mục và sắp xếp theo tên.
    List<Category> findAllByOrderByCategoryNameAsc();
}
