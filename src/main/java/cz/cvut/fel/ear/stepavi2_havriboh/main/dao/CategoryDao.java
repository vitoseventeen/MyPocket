package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao extends BaseDao<Category> {
    public CategoryDao() {
        super(Category.class);
    }

    public Category findByName(String name) {
        return em.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", name)
                .getSingleResult();
    }


}
