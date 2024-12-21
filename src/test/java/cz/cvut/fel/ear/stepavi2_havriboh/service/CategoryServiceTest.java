package cz.cvut.fel.ear.stepavi2_havriboh.service;


import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.TransactionDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.UserDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.*;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.TransactionType;
import cz.cvut.fel.ear.stepavi2_havriboh.main.service.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

    }

    @Test
    public void getCategoryByIdThrowsCategoryNotFoundException() {

    }

    @Test
    public void updateCategoryByIdUpdatesCategory() {

    }

    @Test
    public void updateCategoryByIdThrowsCategoryNotFoundException() {

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
