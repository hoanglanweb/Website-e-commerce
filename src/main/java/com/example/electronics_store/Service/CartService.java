package com.example.electronics_store.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.electronics_store.Model.Cart;
import com.example.electronics_store.Model.CartItem;
import com.example.electronics_store.Model.CartItemDTO;
import com.example.electronics_store.Model.Product;
import com.example.electronics_store.Model.ProductVariant;
import com.example.electronics_store.Model.User;
import com.example.electronics_store.Model.VariantImage;
import com.example.electronics_store.Repository.CartItemRepository;
import com.example.electronics_store.Repository.CartRepository;
import com.example.electronics_store.Repository.ProductVariantRepository;
import com.example.electronics_store.Repository.UserRepository;
import com.example.electronics_store.Repository.VariantImageRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final VariantImageRepository variantImageRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductVariantRepository productVariantRepository,
                       VariantImageRepository variantImageRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productVariantRepository = productVariantRepository;
        this.variantImageRepository = variantImageRepository;
    }

    private Cart getOrCreateCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản: " + userEmail));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addToCart(String userEmail, Long variantId, int quantity) {
        if (userEmail == null || variantId == null || quantity <= 0) return;

        Cart cart = getOrCreateCart(userEmail);
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên bản sản phẩm không tồn tại."));

        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setVariant(variant);
            item.setQuantity(quantity);
        }
        cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(String userEmail, Long variantId, int quantity) {
        if (userEmail == null || variantId == null) return;

        Cart cart = getOrCreateCart(userEmail);
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElse(null);

        if (item == null) return;

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void removeItem(String userEmail, Long variantId) {
        if (userEmail == null || variantId == null) return;

        Cart cart = getOrCreateCart(userEmail);
        cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .ifPresent(cartItemRepository::delete);
    }

    @Transactional
    public void clearCart(String userEmail) {
        if (userEmail == null) return;

        Cart cart = getOrCreateCart(userEmail);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(String userEmail) {
        if (userEmail == null) return Collections.emptyList();

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return Collections.emptyList();

        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart == null) return Collections.emptyList();

        List<CartItem> items = cartItemRepository.findAllDetailedByCartId(cart.getId());

        return items.stream().map(ci -> {
            ProductVariant v = ci.getVariant();
            Product p = v.getProduct();

            BigDecimal originalPrice = p.getBasePrice();
            BigDecimal unitPrice = v.getPriceOverride() != null ? v.getPriceOverride() : p.getBasePrice();
            String config = v.getConfigurationSummary() != null ? v.getConfigurationSummary() : p.getConfigurationSummary();
            String brandName = p.getBrand() != null ? p.getBrand().getBrandName() : "";

            List<VariantImage> imgs = variantImageRepository.findByVariantIdOrderByPrimaryDescDisplayOrderAsc(v.getId());
            String imageUrl = !imgs.isEmpty() ? imgs.get(0).getImageUrl() : "/images/default-laptop.png";

            return new CartItemDTO(
                    ci.getId(),
                    v.getId(),
                    p.getId(),
                    brandName,
                    p.getProductName(),
                    p.getSlug(),
                    v.getSku(),
                    v.getVariantName(),
                    config,
                    imageUrl,
                    originalPrice,
                    unitPrice != null ? unitPrice : BigDecimal.ZERO,
                    ci.getQuantity()
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public int getTotalQuantity(String userEmail) {
        return getCartItems(userEmail).stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmount(String userEmail) {
        return getCartItems(userEmail).stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalSavings(String userEmail) {
        return getCartItems(userEmail).stream()
                .map(item -> item.getSavingsPerUnit().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}