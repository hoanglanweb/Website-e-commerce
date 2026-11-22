package com.example.electronics_store.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.electronics_store.Model.Product;
import com.example.electronics_store.Model.ProductReview;
import com.example.electronics_store.Model.ReviewReaction;
import com.example.electronics_store.Model.User;
import com.example.electronics_store.Repository.ProductRepository;
import com.example.electronics_store.Repository.ProductReviewRepository;
import com.example.electronics_store.Repository.ReviewReactionRepository;
import com.example.electronics_store.Repository.UserRepository;

@Service
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewReactionRepository reactionRepository;

    public ProductReviewService(
            ProductReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            ReviewReactionRepository reactionRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reactionRepository = reactionRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductReview> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdWithUser(productId);
    }

    @Transactional
    public void addReview(Long productId, String userEmail, Integer rating, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung bình luận không được để trống.");
        }

        int validRating = (rating == null || rating < 1 || rating > 5) ? 5 : rating;

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản người dùng."));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(validRating);
        review.setComment(comment.trim());

        reviewRepository.save(review);
    }

   @Transactional
    public void addReply(Long parentReviewId, String userEmail, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung trả lời không được để trống.");
        }

        ProductReview parentReview = reviewRepository.findById(parentReviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận cần trả lời."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản người dùng."));

        // Luôn neo về bình luận gốc để danh sách trả lời gọn gàng
        ProductReview rootParent = parentReview.getParent() != null ? parentReview.getParent() : parentReview;

        String cleanedComment = comment.trim();
        // Nếu người dùng trả lời vào một câu trả lời con mà chưa gắn @Tên, tự thêm tiền tố @ để rõ ngữ cảnh
        if (parentReview.getParent() != null && parentReview.getUser() != null) {
            String targetName = parentReview.getUser().getFullName();
            if (targetName != null && !targetName.isBlank() && !cleanedComment.startsWith("@")) {
                cleanedComment = "@" + targetName.trim() + " " + cleanedComment;
            }
        }

        ProductReview reply = new ProductReview();
        reply.setProduct(rootParent.getProduct());
        reply.setUser(user);
        reply.setParent(rootParent);
        reply.setRating(5); // Giữ mặc định hợp lệ với cột rating NOT NULL
        reply.setComment(cleanedComment);

        reviewRepository.save(reply);
    }

    @Transactional
    public Map<String, Long> toggleReaction(Long reviewId, String reactionType, String actorKey) {
        String normalizedType = (reactionType == null || reactionType.isBlank())
                ? "LIKE"
                : reactionType.trim().toUpperCase();

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận."));

        reactionRepository.findByReviewIdAndActorKeyAndReactionType(reviewId, actorKey, normalizedType)
                .ifPresentOrElse(
                        reactionRepository::delete,
                        () -> {
                            ReviewReaction rr = new ReviewReaction();
                            rr.setReview(review);
                            rr.setReactionType(normalizedType);
                            rr.setActorKey(actorKey);
                            reactionRepository.save(rr);
                        });

        return getReactionCountsForReview(reviewId);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getReactionCountsForReview(Long reviewId) {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("LIKE", reactionRepository.countByReviewIdAndReactionType(reviewId, "LIKE"));
        counts.put("HELPFUL", reactionRepository.countByReviewIdAndReactionType(reviewId, "HELPFUL"));
        counts.put("LOVE", reactionRepository.countByReviewIdAndReactionType(reviewId, "LOVE"));
        return counts;
    }

    @Transactional(readOnly = true)
    public Map<Long, Map<String, Long>> getReactionCountsForReviews(List<ProductReview> reviews) {
        Map<Long, Map<String, Long>> result = new HashMap<>();
        if (reviews == null || reviews.isEmpty()) {
            return result;
        }
        for (ProductReview rev : reviews) {
            result.put(rev.getId(), getReactionCountsForReview(rev.getId()));
        }
        return result;
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận."));

        if (!review.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new SecurityException("Bạn chỉ có thể xóa bình luận của chính mình.");
        }

        reviewRepository.delete(review);
    }
}