
package com.example.electronics_store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.Model.Attribute;
import com.example.electronics_store.Model.Attribute.DataType;

/**
 * Repository quản lý truy vấn dữ liệu thuộc tính sản phẩm.
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    // Tìm thuộc tính theo mã.
    Optional<Attribute> findByAttributeCode(String attributeCode);

    // Kiểm tra mã thuộc tính đã tồn tại hay chưa.
    boolean existsByAttributeCode(String attributeCode);

    // Tìm thuộc tính theo tên, không phân biệt chữ hoa và chữ thường.
    List<Attribute> findByAttributeNameContainingIgnoreCase(
            String attributeName
    );

    // Lấy danh sách thuộc tính theo trạng thái.
    List<Attribute> findByStatus(Boolean status);

    // Lấy danh sách thuộc tính theo kiểu dữ liệu.
    List<Attribute> findByDataType(DataType dataType);

    // Lấy danh sách thuộc tính theo trạng thái và kiểu dữ liệu.
    List<Attribute> findByStatusAndDataType(
            Boolean status,
            DataType dataType
    );

    // Lấy danh sách thuộc tính đang hoạt động, sắp xếp theo tên.
    List<Attribute> findByStatusOrderByAttributeNameAsc(Boolean status);

    // Lấy toàn bộ thuộc tính và sắp xếp theo tên.
    List<Attribute> findAllByOrderByAttributeNameAsc();
}
