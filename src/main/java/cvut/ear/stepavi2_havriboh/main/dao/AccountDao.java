package cvut.ear.stepavi2_havriboh.main.dao;

import cvut.ear.stepavi2_havriboh.main.model.Account;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDao extends BaseDao<Account> {
    public AccountDao() {
        super(Account.class);
    }

}
