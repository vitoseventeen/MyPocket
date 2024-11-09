package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao extends BaseDao<Report> {
    public ReportDao() {
        super(Report.class);
    }

    public Report getReportByUserId(int userId) {
        return em.createQuery("SELECT r FROM Report r WHERE r.user.id = :userId", Report.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

}
