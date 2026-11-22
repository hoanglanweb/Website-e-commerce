package com.example.electronics_store.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.electronics_store.Model.Brand;
import com.example.electronics_store.Model.ProductCardDTO;
import com.example.electronics_store.Repository.BrandRepository;
import com.example.electronics_store.Repository.ProductReviewRepository;
import com.example.electronics_store.Repository.ProductVariantRepository;

@Service
public class HomeService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductReviewRepository productReviewRepository;
    private final BrandRepository brandRepository;

    // Các từ đệm người dùng hay gõ nhưng không phải mã linh kiện
    private static final Set<String> STOP_WORDS = Set.of(
            "LAPTOP", "MAY", "TINH", "CARD", "GPU", "CPU", "CHIP", "DO", "HOA", "CHOI", "GAME");

    public HomeService(
            ProductVariantRepository productVariantRepository,
            ProductReviewRepository productReviewRepository,
            BrandRepository brandRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productReviewRepository = productReviewRepository;
        this.brandRepository = brandRepository;
    }

    @Transactional(readOnly = true)
    public List<Brand> getActiveBrands() {
        return brandRepository.findAll().stream()
                .filter(b -> b.getStatus() == null || Boolean.TRUE.equals(b.getStatus()))
                .sorted(Comparator.comparing(
                        b -> b.getBrandName() != null ? b.getBrandName().toLowerCase() : ""))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductCardDTO> getHomeProducts(String brand, String category, String keyword, String sort, int page,
            int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        Sort sortSpec = Sort.by(Sort.Direction.DESC, "p.id");
        if ("price_asc".equalsIgnoreCase(sort)) {
            sortSpec = JpaSort.unsafe(Sort.Direction.ASC, "COALESCE(pv.priceOverride, p.basePrice)");
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sortSpec = JpaSort.unsafe(Sort.Direction.DESC, "COALESCE(pv.priceOverride, p.basePrice)");
        }

        Pageable pageable = PageRequest.of(safePage, safeSize, sortSpec);

        String brandFilter = (brand == null || brand.isBlank() || "ALL".equalsIgnoreCase(brand))
                ? null
                : brand.trim();
        String categoryFilter = (category == null || category.isBlank() || "ALL".equalsIgnoreCase(category))
                ? null
                : category.trim();

        String[] tokens = parseHardwareTokens(keyword);

        // 1. Lấy kết quả phân trang từ query biến thể/sản phẩm
        Page<ProductCardDTO> pageResult = productVariantRepository.searchSmartProductCards(
                brandFilter, categoryFilter, tokens[0], tokens[1], tokens[2], pageable);

        // 2. Gom số lượng bình luận theo productId rồi gán vào từng card
        Map<Long, Long> reviewCountMap = productReviewRepository.countReviewsGroupedByProduct()
                .stream()
                .collect(Collectors.toMap(
                        ProductReviewRepository.ProductReviewCountProjection::getProductId,
                        ProductReviewRepository.ProductReviewCountProjection::getReviewCount,
                        (a, b) -> a));

        for (ProductCardDTO card : pageResult.getContent()) {
            if (card.getProductId() != null) {
                long count = reviewCountMap.getOrDefault(card.getProductId(), 0L);
                card.setReviewCount(count);
            }
        }

        return pageResult;
    }

    @Transactional(readOnly = true)
    public List<ProductCardDTO> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return List.of();
        }
        String[] tokens = parseHardwareTokens(keyword);
        return productVariantRepository
                .searchSmartProductCards(null, null, tokens[0], tokens[1], tokens[2], PageRequest.of(0, 6))
                .getContent();
    }

    /**
     * Tách chuỗi tìm kiếm thành tối đa 3 từ khóa linh kiện cốt lõi.
     */
    private String[] parseHardwareTokens(String rawKeyword) {
        String[] result = new String[] { null, null, null };
        if (rawKeyword == null || rawKeyword.isBlank()) {
            return result;
        }

        String normalized = rawKeyword.trim().toUpperCase()
                .replaceAll("(RTX|GTX|RX)(\\d+)", "$1 $2");

        List<String> validTokens = Arrays.stream(normalized.split("[\\s\\-/_,]+"))
                .filter(t -> !t.isBlank())
                .filter(t -> !STOP_WORDS.contains(t))
                .distinct()
                .limit(3)
                .toList();

        for (int i = 0; i < validTokens.size(); i++) {
            result[i] = validTokens.get(i);
        }
        return result;
    }
}