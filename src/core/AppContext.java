package core;

import core.controller.AppointmentController;
import core.controller.AuthController;
import core.controller.DoctorController;
import core.controller.HospitalizationController;
import core.controller.PatientController;
import core.controller.PrescriptionController;
import core.model.business.AppointmentManager;
import core.model.business.AuthManager;
import core.model.business.HospitalizationManager;
import core.model.repositories.AppointmentRepository;
import core.model.repositories.HospitalizationRepository;
import core.model.repositories.UserRepository;

public final class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;

    private final AuthController authController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;
    private final PrescriptionController prescriptionController;

    private AppContext() {
        userRepository = new UserRepository();
        appointmentRepository = new AppointmentRepository();
        hospitalizationRepository = new HospitalizationRepository();

        AuthManager authManager = new AuthManager(userRepository);
        AppointmentManager appointmentManager = new AppointmentManager(appointmentRepository, userRepository);
        HospitalizationManager hospitalizationManager =
                new HospitalizationManager(hospitalizationRepository, appointmentRepository);

        authController = new AuthController(authManager);
        patientController = new PatientController(userRepository);
        doctorController = new DoctorController(userRepository);
        appointmentController = new AppointmentController(appointmentManager, appointmentRepository, userRepository);
        hospitalizationController = new HospitalizationController(
                hospitalizationManager, hospitalizationRepository, userRepository, appointmentRepository);
        prescriptionController = new PrescriptionController(appointmentRepository);
    }

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AppointmentRepository getAppointmentRepository() {
        return appointmentRepository;
    }

    public HospitalizationRepository getHospitalizationRepository() {
        return hospitalizationRepository;
    }

    public AuthController getAuthController() {
        return authController;
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public DoctorController getDoctorController() {
        return doctorController;
    }

    public AppointmentController getAppointmentController() {
        return appointmentController;
    }

    public HospitalizationController getHospitalizationController() {
        return hospitalizationController;
    }

    public PrescriptionController getPrescriptionController() {
        return prescriptionController;
    }
}
