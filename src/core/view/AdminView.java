package core.view;

import core.controller.AppointmentController;
import core.controller.AuthController;
import core.controller.DoctorController;
import core.controller.HospitalizationController;
import core.controller.PatientController;
import core.controller.PrescriptionController;
import core.controller.Response;
import core.model.business.AuthManager;

import core.model.entities.Administrator;
import core.model.entities.Doctor;
import core.model.entities.Patient;
import core.model.entities.User;
import core.model.enums.Specialty;
import core.model.observers.ModelObserver;
import core.model.repositories.IUserRepository;

import javax.swing.*;
import java.awt.*;

public class AdminView extends JFrame implements ModelObserver {

    private int dragX, dragY;
    private final Administrator admin;
    private final DoctorController doctorController;
    private final PatientController patientController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;
    private final PrescriptionController prescriptionController;
    private final IUserRepository userRepository;

    // Title bar
    private PanelRound pnlMain;
    private PanelRound pnlTitleBar;
    private JButton btnClose;
    private JLabel lblTitle;
    private PanelRound pnlContent;

    // Navigation
    private JButton btnDoctorView;
    private JButton btnPatientView;
    private JButton btnLogout;

    // Doctor registration fields
    private JLabel lblFirstname;     private JTextField txtFirstname;
    private JLabel lblLastname;      private JTextField txtLastname;
    private JLabel lblId;            private JTextField txtId;
    private JLabel lblSpecialty;     private JComboBox<String> cmbSpecialty;
    private JLabel lblLicence;       private JTextField txtLicence;
    private JLabel lblOffice;        private JTextField txtOffice;
    private JLabel lblUsername;      private JTextField txtUsername;
    private JLabel lblPassword;      private JTextField txtPassword;
    private JLabel lblPasswordConfirm; private JTextField txtPasswordConfirm;
    private JButton btnSaveDoctor;
    private JSeparator sepV1;
    private JSeparator sepV2;

    // ComboBox for selecting doctor/patient to view
    private JLabel lblDoctorSelect;  private JComboBox<String> cmbDoctorSelect;
    private JLabel lblPatientSelect; private JComboBox<String> cmbPatientSelect;

    public AdminView(Administrator admin, DoctorController doctorController,
                     PatientController patientController, AppointmentController appointmentController,
                     HospitalizationController hospitalizationController,
                     PrescriptionController prescriptionController, IUserRepository userRepository) {
        this.admin = admin;
        this.doctorController = doctorController;
        this.patientController = patientController;
        this.appointmentController = appointmentController;
        this.hospitalizationController = hospitalizationController;
        this.prescriptionController = prescriptionController;
        this.userRepository = userRepository;
        userRepository.addObserver(this);
        initComponents();
        loadComboBoxes();
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        pnlMain = new PanelRound();
        pnlTitleBar = new PanelRound();
        btnClose = new JButton();
        lblTitle = new JLabel();
        pnlContent = new PanelRound();
        btnDoctorView = new JButton();
        btnPatientView = new JButton();
        btnLogout = new JButton();
        lblFirstname = new JLabel(); txtFirstname = new JTextField();
        lblLastname = new JLabel(); txtLastname = new JTextField();
        lblId = new JLabel(); txtId = new JTextField();
        lblSpecialty = new JLabel(); cmbSpecialty = new JComboBox<>();
        lblLicence = new JLabel(); txtLicence = new JTextField();
        lblOffice = new JLabel(); txtOffice = new JTextField();
        lblUsername = new JLabel(); txtUsername = new JTextField();
        lblPassword = new JLabel(); txtPassword = new JTextField();
        lblPasswordConfirm = new JLabel(); txtPasswordConfirm = new JTextField();
        btnSaveDoctor = new JButton();
        sepV1 = new JSeparator();
        sepV2 = new JSeparator();
        lblDoctorSelect = new JLabel(); cmbDoctorSelect = new JComboBox<>();
        lblPatientSelect = new JLabel(); cmbPatientSelect = new JComboBox<>();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        pnlMain.setRadius(50);

        pnlTitleBar.setRadius(50);
        pnlTitleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                setLocation(getLocation().x + evt.getX() - dragX, getLocation().y + evt.getY() - dragY);
            }
        });
        pnlTitleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) { dragX = evt.getX(); dragY = evt.getY(); }
        });

        btnClose.setFont(new Font("Yu Gothic UI", 0, 18));
        btnClose.setText("X"); btnClose.setBorderPainted(false); btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); btnClose.setFocusable(false);
        btnClose.addActionListener(e -> System.exit(0));

        lblTitle.setFont(new Font("Yu Gothic UI", 0, 14));
        lblTitle.setText("ADMIN VIEW");

        GroupLayout titleLayout = new GroupLayout(pnlTitleBar);
        pnlTitleBar.setLayout(titleLayout);
        titleLayout.setHorizontalGroup(
            titleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, titleLayout.createSequentialGroup()
                .addGap(20, 20, 20).addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose).addGap(19, 19, 19))
        );
        titleLayout.setVerticalGroup(
            titleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING,
                titleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnClose, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitle))
        );

        btnDoctorView.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));
        btnDoctorView.setText("DOCTOR VIEW");
        btnDoctorView.addActionListener(e -> onDoctorView());

        btnPatientView.setFont(new Font("Yu Gothic UI", Font.BOLD, 18));
        btnPatientView.setText("PATIENT VIEW");
        btnPatientView.addActionListener(e -> onPatientView());

        btnLogout.setFont(new Font("Yu Gothic UI", 0, 18));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(e -> onLogout());

        lblFirstname.setFont(new Font("Yu Gothic UI", 0, 18)); lblFirstname.setText("Firstname");
        txtFirstname.setFont(new Font("Yu Gothic UI", 0, 18));
        lblLastname.setFont(new Font("Yu Gothic UI", 0, 18)); lblLastname.setText("Lastname");
        txtLastname.setFont(new Font("Yu Gothic UI", 0, 18));
        lblId.setFont(new Font("Yu Gothic UI", 0, 18)); lblId.setText("ID");
        txtId.setFont(new Font("Yu Gothic UI", 0, 18));
        lblSpecialty.setFont(new Font("Yu Gothic UI", 0, 18)); lblSpecialty.setText("Specialty");
        cmbSpecialty.setFont(new Font("Yu Gothic UI", 0, 18));
        String[] specialties = new String[Specialty.values().length + 1];
        specialties[0] = "Select one";
        for (int i = 0; i < Specialty.values().length; i++) specialties[i+1] = Specialty.values()[i].getDisplayName();
        cmbSpecialty.setModel(new DefaultComboBoxModel<>(specialties));
        lblLicence.setFont(new Font("Yu Gothic UI", 0, 18)); lblLicence.setText("License Number");
        txtLicence.setFont(new Font("Yu Gothic UI", 0, 18));
        lblOffice.setFont(new Font("Yu Gothic UI", 0, 18)); lblOffice.setText("Assigned office");
        txtOffice.setFont(new Font("Yu Gothic UI", 0, 18));
        lblUsername.setFont(new Font("Yu Gothic UI", 0, 18)); lblUsername.setText("User");
        txtUsername.setFont(new Font("Yu Gothic UI", 0, 18));
        lblPassword.setFont(new Font("Yu Gothic UI", 0, 18)); lblPassword.setText("Password");
        txtPassword.setFont(new Font("Yu Gothic UI", 0, 18));
        lblPasswordConfirm.setFont(new Font("Yu Gothic UI", 0, 18)); lblPasswordConfirm.setText("Password confirmation");
        txtPasswordConfirm.setFont(new Font("Yu Gothic UI", 0, 18));
        btnSaveDoctor.setFont(new Font("Yu Gothic UI", 0, 18)); btnSaveDoctor.setText("Save");
        btnSaveDoctor.addActionListener(e -> onSaveDoctor());

        sepV1.setOrientation(SwingConstants.VERTICAL);
        sepV2.setOrientation(SwingConstants.VERTICAL);

        lblDoctorSelect.setFont(new Font("Yu Gothic UI", 0, 18)); lblDoctorSelect.setText("Doctor");
        cmbDoctorSelect.setFont(new Font("Yu Gothic UI", 0, 18));
        lblPatientSelect.setFont(new Font("Yu Gothic UI", 0, 18)); lblPatientSelect.setText("Patient");
        cmbPatientSelect.setFont(new Font("Yu Gothic UI", 0, 18));

        GroupLayout contentLayout = new GroupLayout(pnlContent);
        pnlContent.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(contentLayout.createSequentialGroup()
                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createSequentialGroup().addGap(326, 326, 326).addComponent(btnSaveDoctor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(contentLayout.createSequentialGroup().addGap(32, 32, 32)
                        .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFirstname).addComponent(lblSpecialty))
                                .addGap(18, 18, 18)
                                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addComponent(cmbSpecialty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18).addComponent(lblLicence).addGap(18, 18, 18)
                                        .addComponent(txtLicence, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentLayout.createSequentialGroup()
                                        .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addGap(35, 35, 35).addComponent(lblLastname).addGap(18, 18, 18)
                                        .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18).addComponent(lblId).addGap(18, 18, 18)
                                        .addComponent(txtId, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(contentLayout.createSequentialGroup().addComponent(lblOffice)
                                .addGap(18, 18, 18).addComponent(txtOffice, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(contentLayout.createSequentialGroup().addComponent(lblUsername)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentLayout.createSequentialGroup().addComponent(lblPassword)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentLayout.createSequentialGroup().addComponent(lblPasswordConfirm)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtPasswordConfirm, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                                .addGap(333, 333, 333)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                        .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(btnDoctorView)
                            .addGroup(contentLayout.createSequentialGroup()
                                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(contentLayout.createSequentialGroup().addGap(12, 12, 12)
                                        .addComponent(cmbDoctorSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentLayout.createSequentialGroup().addGap(47, 47, 47).addComponent(lblDoctorSelect)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(74, 74, 74))
                    .addGroup(contentLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnLogout).addGap(318, 318, 318)))
                .addComponent(sepV1, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(btnPatientView)
                            .addGroup(contentLayout.createSequentialGroup().addGap(13, 13, 13)
                                .addComponent(cmbPatientSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(contentLayout.createSequentialGroup().addGap(59, 59, 59).addComponent(lblPatientSelect)))
                .addGap(88, 88, 88)
            .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                    .addContainerGap(707, Short.MAX_VALUE)
                    .addComponent(sepV2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                    .addGap(523, 523, 523)))
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(contentLayout.createSequentialGroup().addComponent(sepV1).addContainerGap())
            .addGroup(contentLayout.createSequentialGroup().addGap(41, 41, 41)
                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFirstname)
                    .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLastname)
                    .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId)
                    .addComponent(txtId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSpecialty)
                    .addComponent(cmbSpecialty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLicence)
                    .addComponent(txtLicence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblOffice)
                    .addComponent(txtOffice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(contentLayout.createSequentialGroup().addGap(81, 81, 81)
                        .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUsername)
                            .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPassword)
                            .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPasswordConfirm)
                            .addComponent(txtPasswordConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(contentLayout.createSequentialGroup().addGap(36, 36, 36)
                        .addComponent(lblDoctorSelect).addGap(18, 18, 18)
                        .addComponent(cmbDoctorSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43).addComponent(btnDoctorView)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(btnSaveDoctor).addGap(123, 123, 123).addComponent(btnLogout).addGap(38, 38, 38))
            .addGroup(contentLayout.createSequentialGroup().addGap(203, 203, 203)
                .addComponent(lblPatientSelect).addGap(18, 18, 18)
                .addComponent(cmbPatientSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43).addComponent(btnPatientView)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(contentLayout.createSequentialGroup().addContainerGap()
                    .addComponent(sepV2).addContainerGap()))
        );

        GroupLayout mainLayout = new GroupLayout(pnlMain);
        pnlMain.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitleBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContent, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(pnlTitleBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlContent, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout frameLayout = new GroupLayout(getContentPane());
        getContentPane().setLayout(frameLayout);
        frameLayout.setHorizontalGroup(
            frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        frameLayout.setVerticalGroup(
            frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }

    private void loadComboBoxes() {
        cmbDoctorSelect.removeAllItems();
        cmbDoctorSelect.addItem("Select one");
        userRepository.getDoctors().forEach(d -> cmbDoctorSelect.addItem(d.getId() + " - " + d.getFirstname() + " " + d.getLastname()));

        cmbPatientSelect.removeAllItems();
        cmbPatientSelect.addItem("Select one");
        userRepository.getPatients().forEach(p -> cmbPatientSelect.addItem(p.getId() + " - " + p.getFirstname() + " " + p.getLastname()));
    }

    @Override
    public void onModelChanged() {
        loadComboBoxes();
    }

    private void onSaveDoctor() {
        String spec = (String) cmbSpecialty.getSelectedItem();
        Response response = doctorController.register(
                txtId.getText(), txtUsername.getText(), txtFirstname.getText(),
                txtLastname.getText(), txtPassword.getText(), txtPasswordConfirm.getText(),
                spec, txtLicence.getText(), txtOffice.getText());
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (response.isOk()) clearDoctorFields();
    }

    private void clearDoctorFields() {
        txtFirstname.setText(""); txtLastname.setText(""); txtId.setText("");
        cmbSpecialty.setSelectedIndex(0); txtLicence.setText(""); txtOffice.setText("");
        txtUsername.setText(""); txtPassword.setText(""); txtPasswordConfirm.setText("");
    }

    private void onDoctorView() {
        String selected = (String) cmbDoctorSelect.getSelectedItem();
        if (selected == null || selected.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long doctorId = Long.parseLong(selected.split(" - ")[0]);
        User u = userRepository.findById(doctorId).orElse(null);
        if (!(u instanceof Doctor doctor)) return;
        setVisible(false);
        DoctorView view = new DoctorView(admin, doctor, patientController, doctorController,
                appointmentController, hospitalizationController, prescriptionController, userRepository);
        view.setVisible(true);
    }

    private void onPatientView() {
        String selected = (String) cmbPatientSelect.getSelectedItem();
        if (selected == null || selected.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Please select a patient.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long patientId = Long.parseLong(selected.split(" - ")[0]);
        User u = userRepository.findById(patientId).orElse(null);
        if (!(u instanceof Patient patient)) return;
        setVisible(false);
        PatientView view = new PatientView(admin, patient, patientController, doctorController,
                appointmentController, hospitalizationController, prescriptionController, userRepository);
        view.setVisible(true);
    }

    private void onLogout() {
        userRepository.removeObserver(this);
        setVisible(false);
        LoginView loginView = createLoginView();
        loginView.setVisible(true);
    }

    private LoginView createLoginView() {
        AuthManager authManager = new AuthManager(userRepository);
        AuthController authCtrl = new AuthController(authManager);
        return new LoginView(authCtrl, patientController, doctorController, appointmentController,
                hospitalizationController, prescriptionController, userRepository);
    }
}
