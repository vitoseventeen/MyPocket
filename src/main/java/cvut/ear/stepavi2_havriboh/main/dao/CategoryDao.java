package cvut.ear.stepavi2_havriboh.main.dao;

import cvut.ear.stepavi2_havriboh.main.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao extends BaseDao<Category> {
    public CategoryDao() {
        super(Category.class);
    }


}
