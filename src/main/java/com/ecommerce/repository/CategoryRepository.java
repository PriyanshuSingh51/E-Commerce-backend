package com.ecommerce.repository;
import com.ecommerce.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    List<Category> findRootCategories();
    List<Category> findByParentCategoryId(Long parentId);
}
