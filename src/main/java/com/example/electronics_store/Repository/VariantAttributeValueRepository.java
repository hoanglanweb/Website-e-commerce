
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.VariantAttributeValue;

/**
 * Repository quản lý giá trị thuộc tính của phiên bản sản phẩm.
 */
@Repository
public interface VariantAttributeValueRepository
        extends JpaRepository<VariantAttributeValue, Long> {

    // Lấy tất cả giá trị thuộc tính của một phiên bản sản phẩm.
    List<VariantAttributeValue> findByVariantId(Long variantId);

    // Lấy giá trị thuộc tính và sắp xếp theo thứ tự hiển thị.
    List<VariantAttributeValue> findByVariantIdOrderByDisplayOrderAsc(
            Long variantId
    );

    // Tìm các giá trị theo phiên bản và thuộc tính.
    List<VariantAttributeValue> findByVariantIdAndAttributeId(
            Long variantId,
            Long attributeId
    );

    // Tìm một giá trị cụ thể theo phiên bản, thuộc tính và giá trị thuộc tính.
    Optional<VariantAttributeValue>
    findByVariantIdAndAttributeIdAndAttributeValueId(
            Long variantId,
            Long attributeId,
            Long attributeValueId
    );

    // Lấy tất cả bản ghi sử dụng một thuộc tính.
    List<VariantAttributeValue> findByAttributeId(Long attributeId);

    // Lấy tất cả bản ghi sử dụng một giá trị thuộc tính có sẵn.
    List<VariantAttributeValue> findByAttributeValueId(Long attributeValueId);

    // Lấy các bản ghi có giá trị nhập tùy chỉnh.
    List<VariantAttributeValue> findByCustomValueIsNotNull();

    // Kiểm tra phiên bản đã có thuộc tính tương ứng hay chưa.
    boolean existsByVariantIdAndAttributeId(
            Long variantId,
            Long attributeId
    );

    // Kiểm tra một giá trị thuộc tính cụ thể đã tồn tại hay chưa.
    boolean existsByVariantIdAndAttributeIdAndAttributeValueId(
            Long variantId,
            Long attributeId,
            Long attributeValueId
    );

    // Xóa toàn bộ giá trị thuộc tính của một phiên bản.
    void deleteByVariantId(Long variantId);

    // Xóa giá trị thuộc tính theo phiên bản và thuộc tính.
    void deleteByVariantIdAndAttributeId(
            Long variantId,
            Long attributeId
    );
}
