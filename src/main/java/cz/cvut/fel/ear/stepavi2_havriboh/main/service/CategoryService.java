package cz.cvut.fel.ear.stepavi2_havriboh.main.service;

import cz.cvut.fel.ear.stepavi2_havriboh.main.dao.CategoryDao;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryHasTransactionsException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.CategoryNotFoundException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.EmptyDescriptionException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.EmptyNameException;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    private void validateCategory(String name, String description) {
        if (name.isBlank()) {
            throw new EmptyNameException("Name cannot be empty");
        }
        if (description.isBlank()) {
            throw new EmptyDescriptionException("Description cannot be empty");
        }
    }
    @Transactional
    public void createCategory(String name, String description) {
        Category category = new Category();
        validateCategory(name, description);
        category.setName(name);
        category.setDescription(description);

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
    public void updateCategoryById(int categoryId, String name, String description) {
        Category category = getCategoryById(categoryId);
        validateCategory(name, description);
        category.setName(name);
        category.setDescription(description);

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
    public void updateCategoryNameById(int categoryId, String newName) {
        Category category = getCategoryById(categoryId);
        validateCategory(newName, category.getDescription());
        category.setName(newName);
        categoryDao.update(category);
    }

    @Transactional
    public void updateCategoryDescriptionById(int categoryId, String newDescription) {
        Category category = getCategoryById(categoryId);
        validateCategory(category.getName(), newDescription);
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
