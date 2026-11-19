package com.pos.inventory.config;

import com.pos.inventory.entity.*;
import com.pos.inventory.repository.CategoryRepository;
import com.pos.inventory.repository.ProductRepository;
import com.pos.inventory.repository.RoleRepository;
import com.pos.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and initializing seed data...");

        // 1. Roles
        Role managerRole = roleRepository.findByName(RoleType.ROLE_STORE_MANAGER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_STORE_MANAGER)
                        .description("Store Manager with full administrative access")
                        .build()));

        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_CUSTOMER)
                        .description("Customer with public catalogue view and order access")
                        .build()));

        // 2. Default Store Manager
        if (!userRepository.existsByUsername("manager")) {
            User manager = User.builder()
                    .fullName("Default Store Manager")
                    .username("manager")
                    .email("manager@possystem.com")
                    .password(passwordEncoder.encode("Manager@123"))
                    .enabled(true)
                    .roles(Set.of(managerRole))
                    .build();
            userRepository.save(manager);
            log.info("Created default Store Manager account: username='manager', password='Manager@123'");
        }

        // 3. Default Customer
        if (!userRepository.existsByUsername("customer")) {
            User customer = User.builder()
                    .fullName("Rohan Sharma")
                    .username("customer")
                    .email("customer@possystem.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .enabled(true)
                    .roles(Set.of(customerRole))
                    .build();
            userRepository.save(customer);
            log.info("Created default Customer account: username='customer', password='Customer@123'");
        }

        // 4. Sample Categories
        Category electronics = categoryRepository.findByNameIgnoreCase("Electronics")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Electronics")
                        .description("Gadgets, accessories and electronic equipment")
                        .active(true)
                        .build()));

        Category grocery = categoryRepository.findByNameIgnoreCase("Grocery")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Grocery")
                        .description("Daily FMCG, packaged foods and beverages")
                        .active(true)
                        .build()));

        Category stationery = categoryRepository.findByNameIgnoreCase("Stationery")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Stationery")
                        .description("Office supplies, books, pens and paper")
                        .active(true)
                        .build()));

        // 5. Sample Products
        if (!productRepository.existsBySku("ELEC-LOGI-M185")) {
            productRepository.save(Product.builder()
                    .name("Logitech M185 Wireless Mouse")
                    .sku("ELEC-LOGI-M185")
                    .description("2.4GHz wireless mouse with nano USB receiver, 12-month battery life")
                    .price(new BigDecimal("799.00"))
                    .costPrice(new BigDecimal("520.00"))
                    .stockQuantity(45)
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("ELEC-SAMS-T500")) {
            productRepository.save(Product.builder()
                    .name("Samsung 25W Type-C Fast Charger")
                    .sku("ELEC-SAMS-T500")
                    .description("Super fast charging adapter with power delivery 3.0")
                    .price(new BigDecimal("1299.00"))
                    .costPrice(new BigDecimal("850.00"))
                    .stockQuantity(8) // low stock (< 10)
                    .reorderThreshold(12)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-NESC-CL200")) {
            productRepository.save(Product.builder()
                    .name("Nescafe Classic Instant Coffee Jar 200g")
                    .sku("GROC-NESC-CL200")
                    .description("100% pure natural instant coffee powder")
                    .price(new BigDecimal("599.00"))
                    .costPrice(new BigDecimal("460.00"))
                    .stockQuantity(60)
                    .reorderThreshold(15)
                    .taxRate(new BigDecimal("5.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-CLAS-REG400")) {
            productRepository.save(Product.builder()
                    .name("Classmate Long Notebook 400 Pages")
                    .sku("STAT-CLAS-REG400")
                    .description("Single ruled premium spiral notebook for college")
                    .price(new BigDecimal("160.00"))
                    .costPrice(new BigDecimal("110.00"))
                    .stockQuantity(4) // low stock
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("12.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        // Additional Electronics
        if (!productRepository.existsBySku("ELEC-BOAT-ROK255")) {
            productRepository.save(Product.builder()
                    .name("boAt Rockerz 255 Pro+ Earphones")
                    .sku("ELEC-BOAT-ROK255")
                    .description("Bluetooth wireless neckband with ASAP charge and 40H battery")
                    .price(new BigDecimal("1499.00"))
                    .costPrice(new BigDecimal("950.00"))
                    .stockQuantity(25)
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("ELEC-SAND-USB64")) {
            productRepository.save(Product.builder()
                    .name("SanDisk Ultra 64GB USB 3.0 Pen Drive")
                    .sku("ELEC-SAND-USB64")
                    .description("High-speed flash drive with up to 130MB/s read speeds")
                    .price(new BigDecimal("549.00"))
                    .costPrice(new BigDecimal("350.00"))
                    .stockQuantity(35)
                    .reorderThreshold(12)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        // Additional Grocery
        if (!productRepository.existsBySku("GROC-TATA-TEA500")) {
            productRepository.save(Product.builder()
                    .name("Tata Tea Gold Premium 500g")
                    .sku("GROC-TATA-TEA500")
                    .description("Rich aroma black tea with gently rolled aromatic long leaves")
                    .price(new BigDecimal("340.00"))
                    .costPrice(new BigDecimal("260.00"))
                    .stockQuantity(40)
                    .reorderThreshold(15)
                    .taxRate(new BigDecimal("5.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-CADB-SILK150")) {
            productRepository.save(Product.builder()
                    .name("Cadbury Dairy Milk Silk Chocolate 150g")
                    .sku("GROC-CADB-SILK150")
                    .description("Delicious creamy milk chocolate bar")
                    .price(new BigDecimal("175.00"))
                    .costPrice(new BigDecimal("130.00"))
                    .stockQuantity(50)
                    .reorderThreshold(20)
                    .taxRate(new BigDecimal("18.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-AMUL-BUTTER")) {
            productRepository.save(Product.builder()
                    .name("Amul Pasteurised Butter 500g")
                    .sku("GROC-AMUL-BUTTER")
                    .description("Utterly butterly delicious pure butter from fresh cream")
                    .price(new BigDecimal("275.00"))
                    .costPrice(new BigDecimal("220.00"))
                    .stockQuantity(30)
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("12.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        // Additional Stationery
        if (!productRepository.existsBySku("STAT-PARK-JOTTER")) {
            productRepository.save(Product.builder()
                    .name("Parker Jotter Stainless Steel Ball Pen")
                    .sku("STAT-PARK-JOTTER")
                    .description("Iconic retractable stainless steel barrel with Quinkflow ink")
                    .price(new BigDecimal("320.00"))
                    .costPrice(new BigDecimal("210.00"))
                    .stockQuantity(15)
                    .reorderThreshold(8)
                    .taxRate(new BigDecimal("18.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-CASI-FX991")) {
            productRepository.save(Product.builder()
                    .name("Casio FX-991CW Scientific Calculator")
                    .sku("STAT-CASI-FX991")
                    .description("Advanced 540 functions scientific calculator for engineering & MCA")
                    .price(new BigDecimal("1295.00"))
                    .costPrice(new BigDecimal("980.00"))
                    .stockQuantity(12)
                    .reorderThreshold(5)
                    .taxRate(new BigDecimal("18.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-POST-IT300")) {
            productRepository.save(Product.builder()
                    .name("Post-it 3x3 Self-Adhesive Notes Pack")
                    .sku("STAT-POST-IT300")
                    .description("Multi-colour sticky memo notes 100 sheets x 3 pads")
                    .price(new BigDecimal("145.00"))
                    .costPrice(new BigDecimal("90.00"))
                    .stockQuantity(22)
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("12.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        log.info("Seed data verification complete.");
    }
}
