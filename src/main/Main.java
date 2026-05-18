package main;

import com.formdev.flatlaf.FlatDarkLaf;
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
import core.model.repositories.IAppointmentRepository;
import core.model.repositories.IHospitalizationRepository;
import core.model.repositories.IUserRepository;
import core.model.repositories.UserRepository;
import core.view.LoginView;

import javax.swing.*;

/**
 * Punto de entrada y raiz de composicion (Composition Root).
 *
 * Este es el UNICO lugar donde se instancian clases concretas.
 * Todo el resto del sistema depende de interfaces (Dependency Inversion).
 *
 * Flujo de dependencias:
 *   Repositorios concretos → Managers (logica de negocio) → Controladores → Vistas
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf theme: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {

            // --- Repositorios (implementaciones concretas, declaradas como interfaces) ---
            IUserRepository userRepository                       = new UserRepository();
            IAppointmentRepository appointmentRepository         = new AppointmentRepository();
            IHospitalizationRepository hospitalizationRepository = new HospitalizationRepository();

            // --- Managers (logica de negocio, capa business/) ---
            AuthManager authManager               = new AuthManager(userRepository);
            AppointmentManager appointmentManager = new AppointmentManager(appointmentRepository, userRepository);
            HospitalizationManager hospManager    = new HospitalizationManager(hospitalizationRepository, appointmentRepository);

            // --- Controladores (reciben managers e interfaces de repositorio) ---
            AuthController authController                       = new AuthController(authManager);
            PatientController patientController                 = new PatientController(userRepository);
            DoctorController doctorController                   = new DoctorController(userRepository);
            AppointmentController appointmentController         = new AppointmentController(appointmentManager, appointmentRepository, userRepository);
            HospitalizationController hospitalizationController = new HospitalizationController(
                    hospManager, hospitalizationRepository, userRepository, appointmentRepository);
            PrescriptionController prescriptionController       = new PrescriptionController(appointmentRepository);

            // --- Vista inicial ---
            LoginView loginView = new LoginView(authController, patientController, doctorController,
                    appointmentController, hospitalizationController, prescriptionController, userRepository);
            loginView.setVisible(true);
        });
    }
}
