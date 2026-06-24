package com.ecommerce.repository;
import com.ecommerce.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
