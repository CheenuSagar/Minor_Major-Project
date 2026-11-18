package com.pos.inventory.controller;

import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.dto.payment.PaymentRequest;
import com.pos.inventory.dto.payment.PaymentResponse;
import com.pos.inventory.entity.User;
import com.pos.inventory.service.PaymentService;
import com.pos.inventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments & Settlement", description = "Endpoints for processing CASH and DIGITAL sandbox payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Process a payment for a sale", description = "Supports CASH or DIGITAL payment mode. For DIGITAL, invokes the sandbox payment gateway simulator.")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        PaymentResponse response = paymentService.processPayment(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment processed successfully", response));
    }

    @GetMapping("/sale/{saleId}")
    @Operation(summary = "Get all payments associated with a sale")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsForSale(@PathVariable Long saleId) {
        List<PaymentResponse> payments = paymentService.getPaymentsForSale(saleId);
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment transaction by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.ok(payment));
    }
}
