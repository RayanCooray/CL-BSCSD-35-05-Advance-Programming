package lk.sunrise.dentalclinic.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lk.sunrise.dentalclinic.dao.ReportDAO;
import lk.sunrise.dentalclinic.dto.ReportRequestDTO;
import lk.sunrise.dentalclinic.dto.RevenueReportDTO;
import lk.sunrise.dentalclinic.entity.Appointment;
import lk.sunrise.dentalclinic.entity.AppointmentStatus;
import lk.sunrise.dentalclinic.entity.Dentist;
import lk.sunrise.dentalclinic.entity.Patient;
import lk.sunrise.dentalclinic.entity.Treatment;

public class ReportDAOImpl extends BaseDAOImpl implements ReportDAO {

    @Override
    public List<Appointment> dailyAppointments(
            ReportRequestDTO x
    ) throws Exception {

        String q =
                "SELECT a.*, " +
                        "p.patient_code, " +
                        "p.full_name AS patient_name, " +
                        "d.dentist_id, " +
                        "d.dentist_code, " +
                        "d.full_name AS dentist_name, " +
                        "d.consultation_fee, " +
                        "t.treatment_id, " +
                        "t.treatment_code, " +
                        "t.name AS treatment_name, " +
                        "t.base_price " +
                        "FROM appointments a " +
                        "JOIN patients p ON p.patient_id = a.patient_id " +
                        "JOIN dentists d ON d.dentist_id = a.dentist_id " +
                        "JOIN treatments t ON t.treatment_id = a.treatment_id " +
                        "WHERE a.appointment_date BETWEEN ? AND ?" +
                        (x.getDentistId() == null
                                ? ""
                                : " AND a.dentist_id = ?") +
                        " ORDER BY a.appointment_date, a.start_time";

        List<Appointment> appointments = new ArrayList<>();

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(q)
        ) {

            p.setDate(
                    1,
                    Date.valueOf(x.getFromDate())
            );

            p.setDate(
                    2,
                    Date.valueOf(x.getToDate())
            );

            if (x.getDentistId() != null) {

                p.setInt(
                        3,
                        x.getDentistId()
                );
            }

            try (ResultSet r = p.executeQuery()) {

                while (r.next()) {

                    Patient patient = new Patient();

                    patient.setPatientId(
                            r.getInt("patient_id")
                    );

                    patient.setPatientCode(
                            r.getString("patient_code")
                    );

                    patient.setFullName(
                            r.getString("patient_name")
                    );

                    Dentist dentist = new Dentist();

                    dentist.setDentistId(
                            r.getInt("dentist_id")
                    );

                    dentist.setDentistCode(
                            r.getString("dentist_code")
                    );

                    dentist.setFullName(
                            r.getString("dentist_name")
                    );

                    dentist.setConsultationFee(
                            r.getBigDecimal("consultation_fee")
                    );

                    Treatment treatment = new Treatment();

                    treatment.setTreatmentId(
                            r.getInt("treatment_id")
                    );

                    treatment.setTreatmentCode(
                            r.getString("treatment_code")
                    );

                    treatment.setName(
                            r.getString("treatment_name")
                    );

                    treatment.setBasePrice(
                            r.getBigDecimal("base_price")
                    );

                    Appointment appointment = new Appointment();

                    appointment.setAppointmentId(
                            r.getInt("appointment_id")
                    );

                    appointment.setAppointmentNo(
                            r.getString("appointment_no")
                    );

                    appointment.setPatient(
                            patient
                    );

                    appointment.setDentist(
                            dentist
                    );

                    appointment.setTreatment(
                            treatment
                    );

                    appointment.setAppointmentDate(
                            date(
                                    r,
                                    "appointment_date"
                            )
                    );

                    appointment.setStartTime(
                            time(
                                    r,
                                    "start_time"
                            )
                    );

                    appointment.setEndTime(
                            time(
                                    r,
                                    "end_time"
                            )
                    );

                    appointment.setStatus(
                            AppointmentStatus.valueOf(
                                    r.getString("status")
                            )
                    );

                    appointment.setRemarks(
                            r.getString("remarks")
                    );

                    appointments.add(
                            appointment
                    );
                }
            }
        }

        return appointments;
    }

    @Override
    public RevenueReportDTO monthlyRevenue(
            int year,
            int month
    ) throws Exception {

        String q =
                "SELECT " +
                        "COUNT(*) AS invoice_count, " +
                        "COALESCE(SUM(i.total_amount), 0) AS revenue, " +
                        "COALESCE(SUM(i.tax_amount), 0) AS tax, " +
                        "COALESCE(SUM(paid.paid), 0) AS collected " +
                        "FROM invoice i " +
                        "LEFT JOIN ( " +
                        "    SELECT invoice_id, SUM(amount_paid) AS paid " +
                        "    FROM payment " +
                        "    GROUP BY invoice_id " +
                        ") paid ON paid.invoice_id = i.invoice_id " +
                        "WHERE YEAR(i.issue_date) = ? " +
                        "AND MONTH(i.issue_date) = ?";

        try (
                Connection c = connection();
                PreparedStatement p = c.prepareStatement(q)
        ) {

            p.setInt(
                    1,
                    year
            );

            p.setInt(
                    2,
                    month
            );

            try (ResultSet r = p.executeQuery()) {

                r.next();

                BigDecimal revenue =
                        r.getBigDecimal("revenue");

                BigDecimal collected =
                        r.getBigDecimal("collected");

                BigDecimal tax =
                        r.getBigDecimal("tax");

                BigDecimal outstanding =
                        revenue.subtract(collected);

                return new RevenueReportDTO(
                        r.getInt("invoice_count"),
                        revenue,
                        tax,
                        collected,
                        outstanding
                );
            }
        }
    }
}