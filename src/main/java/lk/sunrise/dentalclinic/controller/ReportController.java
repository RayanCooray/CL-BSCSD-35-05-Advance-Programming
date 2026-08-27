package lk.sunrise.dentalclinic.controller;

import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.model.ReportModel;

import java.util.List;

public class ReportController {

    private final ReportModel model = new ReportModel();

    public List<Appointment> dailyAppointments(
            ReportRequestDTO request
    ) throws Exception {

        return model.daily(request);
    }

    public RevenueReportDTO monthlyRevenue(
            int year,
            int month
    ) throws Exception {

        return model.monthly(year, month);
    }
}