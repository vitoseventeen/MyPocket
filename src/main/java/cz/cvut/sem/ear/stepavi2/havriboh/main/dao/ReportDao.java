package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ReportDao extends BaseDao<Report> {

    public ReportDao() {
        super(Report.class);
    }

    public List<Report> findReportsByUserId(int userId) {
        return em.createQuery("SELECT r FROM Report r WHERE r.user.id = :userId", Report.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Report> findReportsByUserIdAndDateRange(int userId, LocalDate fromDate, LocalDate toDate) {
        return em.createQuery("SELECT r FROM Report r WHERE r.user.id = :userId AND r.fromDate >= :fromDate AND r.toDate <= :toDate", Report.class)
                .setParameter("userId", userId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

}
