package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.PremiumUser;
import org.springframework.stereotype.Repository;

@Repository
public class PremiumUserDao extends BaseDao<PremiumUser>{
    public PremiumUserDao() {
        super(PremiumUser.class);
    }
}
