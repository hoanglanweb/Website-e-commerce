
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.ProductAttributeValue;

/**
 * Repository quản lý giá trị thuộc tính của sản phẩm.
 */
@Repository
public interface ProductAttributeValueRepository
        extends JpaRepository<ProductAttributeValue, Long> {

    // Lấy tất cả giá trị thuộc tính của một sản phẩm.
    List<ProductAttributeValue> findByProductId(Long productId);

    // Lấy các giá trị thuộc tính của sản phẩm theo thứ tự hiển thị.
    List<ProductAttributeValue> findByProductIdOrderByDisplayOrderAsc(
            Long productId
    );

    // Tìm giá trị thuộc tính theo sản phẩm và thuộc tính.
    List<ProductAttributeValue> findByProductIdAndAttributeId(
            Long productId,
            Long attributeId
    );

    // Tìm một giá trị thuộc tính cụ thể của sản phẩm.
    Optional<ProductAttributeValue> findByProductIdAndAttributeIdAndAttributeValueId(
            Long productId,
            Long attributeId,
            Long attributeValueId
    );

    // Lấy các giá trị thuộc tính theo ID thuộc tính.
    List<ProductAttributeValue> findByAttributeId(Long attributeId);

    // Lấy các bản ghi sử dụng một giá trị thuộc tính có sẵn.
    List<ProductAttributeValue> findByAttributeValueId(Long attributeValueId);

    // Lấy các bản ghi có giá trị nhập tùy chỉnh khác NULL.
    List<ProductAttributeValue> findByCustomValueIsNotNull();

    // Lấy các giá trị thuộc tính của một sản phẩm theo thuộc tính.
    List<ProductAttributeValue> findByProductIdAndAttributeIdOrderByDisplayOrderAsc(
            Long productId,
            Long attributeId
    );

    // Kiểm tra sản phẩm đã có bản ghi cho thuộc tính hay chưa.
    boolean existsByProductIdAndAttributeId(
            Long productId,
            Long attributeId
    );

    // Xóa các giá trị thuộc tính của một sản phẩm.
    void deleteByProductId(Long productId);

    // Xóa các giá trị thuộc tính theo sản phẩm và thuộc tính.
    void deleteByProductIdAndAttributeId(
            Long productId,
            Long attributeId
    );
}
