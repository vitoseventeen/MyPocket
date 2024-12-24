package cz.cvut.fel.ear.stepavi2_havriboh.main.dao;

import cz.cvut.fel.ear.stepavi2_havriboh.main.model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDao extends BaseDao<Report> {

    public ReportDao() {
        super(Report.class);
    }

}
