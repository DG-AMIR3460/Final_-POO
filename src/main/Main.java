package main;

import com.formdev.flatlaf.FlatDarkLaf;
import main.controller.AppointmentController;
import main.controller.AuthController;
import main.controller.DoctorController;
import main.controller.HospitalizationController;
import main.controller.PatientController;
import main.controller.PrescriptionController;
import main.model.business.AppointmentManager;
import main.model.business.AuthManager;
import main.model.business.HospitalizationManager;
import main.model.repositories.AppointmentRepository;
import main.model.repositories.HospitalizationRepository;
import main.model.repositories.IAppointmentRepository;
import main.model.repositories.IHospitalizationRepository;
import main.model.repositories.IUserRepository;
import main.model.repositories.UserRepository;
import main.view.LoginView;

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
