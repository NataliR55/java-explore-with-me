package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.exception.NotFoundException;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select count(c) from Category c " +
            "where(lower(c.name) like lower(:name)) " +
            "AND (:#{#categoryId == null} = true OR c.id != :categoryId)")
    Long countByName(@Param("name") String name, @Param("categoryId") Long categoryId);

    default Category getCategoryById(Long id) {
        return findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Category with id: %d is not exists!", id)));
    }

    default void categoryExists(Long id) {
        if (!existsById(id)) {
            throw new NotFoundException(String.format("Category with id: %d is not exists!", id));
        }
    }
}
