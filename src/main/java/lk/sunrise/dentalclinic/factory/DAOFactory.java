package lk.sunrise.dentalclinic.factory;

import lk.sunrise.dentalclinic.dao.*;
import lk.sunrise.dentalclinic.dao.impl.*;

public final class DAOFactory {
    private DAOFactory() {
    }
    public static UserDAO userDAO() {
        return new UserDAOImpl();
    }
    public static PatientDAO patientDAO() {
        return new PatientDAOImpl();
    }
    public static DentistDAO dentistDAO() {
        return new DentistDAOImpl();
    }
    public static TreatmentDAO treatmentDAO() {
        return new TreatmentDAOImpl();
    }
    public static AppointmentDAO appointmentDAO() {
        return new AppointmentDAOImpl();
    }
    public static TreatmentRecordDAO treatmentRecordDAO() {
        return new TreatmentRecordDAOImpl();
    }
    public static InvoiceDAO invoiceDAO() {
        return new InvoiceDAOImpl();
    }
    public static PaymentDAO paymentDAO() {
        return new PaymentDAOImpl();
    }
    public static ReportDAO reportDAO() {
        return new ReportDAOImpl();
    }
}
