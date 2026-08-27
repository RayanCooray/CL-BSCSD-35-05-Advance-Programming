package lk.sunrise.dentalclinic.dao;

import java.util.*;
import lk.sunrise.dentalclinic.dto.*;
import lk.sunrise.dentalclinic.entity.*;

public interface ReportDAO {
    List<Appointment> dailyAppointments(ReportRequestDTO request) throws Exception;
    RevenueReportDTO monthlyRevenue(int year,int month) throws Exception;
}
