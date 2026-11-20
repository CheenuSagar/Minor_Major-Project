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
import java.util.HashSet;
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

        Role cashierRole = roleRepository.findByName(RoleType.ROLE_CASHIER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_CASHIER)
                        .description("Cashier with POS billing and shift audit access")
                        .build()));

        Role ownerRole = roleRepository.findByName(RoleType.ROLE_OWNER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_OWNER)
                        .description("Store Owner with executive revenue and AI forecast access")
                        .build()));

        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_CUSTOMER)
                        .description("Customer with public catalogue view and order access")
                        .build()));

        // 2. Default Store Manager
        userRepository.findByUsername("manager").ifPresentOrElse(
                u -> {
                    u.setPassword(passwordEncoder.encode("Manager@2026"));
                    u.setRoles(new HashSet<>(Set.of(managerRole)));
                    userRepository.save(u);
                },
                () -> userRepository.save(User.builder()
                        .fullName("Store Manager (Admin)")
                        .username("manager")
                        .email("manager@nextstore.com")
                        .password(passwordEncoder.encode("Manager@2026"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(managerRole)))
                        .build())
        );
        log.info("Configured Store Manager account: username='manager', password='Manager@2026'");

        // 3. Default Cashier
        userRepository.findByUsername("cashier").ifPresentOrElse(
                u -> {
                    u.setPassword(passwordEncoder.encode("Cashier@2026"));
                    u.setRoles(new HashSet<>(Set.of(cashierRole)));
                    userRepository.save(u);
                },
                () -> userRepository.save(User.builder()
                        .fullName("Main Counter Cashier")
                        .username("cashier")
                        .email("cashier@nextstore.com")
                        .password(passwordEncoder.encode("Cashier@2026"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(cashierRole)))
                        .build())
        );
        log.info("Configured Cashier account: username='cashier', password='Cashier@2026'");

        // 4. Default Store Owner
        userRepository.findByUsername("owner").ifPresentOrElse(
                u -> {
                    u.setPassword(passwordEncoder.encode("Owner@2026"));
                    u.setRoles(new HashSet<>(Set.of(ownerRole)));
                    userRepository.save(u);
                },
                () -> userRepository.save(User.builder()
                        .fullName("Store Owner (Executive)")
                        .username("owner")
                        .email("owner@nextstore.com")
                        .password(passwordEncoder.encode("Owner@2026"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(ownerRole)))
                        .build())
        );
        log.info("Configured Store Owner account: username='owner', password='Owner@2026'");

        // 5. Default Customer
        userRepository.findByUsername("customer").ifPresentOrElse(
                u -> {
                    u.setPassword(passwordEncoder.encode("Customer@2026"));
                    u.setRoles(new HashSet<>(Set.of(customerRole)));
                    userRepository.save(u);
                },
                () -> userRepository.save(User.builder()
                        .fullName("Rohan Sharma (Customer)")
                        .username("customer")
                        .email("customer@nextstore.com")
                        .password(passwordEncoder.encode("Customer@2026"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(customerRole)))
                        .build())
        );
        log.info("Configured Customer account: username='customer', password='Customer@2026'");

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

        // Additional 12 Products (Electronics, Grocery, Stationery)
        if (!productRepository.existsBySku("ELEC-SONY-CH520")) {
            productRepository.save(Product.builder()
                    .name("Sony WH-CH520 Wireless Bluetooth Headphones")
                    .sku("ELEC-SONY-CH520")
                    .description("Lightweight on-ear wireless headphones with 50-hour battery and DSEE sound")
                    .price(new BigDecimal("4490.00"))
                    .costPrice(new BigDecimal("3100.00"))
                    .stockQuantity(18)
                    .reorderThreshold(5)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("ELEC-APPL-20W")) {
            productRepository.save(Product.builder()
                    .name("Apple 20W USB-C Power Adapter")
                    .sku("ELEC-APPL-20W")
                    .description("Fast, efficient charging for iPhone, iPad, and AirPods at home or on the go")
                    .price(new BigDecimal("1900.00"))
                    .costPrice(new BigDecimal("1350.00"))
                    .stockQuantity(24)
                    .reorderThreshold(8)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("ELEC-PORT-USBC")) {
            productRepository.save(Product.builder()
                    .name("Portronics Konnect Pro Type-C Fast Cable 1.2M")
                    .sku("ELEC-PORT-USBC")
                    .description("Braided durable 3A fast charging and 480Mbps data sync cable")
                    .price(new BigDecimal("249.00"))
                    .costPrice(new BigDecimal("120.00"))
                    .stockQuantity(55)
                    .reorderThreshold(15)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("ELEC-HP-X1000")) {
            productRepository.save(Product.builder()
                    .name("HP X1000 Wired Optical Mouse")
                    .sku("ELEC-HP-X1000")
                    .description("Reliable 3-button USB optical mouse with responsive optical sensor")
                    .price(new BigDecimal("399.00"))
                    .costPrice(new BigDecimal("250.00"))
                    .stockQuantity(32)
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("18.00"))
                    .category(electronics)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-FORT-OIL1L")) {
            productRepository.save(Product.builder()
                    .name("Fortune Sunlite Refined Sunflower Oil 1L")
                    .sku("GROC-FORT-OIL1L")
                    .description("Light, healthy refined cooking oil enriched with Vitamins A and D")
                    .price(new BigDecimal("145.00"))
                    .costPrice(new BigDecimal("115.00"))
                    .stockQuantity(40)
                    .reorderThreshold(12)
                    .taxRate(new BigDecimal("5.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-KELL-CORN500")) {
            productRepository.save(Product.builder()
                    .name("Kellogg's Corn Flakes Original 500g Box")
                    .sku("GROC-KELL-CORN500")
                    .description("Crispy golden flakes made from sun-ripened corn with Iron and B Vitamins")
                    .price(new BigDecimal("215.00"))
                    .costPrice(new BigDecimal("165.00"))
                    .stockQuantity(28)
                    .reorderThreshold(8)
                    .taxRate(new BigDecimal("5.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-MAGG-MAS12")) {
            productRepository.save(Product.builder()
                    .name("Maggi 2-Minute Masala Noodles 12-Pack")
                    .sku("GROC-MAGG-MAS12")
                    .description("India's favourite instant masala noodles made with choicest roasted spices")
                    .price(new BigDecimal("168.00"))
                    .costPrice(new BigDecimal("132.00"))
                    .stockQuantity(65)
                    .reorderThreshold(20)
                    .taxRate(new BigDecimal("12.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("GROC-HALD-BHUJ400")) {
            productRepository.save(Product.builder()
                    .name("Haldiram's Nagpur Bhujia Sev 400g")
                    .sku("GROC-HALD-BHUJ400")
                    .description("Crispy spicy Indian snack made of moth bean flour and aromatic spices")
                    .price(new BigDecimal("130.00"))
                    .costPrice(new BigDecimal("98.00"))
                    .stockQuantity(42)
                    .reorderThreshold(15)
                    .taxRate(new BigDecimal("12.00"))
                    .category(grocery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-CAML-WB4")) {
            productRepository.save(Product.builder()
                    .name("Camlin Kokuyo Whiteboard Marker Pack (4 Colours)")
                    .sku("STAT-CAML-WB4")
                    .description("Bright ink easy dry-wipe whiteboard markers in Black, Blue, Red, Green")
                    .price(new BigDecimal("140.00"))
                    .costPrice(new BigDecimal("95.00"))
                    .stockQuantity(35)
                    .reorderThreshold(10)
                    .taxRate(new BigDecimal("18.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-KANG-STAP10")) {
            productRepository.save(Product.builder()
                    .name("Kangaro No. 10 Heavy Duty Stapler with Pins")
                    .sku("STAT-KANG-STAP10")
                    .description("All-metal body desktop stapler with built-in staple remover and 1000 pins")
                    .price(new BigDecimal("95.00"))
                    .costPrice(new BigDecimal("62.00"))
                    .stockQuantity(48)
                    .reorderThreshold(12)
                    .taxRate(new BigDecimal("18.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-FEVI-GLUE15")) {
            productRepository.save(Product.builder()
                    .name("Fevistik Super Glue Stick 15g (Pack of 3)")
                    .sku("STAT-FEVI-GLUE15")
                    .description("Non-toxic smooth paper bonding adhesive glue stick for office and school")
                    .price(new BigDecimal("105.00"))
                    .costPrice(new BigDecimal("70.00"))
                    .stockQuantity(50)
                    .reorderThreshold(15)
                    .taxRate(new BigDecimal("18.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        if (!productRepository.existsBySku("STAT-CELL-BFLW10")) {
            productRepository.save(Product.builder()
                    .name("Cello Butterflow Classic Blue Ball Pen (Pack of 10)")
                    .sku("STAT-CELL-BFLW10")
                    .description("Ultra-smooth low viscosity ink ball pen with comfortable soft rubber grip")
                    .price(new BigDecimal("100.00"))
                    .costPrice(new BigDecimal("68.00"))
                    .stockQuantity(70)
                    .reorderThreshold(20)
                    .taxRate(new BigDecimal("18.00"))
                    .category(stationery)
                    .active(true)
                    .build());
        }

        log.info("Seed data verification complete.");
    }
}
