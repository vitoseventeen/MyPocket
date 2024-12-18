package cz.cvut.sem.ear.stepavi2.havriboh.main.service;


import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.UserDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.*;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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

    @SpyBean
    private CategoryDao categoryDao;

    private Category category;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setDefaultLimit(BigDecimal.valueOf(100));
    }

    @Test
    public void createCategoryCreatesCategoryIfDataIsValid() {
        categoryService.createCategory("Valid Name", "Valid Description", BigDecimal.valueOf(200));
        verify(categoryDao, times(1)).persist(Mockito.any(Category.class));
    }

    @Test
    public void createCategoryThrowsEmptyNameException() {
        assertThrows(EmptyNameException.class, () ->
                categoryService.createCategory("", "Valid Description", BigDecimal.valueOf(200)));
    }

    @Test
    public void createCategoryThrowsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                categoryService.createCategory("Valid Name", "", BigDecimal.valueOf(200)));
    }

    @Test
    public void createCategoryThrowsNegativeCategoryLimitException() {
        assertThrows(NegativeCategoryLimitException.class, () ->
                categoryService.createCategory("Valid Name", "Valid Description", BigDecimal.valueOf(-100)));
    }

    @Test
    public void getCategoryByIdReturnsCategoryIfCategoryExists() {
        when(categoryDao.find(1)).thenReturn(category);

        Category result = categoryService.getCategoryById(1);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(BigDecimal.valueOf(100), result.getDefaultLimit());
    }

    @Test
    public void getCategoryByIdThrowsCategoryNotFoundException() {
        when(categoryDao.find(1)).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1));
    }

    @Test
    public void updateCategoryByIdUpdatesCategory() {
        when(categoryDao.find(1)).thenReturn(category);

        categoryService.updateCategoryById(1, "Updated Name", "Updated Description", BigDecimal.valueOf(300));

        assertEquals("Updated Name", category.getName());
        assertEquals("Updated Description", category.getDescription());
        assertEquals(BigDecimal.valueOf(300), category.getDefaultLimit());
        verify(categoryDao, times(1)).update(category);
    }

    @Test
    public void updateCategoryByIdThrowsCategoryNotFoundException() {
        when(categoryDao.find(1)).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategoryById(1, "Updated Name", "Updated Description", BigDecimal.valueOf(300)));
    }

    @Test
    public void deleteCategoryByIdDeletesCategory() {
        Category newCategory = new Category();
        newCategory.setName("CategoryToDelete");
        newCategory.setDescription("Description");
        newCategory.setDefaultLimit(BigDecimal.valueOf(100));
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
        transaction.setUser(userDao.find(1));
        transactionDao.persist(transaction);

        Category newCategory = new Category();
        newCategory.setName("CategoryToDelete");
        newCategory.setDescription("Description");
        newCategory.setDefaultLimit(BigDecimal.valueOf(100));
        newCategory.setTransactions(Collections.singletonList(transaction));
        categoryDao.persist(newCategory);

        assertTrue(!newCategory.getTransactions().isEmpty(), "Category should have transactions");

        assertThrows(CategoryHasTransactionsException.class, () -> {
            categoryService.deleteCategoryById(newCategory.getId());
        });

        Category deletedCategory = categoryDao.find(newCategory.getId());
        assertNotNull(deletedCategory, "Category should not be deleted");
    }


}
