package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryHasTransactionsException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NegativeCategoryLimitException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Transactional
    public void createCategory(String name, String description, BigDecimal defaultLimit) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setDefaultLimit(defaultLimit);

        categoryDao.persist(category);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(int categoryId) {
        Category category = categoryDao.find(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found");
        }
        return category;
    }

    @Transactional
    public void updateCategoryById(int categoryId, String name, String description, BigDecimal defaultLimit) {
        Category category = getCategoryById(categoryId);
        category.setName(name);
        category.setDescription(description);
        category.setDefaultLimit(defaultLimit);

        categoryDao.update(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategoryId(int categoryId) {
        Category category = getCategoryById(categoryId);
        return category.getTransactions();
    }

    @Transactional(readOnly = true)
    public BigDecimal getCategoryLimitById(int categoryId) {
        Category category = getCategoryById(categoryId);
        return category.getDefaultLimit();
    }


    @Transactional(readOnly = true)
    public String getCategoryNameById(int categoryId) {
        Category category = getCategoryById(categoryId);
        return category.getName();
    }

    @Transactional(readOnly = true)
    public String getCategoryDescriptionById(int categoryId) {
        Category category = getCategoryById(categoryId);
        return category.getDescription();
    }

    @Transactional
    public void updateCategoryLimitById(int categoryId, BigDecimal newLimit) {
        if (newLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeCategoryLimitException("Limit cannot be negative");
        }
        Category category = getCategoryById(categoryId);
        category.setDefaultLimit(newLimit);
        categoryDao.update(category);
    }


    @Transactional
    public void updateCategoryNameById(int categoryId, String newName) {
        Category category = getCategoryById(categoryId);
        category.setName(newName);
        categoryDao.update(category);
    }

    @Transactional
    public void updateCategoryDescriptionById(int categoryId, String newDescription) {
        Category category = getCategoryById(categoryId);
        category.setDescription(newDescription);
        categoryDao.update(category);
    }

    @Transactional
    public void deleteCategoryById(int categoryId) {
        Category category = getCategoryById(categoryId);
        if (!category.getTransactions().isEmpty()) {
            throw new CategoryHasTransactionsException("Category has associated transactions, cannot delete");
        }
        categoryDao.remove(category);
    }

}
