package lk.sunrise.dentalclinic.model;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;
import lk.sunrise.dentalclinic.factory.DAOFactory;

import java.util.*;

public class ReportModel {
    private final ReportDAO dao;

    public ReportModel() {
        this(DAOFactory.reportDAO());
    }

    ReportModel(ReportDAO dao) {
        this.dao = dao;
    }

    public List<Appointment> daily(ReportRequestDTO r) throws Exception {
        return dao.dailyAppointments(r);
    }

    public RevenueReportDTO monthly(int y, int m) throws Exception {
        return dao.monthlyRevenue(y, m);
    }
}
