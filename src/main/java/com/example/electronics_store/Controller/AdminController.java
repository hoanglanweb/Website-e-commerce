package com.example.electronics_store.Controller;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.electronics_store.Model.Accessory;
import com.example.electronics_store.Model.Brand;
import com.example.electronics_store.Model.Category;
import com.example.electronics_store.Model.Order;
import com.example.electronics_store.Model.OrderItem;
import com.example.electronics_store.Model.Product;
import com.example.electronics_store.Model.ProductActivityDTO;
import com.example.electronics_store.Model.ProductDescription;
import com.example.electronics_store.Model.ProductDescriptionSection;
import com.example.electronics_store.Model.ProductImage;
import com.example.electronics_store.Model.ProductSpecification;
import com.example.electronics_store.Model.ProductVariant;
import com.example.electronics_store.Model.SpecificationGroup;
import com.example.electronics_store.Model.User;
import com.example.electronics_store.Model.VariantImage;
import com.example.electronics_store.Repository.BrandRepository;
import com.example.electronics_store.Repository.CategoryRepository;
import com.example.electronics_store.Repository.OrderRepository;
import com.example.electronics_store.Repository.ProductDescriptionRepository;
import com.example.electronics_store.Repository.ProductDescriptionSectionRepository;
import com.example.electronics_store.Repository.ProductImageRepository;
import com.example.electronics_store.Repository.ProductRepository;
import com.example.electronics_store.Repository.ProductSpecificationRepository;
import com.example.electronics_store.Repository.ProductVariantRepository;
import com.example.electronics_store.Repository.SpecificationGroupRepository;
import com.example.electronics_store.Repository.UserRepository;
import com.example.electronics_store.Repository.VariantImageRepository;
import com.example.electronics_store.Repository.AccessoryRepository;

@Controller
@RequestMapping({ "/admin", "/Admin/admin" })
public class AdminController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository productVariantRepository;
    private final SpecificationGroupRepository specificationGroupRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductDescriptionRepository productDescriptionRepository;
    private final ProductDescriptionSectionRepository productDescriptionSectionRepository;
    private final ProductImageRepository productImageRepository;
    private final VariantImageRepository variantImageRepository;
    private final UserRepository userRepository;
    private final AccessoryRepository accessoryRepository;

    public AdminController(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ProductVariantRepository productVariantRepository,
            SpecificationGroupRepository specificationGroupRepository,
            ProductSpecificationRepository productSpecificationRepository,
            ProductDescriptionRepository productDescriptionRepository,
            ProductDescriptionSectionRepository productDescriptionSectionRepository,
            ProductImageRepository productImageRepository,
            VariantImageRepository variantImageRepository,
            UserRepository userRepository,
            AccessoryRepository accessoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productVariantRepository = productVariantRepository;
        this.specificationGroupRepository = specificationGroupRepository;
        this.productSpecificationRepository = productSpecificationRepository;
        this.productDescriptionRepository = productDescriptionRepository;
        this.productDescriptionSectionRepository = productDescriptionSectionRepository;
        this.productImageRepository = productImageRepository;
        this.variantImageRepository = variantImageRepository;
        this.userRepository = userRepository;
        this.accessoryRepository = accessoryRepository;
    }

    @GetMapping({ "", "/" })
    public String admin(
            @RequestParam(required = false) String param,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        String cleanKeyword = (keyword != null) ? keyword.trim() : "";

        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by("id").descending());

        Page<Product> productPage = cleanKeyword.isEmpty()
                ? productRepository.findAll(pageable)
                : productRepository.searchProducts(cleanKeyword, pageable);

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getOrderStatus()) && o.getTotalAmount() != null)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("activeTab", param != null ? param : "overview");
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("productCount", productPage.getTotalElements());
        model.addAttribute("keyword", cleanKeyword);
        model.addAttribute("orders", orders);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", safePage);
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "Admin/admin";
    }

    @GetMapping("/products")
    @Transactional(readOnly = true)
    public String adminProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model) {

        List<Product> allProducts = productRepository.findAll();
        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        String st = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : "ALL";

        List<Product> filtered = allProducts.stream()
                .filter(p -> {
                    boolean isActive = Boolean.TRUE.equals(p.getStatus());
                    boolean matchStatus = "ALL".equals(st)
                            || ("ACTIVE".equals(st) && isActive)
                            || ("HIDDEN".equals(st) && !isActive);
                    if (!matchStatus) {
                        return false;
                    }
                    if (kw.isEmpty()) {
                        return true;
                    }
                    String name = p.getProductName() != null ? p.getProductName().toLowerCase() : "";
                    String code = p.getProductCode() != null ? p.getProductCode().toLowerCase() : "";
                    String summary = p.getConfigurationSummary() != null ? p.getConfigurationSummary().toLowerCase()
                            : "";
                    return name.contains(kw) || code.contains(kw) || summary.contains(kw);
                })
                .toList();

        Map<Long, String> productImageMap = new HashMap<>();
        for (ProductImage img : productImageRepository.findAll()) {
            if (img.getProduct() != null && img.getProduct().getId() != null
                    && img.getImageUrl() != null && !img.getImageUrl().isBlank()) {
                // Lấy ảnh đầu tiên tìm thấy cho mỗi sản phẩm (hoặc ưu tiên ảnh chính nếu có cờ
                // isPrimary)
                productImageMap.putIfAbsent(img.getProduct().getId(), img.getImageUrl().trim());
            }
        }

        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm");

        // 1. Lịch sử thêm sản phẩm gần đây (mới tạo nhất trước, lấy top 6)
        List<ProductActivityDTO> recentCreatedProducts = allProducts.stream()
                .filter(p -> p.getCreatedAt() != null)
                .sorted(java.util.Comparator.comparing(Product::getCreatedAt).reversed())
                .limit(6)
                .map(p -> new ProductActivityDTO(
                        p.getId(),
                        p.getProductName(),
                        p.getCreatedAt().format(timeFormatter)))
                .toList();

        // 2. Lịch sử sửa sản phẩm gần đây (có updatedAt và khác createdAt nếu có, mới
        // sửa nhất trước, lấy top 6)
        List<ProductActivityDTO> recentUpdatedProducts = allProducts.stream()
                .filter(p -> p.getUpdatedAt() != null)
                .filter(p -> p.getCreatedAt() == null || !p.getUpdatedAt().isEqual(p.getCreatedAt()))
                .sorted(java.util.Comparator.comparing(Product::getUpdatedAt).reversed())
                .limit(6)
                .map(p -> new ProductActivityDTO(
                        p.getId(),
                        p.getProductName(),
                        p.getUpdatedAt().format(timeFormatter)))
                .toList();

        int safeSize = Math.max(1, size);
        int totalItems = filtered.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / safeSize));
        int currentPage = Math.min(Math.max(1, page), totalPages);
        int fromIndex = (currentPage - 1) * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, totalItems);
        List<Product> pagedProducts = totalItems == 0 ? List.of() : filtered.subList(fromIndex, toIndex);

        Map<Long, Integer> productStockMap = new HashMap<>();
        for (Product p : pagedProducts) {
            if (p.getId() != null) {
                int totalStock = productVariantRepository.findByProductId(p.getId()).stream()
                        .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                        .sum();
                productStockMap.put(p.getId(), totalStock);
            }
        }

        long activeCount = allProducts.stream().filter(p -> Boolean.TRUE.equals(p.getStatus())).count();
        long hiddenCount = allProducts.size() - activeCount;

        model.addAttribute("products", pagedProducts);
        model.addAttribute("productCount", totalItems);
        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("activeProducts", activeCount);
        model.addAttribute("hiddenProducts", hiddenCount);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", st);
        model.addAttribute("productImageMap", productImageMap);
        model.addAttribute("recentCreatedProducts", recentCreatedProducts);
        model.addAttribute("recentUpdatedProducts", recentUpdatedProducts);
        model.addAttribute("productStockMap", productStockMap);

        return "Admin/admin-product";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        Product product = new Product();
        product.setStatus(true);
        if (product.getCategory() == null) {
            product.setCategory(new Category());
        }
        if (product.getBrand() == null) {
            product.setBrand(new Brand());
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "Admin/admin-product-form";
    }

    @GetMapping("/products/{id}/edit")
    @Transactional(readOnly = true)
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm #" + id));

        if (product.getCategory() == null) {
            product.setCategory(new Category());
        }
        if (product.getBrand() == null) {
            product.setBrand(new Brand());
        }

        // 1. Nạp thông số kỹ thuật theo tên thông số
        List<ProductSpecification> specs = productSpecificationRepository.findAllByProductIdSorted(id);
        model.addAttribute("specGpu", findSpecValue(specs, "Loại card đồ họa"));
        model.addAttribute("specRamCapacity", findSpecValue(specs, "Dung lượng RAM"));
        model.addAttribute("specRamType", findSpecValue(specs, "Loại RAM"));
        model.addAttribute("specRamSlots", findSpecValue(specs, "Số khe ram"));
        model.addAttribute("specStorage", findSpecValue(specs, "Ổ cứng"));
        model.addAttribute("specDisplayTech", findSpecValue(specs, "Công nghệ màn hình"));
        model.addAttribute("specOs", findSpecValue(specs, "Hệ điều hành khi ra mắt"));
        model.addAttribute("specCpu", findSpecValue(specs, "Loại CPU"));
        model.addAttribute("specDisplaySize", findSpecValue(specs, "Kích thước màn hình"));
        model.addAttribute("specDisplayResolution", findSpecValue(specs, "Độ phân giải màn hình"));
        model.addAttribute("specBattery", findSpecValue(specs, "Pin"));
        model.addAttribute("specPorts", findSpecValue(specs, "Cổng giao tiếp"));

        // 2. Nạp mô tả, đặc điểm nổi bật và đánh giá chi tiết
        String highlightsText = "";
        String introOverviewText = "";
        String detailedReviewText = "";

        var descOpt = productDescriptionRepository.findByProductId(id);
        if (descOpt.isPresent()) {
            ProductDescription desc = descOpt.get();
            introOverviewText = desc.getIntroduction() != null ? desc.getIntroduction() : "";
            highlightsText = parseHighlightsJsonToLines(desc.getHighlights());

            List<ProductDescriptionSection> sections = productDescriptionSectionRepository
                    .findByProductDescriptionIdOrderByDisplayOrderAscIdAsc(desc.getId());
            if (sections != null && !sections.isEmpty()) {
                detailedReviewText = sections.stream()
                        .map(ProductDescriptionSection::getSectionContent)
                        .filter(s -> s != null && !s.isBlank())
                        .reduce((a, b) -> a + "\n\n" + b)
                        .orElse("");
            }
        }

        // 3. Nạp ảnh đại diện và ảnh thư viện
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(id);
        String primaryImageUrl = "";
        List<String> galleryList = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (ProductImage img : images) {
                String url = img.getImageUrl();
                if (url == null || url.isBlank()) {
                    continue;
                }
                if (Boolean.TRUE.equals(img.getPrimary()) && primaryImageUrl.isEmpty()) {
                    primaryImageUrl = url;
                } else {
                    galleryList.add(url);
                }
            }
            if (primaryImageUrl.isEmpty() && !galleryList.isEmpty()) {
                primaryImageUrl = galleryList.remove(0);
            }
        }
        // 4. Nạp số lượng tồn kho hiện tại từ các biến thể (kể cả khi bằng 0)
        int currentStock = 0;
        List<ProductVariant> variants = productVariantRepository.findByProductId(id);
        if (variants != null && !variants.isEmpty()) {
            currentStock = variants.stream()
                    .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                    .sum();
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("brands", brandRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("highlights", highlightsText);
        model.addAttribute("introOverview", introOverviewText);
        model.addAttribute("detailedReview", detailedReviewText);
        model.addAttribute("primaryImageUrl", primaryImageUrl);
        model.addAttribute("galleryImageUrls", String.join(", ", galleryList));
        model.addAttribute("stockQuantity", currentStock);

        return "Admin/admin-product-form";
    }

    @PostMapping("/products/save")
    @Transactional
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam(value = "stockQuantity", required = false) Integer stockQuantity,
            @RequestParam(value = "specGpu", required = false) String specGpu,
            @RequestParam(value = "specRamCapacity", required = false) String specRamCapacity,
            @RequestParam(value = "specRamType", required = false) String specRamType,
            @RequestParam(value = "specRamSlots", required = false) String specRamSlots,
            @RequestParam(value = "specStorage", required = false) String specStorage,
            @RequestParam(value = "specDisplayTech", required = false) String specDisplayTech,
            @RequestParam(value = "specOs", required = false) String specOs,
            @RequestParam(value = "specCpu", required = false) String specCpu,
            @RequestParam(value = "specDisplaySize", required = false) String specDisplaySize,
            @RequestParam(value = "specDisplayResolution", required = false) String specDisplayResolution,
            @RequestParam(value = "specBattery", required = false) String specBattery,
            @RequestParam(value = "specPorts", required = false) String specPorts,
            @RequestParam(value = "highlights", required = false) String highlights,
            @RequestParam(value = "introOverview", required = false) String introOverview,
            @RequestParam(value = "detailedReview", required = false) String detailedReview,
            @RequestParam(value = "primaryImageUrl", required = false) String primaryImageUrl,
            @RequestParam(value = "galleryImageUrls", required = false) String galleryImageUrls,
            RedirectAttributes ra) {

        boolean isNew = (product.getId() == null);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (!isNew) {
            productRepository.findById(product.getId()).ifPresent(existing -> {
                if (product.getCreatedAt() == null) {
                    product.setCreatedAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : now);
                }
            });
        } else if (product.getCreatedAt() == null) {
            product.setCreatedAt(now);
        }

        // Luôn cập nhật updatedAt khi lưu (cả thêm mới và chỉnh sửa)
        product.setUpdatedAt(now);

        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(toSlug(product.getProductName()));
        } else {
            product.setSlug(toSlug(product.getSlug()));
        }
        if (product.getStatus() == null) {
            product.setStatus(true);
        }

        if (product.getConfigurationSummary() == null || product.getConfigurationSummary().isBlank()) {
            String ramCombined = joinNonBlank(" ", specRamCapacity, specRamType);
            String displayCombined = joinNonBlank(" - ", specDisplaySize, specDisplayResolution);
            String autoSummary = buildFallbackSummary(specCpu, specGpu, ramCombined, specStorage, displayCombined);
            if (!autoSummary.isBlank()) {
                product.setConfigurationSummary(autoSummary);
            }
        }

        Product savedProduct = productRepository.save(product);
        ProductVariant defaultVariant = ensureDefaultVariantForProduct(savedProduct, isNew);

        if (stockQuantity != null) {
            defaultVariant.setStockQuantity(Math.max(0, stockQuantity));
            productVariantRepository.save(defaultVariant);
        } else if (isNew && defaultVariant.getStockQuantity() == null) {
            defaultVariant.setStockQuantity(0);
            productVariantRepository.save(defaultVariant);
        }

        Map<String, String> specMap = buildSpecMap(
                specGpu, specRamCapacity, specRamType, specRamSlots, specStorage,
                specDisplayTech, specOs, specCpu, specDisplaySize, specDisplayResolution,
                specBattery, specPorts);

        List<String> imageUrls = parseImageUrls(primaryImageUrl, galleryImageUrls);

        saveExtendedDetails(savedProduct, defaultVariant, specMap, highlights, introOverview, detailedReview,
                imageUrls);

        ra.addFlashAttribute("adminSuccess",
                (isNew ? "Đã thêm sản phẩm: " : "Đã cập nhật sản phẩm: ") + savedProduct.getProductName());
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/toggle-status")
    public String toggleProductStatus(@PathVariable Long id, RedirectAttributes ra) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm #" + id));
        boolean isCurrentlyActive = Boolean.TRUE.equals(product.getStatus());
        boolean nextStatus = !isCurrentlyActive;
        product.setStatus(nextStatus);
        productRepository.save(product);
        ra.addFlashAttribute("adminSuccess",
                "Sản phẩm \"" + product.getProductName() + "\" -> " + (nextStatus ? "Hiển thị" : "Đang ẩn"));
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public String listOrders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            Model model) {

        List<Order> allOrders = orderRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));

        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        String st = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : "ALL";

        List<Order> filteredOrders = allOrders.stream()
                .filter(o -> {
                    boolean matchStatus = "ALL".equals(st) || st.equalsIgnoreCase(String.valueOf(o.getOrderStatus()));
                    if (!matchStatus) {
                        return false;
                    }
                    if (kw.isEmpty()) {
                        return true;
                    }
                    String code = o.getOrderCode() != null ? o.getOrderCode().toLowerCase() : "";
                    String name = o.getRecipientName() != null ? o.getRecipientName().toLowerCase() : "";
                    String phone = o.getRecipientPhone() != null ? o.getRecipientPhone().toLowerCase() : "";
                    return code.contains(kw) || name.contains(kw) || phone.contains(kw);
                })
                .toList();

        long pendingCount = allOrders.stream()
                .filter(o -> "PENDING".equalsIgnoreCase(String.valueOf(o.getOrderStatus())))
                .count();

        long shippingCount = allOrders.stream()
                .filter(o -> "SHIPPING".equalsIgnoreCase(String.valueOf(o.getOrderStatus())))
                .count();

        java.math.BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(String.valueOf(o.getOrderStatus())))
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Map ảnh sản phẩm theo product_id (nếu có repository ảnh riêng thì đổ vào đây)
        java.util.Map<Long, String> productImageMap = new java.util.HashMap<>();
        for (ProductImage img : productImageRepository.findAll()) {
            Long pId = (img.getProduct() != null) ? img.getProduct().getId() : null;
            // Đổi getImageUrl() thành tên getter URL thật sự trong ProductImage của bạn (ví
            // dụ getUrl())
            String url = img.getImageUrl();
            if (pId != null && url != null && !url.isBlank()) {
                productImageMap.putIfAbsent(pId, url.trim());
            }
        }

        java.util.Map<Long, String> orderItemsJsonMap = new java.util.HashMap<>();

        for (Order order : filteredOrders) {
            List<java.util.Map<String, Object>> itemList = new java.util.ArrayList<>();

            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Product p = item.getProduct();
                    ProductVariant v = item.getVariant();
                    Long prodId = (p != null) ? p.getId()
                            : (v != null && v.getProduct() != null ? v.getProduct().getId() : null);

                    String code = (item.getSku() != null && !item.getSku().isBlank())
                            ? item.getSku().trim()
                            : (prodId != null ? "PROD-" + prodId : "");

                    String name = (item.getProductName() != null && !item.getProductName().isBlank())
                            ? item.getProductName().trim()
                            : (p != null && p.getProductName() != null
                                    ? p.getProductName()
                                    : "Sản phẩm");

                    // Ưu tiên lấy cấu hình chi tiết (CPU / GPU / RAM / SSD) từ Variant hoặc Product
                    String specs;
                    if (v != null && v.getConfigurationSummary() != null && !v.getConfigurationSummary().isBlank()) {
                        specs = v.getConfigurationSummary().trim();
                    } else if (p != null && p.getConfigurationSummary() != null
                            && !p.getConfigurationSummary().isBlank()) {
                        specs = p.getConfigurationSummary().trim();
                    } else if (item.getVariantName() != null && !item.getVariantName().isBlank()) {
                        specs = item.getVariantName().trim();
                    } else {
                        specs = "Đang cập nhật thông số";
                    }

                    java.util.Map<String, Object> dto = new java.util.LinkedHashMap<>();
                    dto.put("imageUrl", prodId != null ? productImageMap.get(prodId) : null);
                    dto.put("productCode", code);
                    dto.put("productName", name);
                    dto.put("quantity", item.getQuantity() != null ? item.getQuantity() : 1);
                    dto.put("specs", specs);
                    dto.put("unitPrice", item.getUnitPrice());

                    itemList.add(dto);
                }
            }

            orderItemsJsonMap.put(order.getId(), toJsonArray(itemList));
        }

        model.addAttribute("orders", filteredOrders);
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("pendingOrders", pendingCount);
        model.addAttribute("shippingOrders", shippingCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", st);
        model.addAttribute("orderItemsJsonMap", orderItemsJsonMap);

        return "Admin/admin-order";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String orderStatus,
            RedirectAttributes ra) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + id));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        ra.addFlashAttribute("adminSuccess", "Đã cập nhật đơn " + order.getOrderCode() + " -> " + orderStatus);
        return "redirect:/admin/orders";
    }

    @GetMapping("/categories")
    public String catalogPage(Model model) {
        List<Brand> brands = brandRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));
        List<Category> categories = categoryRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));

        model.addAttribute("brands", brands);
        model.addAttribute("categories", categories);
        return "Admin/admin-catalog";
    }

    @PostMapping("/categories/save")
    public String saveCategory(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "slug", required = false) String slug,
            @RequestParam(value = "status", required = false, defaultValue = "true") Boolean status,
            RedirectAttributes ra) {

        String cleanName = categoryName != null ? categoryName.trim() : "";
        if (cleanName.isEmpty()) {
            ra.addFlashAttribute("adminError", "Tên danh mục không được để trống.");
            return "redirect:/admin/categories";
        }

        Category category = (id != null)
                ? categoryRepository.findById(id).orElse(new Category())
                : new Category();

        String finalSlug = (slug != null && !slug.isBlank())
                ? toSlug(slug.trim())
                : toSlug(cleanName);

        // Đổi thành setName(...) nếu entity Category dùng field 'name'
        category.setCategoryName(cleanName);
        category.setSlug(finalSlug);
        // Nếu Category có trường status thì giữ dòng dưới, không có thì xóa đi
        category.setStatus(Boolean.TRUE.equals(status));

        categoryRepository.save(category);
        ra.addFlashAttribute("adminSuccess",
                (id != null ? "Đã cập nhật danh mục: " : "Đã thêm danh mục: ") + cleanName);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        try {
            if (!categoryRepository.existsById(id)) {
                ra.addFlashAttribute("adminError", "Không tìm thấy danh mục #" + id);
                return "redirect:/admin/categories";
            }
            categoryRepository.deleteById(id);
            ra.addFlashAttribute("adminSuccess", "Đã xóa danh mục #" + id);
        } catch (Exception ex) {
            ra.addFlashAttribute("adminError",
                    "Không thể xóa danh mục #" + id + " vì đang có sản phẩm thuộc danh mục này.");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/brands/save")
    public String saveBrand(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("brandName") String brandName,
            @RequestParam(value = "slug", required = false) String slug,
            @RequestParam(value = "logo", required = false) String logo,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false, defaultValue = "true") Boolean status,
            RedirectAttributes ra) {

        String cleanName = brandName != null ? brandName.trim() : "";
        if (cleanName.isEmpty()) {
            ra.addFlashAttribute("adminError", "Tên hãng không được để trống.");
            return "redirect:/admin/categories";
        }

        Brand brand = (id != null)
                ? brandRepository.findById(id).orElse(new Brand())
                : new Brand();

        String finalSlug = (slug != null && !slug.isBlank())
                ? toSlug(slug.trim())
                : toSlug(cleanName);

        brand.setBrandName(cleanName);
        brand.setSlug(finalSlug);
        brand.setLogo(logo != null && !logo.isBlank() ? logo.trim() : null);
        brand.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        brand.setStatus(Boolean.TRUE.equals(status));

        brandRepository.save(brand);
        ra.addFlashAttribute("adminSuccess", (id != null ? "Đã cập nhật hãng: " : "Đã thêm hãng: ") + cleanName);
        return "redirect:/admin/categories";
    }

    @PostMapping("/brands/{id}/delete")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes ra) {
        try {
            if (!brandRepository.existsById(id)) {
                ra.addFlashAttribute("adminError", "Không tìm thấy hãng #" + id);
                return "redirect:/admin/categories";
            }
            brandRepository.deleteById(id);
            ra.addFlashAttribute("adminSuccess", "Đã xóa hãng #" + id);
        } catch (Exception ex) {
            // Thường xảy ra nếu hãng đang được sản phẩm tham chiếu khóa ngoại
            ra.addFlashAttribute("adminError", "Không thể xóa hãng #" + id + " vì đang có sản phẩm sử dụng.");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/accessories")
    public String accessories(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Accessory> allActive = accessoryRepository.findByStatusTrueOrderByIdDesc();

        List<String> accessoryTypes = allActive.stream()
                .map(Accessory::getAccessoryType)
                .filter(t -> t != null && !t.isBlank())
                .map(String::trim)
                .distinct()
                .sorted()
                .toList();

        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;
        String cleanType = (type != null && !type.isBlank() && !"ALL".equalsIgnoreCase(type)) ? type.trim() : null;
        String cleanBrand = (brand != null && !brand.isBlank() && !"ALL".equalsIgnoreCase(brand)) ? brand.trim() : null;

        List<Accessory> filtered = allActive.stream()
                .filter(a -> cleanType == null
                        || (a.getAccessoryType() != null && a.getAccessoryType().equalsIgnoreCase(cleanType)))
                .filter(a -> cleanBrand == null
                        || (a.getBrand() != null && a.getBrand().getBrandName() != null
                                && a.getBrand().getBrandName().equalsIgnoreCase(cleanBrand)))
                .filter(a -> cleanKeyword == null
                        || (a.getAccessoryName() != null && a.getAccessoryName().toLowerCase().contains(cleanKeyword))
                        || (a.getAccessoryCode() != null && a.getAccessoryCode().toLowerCase().contains(cleanKeyword))
                        || (a.getAccessoryType() != null && a.getAccessoryType().toLowerCase().contains(cleanKeyword))
                        || (a.getCompatibility() != null && a.getCompatibility().toLowerCase().contains(cleanKeyword)))
                .toList();

        List<Brand> activeBrands = brandRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .filter(b -> b.getStatus() == null || Boolean.TRUE.equals(b.getStatus()))
                .toList();

        model.addAttribute("accessories", filtered);
        model.addAttribute("accessoryTypes", accessoryTypes);
        model.addAttribute("brands", activeBrands);
        model.addAttribute("selectedType", cleanType != null ? cleanType : "ALL");
        model.addAttribute("selectedBrand", cleanBrand != null ? cleanBrand.toUpperCase() : "ALL");
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");

        return "Admin/admin-accessories";
    }

    @PostMapping("/accessories/save")
    @Transactional
    public String saveAccessory(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("accessoryCode") String accessoryCode,
            @RequestParam("accessoryName") String accessoryName,
            @RequestParam("accessoryType") String accessoryType,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "stockQuantity", required = false, defaultValue = "0") Integer stockQuantity,
            @RequestParam(value = "compatibility", required = false) String compatibility,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false, defaultValue = "true") Boolean status,
            RedirectAttributes ra) {

        boolean isNew = (id == null);
        Accessory acc = isNew
                ? new Accessory()
                : accessoryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phụ kiện #" + id));

        acc.setAccessoryCode(accessoryCode.trim().toUpperCase());
        acc.setAccessoryName(accessoryName.trim());
        acc.setAccessoryType(accessoryType.trim());
        acc.setPrice(price != null ? price : BigDecimal.ZERO);
        acc.setStockQuantity(stockQuantity != null ? Math.max(0, stockQuantity) : 0);
        acc.setCompatibility(compatibility != null && !compatibility.isBlank() ? compatibility.trim() : null);
        acc.setImageUrl(imageUrl != null && !imageUrl.isBlank() ? imageUrl.trim() : null);
        acc.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        acc.setStatus(Boolean.TRUE.equals(status));

        if (brandId != null) {
            brandRepository.findById(brandId).ifPresent(acc::setBrand);
        } else {
            acc.setBrand(null);
        }

        accessoryRepository.save(acc);
        ra.addFlashAttribute("adminSuccess",
                (isNew ? "Đã thêm phụ kiện: " : "Đã cập nhật phụ kiện: ") + acc.getAccessoryName());
        return "redirect:/admin/accessories";
    }

    @PostMapping("/accessories/{id}/toggle-status")
    public String toggleAccessoryStatus(@PathVariable Long id, RedirectAttributes ra) {
        Accessory acc = accessoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phụ kiện #" + id));
        boolean nextStatus = !Boolean.TRUE.equals(acc.getStatus());
        acc.setStatus(nextStatus);
        accessoryRepository.save(acc);
        ra.addFlashAttribute("adminSuccess",
                "Phụ kiện \"" + acc.getAccessoryName() + "\" -> " + (nextStatus ? "Hiển thị" : "Đang ẩn"));
        return "redirect:/admin/accessories";
    }

    @PostMapping("/accessories/{id}/delete")
    public String deleteAccessory(@PathVariable Long id, RedirectAttributes ra) {
        try {
            if (!accessoryRepository.existsById(id)) {
                ra.addFlashAttribute("adminError", "Không tìm thấy phụ kiện #" + id);
                return "redirect:/admin/accessories";
            }
            accessoryRepository.deleteById(id);
            ra.addFlashAttribute("adminSuccess", "Đã xóa phụ kiện #" + id);
        } catch (Exception ex) {
            ra.addFlashAttribute("adminError",
                    "Không thể xóa phụ kiện #" + id + " vì đang phát sinh dữ liệu liên quan.");
        }
        return "redirect:/admin/accessories";
    }

    @GetMapping("/customers")
    public String customersPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        java.util.List<User> allUsers = userRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));

        java.util.List<User> filteredUsers = kw.isEmpty()
                ? allUsers
                : allUsers.stream()
                        .filter(u -> {
                            String fullName = u.getFullName() != null ? u.getFullName().toLowerCase() : "";
                            String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                            String phone = u.getPhone() != null ? u.getPhone().toLowerCase() : "";
                            return fullName.contains(kw) || email.contains(kw) || phone.contains(kw);
                        })
                        .toList();

        model.addAttribute("customers", filteredUsers);
        model.addAttribute("keyword", keyword);
        return "Admin/admin-customer";
    }

    private ProductVariant ensureDefaultVariantForProduct(Product product, boolean isNew) {
        if (product == null || product.getId() == null) {
            return null;
        }

        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

        if (variants == null || variants.isEmpty()) {
            String baseCode = (product.getProductCode() != null && !product.getProductCode().isBlank())
                    ? product.getProductCode().trim().toUpperCase()
                    : "SP-" + product.getId();

            String sku = baseCode + "-STD";
            int suffix = 1;
            while (productVariantRepository.existsBySku(sku)) {
                sku = baseCode + "-STD-" + suffix++;
            }

            ProductVariant defaultVariant = new ProductVariant();
            defaultVariant.setProduct(product);
            defaultVariant.setSku(sku);
            defaultVariant.setVariantName(product.getProductName() + " (Tiêu chuẩn)");
            defaultVariant.setPriceOverride(product.getBasePrice());
            defaultVariant.setConfigurationSummary(product.getConfigurationSummary());
            defaultVariant.setVariantSignature("DEFAULT-" + product.getId() + "-" + sku);
            defaultVariant.setStatus(Boolean.TRUE.equals(product.getStatus()));

            return productVariantRepository.save(defaultVariant);
        }

        ProductVariant firstVariant = variants.get(0);
        if (!isNew) {
            if (firstVariant.getConfigurationSummary() == null || firstVariant.getConfigurationSummary().isBlank()) {
                firstVariant.setConfigurationSummary(product.getConfigurationSummary());
            }
            if (firstVariant.getPriceOverride() == null && product.getBasePrice() != null) {
                firstVariant.setPriceOverride(product.getBasePrice());
            }
            firstVariant.setStatus(Boolean.TRUE.equals(product.getStatus()));
            firstVariant = productVariantRepository.save(firstVariant);
        }
        return firstVariant;
    }

    private void saveExtendedDetails(
            Product product,
            ProductVariant defaultVariant,
            Map<String, String> specMap,
            String highlights,
            String introOverview,
            String detailedReview,
            List<String> imageUrls) {
        if (product == null || product.getId() == null) {
            return;
        }

        // 1. Lưu thông số kỹ thuật vào bảng product_specifications
        if (specMap != null && !specMap.isEmpty()) {
            SpecificationGroup defaultGroup = specificationGroupRepository
                    .findFirstByOrderByDisplayOrderAscIdAsc()
                    .orElseGet(() -> {
                        SpecificationGroup g = new SpecificationGroup();
                        g.setGroupName("Thông số kỹ thuật");
                        g.setDisplayOrder(1);
                        return specificationGroupRepository.save(g);
                    });

            List<ProductSpecification> existingSpecs = productSpecificationRepository
                    .findAllByProductIdSorted(product.getId());
            if (existingSpecs != null && !existingSpecs.isEmpty()) {
                productSpecificationRepository.deleteAll(existingSpecs);
                productSpecificationRepository.flush();
            }

            int order = 1;
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                ProductSpecification spec = new ProductSpecification();
                spec.setProduct(product);
                spec.setGroup(defaultGroup);
                spec.setSpecName(entry.getKey());
                spec.setSpecValue(entry.getValue());
                spec.setHighlight(true);
                spec.setDisplayOrder(order++);
                productSpecificationRepository.save(spec);
            }
        }

        // 2. ProductDescription
        ProductDescription description = productDescriptionRepository.findByProductId(product.getId())
                .orElseGet(ProductDescription::new);
        description.setProduct(product);

        if (product.getConfigurationSummary() != null && !product.getConfigurationSummary().isBlank()) {
            description.setShortDescription(product.getConfigurationSummary().trim());
        }
        description.setIntroductionTitle("Giới thiệu " + product.getProductName());

        if (introOverview != null && !introOverview.trim().isEmpty()) {
            description.setIntroduction(introOverview.trim());
        }

        String highlightsJson = toHighlightsJsonArray(highlights);
        if (highlightsJson != null) {
            description.setHighlights(highlightsJson);
        }

        ProductDescription savedDescription = productDescriptionRepository.save(description);

        // 3. ProductDescriptionSection
        if (detailedReview != null && !detailedReview.trim().isEmpty()) {
            List<ProductDescriptionSection> existingSections = productDescriptionSectionRepository
                    .findByProductDescriptionIdOrderByDisplayOrderAscIdAsc(
                            savedDescription.getId());
            if (existingSections != null && !existingSections.isEmpty()) {
                productDescriptionSectionRepository.deleteAll(existingSections);
                productDescriptionSectionRepository.flush();
            }

            ProductDescriptionSection section = new ProductDescriptionSection();
            section.setProductDescription(savedDescription);
            section.setSectionTitle("Đánh giá chi tiết thiết kế & trải nghiệm thực tế " + product.getProductName());
            section.setSectionContent(detailedReview.trim());
            section.setSectionImage((imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null);
            section.setDisplayOrder(1);
            productDescriptionSectionRepository.save(section);
        }

        // 4. Lưu ảnh đại diện (1) + thư viện ảnh phụ góc nhìn khác (2, 3, ...) vào
        // product_images
        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<ProductImage> existingImages = productImageRepository.findByProductId(product.getId());
            if (existingImages != null && !existingImages.isEmpty()) {
                productImageRepository.deleteAll(existingImages);
                productImageRepository.flush();
            }

            for (int i = 0; i < imageUrls.size(); i++) {
                String url = imageUrls.get(i);
                boolean isPrimaryImg = (i == 0);

                ProductImage img = new ProductImage();
                img.setProduct(product);
                img.setImageUrl(url);
                img.setAltText(isPrimaryImg
                        ? product.getProductName() + " - Ảnh đại diện"
                        : product.getProductName() + " - Góc nhìn chi tiết " + i);
                img.setDisplayOrder(i + 1);
                img.setPrimary(isPrimaryImg);
                productImageRepository.saveAndFlush(img);
            }

            // Đồng bộ ảnh đại diện chính cho biến thể mặc định (dùng cho trang chủ)
            if (defaultVariant != null && defaultVariant.getId() != null) {
                String mainUrl = imageUrls.get(0);
                VariantImage primaryVariantImg = variantImageRepository
                        .findByVariantIdAndPrimaryTrue(defaultVariant.getId())
                        .orElseGet(() -> {
                            VariantImage v = new VariantImage();
                            v.setVariant(defaultVariant);
                            v.setDisplayOrder(1);
                            v.setPrimary(true);
                            return v;
                        });
                primaryVariantImg.setImageUrl(mainUrl);
                primaryVariantImg.setAltText(product.getProductName() + " - Ảnh đại diện");
                variantImageRepository.save(primaryVariantImg);
            }
        }
    }

    private void putIfHasText(Map<String, String> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value.trim());
        }
    }

    private String buildFallbackSummary(String cpu, String gpu, String ram, String storage, String display) {
        List<String> parts = new ArrayList<>();
        if (cpu != null && !cpu.isBlank())
            parts.add(cpu.trim());
        if (gpu != null && !gpu.isBlank())
            parts.add(gpu.trim());
        if (ram != null && !ram.isBlank())
            parts.add(ram.trim());
        if (storage != null && !storage.isBlank())
            parts.add(storage.trim());
        if (display != null && !display.isBlank())
            parts.add(display.trim());
        return String.join(" | ", parts);
    }

    private List<String> parseImageUrls(String primaryImageUrl, String galleryImageUrls) {
        List<String> urls = new ArrayList<>();

        if (primaryImageUrl != null && !primaryImageUrl.trim().isEmpty()) {
            urls.add(primaryImageUrl.trim());
        }

        if (galleryImageUrls != null && !galleryImageUrls.trim().isEmpty()) {
            for (String token : galleryImageUrls.split("[,\\r\\n]+")) {
                String clean = token.trim();
                if (!clean.isEmpty() && !urls.contains(clean)) {
                    urls.add(clean);
                }
            }
        }

        return urls;
    }

    private String toHighlightsJsonArray(String rawHighlights) {
        if (rawHighlights == null || rawHighlights.trim().isEmpty()) {
            return null;
        }
        List<String> items = new ArrayList<>();
        for (String line : rawHighlights.split("\\r?\\n")) {
            String cleaned = line.replaceAll("^[-*•]+\\s*", "").trim();
            if (!cleaned.isEmpty()) {
                items.add(cleaned);
            }
        }
        if (items.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0)
                sb.append(",");
            String escaped = items.get(i)
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");
            sb.append("\"").append(escaped).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toSlug(String input) {
        if (input == null)
            return "";
        String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'd');
        return noAccent.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");
    }

    private String findSpecValue(List<ProductSpecification> specs, String specName) {
        if (specs == null || specs.isEmpty() || specName == null) {
            return "";
        }
        return specs.stream()
                .filter(s -> specName.equalsIgnoreCase(s.getSpecName()))
                .map(ProductSpecification::getSpecValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse("");
    }

    private String parseHighlightsJsonToLines(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return "";
        }
        String trimmed = rawJson.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]") && trimmed.length() >= 2) {
            String inner = trimmed.substring(1, trimmed.length() - 1).trim();
            if (inner.isEmpty()) {
                return "";
            }
            // Tách các phần tử chuỗi trong mảng JSON đơn giản
            String[] parts = inner.split("\"\\s*,\\s*\"");
            List<String> lines = new ArrayList<>();
            for (String part : parts) {
                String unquoted = part.replaceAll("^\"|\"$", "")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\")
                        .trim();
                if (!unquoted.isEmpty()) {
                    lines.add("- " + unquoted);
                }
            }
            return String.join("\n", lines);
        }
        return trimmed;
    }

    private Map<String, String> buildSpecMap(
            String gpu, String ramCapacity, String ramType, String ramSlots, String storage,
            String displayTech, String os, String cpu, String displaySize, String displayResolution,
            String battery, String ports) {
        Map<String, String> map = new LinkedHashMap<>();
        putIfHasText(map, "Loại card đồ họa", gpu);
        putIfHasText(map, "Dung lượng RAM", ramCapacity);
        putIfHasText(map, "Loại RAM", ramType);
        putIfHasText(map, "Số khe ram", ramSlots);
        putIfHasText(map, "Ổ cứng", storage);
        putIfHasText(map, "Công nghệ màn hình", displayTech);
        putIfHasText(map, "Hệ điều hành khi ra mắt", os);
        putIfHasText(map, "Loại CPU", cpu);
        putIfHasText(map, "Kích thước màn hình", displaySize);
        putIfHasText(map, "Độ phân giải màn hình", displayResolution);
        putIfHasText(map, "Pin", battery);
        putIfHasText(map, "Cổng giao tiếp", ports);
        return map;
    }

    private String joinNonBlank(String sep, String a, String b) {
        List<String> parts = new ArrayList<>();
        if (a != null && !a.isBlank())
            parts.add(a.trim());
        if (b != null && !b.isBlank())
            parts.add(b.trim());
        return String.join(sep, parts);
    }

    private String toJsonArray(List<java.util.Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0)
                sb.append(",");
            java.util.Map<String, Object> map = list.get(i);
            sb.append("{");
            int idx = 0;
            for (java.util.Map.Entry<String, Object> entry : map.entrySet()) {
                if (idx > 0)
                    sb.append(",");
                sb.append("\"").append(escapeJson(entry.getKey())).append("\":");
                Object val = entry.getValue();
                if (val == null) {
                    sb.append("null");
                } else if (val instanceof Number || val instanceof Boolean) {
                    sb.append(val);
                } else {
                    sb.append("\"").append(escapeJson(String.valueOf(val))).append("\"");
                }
                idx++;
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
