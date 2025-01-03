package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ReportDao extends BaseDao<Report> {

    public ReportDao() {
        super(Report.class);
    }

    public List<Report> findAll() {
        return em.createQuery("SELECT r FROM Report r", Report.class).getResultList();
    }

    public List<Report> findByAccountId(int accountId) {
        return em.createNamedQuery("Report.findByAccountId", Report.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    public List<Report> findByDateRange(LocalDate fromDate, LocalDate toDate) {
        return em.createNamedQuery("Report.findByDateRange", Report.class)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

    public List<Report> findByAccountAndDateRange(int accountId, LocalDate fromDate, LocalDate toDate) {
        return em.createNamedQuery("Report.findByAccountAndDateRange", Report.class)
                .setParameter("accountId", accountId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }
}

