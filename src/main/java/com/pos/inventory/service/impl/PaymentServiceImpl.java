package com.pos.inventory.service.impl;

import com.pos.inventory.dto.payment.MockGatewayPaymentRequest;
import com.pos.inventory.dto.payment.MockGatewayPaymentResponse;
import com.pos.inventory.dto.payment.PaymentRequest;
import com.pos.inventory.dto.payment.PaymentResponse;
import com.pos.inventory.entity.*;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.PaymentFailedException;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.PaymentRepository;
import com.pos.inventory.repository.SaleRepository;
import com.pos.inventory.service.CashDrawerService;
import com.pos.inventory.service.MockPaymentGatewayService;
import com.pos.inventory.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final MockPaymentGatewayService gatewayService;
    private final CashDrawerService cashDrawerService;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, User recordedBy) {
        Sale sale = saleRepository.findByIdWithItems(request.getSaleId())
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + request.getSaleId()));

        if (sale.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Sale invoice " + sale.getInvoiceNumber() + " is already marked as PAID");
        }

        // Amount must be >= bill total
        if (request.getAmount().compareTo(sale.getTotalAmount()) < 0) {
            throw new BadRequestException("Tendered amount (₹" + request.getAmount() +
                    ") is less than the bill total (₹" + sale.getTotalAmount() + ")");
        }

        Payment payment = Payment.builder()
                .sale(sale)
                .paymentDate(LocalDateTime.now())
                .amount(request.getAmount())
                .paymentMode(request.getPaymentMode())
                .notes(request.getNotes())
                .recordedBy(recordedBy)
                .build();

        if (request.getPaymentMode() == PaymentMode.DIGITAL) {
            MockGatewayPaymentRequest gatewayRequest = MockGatewayPaymentRequest.builder()
                    .invoiceNumber(sale.getInvoiceNumber())
                    .amount(sale.getTotalAmount())
                    .cardNumber(request.getCardNumber())
                    .cardExpiry(request.getCardExpiry())
                    .upiId(request.getUpiId())
                    .customerEmail(sale.getCustomer() != null ? sale.getCustomer().getEmail() : null)
                    .build();

            MockGatewayPaymentResponse gatewayResponse = gatewayService.charge(gatewayRequest);

            payment.setTransactionReference(gatewayResponse.getTransactionId());
            payment.setGatewayResponse(gatewayResponse.getMessage() + " [" + gatewayResponse.getResponseCode() + "]");

            if (!gatewayResponse.isSuccess()) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                sale.setPaymentStatus(PaymentStatus.FAILED);
                saleRepository.save(sale);
                throw new PaymentFailedException("Digital payment failed: " + gatewayResponse.getMessage());
            }

            payment.setStatus(PaymentStatus.PAID);
            sale.setPaymentStatus(PaymentStatus.PAID);
        } else {
            // CASH mode
            String cashTxnRef = "CASH-REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.setTransactionReference(cashTxnRef);
            payment.setStatus(PaymentStatus.PAID);
            payment.setGatewayResponse("Cash received at counter. Change due: ₹" +
                    request.getAmount().subtract(sale.getTotalAmount()));

            sale.setPaymentStatus(PaymentStatus.PAID);

            // Record into active cash drawer shift if available for cashier
            if (recordedBy != null) {
                Optional<CashShift> activeShift = cashDrawerService.getActiveShiftForCashier(recordedBy.getId());
                activeShift.ifPresent(shift ->
                        cashDrawerService.recordSaleCashCollection(shift, sale.getTotalAmount(), sale.getInvoiceNumber(), recordedBy));
            }
        }

        Payment savedPayment = paymentRepository.save(payment);
        saleRepository.save(sale);

        log.info("Processed {} payment of ₹{} for invoice {}. Status: {}",
                savedPayment.getPaymentMode(), savedPayment.getAmount(), sale.getInvoiceNumber(), savedPayment.getStatus());

        return PaymentResponse.fromEntity(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsForSale(Long saleId) {
        return paymentRepository.findBySaleId(saleId).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentResponse.fromEntity(payment);
    }
}
