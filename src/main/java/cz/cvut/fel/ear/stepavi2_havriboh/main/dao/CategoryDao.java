package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao extends BaseDao<Category> {
    public CategoryDao() {
        super(Category.class);
    }

    public Category findByName(String name) {
        try {
            return em.createNamedQuery("Category.findByName", Category.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean deleteByName(String name) {
        Category category = findByName(name);
        if (category != null) {
            em.remove(category);
            return true;
        }
        return false;
    }
}
