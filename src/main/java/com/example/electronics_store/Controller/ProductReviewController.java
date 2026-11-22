package com.example.electronics_store.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.electronics_store.Service.ProductReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/products/{slug}/reviews")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public String submitReview(
            @PathVariable("slug") String slug,
            @RequestParam("productId") Long productId,
            @RequestParam(value = "variantId", required = false) Long variantId,
            @RequestParam(value = "rating", defaultValue = "5") Integer rating,
            @RequestParam("comment") String comment,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            reviewService.addReview(productId, principal.getName(), rating, comment);
            redirectAttributes.addFlashAttribute("reviewSuccess", "Đã lưu đánh giá của bạn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        }

        String query = (variantId != null) ? "?variantId=" + variantId : "";
        return "redirect:/products/" + slug + query + "#reviews";
    }

    @PostMapping("/{reviewId}/replies")
    public String replyReview(
            @PathVariable("slug") String slug,
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "variantId", required = false) Long variantId,
            @RequestParam("comment") String comment,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            reviewService.addReply(reviewId, principal.getName(), comment);
            redirectAttributes.addFlashAttribute("reviewSuccess", "Đã gửi câu trả lời!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        }

        String query = (variantId != null) ? "?variantId=" + variantId : "";
        return "redirect:/products/" + slug + query + "#reviews";
    }

    @ResponseBody
    @PostMapping("/{reviewId}/reactions")
    public ResponseEntity<Map<String, Long>> toggleReaction(
            @PathVariable("slug") String slug,
            @PathVariable("reviewId") Long reviewId,
            @RequestParam("type") String type,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String actorKey = "USER_" + principal.getName().toLowerCase();

        try {
            Map<String, Long> counts = reviewService.toggleReaction(reviewId, type, actorKey);
            return ResponseEntity.ok(counts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReview(
            @PathVariable("slug") String slug,
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "variantId", required = false) Long variantId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            reviewService.deleteReview(reviewId, principal.getName());
            redirectAttributes.addFlashAttribute("reviewSuccess", "Đã xóa bình luận.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        }

        String query = (variantId != null) ? "?variantId=" + variantId : "";
        return "redirect:/products/" + slug + query + "#reviews";
    }
}