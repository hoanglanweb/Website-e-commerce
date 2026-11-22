package com.example.electronics_store.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.electronics_store.Model.CartItemDTO;
import com.example.electronics_store.Model.CheckoutDTO;
import com.example.electronics_store.Model.Order;
import com.example.electronics_store.Model.OrderItem;
import com.example.electronics_store.Model.ProductVariant;
import com.example.electronics_store.Model.User;
import com.example.electronics_store.Repository.OrderRepository;
import com.example.electronics_store.Repository.ProductVariantRepository;
import com.example.electronics_store.Repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
            UserRepository userRepository,
            ProductVariantRepository productVariantRepository,
            CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productVariantRepository = productVariantRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order placeOrder(String userEmail, CheckoutDTO dto) {
        if (dto.getRecipientName() == null || dto.getRecipientName().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập họ và tên người nhận.");
        }
        if (dto.getRecipientPhone() == null || dto.getRecipientPhone().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập số điện thoại nhận hàng.");
        }
        if (dto.getShippingAddress() == null || dto.getShippingAddress().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập địa chỉ giao hàng chi tiết.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản."));

        List<CartItemDTO> cartItems = cartService.getCartItems(userEmail);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng của bạn đang trống, không thể đặt hàng.");
        }

        BigDecimal totalAmount = cartService.getTotalAmount(userEmail);

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setUser(user);
        order.setRecipientName(dto.getRecipientName().trim());
        order.setRecipientPhone(dto.getRecipientPhone().trim());
        order.setShippingAddress(dto.getShippingAddress().trim());
        order.setNote(dto.getNote() != null ? dto.getNote().trim() : null);
        order.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "COD");
        order.setPaymentStatus("UNPAID");
        order.setOrderStatus("PENDING");
        order.setTotalAmount(totalAmount);

        for (CartItemDTO itemDto : cartItems) {
            ProductVariant variant = productVariantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Sản phẩm không còn tồn tại: " + itemDto.getProductName()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(variant.getProduct());
            orderItem.setVariant(variant);
            orderItem.setProductName(itemDto.getProductName());
            orderItem.setVariantName(itemDto.getVariantName());
            orderItem.setSku(itemDto.getSku());
            orderItem.setUnitPrice(itemDto.getUnitPrice());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setSubtotal(itemDto.getSubtotal());

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng sau khi tạo đơn hàng thành công
        cartService.clearCart(userEmail);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Order getOrderByCodeForUser(String orderCode, String userEmail) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderCode));

        if (!order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new IllegalArgumentException("Bạn không có quyền xem đơn hàng này.");
        }
        return order;
    }

    private String generateOrderCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + datePart + "-" + randomPart;
    }
}