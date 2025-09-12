package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.transaction.PaypalAuthResponseDto;
import com.shop.online_shop.dto.transaction.TransactionDto;
import com.shop.online_shop.entities.Transaction;
import com.shop.online_shop.entities.Product;
import com.shop.online_shop.entities.User;
import com.shop.online_shop.repositories.TransactionRepository;
import com.shop.online_shop.repositories.ProductRepository;
import com.shop.online_shop.repositories.UserRepository;
import lombok.Data;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Data
class PaypalRequestBody {
    String grant_type;
}

@RestController
//@CrossOrigin(origins = "${ALLOWED_ORIGINS}")
@RequestMapping("api/v1/orders")
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository customerRepository;

    @Value("${PAYPAL_CLIENT_ID}")
    private String PaypalClientID;
    @Value("${PAYPAL_CLIENT_SECRET}")
    private String PaypalSecret;
    @Value("${PAYPAL_URL}")
    private String PaypalUrl;

    public TransactionController(TransactionRepository transactionRepository, ProductRepository productRepository, UserRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    // User type without unsafe information
    public record SafeUser(
            Integer user_id,
            String name,
            String email,
            Integer age
    ) {
    }

    // Type of order to replace standard order entity with unsafe information
    public record SafeTransaction(
            Integer transaction_id,
            List<Product> products,
            SafeUser user,
            Integer[] quantities,
            String status
    ) {
    }

    @GetMapping
    public ResponseEntity<List<SafeTransaction>> getTransactions() {
        List<Transaction> transactions = this.transactionRepository.findAll();
        List<SafeTransaction> safeTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            User user = transaction.getUser_id();
            SafeUser safeUser = new SafeUser(user.getUser_id(), user.getName(), user.getEmail(), user.getAge());
            SafeTransaction safeTransaction = new SafeTransaction(transaction.getTransaction_id(), transaction.getProducts(), safeUser, transaction.getQuantities(), transaction.getStatus());
            safeTransactions.add(safeTransaction);
        }

        return new ResponseEntity<>(safeTransactions, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody TransactionDto orders) {
        Product[] products = new Product[orders.product_ids.length];
        int count = 0;
        for (int id : orders.product_ids) {
            products[count++] = this.productRepository.getReferenceById(id);
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setProducts(Arrays.asList(products));
        Optional<User> user = this.customerRepository.findByEmail(orders.user_email);
        if (user.isPresent()) {
            newTransaction.setUser_id(user.get());
        } else {
            return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
        }

        newTransaction.setQuantities(orders.quantities);
        newTransaction.setStatus("Order received");


        this.transactionRepository.save(newTransaction);
        return new ResponseEntity<>("Successfully placed order", HttpStatus.OK);
    }

    @GetMapping(path = "{transaction_id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable("transaction_id") int id) {
        // implement logic for not found orders
        Optional<Transaction> transaction = this.transactionRepository.findById(id);

        if (transaction.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(transaction.get(), HttpStatus.OK);

    }

    @PatchMapping(path = "{transaction_id}")
    public ResponseEntity<Object> updateTransaction(@PathVariable("transaction_id") int id, SafeTransaction transaction) {
        Transaction toUpdate = this.transactionRepository.getReferenceById(id);

        // Assume toUpdate exists
        toUpdate.setProducts(transaction.products);
        toUpdate.setQuantities(transaction.quantities);
        toUpdate.setStatus(transaction.status);

        this.transactionRepository.save(toUpdate);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @DeleteMapping(path = "{transaction_id}")
    public ResponseEntity<Boolean> deleteTransaction(@PathVariable int transaction_id) {
        Optional<Transaction> toDelete = this.transactionRepository.findById(transaction_id);

        if (toDelete.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        this.transactionRepository.delete(toDelete.get());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PatchMapping(path = "{transaction_id}/status")
    public ResponseEntity<Boolean> updateStatus(@PathVariable int transaction_id) {


        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private <T> ResponseEntity<T> sendRequest(String url, HttpHeaders headers, MultiValueMap<String, String> body, T dtoType) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return (ResponseEntity<T>) restTemplate.postForEntity(url, request, dtoType.getClass());
    }

    private String getPaypalAuthToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = PaypalUrl + "/v1/oauth2/token";

        HttpHeaders headers = new HttpHeaders();

        headers.setBasicAuth(PaypalClientID, PaypalSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        PaypalAuthResponseDto dto = new PaypalAuthResponseDto();

        ResponseEntity<PaypalAuthResponseDto> response = sendRequest(url, headers, body, dto);

        return response.getBody().getAccess_token();
    }


    public Object temp(String uri, HttpHeaders headers, Class<?> classType) {
        WebClient client = WebClient.builder()
                .baseUrl(PaypalUrl)
                .build();
        return client.post()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue("""
                              {
                            "intent": "CAPTURE",
                            "payment_source": {
                                "paypal": {
                                    "experience_context": {
                                        "return_url": "https://developer.paypal.com",
                                        "cancel_url": "https://www.bing.com",
                                        "user_action": "PAY_NOW"
                                    }
                                }
                            },
                            "purchase_units": [
                                {
                                    "amount": {
                                        "currency_code": "USD",
                                        "value": "100.00"
                                    }
                                }
                            ]
                        }
                        """)
                .retrieve().bodyToMono(classType).block();
    }


    @PostMapping(path = "paypal/createorder")
    public ResponseEntity<String> createPaypalOrder() {
        String token = getPaypalAuthToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Object response = temp("/v2/checkout/orders", headers, Object.class);

        System.out.println(response);

        return new ResponseEntity<String>("Test", HttpStatus.CREATED);
    }

}
