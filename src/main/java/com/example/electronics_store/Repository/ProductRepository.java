package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.Product;

/**
 * Repository quản lý truy vấn dữ liệu sản phẩm.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm sản phẩm theo slug.
    Optional<Product> findBySlug(String slug);

    // Kiểm tra slug đã tồn tại hay chưa.
    boolean existsBySlug(String slug);

    // Kiểm tra mã sản phẩm đã tồn tại hay chưa.
    boolean existsByProductCode(String productCode);

    // Tìm sản phẩm theo mã sản phẩm.
    Optional<Product> findByProductCode(String productCode);

    // Tìm sản phẩm theo tên, không phân biệt chữ hoa và chữ thường.
    List<Product> findByProductNameContainingIgnoreCase(String productName);

    // Tìm kiếm sản phẩm theo tên, mã SP hoặc cấu hình có phân trang.
    @Query("""
            SELECT p FROM Product p
            WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.configurationSummary, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    // Lấy danh sách sản phẩm theo trạng thái.
    List<Product> findByStatus(Boolean status);

    // Lấy sản phẩm theo danh mục.
    List<Product> findByCategoryId(Long categoryId);

    // Lấy sản phẩm theo thương hiệu.
    List<Product> findByBrandId(Long brandId);

    // Lấy sản phẩm theo danh mục và trạng thái.
    List<Product> findByCategoryIdAndStatus(
            Long categoryId,
            Boolean status
    );

    // Lấy sản phẩm theo thương hiệu và trạng thái.
    List<Product> findByBrandIdAndStatus(
            Long brandId,
            Boolean status
    );

    // Lấy sản phẩm theo danh mục và thương hiệu.
    List<Product> findByCategoryIdAndBrandId(
            Long categoryId,
            Long brandId
    );

    // Lấy sản phẩm đang hoạt động và sắp xếp theo tên.
    List<Product> findByStatusOrderByProductNameAsc(Boolean status);

    // Lấy danh sách sản phẩm mới nhất trước.
    List<Product> findAllByOrderByCreatedAtDesc();

    // Lấy danh sách sản phẩm đang hoạt động và sắp xếp theo ID giảm dần.
    List<Product> findByStatusTrueOrderByIdDesc();
}