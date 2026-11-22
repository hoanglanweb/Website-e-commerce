package com.example.electronics_store.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.electronics_store.Model.Accessory;
import com.example.electronics_store.Model.Brand;
import com.example.electronics_store.Model.ProductCardDTO;
import com.example.electronics_store.Repository.AccessoryRepository;
import com.example.electronics_store.Service.HomeService;

@Controller
public class HomeController {

    private final HomeService homeService;
    private final AccessoryRepository accessoryRepository;

    public HomeController(HomeService homeService, AccessoryRepository accessoryRepository) {
        this.homeService = homeService;
        this.accessoryRepository = accessoryRepository;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "default") String sort,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        int safePage = Math.max(page, 1);
        String normalizedBrand = (brand != null && !brand.isBlank()) ? brand.trim().toUpperCase() : "ALL";
        String normalizedCategory = (category != null && !category.isBlank()) ? category.trim() : "ALL";
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : "";
        String normalizedSort = (sort != null && !sort.isBlank()) ? sort.trim().toLowerCase() : "default";
        int pageSize = 8;

        Page<ProductCardDTO> productPage = homeService.getHomeProducts(
                normalizedBrand, normalizedCategory, normalizedKeyword, normalizedSort, safePage - 1, pageSize);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("selectedBrand", normalizedBrand);
        model.addAttribute("selectedCategory", normalizedCategory);
        model.addAttribute("keyword", normalizedKeyword);
        model.addAttribute("selectedSort", normalizedSort);

        return "Home";
    }

    @GetMapping("/accessories")
    public String userAccessories(
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

        model.addAttribute("accessories", filtered);
        model.addAttribute("accessoryTypes", accessoryTypes);
        model.addAttribute("selectedType", cleanType != null ? cleanType : "ALL");
        model.addAttribute("selectedBrand", cleanBrand != null ? cleanBrand.toUpperCase() : "ALL");
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");

        return "Accessory/accessory";
    }

    @GetMapping("/api/search-suggestions")
    @ResponseBody
    public List<ProductCardDTO> searchSuggestions(@RequestParam("keyword") String keyword) {
        return homeService.getSearchSuggestions(keyword);
    }
}