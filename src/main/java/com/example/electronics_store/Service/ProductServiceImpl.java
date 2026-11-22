package com.example.electronics_store.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.electronics_store.Model.Product;
import com.example.electronics_store.Model.ProductCardDTO;
import com.example.electronics_store.Model.ProductDescriptionSection;
import com.example.electronics_store.Model.ProductDetailDTO;
import com.example.electronics_store.Model.ProductImage;
import com.example.electronics_store.Model.ProductSpecification;
import com.example.electronics_store.Model.ProductVariant;
import com.example.electronics_store.Model.VariantImage;
import com.example.electronics_store.Repository.ProductDescriptionRepository;
import com.example.electronics_store.Repository.ProductDescriptionSectionRepository;
import com.example.electronics_store.Repository.ProductImageRepository;
import com.example.electronics_store.Repository.ProductRepository;
import com.example.electronics_store.Repository.ProductReviewRepository;
import com.example.electronics_store.Repository.ProductSpecificationRepository;
import com.example.electronics_store.Repository.ProductVariantRepository;
import com.example.electronics_store.Repository.VariantImageRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Set<String> STOP_WORDS = Set.of(
            "LAPTOP", "MAY", "TINH", "CARD", "GPU", "CPU", "CHIP", "DO", "HOA", "CHOI", "GAME");

    private static final BigDecimal PRICE_30M = new BigDecimal("30000000");
    private static final BigDecimal PRICE_70M = new BigDecimal("70000000");

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final VariantImageRepository variantImageRepository;
    private final ProductDescriptionRepository productDescriptionRepository;
    private final ProductDescriptionSectionRepository productDescriptionSectionRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductReviewRepository productReviewRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ProductImageRepository productImageRepository,
            VariantImageRepository variantImageRepository,
            ProductDescriptionRepository productDescriptionRepository,
            ProductDescriptionSectionRepository productDescriptionSectionRepository,
            ProductSpecificationRepository productSpecificationRepository,
            ProductReviewRepository productReviewRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.variantImageRepository = variantImageRepository;
        this.productDescriptionRepository = productDescriptionRepository;
        this.productDescriptionSectionRepository = productDescriptionSectionRepository;
        this.productSpecificationRepository = productSpecificationRepository;
        this.productReviewRepository = productReviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getAllActiveProductCards() {
        List<ProductCardDTO> cards = productVariantRepository.findAllActiveProductCards();
        attachReviewCounts(cards);
        return cards;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetailBySlug(String slug, Long variantId) {
        Product product = productRepository.findBySlug(slug)
                .filter(p -> Boolean.TRUE.equals(p.getStatus()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm: " + slug));

        List<ProductVariant> activeVariants = productVariantRepository
                .findByProductIdAndStatusTrueOrderByIdAsc(product.getId());

        if (activeVariants.isEmpty()) {
            throw new IllegalStateException("Sản phẩm hiện chưa có phiên bản cấu hình nào.");
        }

        ProductVariant selectedVariant = activeVariants.stream()
                .filter(v -> variantId != null && v.getId().equals(variantId))
                .findFirst()
                .orElse(activeVariants.get(0));

        List<ProductImage> productImages = productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(product.getId());
        if (productImages != null && !productImages.isEmpty()) {
            productImages.sort((a, b) -> {
                boolean aPrimary = Boolean.TRUE.equals(a.getPrimary());
                boolean bPrimary = Boolean.TRUE.equals(b.getPrimary());
                if (aPrimary != bPrimary) {
                    return aPrimary ? -1 : 1;
                }
                int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : Integer.MAX_VALUE;
                int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : Integer.MAX_VALUE;
                return Integer.compare(orderA, orderB);
            });
        }

        List<ProductDetailDTO.ImageItemDTO> combinedImages = new ArrayList<>();
        List<ProductDetailDTO.ImageItemDTO> extraGalleryImages = new ArrayList<>();
        Set<String> seenImageUrls = new LinkedHashSet<>();

        if (productImages != null) {
            for (int i = 0; i < productImages.size(); i++) {
                ProductImage pImg = productImages.get(i);
                if (pImg.getImageUrl() == null || pImg.getImageUrl().isBlank()) {
                    continue;
                }
                String cleanUrl = pImg.getImageUrl().trim();
                if (!seenImageUrls.add(cleanUrl)) {
                    continue;
                }

                boolean isPrimaryImg = Boolean.TRUE.equals(pImg.getPrimary()) || combinedImages.isEmpty();
                String alt = (pImg.getAltText() != null && !pImg.getAltText().isBlank())
                        ? pImg.getAltText()
                        : product.getProductName() + " - Hình ảnh " + (i + 1);

                ProductDetailDTO.ImageItemDTO imgDto = new ProductDetailDTO.ImageItemDTO(cleanUrl, alt, isPrimaryImg);
                combinedImages.add(imgDto);

                if (combinedImages.size() > 1) {
                    extraGalleryImages.add(imgDto);
                }
            }
        }

        if (combinedImages.isEmpty() && selectedVariant.getId() != null) {
            List<VariantImage> variantImages = variantImageRepository
                    .findByVariantIdOrderByPrimaryDescDisplayOrderAsc(selectedVariant.getId());
            for (VariantImage vImg : variantImages) {
                if (vImg.getImageUrl() == null || vImg.getImageUrl().isBlank()) {
                    continue;
                }
                String cleanUrl = vImg.getImageUrl().trim();
                if (seenImageUrls.add(cleanUrl)) {
                    combinedImages.add(new ProductDetailDTO.ImageItemDTO(
                            cleanUrl,
                            vImg.getAltText() != null ? vImg.getAltText() : product.getProductName(),
                            Boolean.TRUE.equals(vImg.getPrimary())));
                }
            }
        }

        List<ProductDetailDTO.VariantOptionDTO> variantOptions = activeVariants.stream()
                .map(v -> new ProductDetailDTO.VariantOptionDTO(
                        v.getId(),
                        v.getSku(),
                        v.getVariantName(),
                        v.getConfigurationSummary() != null ? v.getConfigurationSummary()
                                : product.getConfigurationSummary(),
                        v.getPriceOverride() != null ? v.getPriceOverride() : product.getBasePrice()))
                .toList();
        int totalStock = activeVariants.stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        dto.setSlug(product.getSlug());
        dto.setConfigurationSummary(product.getConfigurationSummary());
        dto.setBrandName(product.getBrand() != null ? product.getBrand().getBrandName() : "");
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : "");
        dto.setBasePrice(product.getBasePrice());
        dto.setStockQuantity(totalStock);

        dto.setSelectedVariantId(selectedVariant.getId());
        dto.setSelectedSku(selectedVariant.getSku());
        dto.setSelectedVariantName(selectedVariant.getVariantName());
        dto.setSelectedConfiguration(
                selectedVariant.getConfigurationSummary() != null
                        ? selectedVariant.getConfigurationSummary()
                        : product.getConfigurationSummary());
        dto.setDisplayPrice(
                selectedVariant.getPriceOverride() != null
                        ? selectedVariant.getPriceOverride()
                        : product.getBasePrice());
        dto.setVariants(variantOptions);

        productDescriptionRepository.findByProductId(product.getId())
                .ifPresent(desc -> {
                    dto.setShortDescription(desc.getShortDescription());
                    dto.setIntroductionTitle(desc.getIntroductionTitle());
                    dto.setIntroduction(desc.getIntroduction());
                    dto.setHighlightList(parseHighlightsJson(desc.getHighlights()));

                    List<ProductDescriptionSection> rawSections = productDescriptionSectionRepository
                            .findByProductDescriptionIdOrderByDisplayOrderAscIdAsc(desc.getId());

                    List<ProductDetailDTO.SectionItemDTO> contentSections = new ArrayList<>();

                    for (ProductDescriptionSection sec : rawSections) {
                        boolean hasText = sec.getSectionContent() != null && !sec.getSectionContent().isBlank();
                        boolean hasImage = sec.getSectionImage() != null && !sec.getSectionImage().isBlank();

                        if (hasImage) {
                            String secUrl = sec.getSectionImage().trim();
                            if (seenImageUrls.add(secUrl)) {
                                ProductDetailDTO.ImageItemDTO secImg = new ProductDetailDTO.ImageItemDTO(
                                        secUrl,
                                        sec.getSectionTitle() != null && !sec.getSectionTitle().isBlank()
                                                ? sec.getSectionTitle()
                                                : product.getProductName(),
                                        false);
                                combinedImages.add(secImg);
                                if (combinedImages.size() > 1) {
                                    extraGalleryImages.add(secImg);
                                }
                            }
                        }

                        if (hasText) {
                            contentSections.add(new ProductDetailDTO.SectionItemDTO(
                                    sec.getSectionTitle(),
                                    sec.getSectionContent(),
                                    sec.getSectionImage()));
                        }
                    }

                    dto.setSections(contentSections);
                });

        dto.setImages(combinedImages);
        dto.setExtraImages(extraGalleryImages);

        List<ProductSpecification> specs = productSpecificationRepository.findAllByProductIdSorted(product.getId());

        List<ProductCardDTO> allCards = productVariantRepository.findAllActiveProductCards();
        attachReviewCounts(allCards);

        if (variantOptions.size() <= 1) {
            BigDecimal currentPrice = dto.getDisplayPrice() != null ? dto.getDisplayPrice() : BigDecimal.ZERO;
            boolean isCurrentGaming = isGamingProduct(product.getProductName());
            String currentSegment = resolvePriceSegment(currentPrice);

            List<ProductCardDTO> otherModels = allCards.stream()
                    .filter(c -> c.getProductId() != null && !c.getProductId().equals(product.getId()))
                    .filter(c -> isGamingProduct(c.getProductName()) == isCurrentGaming)
                    .filter(c -> c.getPrice() != null && currentSegment.equals(resolvePriceSegment(c.getPrice())))
                    .toList();

            List<ProductCardDTO> higherModels = otherModels.stream()
                    .filter(c -> c.getPrice().compareTo(currentPrice) > 0)
                    .sorted(Comparator.comparing(ProductCardDTO::getPrice))
                    .limit(2)
                    .toList();

            List<ProductCardDTO> lowerModels = otherModels.stream()
                    .filter(c -> c.getPrice().compareTo(currentPrice) < 0)
                    .sorted(Comparator.comparing(ProductCardDTO::getPrice).reversed())
                    .limit(2)
                    .toList();

            List<ProductCardDTO> combined = new ArrayList<>();
            if (higherModels.isEmpty()) {
                combined.addAll(otherModels.stream()
                        .filter(c -> c.getPrice().compareTo(currentPrice) < 0)
                        .sorted(Comparator.comparing(ProductCardDTO::getPrice).reversed())
                        .limit(3)
                        .toList());
            } else if (lowerModels.isEmpty()) {
                combined.addAll(otherModels.stream()
                        .filter(c -> c.getPrice().compareTo(currentPrice) > 0)
                        .sorted(Comparator.comparing(ProductCardDTO::getPrice))
                        .limit(3)
                        .toList());
            } else {
                combined.addAll(higherModels);
                combined.add(lowerModels.get(0));
            }

            dto.setRecommendedProducts(combined);
        }

        Map<Long, ProductCardDTO> relatedMap = new LinkedHashMap<>();
        for (ProductCardDTO card : allCards) {
            if (card.getProductId() != null && !card.getProductId().equals(product.getId())) {
                relatedMap.putIfAbsent(card.getProductId(), card);
            }
            if (relatedMap.size() >= 4) {
                break;
            }
        }
        dto.setRelatedProducts(new ArrayList<>(relatedMap.values()));

        List<ProductDetailDTO.SpecItemDTO> highlightSpecs = specs.stream()
                .filter(s -> Boolean.TRUE.equals(s.getHighlight()))
                .sorted(Comparator.comparing(ProductSpecification::getDisplayOrder))
                .map(s -> new ProductDetailDTO.SpecItemDTO(s.getSpecName(), s.getSpecValue()))
                .toList();
        dto.setHighlightSpecs(highlightSpecs);

        Map<String, List<ProductDetailDTO.SpecItemDTO>> groupedMap = new LinkedHashMap<>();
        for (ProductSpecification s : specs) {
            groupedMap
                    .computeIfAbsent(s.getGroup().getGroupName(), k -> new ArrayList<>())
                    .add(new ProductDetailDTO.SpecItemDTO(s.getSpecName(), s.getSpecValue()));
        }

        List<ProductDetailDTO.SpecGroupDTO> specGroups = groupedMap.entrySet().stream()
                .map(entry -> new ProductDetailDTO.SpecGroupDTO(entry.getKey(), entry.getValue()))
                .toList();
        dto.setSpecGroups(specGroups);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardDTO> searchProducts(
            String brand,
            String category,
            String keyword,
            String sort,
            int page,
            int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        Sort sortSpec = Sort.by(Sort.Direction.ASC, "pv.id");
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

        Page<ProductCardDTO> pageResult = productVariantRepository.searchSmartProductCards(
                brandFilter, categoryFilter, tokens[0], tokens[1], tokens[2], pageable);

        attachReviewCounts(pageResult.getContent());
        return pageResult;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return List.of();
        }
        String[] tokens = parseHardwareTokens(keyword);
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "pv.id"));

        List<ProductCardDTO> suggestions = productVariantRepository
                .searchSmartProductCards(null, null, tokens[0], tokens[1], tokens[2], pageable)
                .getContent();

        attachReviewCounts(suggestions);
        return suggestions;
    }

    private void attachReviewCounts(List<ProductCardDTO> cards) {
        if (cards == null || cards.isEmpty()) {
            return;
        }
        Map<Long, Long> reviewCountMap = productReviewRepository.countReviewsGroupedByProduct()
                .stream()
                .collect(Collectors.toMap(
                        ProductReviewRepository.ProductReviewCountProjection::getProductId,
                        ProductReviewRepository.ProductReviewCountProjection::getReviewCount,
                        (a, b) -> a));

        Map<Long, Integer> variantStockMap = productVariantRepository.findActiveVariantStocks()
                .stream()
                .collect(Collectors.toMap(
                        ProductVariantRepository.VariantStockProjection::getVariantId,
                        ProductVariantRepository.VariantStockProjection::getStockQuantity,
                        (a, b) -> a));

        for (ProductCardDTO card : cards) {
            card.setReviewCount(reviewCountMap.getOrDefault(card.getProductId(), 0L));
            card.setStockQuantity(variantStockMap.getOrDefault(card.getVariantId(), 0));
        }
    }

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

    private List<String> parseHighlightsJson(String highlightsJson) {
        if (highlightsJson == null || highlightsJson.isBlank()) {
            return Collections.emptyList();
        }
        String trimmed = highlightsJson.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            List<String> results = new ArrayList<>();
            Matcher matcher = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"").matcher(trimmed);
            while (matcher.find()) {
                results.add(matcher.group(1).replace("\\\"", "\""));
            }
            if (!results.isEmpty()) {
                return results;
            }
        }
        return List.of(trimmed);
    }

    private String resolvePriceSegment(BigDecimal price) {
        if (price == null || price.compareTo(PRICE_30M) < 0) {
            return "MID_RANGE";
        }
        if (price.compareTo(PRICE_70M) < 0) {
            return "HIGH_END";
        }
        return "ULTRA_PREMIUM";
    }

    private boolean isGamingProduct(String productName) {
        if (productName == null) {
            return false;
        }
        String upper = productName.toUpperCase();
        return upper.contains("CYBORG") || upper.contains("KATANA") || upper.contains("RAIDER")
                || upper.contains("NITRO") || upper.contains("PREDATOR")
                || upper.contains("LOQ") || upper.contains("LEGION")
                || upper.contains("TUF") || upper.contains("ROG");
    }
}