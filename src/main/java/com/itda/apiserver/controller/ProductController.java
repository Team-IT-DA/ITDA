package com.itda.apiserver.controller;

import com.itda.apiserver.annotation.LoginRequired;
import com.itda.apiserver.annotation.UserId;
import com.itda.apiserver.domain.Product;
import com.itda.apiserver.domain.User;
import com.itda.apiserver.dto.*;
import com.itda.apiserver.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResult<List<GetAllProductDto>> getProduct(Pageable pageable) {
        return ApiResult.ok(productService.getProducts(pageable));
    }

    @PostMapping
    @LoginRequired
    public ApiResult<Void> addProduct(@RequestBody AddproductRequestDto addproductRequestDto, @UserId Long userId) {
        productService.addProduct(addproductRequestDto, userId);
        return ApiResult.ok(null);
    }

    @GetMapping("/{productId}")
    public ApiResult<DetailProductResponse> showDetailProduct(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        return ApiResult.ok(new DetailProductResponse(getDetailProduct(product)));
    }

    private DetailProduct getDetailProduct(Product product) {

        User seller = product.getSeller();
        SellerDto sellerDto = new SellerDto(seller.getId(), seller.getName(), seller.getSellerImageUrl(), seller.getSellerDescription());

        return DetailProduct.builder()
                .id(product.getId())
                .name(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .salesUnit(product.getSalesUnit())
                .weight(product.getCapacity())
                .deliveryFee(product.getDeliveryFee())
                .deliveryFeeCondition(product.getDeliveryDescription())
                .origin(product.getOrigin())
                .packagingType(product.getPackageType())
                .detailDescription(product.getDescription())
                .imgUrl(product.getImageUrl())
                .seller(sellerDto)
                .build();
    }

    @GetMapping(params = "productName")
    public ApiResult<SearchProductDto> showProductsByName(@RequestParam String productName) {
        List<GetAllProductDto> productDtos = productService.getProductsByName(productName).stream()
                .map(product -> new GetAllProductDto(product.getId(), product.getImageUrl(), product.getTitle(),
                        product.getSeller().getName(), product.getPrice()))
                .collect(Collectors.toList());

        return ApiResult.ok(new SearchProductDto(productDtos));
    }
}
