
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.AttributeValue;

/**
 * Repository quản lý truy vấn giá trị thuộc tính sản phẩm.
 */
@Repository
public interface AttributeValueRepository
        extends JpaRepository<AttributeValue, Long> {

    // Lấy tất cả giá trị của một thuộc tính.
    List<AttributeValue> findByAttributeId(Long attributeId);

    // Lấy các giá trị đang hoạt động của một thuộc tính.
    List<AttributeValue> findByAttributeIdAndStatus(
            Long attributeId,
            Boolean status
    );

    // Tìm giá trị thuộc tính theo nội dung value.
    Optional<AttributeValue> findByAttributeIdAndValue(
            Long attributeId,
            String value
    );

    // Kiểm tra giá trị đã tồn tại trong một thuộc tính hay chưa.
    boolean existsByAttributeIdAndValue(
            Long attributeId,
            String value
    );

    // Tìm kiếm giá trị theo nội dung, không phân biệt chữ hoa và chữ thường.
    List<AttributeValue> findByValueContainingIgnoreCase(String value);

    // Lấy các giá trị theo thuộc tính và sắp xếp theo thứ tự hiển thị.
    List<AttributeValue> findByAttributeIdOrderByDisplayOrderAsc(
            Long attributeId
    );

    // Lấy các giá trị đang hoạt động theo thứ tự hiển thị.
    List<AttributeValue> findByAttributeIdAndStatusOrderByDisplayOrderAsc(
            Long attributeId,
            Boolean status
    );

    // Lấy tất cả giá trị đang hoạt động.
    List<AttributeValue> findByStatus(Boolean status);

    // Lấy tất cả giá trị và sắp xếp theo thứ tự hiển thị.
    List<AttributeValue> findAllByOrderByDisplayOrderAsc();
}
