package packagee;

import com.formdev.flatlaf.FlatDarkLaf;
import packagee.controller.AppointmentController;
import packagee.controller.AuthController;
import packagee.controller.DoctorController;
import packagee.controller.HospitalizationController;
import packagee.controller.PatientController;
import packagee.controller.PrescriptionController;
import packagee.model.repositories.AppointmentRepository;
import packagee.model.repositories.HospitalizationRepository;
import packagee.model.repositories.UserRepository;
import packagee.view.LoginView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf theme: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            UserRepository userRepository = new UserRepository();
            AppointmentRepository appointmentRepository = new AppointmentRepository();
            HospitalizationRepository hospitalizationRepository = new HospitalizationRepository();

            AuthController authController = new AuthController(userRepository);
            PatientController patientController = new PatientController(userRepository);
            DoctorController doctorController = new DoctorController(userRepository);
            AppointmentController appointmentController = new AppointmentController(appointmentRepository, userRepository);
            HospitalizationController hospitalizationController = new HospitalizationController(
                    hospitalizationRepository, userRepository, appointmentRepository);
            PrescriptionController prescriptionController = new PrescriptionController(appointmentRepository);

            LoginView loginView = new LoginView(authController, patientController, doctorController,
                    appointmentController, hospitalizationController, prescriptionController, userRepository);
            loginView.setVisible(true);
        });
    }
}
