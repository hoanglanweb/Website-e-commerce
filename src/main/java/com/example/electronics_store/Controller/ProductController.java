package com.example.electronics_store.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.electronics_store.Model.ProductCardDTO;
import com.example.electronics_store.Model.ProductDetailDTO;
import com.example.electronics_store.Model.ProductReview;
import com.example.electronics_store.Service.ProductReviewService;
import com.example.electronics_store.Service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductReviewService reviewService;

    public ProductController(ProductService productService, ProductReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{slug}")
    public String viewProductDetail(
            @PathVariable("slug") String slug,
            @RequestParam(value = "variantId", required = false) Long variantId,
            Model model) {
        try {
            ProductDetailDTO productDetail = productService.getProductDetailBySlug(slug, variantId);
            model.addAttribute("product", productDetail);

            List<ProductReview> reviews = reviewService.getReviewsByProductId(productDetail.getProductId());
            double avg = reviews.isEmpty()
                    ? 0.0
                    : reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0.0);

            model.addAttribute("reviews", reviews);
            model.addAttribute("reviewCount", reviews.size());
            model.addAttribute("averageRating", String.format("%.1f", avg));

            return "ProductDetails/Productdetails";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        Page<ProductCardDTO> resultPage = productService.searchProducts(brand, category, keyword, sort, page, size);
        model.addAttribute("products", resultPage.getContent());
        model.addAttribute("resultPage", resultPage);
        model.addAttribute("keyword", keyword);
        return "Home"; // Đổi thành tên template danh sách/tìm kiếm nếu bạn dùng file riêng
    }

    @GetMapping("/api/search-suggestions")
    @ResponseBody
    public List<ProductCardDTO> searchSuggestions(@RequestParam("keyword") String keyword) {
        return productService.getSearchSuggestions(keyword);
    }
}