package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReportDao extends BaseDao<Report> {
    public ReportDao() {
        super(Report.class);
    }

    public Optional<Report> getReportByUserId(int userId) {
        try {
            return Optional.ofNullable(
                    em.createQuery("SELECT r FROM Report r WHERE r.user.id = :userId", Report.class)
                            .setParameter("userId", userId)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


}
