
package com.shop.service_shop_management.service.impl;

import com.shop.service_shop_management.dto.request.*;
import com.shop.service_shop_management.dto.response.*;
import com.shop.service_shop_management.entity.*;
import com.shop.service_shop_management.enums.ProcessStatus;
import com.shop.service_shop_management.exception.BadRequestException;
import com.shop.service_shop_management.exception.ResourceNotFoundException;
import com.shop.service_shop_management.repository.BillProcessHistoryRepository;
import com.shop.service_shop_management.repository.BillRepository;
import com.shop.service_shop_management.repository.CustomerRepository;
import com.shop.service_shop_management.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;
    private final BillProcessHistoryRepository billProcessHistoryRepository;


    @Override
    public BillResponse createBill(CreateBillRequest request) {

        // 1. Validate customer selection
        if ((request.getCustomerId() == null && request.getCustomer() == null) ||
                (request.getCustomerId() != null && request.getCustomer() != null)) {

            throw new BadRequestException(
                    "Provide either an existing customer ID or new customer details"
            );
        }

        // 2. Check duplicate physical bill number
        String billNumber = request.getBillNumber().trim();

        if (billRepository.existsByBillNumber(billNumber)) {
            throw new BadRequestException(
                    "Bill number already exists: " + billNumber
            );
        }

        // 3. Find existing customer OR create new customer
        Customer customer;

        if (request.getCustomerId() != null) {

            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with ID: " + request.getCustomerId()
                    ));

        } else {

            CreateCustomerRequest customerRequest = request.getCustomer();

            customer = new Customer();
            customer.setName(customerRequest.getName());
            customer.setPhone(customerRequest.getPhone());
            customer.setAddress(customerRequest.getAddress());

            customer = customerRepository.save(customer);
        }

        // 4. Validate expected delivery date
        LocalDate today = LocalDate.now();

        if (request.getExpectedDeliveryDate().isBefore(today)) {
            throw new BadRequestException(
                    "Expected delivery date cannot be in the past"
            );
        }

        // 5. Create bill
        Bill bill = new Bill();

        bill.setBillNumber(billNumber);
        bill.setCustomer(customer);
        bill.setBillDate(today);
        bill.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        bill.setProcessStatus(ProcessStatus.RECEIVED);
        bill.setTotalAmount(request.getTotalAmount());

        // 6. Add products
        for (CreateProductRequest productRequest : request.getProducts()) {

            Product product = new Product();
            product.setCategory(productRequest.getCategory());
            product.setProductType(productRequest.getProductType());
            product.setQuantity(productRequest.getQuantity());
            product.setAmount(productRequest.getAmount());

            bill.addProduct(product);
        }

        // 7. Save bill
        Bill savedBill = billRepository.save(bill);

        // 8. Return response
        return mapToResponse(savedBill);
    }


    private String generateBillNumber(LocalDate date) {

        long count = billRepository.countByBillDate(date);

        String datePart = date.format(
                DateTimeFormatter.ofPattern("ddMMyy"));

        return datePart + "-" + String.format("%03d", count + 1);
    }

    // Fetch a bill using its database ID (used by QR scanning).
    @Override
    public BillResponse getBillById(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found"));

        return mapToResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillByNumber(String billNumber) {

        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found"));

        return mapToResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getBillsByCustomerId(Long customerId) {

        return billRepository.findByCustomer_Id(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getBillsByStatus(ProcessStatus status) {

        return billRepository.findByProcessStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getPendingBills() {

        // Get all bills except delivered bills.
        List<Bill> pendingBills =
                billRepository.findByProcessStatusNot(ProcessStatus.DELIVERED);

        // Status priority for bills having the same delivery date.
        Map<ProcessStatus, Integer> statusPriority = new HashMap<>();

        statusPriority.put(ProcessStatus.READY_FOR_DELIVERY, 1);
        statusPriority.put(ProcessStatus.READY_FOR_SHOP, 2);
        statusPriority.put(ProcessStatus.UNDER_PROCESSING, 3);
        statusPriority.put(ProcessStatus.RECEIVED, 4);

        // Sort by earliest delivery date first, then by process priority.
        pendingBills.sort(
                Comparator
                        .comparing(Bill::getExpectedDeliveryDate)
                        .thenComparingInt(
                                bill -> statusPriority.getOrDefault(
                                        bill.getProcessStatus(), 99
                                )
                        )
        );

        // Convert entities into API response objects.
        return pendingBills.stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public BillResponse updateBillStatus(
            String billNumber,
            UpdateBillStatusRequest request) {

        throw new BadRequestException(
                "Direct status updates are disabled. "
                        + "Use the process verification API."
        );
    }

    @Override
    public BillResponse confirmProcess(
            String billNumber,
            ConfirmProcessRequest request) {

        // 1. Find the bill.
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found"));

        ProcessStatus currentStatus = bill.getProcessStatus();
        ProcessStatus targetStatus = request.getTargetStatus();

        // 2. Do not allow any transition after delivery.
        if (currentStatus == ProcessStatus.DELIVERED) {
            throw new BadRequestException(
                    "This bill has already been delivered."
            );
        }

        // 3. Only the immediate next status is allowed.
        if (targetStatus == null ||
                targetStatus.getCode() != currentStatus.getCode() + 1) {

            throw new BadRequestException(
                    "Invalid process stage. Move to the next status only."
            );
        }

        // 4. Every product in the bill must be verified exactly once.
        List<Product> billProducts = bill.getProducts();

        List<ProductQuantityVerificationRequest> verifiedProducts =
                request.getProducts();

        if (verifiedProducts.size() != billProducts.size()) {
            throw new BadRequestException(
                    "Please verify every product in the bill."
            );
        }

        // 5. Create the history record.
        BillProcessHistory history = new BillProcessHistory();
        history.setBill(bill);
        history.setPreviousStatus(currentStatus);
        history.setNewStatus(targetStatus);

        // 6. Verify each product quantity.
        Map<Long, Product> productMap = new HashMap<>();

        for (Product product : billProducts) {
            productMap.put(product.getId(), product);
        }

        // Prevent the same product from being submitted more than once.
        Map<Long, Boolean> verifiedProductIds = new HashMap<>();

        for (ProductQuantityVerificationRequest item : verifiedProducts) {

            Long productId = item.getProductId();

            if (productId == null ||
                    verifiedProductIds.containsKey(productId)) {

                throw new BadRequestException(
                        "Invalid or duplicate product verification."
                );
            }

            Product product = productMap.get(productId);

            // Product must belong to this particular bill.
            if (product == null) {
                throw new BadRequestException(
                        "Product does not belong to this bill."
                );
            }

            Integer expectedQuantity = product.getQuantity();
            Integer actualQuantity = item.getVerifiedQuantity();

            // Do not allow the process to continue if quantities differ.
            if (!expectedQuantity.equals(actualQuantity)) {
                throw new BadRequestException(
                        "Quantity mismatch for " + product.getProductType()
                                + ". Expected: " + expectedQuantity
                                + ", Verified: " + actualQuantity
                );
            }

            verifiedProductIds.put(productId, true);

            // Save the verified quantity in process history.
            BillProcessHistoryItem historyItem =
                    new BillProcessHistoryItem();

            historyItem.setProduct(product);
            historyItem.setExpectedQuantity(expectedQuantity);
            historyItem.setVerifiedQuantity(actualQuantity);

            history.addItem(historyItem);
        }

        // 7. Update bill status only after all verifications pass.
        bill.setProcessStatus(targetStatus);

        if (targetStatus == ProcessStatus.DELIVERED) {
            bill.setDeliveredAt(OffsetDateTime.now());
        }

        billRepository.save(bill);

        // 8. Save the verification history.
        billProcessHistoryRepository.save(history);

        return mapToResponse(bill);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BillProcessHistoryResponse> getProcessHistory(
            String billNumber) {

        // Ensure that the requested bill exists.
        billRepository.findByBillNumber(billNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bill not found"));

        return billProcessHistoryRepository
                .findByBill_BillNumberOrderByProcessedAtDesc(billNumber)
                .stream()
                .map(history -> BillProcessHistoryResponse.builder()
                        .id(history.getId())
                        .previousStatus(history.getPreviousStatus())
                        .newStatus(history.getNewStatus())
                        .processedAt(history.getProcessedAt())
                        .items(history.getItems().stream()
                                .map(item ->
                                        BillProcessHistoryItemResponse.builder()
                                                .productId(item.getProduct().getId())
                                                .category(item.getProduct().getCategory())
                                                .productType(item.getProduct().getProductType())
                                                .expectedQuantity(item.getExpectedQuantity())
                                                .verifiedQuantity(item.getVerifiedQuantity())
                                                .build()
                                )
                                .toList())
                        .build())
                .toList();
    }

    private BillResponse mapToResponse(Bill bill) {

        BillResponse response = new BillResponse();

        response.setId(bill.getId());
        response.setBillNumber(bill.getBillNumber());

        Customer customer = bill.getCustomer();

        response.setCustomer(new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress()
        ));

        response.setBillDate(bill.getBillDate());
        response.setExpectedDeliveryDate(bill.getExpectedDeliveryDate());
        response.setProcessStatus(bill.getProcessStatus());
        response.setTotalAmount(bill.getTotalAmount());
        response.setCreatedAt(bill.getCreatedAt());
        response.setDeliveredAt(bill.getDeliveredAt());

        List<ProductResponse> products = bill.getProducts()
                .stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .category(product.getCategory())
                        .productType(product.getProductType())
                        .quantity(product.getQuantity())
                        .amount(product.getAmount())
                        .build())
                .toList();

        response.setProducts(products);

        return response;
    }
}
