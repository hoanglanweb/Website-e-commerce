package com.example.electronics_store.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.electronics_store.Model.CartItemDTO;
import com.example.electronics_store.Service.CartService;

@Controller
@ControllerAdvice
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Tự động hiển thị số lượng giỏ hàng của tài khoản đang đăng nhập lên Header tất cả các trang
    @ModelAttribute("cartCount")
    public int populateCartCount(Principal principal) {
        if (principal == null) return 0;
        try {
            return cartService.getTotalQuantity(principal.getName());
        } catch (Exception e) {
            return 0;
        }
    }

    // Trang Giỏ hàng chi tiết
    @GetMapping
    public String viewCart(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login?loginRequired=true";
        }

        String email = principal.getName();
        List<CartItemDTO> items = cartService.getCartItems(email);

        model.addAttribute("cartItems", items);
        model.addAttribute("totalQuantity", cartService.getTotalQuantity(email));
        model.addAttribute("totalAmount", cartService.getTotalAmount(email));
        model.addAttribute("totalSavings", cartService.getTotalSavings(email));
        return "Cart/Cart";
    }

    // Thêm vào giỏ hàng trực tiếp từ Trang chủ (Home.html)
    @PostMapping("/add-home")
    public String addToCartFromHome(
            @RequestParam("variantId") Long variantId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("loginRequiredMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.");
            return "redirect:/login?loginRequired=true";
        }

        cartService.addToCart(principal.getName(), variantId, quantity);
        redirectAttributes.addFlashAttribute("cartSuccessMessage", "Đã thêm sản phẩm vào giỏ hàng!");

        return "redirect:/";
    }

    // Thêm sản phẩm vào giỏ hàng từ trang chi tiết sản phẩm
    @PostMapping("/add-form")
    public String addToCartForm(
            @RequestParam("variantId") Long variantId,
            @RequestParam("slug") String slug,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("loginRequiredMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.");
            return "redirect:/login";
        }

        cartService.addToCart(principal.getName(), variantId, quantity);
        redirectAttributes.addFlashAttribute("cartSuccessMessage", "Đã thêm sản phẩm vào giỏ hàng!");

        return "redirect:/products/" + slug + "?variantId=" + variantId;
    }

    // Nút "Mua ngay" -> Thêm vào giỏ và mở thẳng trang /cart
    @PostMapping("/buy-now")
    public String buyNow(
            @RequestParam("variantId") Long variantId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("loginRequiredMessage", "Vui lòng đăng nhập để tiến hành mua hàng.");
            return "redirect:/login";
        }

        cartService.addToCart(principal.getName(), variantId, quantity);
        return "redirect:/cart";
    }

    // Cập nhật số lượng (+ / -)
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam("variantId") Long variantId,
            @RequestParam("quantity") int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        cartService.updateQuantity(principal.getName(), variantId, quantity);
        redirectAttributes.addFlashAttribute("cartToastMessage", "Đã cập nhật số lượng sản phẩm.");
        return "redirect:/cart";
    }

    // Xóa 1 sản phẩm khỏi giỏ
    @PostMapping("/remove")
    public String removeItem(
            @RequestParam("variantId") Long variantId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        cartService.removeItem(principal.getName(), variantId);
        redirectAttributes.addFlashAttribute("cartToastMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        return "redirect:/cart";
    }

    // Xóa toàn bộ giỏ hàng
    @PostMapping("/clear")
    public String clearCart(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";

        cartService.clearCart(principal.getName());
        redirectAttributes.addFlashAttribute("cartToastMessage", "Đã dọn trống giỏ hàng.");
        return "redirect:/cart";
    }
}