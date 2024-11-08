package cz.cvut.sem.ear.stepavi2.havriboh.main.dao;

import cz.cvut.sem.ear.stepavi2.havriboh.main.model.Report;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao extends BaseDao<Report> {
    public ReportDao() {
        super(Report.class);
    }
}
