package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryLimitExceededException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setName("Food");
        category.setDefaultLimit(new BigDecimal("500.00"));
    }

    @Test
    void testCheckCategoryLimit_noSpentAmount() {
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(BigDecimal.ZERO);

        BigDecimal transactionAmount = new BigDecimal("100.00");

        assertDoesNotThrow(() -> categoryService.checkCategoryLimit(category, transactionAmount));

        verify(transactionDao, times(1)).getTotalSpentByCategory(category);
    }

    @Test
    void testCheckCategoryLimit_limitExceeded() {
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(new BigDecimal("450.00"));

        BigDecimal transactionAmount = new BigDecimal("100.00");

        assertThrows(CategoryLimitExceededException.class, () -> categoryService.checkCategoryLimit(category, transactionAmount));

        verify(transactionDao, times(1)).getTotalSpentByCategory(category);
    }

    @Test
    void testCheckCategoryLimit_exactLimit() {
        when(transactionDao.getTotalSpentByCategory(category)).thenReturn(new BigDecimal("500.00"));

        BigDecimal transactionAmount = new BigDecimal("0.00");

        assertDoesNotThrow(() -> categoryService.checkCategoryLimit(category, transactionAmount));

        verify(transactionDao, times(1)).getTotalSpentByCategory(category);
    }
}
