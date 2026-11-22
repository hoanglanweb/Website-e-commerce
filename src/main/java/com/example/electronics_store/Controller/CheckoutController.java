package com.example.electronics_store.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.electronics_store.Model.CartItemDTO;
import com.example.electronics_store.Model.CheckoutDTO;
import com.example.electronics_store.Model.Order;
import com.example.electronics_store.Repository.UserRepository;
import com.example.electronics_store.Service.CartService;
import com.example.electronics_store.Service.OrderService;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public CheckoutController(CartService cartService,
                              OrderService orderService,
                              UserRepository userRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    // Hiển thị trang Thanh toán
    @GetMapping
    public String showCheckoutPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login?loginRequired=true";
        }

        String email = principal.getName();
        List<CartItemDTO> items = cartService.getCartItems(email);
        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        if (!model.containsAttribute("checkoutForm")) {
            CheckoutDTO form = new CheckoutDTO();
            userRepository.findByEmail(email).ifPresent(user -> {
                form.setRecipientName(user.getFullName());
                form.setRecipientPhone(user.getPhone());
            });
            model.addAttribute("checkoutForm", form);
        }

        model.addAttribute("cartItems", items);
        model.addAttribute("totalQuantity", cartService.getTotalQuantity(email));
        model.addAttribute("totalAmount", cartService.getTotalAmount(email));
        model.addAttribute("totalSavings", cartService.getTotalSavings(email));
        return "Checkout/Checkout";
    }

    // Xử lý xác nhận đặt hàng
    @PostMapping
    public String processCheckout(
            @ModelAttribute("checkoutForm") CheckoutDTO checkoutForm,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login?loginRequired=true";
        }

        try {
            Order createdOrder = orderService.placeOrder(principal.getName(), checkoutForm);
            return "redirect:/checkout/success/" + createdOrder.getOrderCode();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("checkoutError", e.getMessage());
            redirectAttributes.addFlashAttribute("checkoutForm", checkoutForm);
            return "redirect:/checkout";
        }
    }

    // Trang thông báo Đặt hàng thành công
    @GetMapping("/success/{orderCode}")
    public String showOrderSuccess(
            @PathVariable("orderCode") String orderCode,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.getOrderByCodeForUser(orderCode, principal.getName());
            model.addAttribute("order", order);
            return "Checkout/OrderSuccess";
        } catch (IllegalArgumentException e) {
            return "redirect:/";
        }
    }
}