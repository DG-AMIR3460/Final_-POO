package main.view;

import main.controller.AppointmentController;
import main.controller.AuthController;
import main.controller.DoctorController;
import main.controller.HospitalizationController;
import main.controller.PatientController;
import main.controller.PrescriptionController;
import main.controller.Response;
import main.model.entities.Administrator;
import main.model.entities.Doctor;
import main.model.entities.User;
import main.model.enums.Specialty;
import main.model.observers.ModelObserver;
import main.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorView extends JFrame implements ModelObserver {

    private int dragX, dragY;
    private final User loginUser;
    private final Doctor doctor;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;
    private final PrescriptionController prescriptionController;
    private final IUserRepository userRepository;

    // Title bar
    private PanelRound pnlMain;
    private PanelRound pnlTitleBar;
    private JButton btnClose;
    private JLabel lblTitle;
    private JButton btnBack;
    private JTabbedPane tabPaneMain;

    // Tab 1 – Appointments visualization
    private JPanel pnlAppointmentsVis;
    private JRadioButton rdoTotalAppointments;
    private JRadioButton rdoPendingAppointments;
    private JScrollPane scrlAppointmentsVis;
    private JTable tblAppointmentsVis;
    private JButton btnLogout;

    // Tab 2 – History Appointments of a patient
    private JPanel pnlPatientHistory;
    private JLabel lblPatientSelect;
    private JComboBox<String> cmbPatientSelect;
    private JScrollPane scrlPatientHistory;
    private JTable tblPatientHistory;
    private JButton btnSearchPatient;

    // Tab 3 – Modify info
    private JPanel pnlModifyInfo;
    private JLabel lblFirstname;       private JTextField txtFirstname;
    private JLabel lblLastname;        private JTextField txtLastname;
    private JLabel lblSpecialty;       private JComboBox<String> cmbSpecialty;
    private JLabel lblLicenceNumber;   private JTextField txtLicenceNumber;
    private JLabel lblAssignedOffice;  private JTextField txtAssignedOffice;
    private JLabel lblUsername;        private JTextField txtUsername;
    private JLabel lblPassword;        private JTextField txtPassword;
    private JLabel lblPasswordConfirm; private JTextField txtPasswordConfirm;
    private JButton btnSaveInfo;

    // Tab 4 – Request/Appointments
    private JPanel pnlRequestAppointments;
    private JSeparator sepAcceptReschedule;
    private JSeparator sepRescheduleComplete;
    private JSeparator sepCompleteHosp;

    // Accept section
    private JLabel lblAcceptTitle;
    private JLabel lblAcceptAppointmentId;
    private JComboBox<String> cmbAcceptAppointment;
    private JButton btnAccept;

    // Reschedule section
    private JLabel lblRescheduleTitle;
    private JLabel lblRescheduleAppointment;
    private JComboBox<String> cmbRescheduleAppointment;
    private JLabel lblNewTime;         private JTextField txtNewTime;
    private JLabel lblRescheduleReason; private JTextField txtRescheduleReason;
    private JButton btnReschedule;

    // Complete section
    private JLabel lblCompleteTitle;
    private JLabel lblCompleteAppointment;
    private JComboBox<String> cmbCompleteAppointment;
    private JLabel lblDiagnosis;
    private JScrollPane scrlDiagnosis; private JTextArea txtDiagnosis;
    private JLabel lblObservations;
    private JScrollPane scrlObservations; private JTextArea txtObservations;
    private JLabel lblRecommendedTreatment;
    private JScrollPane scrlRecommendedTreatment; private JTextArea txtRecommendedTreatment;
    private JLabel lblFollowUp;
    private JScrollPane scrlFollowUp; private JTextArea txtFollowUp;
    private JButton btnComplete;

    // Hospitalization section
    private JLabel lblHospitalization;
    private JRadioButton rdoHospRequests;
    private JRadioButton rdoPatientId;
    private JComboBox<String> cmbHospLeft;
    private JComboBox<String> cmbHospRight;
    private JLabel lblHospReason;
    private JScrollPane scrlHospReason; private JTextArea txtHospReason;
    private JLabel lblHospDate;        private JTextField txtHospDate;
    private JLabel lblHospDuration;    private JTextField txtHospDuration;
    private JLabel lblHospObs;
    private JScrollPane scrlHospObs;   private JTextArea txtHospObs;
    private JButton btnGenerateHosp;
    private JButton btnCancelHosp;

    // Tab 5 – Prescribe medications
    private JPanel pnlPrescribe;
    private JLabel lblPrescribeAppointmentId;
    private JComboBox<String> cmbPrescribeAppointment;
    private JLabel lblMedicationName;  private JTextField txtMedicationName;
    private JLabel lblDose;            private JTextField txtDose;
    private JLabel lblAdminRoute;      private JTextField txtAdminRoute;
    private JLabel lblFrequency;       private JTextField txtFrequency;
    private JLabel lblTreatmentDuration; private JTextField txtTreatmentDuration;
    private JLabel lblAdditionalInstructions; private JTextField txtAdditionalInstructions;
    private JScrollPane scrlPrescriptions;
    private JTable tblPrescriptions;
    private JButton btnAddPrescription;
    private JButton btnPrescribe;

    public DoctorView(User loginUser, Doctor doctor,
                      PatientController patientController,
                      DoctorController doctorController,
                      AppointmentController appointmentController,
                      HospitalizationController hospitalizationController,
                      PrescriptionController prescriptionController,
                      IUserRepository userRepository) {
        this.loginUser = loginUser;
        this.doctor = doctor;
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        this.hospitalizationController = hospitalizationController;
        this.prescriptionController = prescriptionController;
        this.userRepository = userRepository;
        initComponents();
        btnBack.setVisible(loginUser instanceof Administrator);
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(null);
        userRepository.addObserver(this);
        appointmentController.addObserver(this);
        hospitalizationController.addObserver(this);
        loadDoctorInfo();
        loadComboBoxes();
    }

    private void initComponents() {
        pnlMain = new PanelRound();
        pnlTitleBar = new PanelRound();
        btnClose = new JButton(); lblTitle = new JLabel(); btnBack = new JButton();
        tabPaneMain = new JTabbedPane();

        // Tab 1
        pnlAppointmentsVis = new JPanel();
        rdoTotalAppointments = new JRadioButton(); rdoPendingAppointments = new JRadioButton();
        scrlAppointmentsVis = new JScrollPane(); tblAppointmentsVis = new JTable();
        btnLogout = new JButton();

        // Tab 2
        pnlPatientHistory = new JPanel();
        lblPatientSelect = new JLabel(); cmbPatientSelect = new JComboBox<>();
        scrlPatientHistory = new JScrollPane(); tblPatientHistory = new JTable();
        btnSearchPatient = new JButton();

        // Tab 3
        pnlModifyInfo = new JPanel();
        lblFirstname = new JLabel(); txtFirstname = new JTextField();
        lblLastname = new JLabel(); txtLastname = new JTextField();
        lblSpecialty = new JLabel(); cmbSpecialty = new JComboBox<>();
        lblLicenceNumber = new JLabel(); txtLicenceNumber = new JTextField();
        lblAssignedOffice = new JLabel(); txtAssignedOffice = new JTextField();
        lblUsername = new JLabel(); txtUsername = new JTextField();
        lblPassword = new JLabel(); txtPassword = new JTextField();
        lblPasswordConfirm = new JLabel(); txtPasswordConfirm = new JTextField();
        btnSaveInfo = new JButton();

        // Tab 4
        pnlRequestAppointments = new JPanel();
        sepAcceptReschedule = new JSeparator(); sepRescheduleComplete = new JSeparator(); sepCompleteHosp = new JSeparator();
        lblAcceptTitle = new JLabel(); lblAcceptAppointmentId = new JLabel();
        cmbAcceptAppointment = new JComboBox<>(); btnAccept = new JButton();
        lblRescheduleTitle = new JLabel(); lblRescheduleAppointment = new JLabel();
        cmbRescheduleAppointment = new JComboBox<>();
        lblNewTime = new JLabel(); txtNewTime = new JTextField();
        lblRescheduleReason = new JLabel(); txtRescheduleReason = new JTextField();
        btnReschedule = new JButton();
        lblCompleteTitle = new JLabel(); lblCompleteAppointment = new JLabel();
        cmbCompleteAppointment = new JComboBox<>();
        lblDiagnosis = new JLabel(); scrlDiagnosis = new JScrollPane(); txtDiagnosis = new JTextArea();
        lblObservations = new JLabel(); scrlObservations = new JScrollPane(); txtObservations = new JTextArea();
        lblRecommendedTreatment = new JLabel(); scrlRecommendedTreatment = new JScrollPane(); txtRecommendedTreatment = new JTextArea();
        lblFollowUp = new JLabel(); scrlFollowUp = new JScrollPane(); txtFollowUp = new JTextArea();
        btnComplete = new JButton();
        lblHospitalization = new JLabel();
        rdoHospRequests = new JRadioButton(); rdoPatientId = new JRadioButton();
        cmbHospLeft = new JComboBox<>(); cmbHospRight = new JComboBox<>();
        lblHospReason = new JLabel(); scrlHospReason = new JScrollPane(); txtHospReason = new JTextArea();
        lblHospDate = new JLabel(); txtHospDate = new JTextField();
        lblHospDuration = new JLabel(); txtHospDuration = new JTextField();
        lblHospObs = new JLabel(); scrlHospObs = new JScrollPane(); txtHospObs = new JTextArea();
        btnGenerateHosp = new JButton(); btnCancelHosp = new JButton();

        // Tab 5
        pnlPrescribe = new JPanel();
        lblPrescribeAppointmentId = new JLabel(); cmbPrescribeAppointment = new JComboBox<>();
        lblMedicationName = new JLabel(); txtMedicationName = new JTextField();
        lblDose = new JLabel(); txtDose = new JTextField();
        lblAdminRoute = new JLabel(); txtAdminRoute = new JTextField();
        lblFrequency = new JLabel(); txtFrequency = new JTextField();
        lblTreatmentDuration = new JLabel(); txtTreatmentDuration = new JTextField();
        lblAdditionalInstructions = new JLabel(); txtAdditionalInstructions = new JTextField();
        scrlPrescriptions = new JScrollPane(); tblPrescriptions = new JTable();
        btnAddPrescription = new JButton(); btnPrescribe = new JButton();

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

        btnClose.setFont(new Font("Yu Gothic UI",0,18)); btnClose.setText("X");
        btnClose.setBorderPainted(false); btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); btnClose.setFocusable(false);
        btnClose.addActionListener(e -> System.exit(0));
        lblTitle.setFont(new Font("Yu Gothic UI",0,14)); lblTitle.setText("DOCTOR VIEW");
        btnBack.setFont(new Font("Yu Gothic UI",0,18)); btnBack.setText("Back");
        btnBack.addActionListener(e -> onBack());

        GroupLayout titleLayout = new GroupLayout(pnlTitleBar);
        pnlTitleBar.setLayout(titleLayout);
        titleLayout.setHorizontalGroup(
            titleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, titleLayout.createSequentialGroup()
                .addContainerGap().addComponent(lblTitle).addGap(32,32,32).addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose).addGap(19,19,19))
        );
        titleLayout.setVerticalGroup(
            titleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING,
                titleLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnClose, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addComponent(btnBack))
        );

        // ── Tab 1: Appointments visualization ──
        rdoTotalAppointments.setFont(new Font("Yu Gothic UI",0,18)); rdoTotalAppointments.setText("Total appointments");
        rdoTotalAppointments.addActionListener(e -> onShowTotalAppointments());
        rdoPendingAppointments.setFont(new Font("Yu Gothic UI",0,18)); rdoPendingAppointments.setText("Pending appointments");
        rdoPendingAppointments.addActionListener(e -> onShowPendingAppointments());
        tblAppointmentsVis.setModel(new DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null},{null,null,null,null,null,null},
                           {null,null,null,null,null,null},{null,null,null,null,null,null}},
            new String[]{"ID","Date","Patient","Specialty","Type","Status"}
        ));
        scrlAppointmentsVis.setViewportView(tblAppointmentsVis);
        btnLogout.setFont(new Font("Yu Gothic UI",0,18)); btnLogout.setText("Logout");
        btnLogout.addActionListener(e -> onLogout());

        GroupLayout tab1Layout = new GroupLayout(pnlAppointmentsVis);
        pnlAppointmentsVis.setLayout(tab1Layout);
        tab1Layout.setHorizontalGroup(
            tab1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab1Layout.createSequentialGroup()
                .addGroup(tab1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLogout)
                    .addGroup(tab1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(tab1Layout.createSequentialGroup().addGap(16,16,16)
                            .addComponent(rdoTotalAppointments).addGap(18,18,18)
                            .addComponent(rdoPendingAppointments))
                        .addGroup(tab1Layout.createSequentialGroup().addGap(108,108,108)
                            .addComponent(scrlAppointmentsVis, GroupLayout.PREFERRED_SIZE, 1035, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        tab1Layout.setVerticalGroup(
            tab1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab1Layout.createSequentialGroup().addGap(29,29,29)
                .addGroup(tab1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoTotalAppointments).addComponent(rdoPendingAppointments))
                .addGap(18,18,18)
                .addComponent(scrlAppointmentsVis, GroupLayout.PREFERRED_SIZE, 504, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnLogout).addGap(23,23,23))
        );
        tabPaneMain.addTab("Appointments visualization", pnlAppointmentsVis);

        // ── Tab 2: History Appointments of a patient ──
        lblPatientSelect.setFont(new Font("Yu Gothic UI",0,18)); lblPatientSelect.setText("Patient");
        cmbPatientSelect.setFont(new Font("Yu Gothic UI",0,18));
        cmbPatientSelect.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        tblPatientHistory.setModel(new DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null},{null,null,null,null,null,null},
                           {null,null,null,null,null,null},{null,null,null,null,null,null}},
            new String[]{"ID","Date","Doctor","Specialty","Type","Status"}
        ) {
            final boolean[] canEdit = {false,false,false,false,false,false};
            public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        scrlPatientHistory.setViewportView(tblPatientHistory);
        btnSearchPatient.setFont(new Font("Yu Gothic UI",0,18)); btnSearchPatient.setText("Search");
        btnSearchPatient.addActionListener(e -> onSearchPatient());

        GroupLayout tab2Layout = new GroupLayout(pnlPatientHistory);
        pnlPatientHistory.setLayout(tab2Layout);
        tab2Layout.setHorizontalGroup(
            tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab2Layout.createSequentialGroup()
                .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab2Layout.createSequentialGroup().addGap(37,37,37)
                        .addComponent(lblPatientSelect).addGap(18,18,18)
                        .addComponent(cmbPatientSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab2Layout.createSequentialGroup().addGap(63,63,63)
                        .addComponent(scrlPatientHistory, GroupLayout.PREFERRED_SIZE, 1133, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.TRAILING, tab2Layout.createSequentialGroup()
                .addGap(0,0,Short.MAX_VALUE).addComponent(btnSearchPatient).addGap(601,601,601))
        );
        tab2Layout.setVerticalGroup(
            tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab2Layout.createSequentialGroup().addGap(32,32,32)
                .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPatientSelect)
                    .addComponent(cmbPatientSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18)
                .addComponent(scrlPatientHistory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(44,44,44).addComponent(btnSearchPatient).addContainerGap(67, Short.MAX_VALUE))
        );
        tabPaneMain.addTab("History Appointments of a patient", pnlPatientHistory);

        // ── Tab 3: Modify info ──
        lblFirstname.setFont(new Font("Yu Gothic UI",0,18)); lblFirstname.setText("Firstname");
        txtFirstname.setFont(new Font("Yu Gothic UI",0,18));
        lblLastname.setFont(new Font("Yu Gothic UI",0,18)); lblLastname.setText("Lastname");
        txtLastname.setFont(new Font("Yu Gothic UI",0,18));
        lblSpecialty.setFont(new Font("Yu Gothic UI",0,18)); lblSpecialty.setText("Specialty");
        cmbSpecialty.setFont(new Font("Yu Gothic UI",0,18));
        String[] specialties = new String[Specialty.values().length + 1];
        specialties[0] = "Select one";
        for (int i = 0; i < Specialty.values().length; i++) specialties[i+1] = Specialty.values()[i].getDisplayName();
        cmbSpecialty.setModel(new DefaultComboBoxModel<>(specialties));
        lblLicenceNumber.setFont(new Font("Yu Gothic UI",0,18)); lblLicenceNumber.setText("License Number");
        txtLicenceNumber.setFont(new Font("Yu Gothic UI",0,18));
        lblAssignedOffice.setFont(new Font("Yu Gothic UI",0,18)); lblAssignedOffice.setText("Assigned office");
        txtAssignedOffice.setFont(new Font("Yu Gothic UI",0,18));
        lblUsername.setFont(new Font("Yu Gothic UI",0,18)); lblUsername.setHorizontalAlignment(SwingConstants.CENTER); lblUsername.setText("User");
        txtUsername.setFont(new Font("Yu Gothic UI",0,18));
        lblPassword.setFont(new Font("Yu Gothic UI",0,18)); lblPassword.setHorizontalAlignment(SwingConstants.CENTER); lblPassword.setText("Password");
        txtPassword.setFont(new Font("Yu Gothic UI",0,18));
        lblPasswordConfirm.setFont(new Font("Yu Gothic UI",0,18)); lblPasswordConfirm.setText("Password confirmation");
        txtPasswordConfirm.setFont(new Font("Yu Gothic UI",0,18));
        btnSaveInfo.setFont(new Font("Yu Gothic UI",0,18)); btnSaveInfo.setText("Save");
        btnSaveInfo.addActionListener(e -> onSaveInfo());

        GroupLayout tab3Layout = new GroupLayout(pnlModifyInfo);
        pnlModifyInfo.setLayout(tab3Layout);
        tab3Layout.setHorizontalGroup(
            tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab3Layout.createSequentialGroup()
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab3Layout.createSequentialGroup().addGap(211,211,211)
                        .addComponent(lblFirstname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblLastname).addGap(18,18,18)
                        .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblSpecialty).addGap(18,18,18)
                        .addComponent(cmbSpecialty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(351,351,351)
                        .addComponent(lblLicenceNumber).addGap(18,18,18)
                        .addComponent(txtLicenceNumber, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblAssignedOffice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtAssignedOffice, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(558,558,558)
                        .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                            .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addComponent(lblUsername, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(521,521,521).addComponent(lblPasswordConfirm))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(576,576,576).addComponent(btnSaveInfo))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(561,561,561)
                        .addComponent(txtPasswordConfirm, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        tab3Layout.setVerticalGroup(
            tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab3Layout.createSequentialGroup().addGap(49,49,49)
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFirstname)
                    .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLastname)
                    .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSpecialty)
                    .addComponent(cmbSpecialty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18)
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLicenceNumber)
                    .addComponent(txtLicenceNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAssignedOffice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAssignedOffice))
                .addGap(30,30,30).addComponent(lblUsername).addGap(18,18,18)
                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(lblPassword).addGap(27,27,27)
                .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(lblPasswordConfirm).addGap(18,18,18)
                .addComponent(txtPasswordConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(32,32,32).addComponent(btnSaveInfo).addContainerGap(161, Short.MAX_VALUE))
        );
        tabPaneMain.addTab("Modify info", pnlModifyInfo);

        // ── Tab 4: Request/Appointments ──
        sepAcceptReschedule.setOrientation(SwingConstants.VERTICAL);
        sepRescheduleComplete.setOrientation(SwingConstants.VERTICAL);
        sepCompleteHosp.setOrientation(SwingConstants.VERTICAL);

        lblAcceptTitle.setFont(new Font("Yu Gothic UI",0,18)); lblAcceptTitle.setText("Accept medical appointment");
        lblAcceptAppointmentId.setFont(new Font("Yu Gothic UI",0,18));
        lblAcceptAppointmentId.setHorizontalAlignment(SwingConstants.CENTER);
        lblAcceptAppointmentId.setText("Appointment ID");
        cmbAcceptAppointment.setFont(new Font("Yu Gothic UI",0,18));
        cmbAcceptAppointment.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        btnAccept.setFont(new Font("Yu Gothic UI",0,18)); btnAccept.setText("Accept");
        btnAccept.addActionListener(e -> onAcceptAppointment());

        lblRescheduleTitle.setFont(new Font("Yu Gothic UI",0,18));
        lblRescheduleTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblRescheduleTitle.setText("Reschedule medical appointment");
        lblRescheduleAppointment.setFont(new Font("Yu Gothic UI",0,18));
        lblRescheduleAppointment.setHorizontalAlignment(SwingConstants.CENTER);
        lblRescheduleAppointment.setText("Appointment");
        cmbRescheduleAppointment.setFont(new Font("Yu Gothic UI",0,18));
        cmbRescheduleAppointment.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        lblNewTime.setFont(new Font("Yu Gothic UI",0,18));
        lblNewTime.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewTime.setText("New time appointment");
        txtNewTime.setFont(new Font("Yu Gothic UI",0,18));
        lblRescheduleReason.setFont(new Font("Yu Gothic UI",0,18));
        lblRescheduleReason.setHorizontalAlignment(SwingConstants.CENTER);
        lblRescheduleReason.setText("Reason for appointment");
        txtRescheduleReason.setFont(new Font("Yu Gothic UI",0,18));
        btnReschedule.setFont(new Font("Yu Gothic UI",0,18)); btnReschedule.setText("Accept");
        btnReschedule.addActionListener(e -> onReschedule());

        lblCompleteTitle.setFont(new Font("Yu Gothic UI",0,18));
        lblCompleteTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompleteTitle.setText("Complete medical appointment");
        lblCompleteAppointment.setFont(new Font("Yu Gothic UI",0,18));
        lblCompleteAppointment.setHorizontalAlignment(SwingConstants.CENTER);
        lblCompleteAppointment.setText("Appointment");
        cmbCompleteAppointment.setFont(new Font("Yu Gothic UI",0,18));
        cmbCompleteAppointment.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        lblDiagnosis.setFont(new Font("Yu Gothic UI",0,18));
        lblDiagnosis.setHorizontalAlignment(SwingConstants.CENTER); lblDiagnosis.setText("Diagnosis");
        txtDiagnosis.setColumns(20); txtDiagnosis.setFont(new Font("Yu Gothic UI",0,18)); txtDiagnosis.setRows(5);
        scrlDiagnosis.setViewportView(txtDiagnosis);
        lblObservations.setFont(new Font("Yu Gothic UI",0,18));
        lblObservations.setHorizontalAlignment(SwingConstants.CENTER); lblObservations.setText("Observations");
        txtObservations.setColumns(20); txtObservations.setFont(new Font("Yu Gothic UI",0,18)); txtObservations.setRows(5);
        scrlObservations.setViewportView(txtObservations);
        lblRecommendedTreatment.setFont(new Font("Yu Gothic UI",0,18));
        lblRecommendedTreatment.setHorizontalAlignment(SwingConstants.CENTER); lblRecommendedTreatment.setText("Recommended treatment");
        txtRecommendedTreatment.setColumns(20); txtRecommendedTreatment.setFont(new Font("Yu Gothic UI",0,18)); txtRecommendedTreatment.setRows(5);
        scrlRecommendedTreatment.setViewportView(txtRecommendedTreatment);
        lblFollowUp.setFont(new Font("Yu Gothic UI",0,18));
        lblFollowUp.setHorizontalAlignment(SwingConstants.CENTER); lblFollowUp.setText("Follow-up indication");
        txtFollowUp.setColumns(20); txtFollowUp.setFont(new Font("Yu Gothic UI",0,18)); txtFollowUp.setRows(5);
        scrlFollowUp.setViewportView(txtFollowUp);
        btnComplete.setFont(new Font("Yu Gothic UI",0,18)); btnComplete.setText("Complete");
        btnComplete.addActionListener(e -> onCompleteAppointment());

        lblHospitalization.setFont(new Font("Yu Gothic UI",0,18));
        lblHospitalization.setHorizontalAlignment(SwingConstants.CENTER); lblHospitalization.setText("Hospitalization");
        rdoHospRequests.setFont(new Font("Yu Gothic UI",0,18)); rdoHospRequests.setText("Requests");
        rdoHospRequests.addActionListener(e -> onRdoHospRequests());
        rdoPatientId.setFont(new Font("Yu Gothic UI",0,18)); rdoPatientId.setText("Patient ID");
        rdoPatientId.addActionListener(e -> onRdoPatientId());
        cmbHospLeft.setFont(new Font("Yu Gothic UI",0,18));
        cmbHospLeft.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        cmbHospRight.setFont(new Font("Yu Gothic UI",0,18));
        cmbHospRight.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        lblHospReason.setFont(new Font("Yu Gothic UI",0,18));
        lblHospReason.setHorizontalAlignment(SwingConstants.CENTER); lblHospReason.setText("Reason for hospitalization");
        txtHospReason.setColumns(20); txtHospReason.setFont(new Font("Yu Gothic UI",0,18)); txtHospReason.setRows(5);
        scrlHospReason.setViewportView(txtHospReason);
        lblHospDate.setFont(new Font("Yu Gothic UI",0,18));
        lblHospDate.setHorizontalAlignment(SwingConstants.CENTER); lblHospDate.setText("Date of entry");
        txtHospDate.setFont(new Font("Yu Gothic UI",0,18));
        lblHospDuration.setFont(new Font("Yu Gothic UI",0,18));
        lblHospDuration.setHorizontalAlignment(SwingConstants.CENTER); lblHospDuration.setText("Estimated duration");
        txtHospDuration.setFont(new Font("Yu Gothic UI",0,18));
        lblHospObs.setFont(new Font("Yu Gothic UI",0,18));
        lblHospObs.setHorizontalAlignment(SwingConstants.CENTER); lblHospObs.setText("Observations");
        txtHospObs.setColumns(20); txtHospObs.setFont(new Font("Yu Gothic UI",0,18)); txtHospObs.setRows(5);
        scrlHospObs.setViewportView(txtHospObs);
        btnGenerateHosp.setFont(new Font("Yu Gothic UI",0,18)); btnGenerateHosp.setText("Generate");
        btnGenerateHosp.addActionListener(e -> onGenerateHospitalization());
        btnCancelHosp.setFont(new Font("Yu Gothic UI",0,18)); btnCancelHosp.setText("Cancel");
        btnCancelHosp.addActionListener(e -> onCancelHospitalization());

        GroupLayout tab4Layout = new GroupLayout(pnlRequestAppointments);
        pnlRequestAppointments.setLayout(tab4Layout);
        tab4Layout.setHorizontalGroup(
            tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab4Layout.createSequentialGroup()
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                .addGap(26,26,26)
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                        .addComponent(btnAccept).addGap(87,87,87))
                                    .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                        .addComponent(cmbAcceptAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(67,67,67))))
                            .addComponent(lblAcceptAppointmentId, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE))
                        .addComponent(sepAcceptReschedule, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(1,1,1))
                    .addGroup(tab4Layout.createSequentialGroup().addGap(26,26,26)
                        .addComponent(lblAcceptTitle).addGap(22,22,22)))
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(lblRescheduleTitle, GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
                    .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(lblRescheduleAppointment, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNewTime, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 304, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRescheduleReason, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
                        .addGroup(tab4Layout.createSequentialGroup()
                            .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(tab4Layout.createSequentialGroup().addGap(90,90,90)
                                    .addComponent(cmbRescheduleAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGroup(tab4Layout.createSequentialGroup().addGap(99,99,99)
                                    .addComponent(txtNewTime, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                .addGroup(tab4Layout.createSequentialGroup().addGap(98,98,98)
                                    .addComponent(txtRescheduleReason, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                .addGroup(tab4Layout.createSequentialGroup().addGap(112,112,112)
                                    .addComponent(btnReschedule)))
                            .addGap(91,91,91))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sepRescheduleComplete, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab4Layout.createSequentialGroup().addGap(112,112,112)
                        .addComponent(btnComplete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(tab4Layout.createSequentialGroup()
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(tab4Layout.createSequentialGroup()
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblCompleteAppointment, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblCompleteTitle, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(tab4Layout.createSequentialGroup().addGap(99,99,99)
                                        .addComponent(cmbCompleteAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0,25,Short.MAX_VALUE))
                            .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDiagnosis, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblObservations, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(tab4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblFollowUp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblRecommendedTreatment, GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                                .addGap(0,0,Short.MAX_VALUE))
                            .addGroup(tab4Layout.createSequentialGroup()
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(tab4Layout.createSequentialGroup().addGap(42,42,42)
                                        .addComponent(scrlDiagnosis, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tab4Layout.createSequentialGroup().addGap(41,41,41)
                                        .addComponent(scrlObservations, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0,0,Short.MAX_VALUE))
                            .addGroup(tab4Layout.createSequentialGroup()
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(tab4Layout.createSequentialGroup().addGap(42,42,42)
                                        .addComponent(scrlRecommendedTreatment, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tab4Layout.createSequentialGroup().addGap(43,43,43)
                                        .addComponent(scrlFollowUp, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0,0,Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(sepCompleteHosp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab4Layout.createSequentialGroup()
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(tab4Layout.createSequentialGroup().addGap(7,7,7)
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(lblHospitalization, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblHospDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblHospDuration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblHospObs, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(tab4Layout.createSequentialGroup().addGap(121,121,121)
                                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHospDate, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHospDuration, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                .addGap(0,0,Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(tab4Layout.createSequentialGroup().addGap(45,45,45)
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addGroup(tab4Layout.createSequentialGroup()
                                .addComponent(btnCancelHosp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGenerateHosp))
                            .addComponent(scrlHospObs, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(56, Short.MAX_VALUE))
                    .addGroup(tab4Layout.createSequentialGroup()
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(tab4Layout.createSequentialGroup().addGap(18,18,18)
                                .addComponent(cmbHospLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(tab4Layout.createSequentialGroup().addGap(37,37,37)
                                .addComponent(rdoHospRequests)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                .addComponent(rdoPatientId, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE).addGap(19,19,19))
                            .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                                .addComponent(cmbHospRight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(29,29,29))))
                    .addGroup(tab4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHospReason, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap())
                    .addGroup(GroupLayout.Alignment.TRAILING, tab4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scrlHospReason, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE).addGap(47,47,47))))
        );
        tab4Layout.setVerticalGroup(
            tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(sepAcceptReschedule)
            .addGroup(tab4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(sepRescheduleComplete)
                    .addGroup(tab4Layout.createSequentialGroup()
                        .addGap(20,20,20).addComponent(lblCompleteTitle).addGap(10,10,10)
                        .addComponent(lblCompleteAppointment).addGap(18,18,18)
                        .addComponent(cmbCompleteAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblDiagnosis).addGap(18,18,18)
                        .addComponent(scrlDiagnosis, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblObservations).addGap(18,18,18)
                        .addComponent(scrlObservations, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblRecommendedTreatment).addGap(18,18,18)
                        .addComponent(scrlRecommendedTreatment, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFollowUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrlFollowUp, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnComplete).addGap(12,12,12))
                    .addGroup(tab4Layout.createSequentialGroup()
                        .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(tab4Layout.createSequentialGroup().addGap(17,17,17)
                                .addComponent(lblAcceptTitle).addGap(18,18,18)
                                .addComponent(lblAcceptAppointmentId).addGap(18,18,18)
                                .addComponent(cmbAcceptAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(31,31,31).addComponent(btnAccept))
                            .addGroup(tab4Layout.createSequentialGroup().addGap(19,19,19)
                                .addComponent(lblRescheduleTitle).addGap(18,18,18)
                                .addComponent(lblRescheduleAppointment).addGap(18,18,18)
                                .addComponent(cmbRescheduleAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18,18,18).addComponent(lblNewTime).addGap(18,18,18)
                                .addComponent(txtNewTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18,18,18).addComponent(lblRescheduleReason).addGap(18,18,18)
                                .addComponent(txtRescheduleReason, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(30,30,30).addComponent(btnReschedule)))
                        .addGap(18,18,Short.MAX_VALUE))))
            .addGroup(tab4Layout.createSequentialGroup().addGap(26,26,26)
                .addComponent(lblHospitalization).addGap(18,18,18)
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoHospRequests).addComponent(rdoPatientId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbHospLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbHospRight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18).addComponent(lblHospReason).addGap(16,16,16)
                .addComponent(scrlHospReason, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHospDate).addGap(18,18,18)
                .addComponent(txtHospDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(lblHospDuration).addGap(18,18,18)
                .addComponent(txtHospDuration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(lblHospObs).addGap(18,18,18)
                .addComponent(scrlHospObs, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE).addGap(18,18,18)
                .addGroup(tab4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGenerateHosp).addComponent(btnCancelHosp))
                .addGap(0,0,Short.MAX_VALUE))
            .addComponent(sepCompleteHosp, GroupLayout.Alignment.TRAILING)
        );
        tabPaneMain.addTab("Request/Appointments", pnlRequestAppointments);

        // ── Tab 5: Prescribe medications ──
        lblPrescribeAppointmentId.setFont(new Font("Yu Gothic UI",0,18)); lblPrescribeAppointmentId.setText("Appointment ID");
        cmbPrescribeAppointment.setFont(new Font("Yu Gothic UI",0,18));
        cmbPrescribeAppointment.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        lblMedicationName.setFont(new Font("Yu Gothic UI",0,18)); lblMedicationName.setText("Medication name");
        txtMedicationName.setFont(new Font("Yu Gothic UI",0,18));
        lblDose.setFont(new Font("Yu Gothic UI",0,18)); lblDose.setText("Dose");
        txtDose.setFont(new Font("Yu Gothic UI",0,18));
        lblAdminRoute.setFont(new Font("Yu Gothic UI",0,18)); lblAdminRoute.setText("Administration route");
        txtAdminRoute.setFont(new Font("Yu Gothic UI",0,18));
        lblFrequency.setFont(new Font("Yu Gothic UI",0,18)); lblFrequency.setText("Frecuency");
        txtFrequency.setFont(new Font("Yu Gothic UI",0,18));
        lblTreatmentDuration.setFont(new Font("Yu Gothic UI",0,18)); lblTreatmentDuration.setText("Treatment duration");
        txtTreatmentDuration.setFont(new Font("Yu Gothic UI",0,18));
        lblAdditionalInstructions.setFont(new Font("Yu Gothic UI",0,18)); lblAdditionalInstructions.setText("Additional instructions");
        txtAdditionalInstructions.setFont(new Font("Yu Gothic UI",0,18));
        tblPrescriptions.setModel(new DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null,null},{null,null,null,null,null,null,null},
                           {null,null,null,null,null,null,null},{null,null,null,null,null,null,null}},
            new String[]{"Appointment ID","Medication name","Dose","Administration route","Treatment duration","Additional instructions","Frecuency"}
        ) {
            final Class<?>[] types = {String.class,String.class,String.class,String.class,String.class,String.class,String.class};
            final boolean[] canEdit = {false,false,false,false,false,false,false};
            public Class<?> getColumnClass(int c) { return types[c]; }
            public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        scrlPrescriptions.setViewportView(tblPrescriptions);
        btnAddPrescription.setFont(new Font("Yu Gothic UI",0,18)); btnAddPrescription.setText("Add");
        btnAddPrescription.addActionListener(e -> onAddPrescription());
        btnPrescribe.setFont(new Font("Yu Gothic UI",0,18)); btnPrescribe.setText("Prescribe");
        btnPrescribe.addActionListener(e -> onPrescribe());

        GroupLayout tab5Layout = new GroupLayout(pnlPrescribe);
        pnlPrescribe.setLayout(tab5Layout);
        tab5Layout.setHorizontalGroup(
            tab5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab5Layout.createSequentialGroup()
                .addGroup(tab5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab5Layout.createSequentialGroup().addGap(62,62,62)
                        .addGroup(tab5Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrlPrescriptions, GroupLayout.PREFERRED_SIZE, 1125, GroupLayout.PREFERRED_SIZE)
                            .addGroup(tab5Layout.createSequentialGroup()
                                .addGroup(tab5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(tab5Layout.createSequentialGroup()
                                        .addComponent(lblPrescribeAppointmentId)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmbPrescribeAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(9,9,9).addComponent(lblMedicationName))
                                    .addGroup(tab5Layout.createSequentialGroup()
                                        .addComponent(lblTreatmentDuration)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtTreatmentDuration, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(tab5Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(tab5Layout.createSequentialGroup()
                                        .addComponent(lblAdditionalInstructions)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtAdditionalInstructions, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblFrequency)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtFrequency, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tab5Layout.createSequentialGroup()
                                        .addComponent(txtMedicationName, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18,18,18).addComponent(lblDose)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDose, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18,18,18).addComponent(lblAdminRoute)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtAdminRoute, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddPrescription))))
                    .addGroup(tab5Layout.createSequentialGroup().addGap(583,583,583).addComponent(btnPrescribe)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        tab5Layout.setVerticalGroup(
            tab5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab5Layout.createSequentialGroup().addGap(57,57,57)
                .addGroup(tab5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrescribeAppointmentId).addComponent(lblMedicationName)
                    .addComponent(txtMedicationName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDose)
                    .addComponent(txtDose, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAdminRoute)
                    .addComponent(txtAdminRoute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddPrescription)
                    .addComponent(cmbPrescribeAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18)
                .addGroup(tab5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTreatmentDuration)
                    .addComponent(txtTreatmentDuration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAdditionalInstructions)
                    .addComponent(txtAdditionalInstructions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFrequency)
                    .addComponent(txtFrequency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(30,30,30)
                .addComponent(scrlPrescriptions, GroupLayout.PREFERRED_SIZE, 340, GroupLayout.PREFERRED_SIZE)
                .addGap(47,47,47).addComponent(btnPrescribe).addContainerGap(64, Short.MAX_VALUE))
        );
        tabPaneMain.addTab("Prescribe medications", pnlPrescribe);

        GroupLayout mainLayout = new GroupLayout(pnlMain);
        pnlMain.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTitleBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabPaneMain))
                .addGap(0,0,Short.MAX_VALUE))
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(pnlTitleBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPaneMain))
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

    private void loadDoctorInfo() {
        txtFirstname.setText(doctor.getFirstname());
        txtLastname.setText(doctor.getLastname());
        cmbSpecialty.setSelectedItem(doctor.getSpecialty().getDisplayName());
        txtLicenceNumber.setText(doctor.getLicenceNumber() != null ? doctor.getLicenceNumber() : "");
        txtAssignedOffice.setText(doctor.getAssignedOffice() != null ? doctor.getAssignedOffice() : "");
        txtUsername.setText(doctor.getUsername());
    }

    private void loadComboBoxes() {
        cmbPatientSelect.removeAllItems();
        cmbPatientSelect.addItem("Select one");
        userRepository.getPatients().forEach(p ->
            cmbPatientSelect.addItem(p.getId() + " - " + p.getFirstname() + " " + p.getLastname()));

        Response rAll = appointmentController.getDoctorAppointments(doctor.getId(), false);
        Response rPending = appointmentController.getDoctorAppointments(doctor.getId(), true);

        cmbAcceptAppointment.removeAllItems();
        cmbAcceptAppointment.addItem("Select one");
        cmbRescheduleAppointment.removeAllItems();
        cmbRescheduleAppointment.addItem("Select one");
        cmbCompleteAppointment.removeAllItems();
        cmbCompleteAppointment.addItem("Select one");
        cmbPrescribeAppointment.removeAllItems();
        cmbPrescribeAppointment.addItem("Select one");

        if (rAll.isOk() && rAll.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);
                String id = a.getString("id");
                String status = a.getString("status");
                if (status.equals("REQUESTED")) cmbAcceptAppointment.addItem(id);
                if (status.equals("PENDING")) {
                    cmbRescheduleAppointment.addItem(id);
                    cmbCompleteAppointment.addItem(id);
                    cmbPrescribeAppointment.addItem(id);
                }
            }
        }

        Response rHosp = hospitalizationController.getAllIds();
        cmbHospLeft.removeAllItems();
        cmbHospLeft.addItem("Select one");
        if (rHosp.isOk() && rHosp.getData() instanceof JSONArray hospArr) {
            for (int i = 0; i < hospArr.length(); i++) cmbHospLeft.addItem(hospArr.getString(i));
        }

        cmbHospRight.removeAllItems();
        cmbHospRight.addItem("Select one");
        userRepository.getPatients().forEach(p ->
            cmbHospRight.addItem(p.getId() + " - " + p.getFirstname() + " " + p.getLastname()));
    }

    @Override
    public void onModelChanged() {
        loadComboBoxes();
    }

    private void onShowTotalAppointments() {
        rdoPendingAppointments.setSelected(false);
        DefaultTableModel model = (DefaultTableModel) tblAppointmentsVis.getModel();
        model.setRowCount(0);
        Response r = appointmentController.getDoctorAppointments(doctor.getId(), false);
        if (r.isOk() && r.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);
                model.addRow(new Object[]{a.getString("id"), a.getString("datetime"),
                    a.getString("patientName"), a.getString("specialty"),
                    a.getString("type"), a.getString("status")});
            }
        }
    }

    private void onShowPendingAppointments() {
        rdoTotalAppointments.setSelected(false);
        DefaultTableModel model = (DefaultTableModel) tblAppointmentsVis.getModel();
        model.setRowCount(0);
        Response r = appointmentController.getDoctorAppointments(doctor.getId(), true);
        if (r.isOk() && r.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);
                model.addRow(new Object[]{a.getString("id"), a.getString("datetime"),
                    a.getString("patientName"), a.getString("specialty"),
                    a.getString("type"), a.getString("status")});
            }
        }
    }

    private void onSearchPatient() {
        String selected = (String) cmbPatientSelect.getSelectedItem();
        if (selected == null || selected.equals("Select one")) return;
        long patientId = Long.parseLong(selected.split(" - ")[0]);
        DefaultTableModel model = (DefaultTableModel) tblPatientHistory.getModel();
        model.setRowCount(0);
        Response r = appointmentController.getPatientAppointments(patientId);
        if (r.isOk() && r.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);
                model.addRow(new Object[]{a.getString("id"), a.getString("datetime"),
                    a.getString("doctorName"), a.getString("specialty"),
                    a.getString("type"), a.getString("status")});
            }
        }
    }

    private void onSaveInfo() {
        String spec = (String) cmbSpecialty.getSelectedItem();
        Response response = doctorController.update(
                doctor.getId(), txtUsername.getText(), txtFirstname.getText(),
                txtLastname.getText(), txtPassword.getText(), txtPasswordConfirm.getText(),
                spec, txtLicenceNumber.getText(), txtAssignedOffice.getText());
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void onAcceptAppointment() {
        String id = (String) cmbAcceptAppointment.getSelectedItem();
        if (id == null || id.equals("Select one")) return;
        Response r = appointmentController.accept(id);
        JOptionPane.showMessageDialog(this, r.getMessage(),
                r.isOk() ? "Success" : "Error",
                r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (r.isOk()) loadComboBoxes();
    }

    private void onReschedule() {
        String id = (String) cmbRescheduleAppointment.getSelectedItem();
        if (id == null || id.equals("Select one")) return;
        Response r = appointmentController.reschedule(id, txtNewTime.getText(), txtRescheduleReason.getText());
        JOptionPane.showMessageDialog(this, r.getMessage(),
                r.isOk() ? "Success" : "Error",
                r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void onCompleteAppointment() {
        String id = (String) cmbCompleteAppointment.getSelectedItem();
        if (id == null || id.equals("Select one")) return;
        Response r = appointmentController.complete(id, txtDiagnosis.getText(),
                txtObservations.getText(), txtRecommendedTreatment.getText(), txtFollowUp.getText());
        JOptionPane.showMessageDialog(this, r.getMessage(),
                r.isOk() ? "Success" : "Error",
                r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (r.isOk()) loadComboBoxes();
    }

    private void onRdoHospRequests() {
        rdoPatientId.setSelected(false);
    }

    private void onRdoPatientId() {
        rdoHospRequests.setSelected(false);
    }

    private void onGenerateHospitalization() {
        if (rdoHospRequests.isSelected()) {
            String hospId = (String) cmbHospLeft.getSelectedItem();
            if (hospId == null || hospId.equals("Select one")) return;
            Response r = hospitalizationController.approve(hospId);
            JOptionPane.showMessageDialog(this, r.getMessage(),
                    r.isOk() ? "Success" : "Error",
                    r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isOk()) loadComboBoxes();
        } else if (rdoPatientId.isSelected()) {
            String selected = (String) cmbHospRight.getSelectedItem();
            if (selected == null || selected.equals("Select one")) return;
            long patientId = Long.parseLong(selected.split(" - ")[0]);
            Response r = hospitalizationController.requestDirect(patientId, doctor.getId(),
                    txtHospDate.getText(), txtHospReason.getText(), "STANDARD", txtHospObs.getText());
            JOptionPane.showMessageDialog(this, r.getMessage(),
                    r.isOk() ? "Success" : "Error",
                    r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isOk()) loadComboBoxes();
        }
    }

    private void onCancelHospitalization() {
        if (rdoHospRequests.isSelected()) {
            String hospId = (String) cmbHospLeft.getSelectedItem();
            if (hospId == null || hospId.equals("Select one")) return;
            Response r = hospitalizationController.deny(hospId);
            JOptionPane.showMessageDialog(this, r.getMessage(),
                    r.isOk() ? "Success" : "Error",
                    r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (r.isOk()) loadComboBoxes();
        }
    }

    private void onAddPrescription() {
        String appointmentId = (String) cmbPrescribeAppointment.getSelectedItem();
        if (appointmentId == null || appointmentId.equals("Select one")) return;
        Response r = prescriptionController.prescribe(appointmentId, txtMedicationName.getText(),
                txtDose.getText(), txtAdminRoute.getText(), txtTreatmentDuration.getText(),
                txtAdditionalInstructions.getText(), txtFrequency.getText());
        JOptionPane.showMessageDialog(this, r.getMessage(),
                r.isOk() ? "Success" : "Error",
                r.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (r.isOk()) {
            DefaultTableModel model = (DefaultTableModel) tblPrescriptions.getModel();
            model.addRow(new Object[]{appointmentId, txtMedicationName.getText(),
                txtDose.getText(), txtAdminRoute.getText(), txtTreatmentDuration.getText(),
                txtAdditionalInstructions.getText(), txtFrequency.getText()});
        }
    }

    private void onPrescribe() {
        DefaultTableModel model = (DefaultTableModel) tblPrescriptions.getModel();
        model.setRowCount(0);
        Response r = prescriptionController.getAllPrescriptionsForDoctor(doctor.getId());
        if (r.isOk() && r.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject p = arr.getJSONObject(i);
                model.addRow(new Object[]{p.getString("appointmentId"), p.getString("medicationName"),
                    p.getDouble("dose"), p.getString("administrationRoute"),
                    p.getInt("treatmentDuration"), p.getString("additionalInstructions"),
                    p.getInt("frequency")});
            }
        }
    }

    private void onLogout() {
        userRepository.removeObserver(this);
        appointmentController.removeObserver(this);
        hospitalizationController.removeObserver(this);
        setVisible(false);
        dispose();
        AuthController authCtrl = new AuthController(userRepository);
        LoginView loginView = new LoginView(authCtrl, patientController, doctorController,
                appointmentController, hospitalizationController, prescriptionController, userRepository);
        loginView.setVisible(true);
    }

    private void onBack() {
        userRepository.removeObserver(this);
        appointmentController.removeObserver(this);
        hospitalizationController.removeObserver(this);
        setVisible(false);
        dispose();
        AdminView adminView = new AdminView((Administrator) loginUser, doctorController,
                patientController, appointmentController, hospitalizationController,
                prescriptionController, userRepository);
        adminView.setVisible(true);
    }
}
