package cz.cvut.sem.ear.stepavi2.havriboh.main.service;

import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.CategoryDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.dao.TransactionDao;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.CategoryLimitExceededException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryService {
    private final CategoryDao categoryDao;
    private final TransactionDao transactionDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao, TransactionDao transactionDao) {
        this.categoryDao = categoryDao;
        this.transactionDao = transactionDao;
    }

}
