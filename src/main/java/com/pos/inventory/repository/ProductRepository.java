package com.pos.inventory.repository;

import com.pos.inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity <= p.reorderThreshold ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.stockQuantity <= p.reorderThreshold")
    long countLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> searchPublicProducts(@Param("keyword") String keyword,
                                       @Param("categoryId") Long categoryId,
                                       Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:active IS NULL OR p.active = :active) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> findAllWithFilters(@Param("keyword") String keyword,
                                     @Param("categoryId") Long categoryId,
                                     @Param("active") Boolean active,
                                     Pageable pageable);

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
}
