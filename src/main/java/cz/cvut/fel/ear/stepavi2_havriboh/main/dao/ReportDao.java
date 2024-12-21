package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDao extends BaseDao<Report> {

    public ReportDao() {
        super(Report.class);
    }

    public List<Report> findReportsByUserId(int userId) {
        return em.createQuery(
                        "SELECT r FROM Report r JOIN FETCH r.user WHERE r.user.id = :userId", Report.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Report findWithUser(int reportId) {
        return em.createQuery("SELECT r FROM Report r JOIN FETCH r.user WHERE r.id = :reportId", Report.class)
                .setParameter("reportId", reportId)
                .getSingleResult();
    }

}
