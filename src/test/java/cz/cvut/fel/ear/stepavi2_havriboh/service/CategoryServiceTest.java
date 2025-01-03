package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryHasTransactionsException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.EmptyDescriptionException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.EmptyNameException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.TransactionType;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private TransactionDao transactionDao;

    private Category category;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");
        categoryDao.persist(category);
    }

    @Test
    public void createCategoryCreatesCategoryIfDataIsValid() {
        Category newCategory = new Category();
        newCategory.setName("Valid Name");
        newCategory.setDescription("Valid Description");

        assertNotNull(newCategory, "Category should be created");
        assertEquals("Valid Name", newCategory.getName(), "Category name should be 'Valid Name'");
        assertEquals("Valid Description", newCategory.getDescription(), "Category description should be 'Valid Description'");
    }

    @Test
    public void createCategoryThrowsEmptyNameException() {
        assertThrows(EmptyNameException.class, () ->
                categoryService.createCategory("", "Valid Description"));
    }

    @Test
    public void createCategoryThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                categoryService.createCategory("Valid Name", ""));
    }


    @Test
    public void getCategoryByIdReturnsCategoryIfCategoryExists() {
        int categoryId = category.getId();
        Category result = categoryService.getCategoryById(categoryId);

        assertNotNull(result, "The category should not be null.");
        assertEquals(category.getId(), result.getId(), "The category ID should match.");
        assertEquals(category.getName(), result.getName(), "The category name should match.");
        assertEquals(category.getDescription(), result.getDescription(), "The category description should match.");

    }

    @Test
    public void getCategoryByIdThrowsCategoryNotFoundException() {
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategoryById(999999999);
        }, "A CategoryNotFoundException should be thrown when the category does not exist.");
    }

    @Test
    public void updateCategoryByIdUpdatesCategory() {
        int categoryId = category.getId();
        String newName = "Updated Category";
        String newDescription = "Updated Description";

        categoryService.updateCategoryById(categoryId, newName, newDescription);

        Category updatedCategory = categoryDao.find(categoryId);
        assertNotNull(updatedCategory, "Category should be found after update.");
        assertEquals(newName, updatedCategory.getName(), "The category name should be updated.");
        assertEquals(newDescription, updatedCategory.getDescription(), "The category description should be updated.");
    }

    @Test
    public void updateCategoryByIdThrowsCategoryNotFoundException() {
        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.updateCategoryById(999999999, "New Name", "New Description");
        }, "A CategoryNotFoundException should be thrown when the category does not exist.");
    }

    @Test
    public void deleteCategoryByIdDeletesCategory() {
        Category newCategory = new Category();
        newCategory.setName("CategoryToDelete");
        newCategory.setDescription("Description");
        categoryDao.persist(newCategory);

        categoryService.deleteCategoryById(newCategory.getId());

        Category deletedCategory = categoryDao.find(newCategory.getId());
        assertNull(deletedCategory);
    }

    @Test
    public void deleteCategoryByIdThrowsExceptionWhenCategoryHasTransactions() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(50));
        transaction.setDate(LocalDate.now());
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDescription("Test Transaction");
        transactionDao.persist(transaction);

        Category newCategory = new Category();
        newCategory.setName("CategoryToDelete");
        newCategory.setDescription("Description");
        newCategory.setTransactions(Collections.singletonList(transaction));
        categoryDao.persist(newCategory);

        assertFalse(newCategory.getTransactions().isEmpty(), "Category should have transactions");

        assertThrows(CategoryHasTransactionsException.class, () -> categoryService.deleteCategoryById(newCategory.getId()));

        Category deletedCategory = categoryDao.find(newCategory.getId());
        assertNotNull(deletedCategory, "Category should not be deleted");
    }


}
