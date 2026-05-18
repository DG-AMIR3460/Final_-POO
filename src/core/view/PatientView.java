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
import core.model.enums.RoomType;
import core.model.enums.Specialty;
import core.model.observers.ModelObserver;
import core.model.repositories.IUserRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientView extends JFrame implements ModelObserver {

    private int dragX, dragY;
    private final User loginUser;
    private final Patient patient;
    private final IUserRepository userRepository;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;
    private final PrescriptionController prescriptionController;
    private boolean rdoSpecialtySelected = false;

    // Title bar
    private PanelRound pnlMain;
    private PanelRound pnlTitleBar;
    private JButton btnClose;
    private JLabel lblTitle;
    private JButton btnBack;
    private JTabbedPane tabPaneMain;

    // Tab 1 – Appointment history
    private JPanel pnlAppointmentHistory;
    private JScrollPane scrlAppointments;
    private JTable tblAppointments;
    private JButton btnRefreshAppointments;
    private JButton btnLogout;

    // Tab 2 – Modify info
    private JPanel pnlModifyInfo;
    private JLabel lblFirstname;        private JTextField txtFirstname;
    private JLabel lblLastname;         private JTextField txtLastname;
    private JLabel lblBirthdate;        private JTextField txtBirthdate;
    private JLabel lblGender;           private JComboBox<String> cmbGender;
    private JLabel lblEmail;            private JTextField txtEmail;
    private JLabel lblPhone;            private JTextField txtPhone;
    private JLabel lblAddress;          private JTextField txtAddress;
    private JLabel lblUsername;         private JTextField txtUsername;
    private JLabel lblPassword;         private JTextField txtPassword;
    private JLabel lblPasswordConfirm;  private JTextField txtPasswordConfirm;
    private JButton btnSaveInfo;

    // Tab 3 – Request/Cancel
    private JPanel pnlRequestCancel;
    private JLabel lblRequestAppointment;
    private JRadioButton rdoBySpecialty;
    private JRadioButton rdoByDoctor;
    private JSeparator sepV1;
    private JLabel lblAppointmentDate;  private JTextField txtAppointmentDate;
    private JLabel lblAppointmentTime;  private JTextField txtAppointmentTime;
    private JLabel lblAppointmentType;  private JComboBox<String> cmbAppointmentType;
    private JLabel lblAppointmentReason;
    private JComboBox<String> cmbAppointmentSelect;
    private JScrollPane scrlAppointmentReason; private JTextArea txtAppointmentReason;
    private JButton btnCreateAppointment;

    private JSeparator sepV2;
    private JLabel lblRequestHospitalization;
    private JLabel lblHospitalizationReason;
    private JScrollPane scrlHospitalizationReason; private JTextArea txtHospitalizationReason;
    private JLabel lblAttendingDoctor;  private JComboBox<String> cmbHospitalizationDoctor;
    private JLabel lblEstimatedDate;    private JTextField txtHospitalizationDate;
    private JLabel lblDesiredRoom;      private JComboBox<String> cmbRoomType;
    private JLabel lblHospitalizationObs;
    private JScrollPane scrlHospitalizationObs; private JTextArea txtHospitalizationObs;
    private JButton btnCreateHospitalization;

    private JLabel lblCancelAppointment;
    private JLabel lblCancelId;         private JComboBox<String> cmbCancelAppointment;
    private JLabel lblCancelObservations;
    private JScrollPane scrlCancelObs; private JTextArea txtCancelObs;
    private JButton btnCancelAppointment;

    public PatientView(User loginUser, Patient patient,
                       PatientController patientController,
                       DoctorController doctorController,
                       AppointmentController appointmentController,
                       HospitalizationController hospitalizationController,
                       PrescriptionController prescriptionController,
                       IUserRepository userRepository) {
        this.loginUser = loginUser;
        this.patient = patient;
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
        loadPatientInfo();
        loadComboBoxes();
    }

    private void initComponents() {
        pnlMain = new PanelRound();
        pnlTitleBar = new PanelRound();
        btnClose = new JButton();
        lblTitle = new JLabel();
        btnBack = new JButton();
        tabPaneMain = new JTabbedPane();

        // Tab 1
        pnlAppointmentHistory = new JPanel();
        scrlAppointments = new JScrollPane();
        tblAppointments = new JTable();
        btnRefreshAppointments = new JButton();
        btnLogout = new JButton();

        // Tab 2
        pnlModifyInfo = new JPanel();
        lblFirstname = new JLabel(); txtFirstname = new JTextField();
        lblLastname = new JLabel(); txtLastname = new JTextField();
        lblBirthdate = new JLabel(); txtBirthdate = new JTextField();
        lblGender = new JLabel(); cmbGender = new JComboBox<>();
        lblEmail = new JLabel(); txtEmail = new JTextField();
        lblPhone = new JLabel(); txtPhone = new JTextField();
        lblAddress = new JLabel(); txtAddress = new JTextField();
        lblUsername = new JLabel(); txtUsername = new JTextField();
        lblPassword = new JLabel(); txtPassword = new JTextField();
        lblPasswordConfirm = new JLabel(); txtPasswordConfirm = new JTextField();
        btnSaveInfo = new JButton();

        // Tab 3
        pnlRequestCancel = new JPanel();
        lblRequestAppointment = new JLabel();
        rdoBySpecialty = new JRadioButton(); rdoByDoctor = new JRadioButton();
        sepV1 = new JSeparator(); sepV2 = new JSeparator();
        lblAppointmentDate = new JLabel(); txtAppointmentDate = new JTextField();
        lblAppointmentTime = new JLabel(); txtAppointmentTime = new JTextField();
        lblAppointmentType = new JLabel(); cmbAppointmentType = new JComboBox<>();
        lblAppointmentReason = new JLabel();
        cmbAppointmentSelect = new JComboBox<>();
        scrlAppointmentReason = new JScrollPane(); txtAppointmentReason = new JTextArea();
        btnCreateAppointment = new JButton();
        lblRequestHospitalization = new JLabel();
        lblHospitalizationReason = new JLabel();
        scrlHospitalizationReason = new JScrollPane(); txtHospitalizationReason = new JTextArea();
        lblAttendingDoctor = new JLabel(); cmbHospitalizationDoctor = new JComboBox<>();
        lblEstimatedDate = new JLabel(); txtHospitalizationDate = new JTextField();
        lblDesiredRoom = new JLabel(); cmbRoomType = new JComboBox<>();
        lblHospitalizationObs = new JLabel();
        scrlHospitalizationObs = new JScrollPane(); txtHospitalizationObs = new JTextArea();
        btnCreateHospitalization = new JButton();
        lblCancelAppointment = new JLabel();
        lblCancelId = new JLabel(); cmbCancelAppointment = new JComboBox<>();
        lblCancelObservations = new JLabel();
        scrlCancelObs = new JScrollPane(); txtCancelObs = new JTextArea();
        btnCancelAppointment = new JButton();

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

        btnClose.setFont(new Font("Yu Gothic UI", 0, 18)); btnClose.setText("X");
        btnClose.setBorderPainted(false); btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); btnClose.setFocusable(false);
        btnClose.addActionListener(e -> System.exit(0));

        lblTitle.setFont(new Font("Yu Gothic UI", 0, 14)); lblTitle.setText("PATIENT VIEW");

        btnBack.setFont(new Font("Yu Gothic UI", 0, 18)); btnBack.setText("Back");
        btnBack.addActionListener(e -> onBack());

        GroupLayout titleLayout = new GroupLayout(pnlTitleBar);
        pnlTitleBar.setLayout(titleLayout);
        titleLayout.setHorizontalGroup(
            titleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, titleLayout.createSequentialGroup()
                .addGap(15, 15, 15).addComponent(lblTitle).addGap(29, 29, 29)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose).addGap(19, 19, 19))
        );
        titleLayout.setVerticalGroup(
            titleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, titleLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE).addComponent(btnClose))
            .addGroup(titleLayout.createSequentialGroup()
                .addContainerGap().addComponent(btnBack)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblTitle, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // ── Tab 1: Appointment history ──
        tblAppointments.setAutoCreateRowSorter(true);
        tblAppointments.setModel(new DefaultTableModel(
            new Object[][]{{null,null,null,null,null,null},{null,null,null,null,null,null},
                           {null,null,null,null,null,null},{null,null,null,null,null,null}},
            new String[]{"ID","Date","Doctor","Specialty","Type","Status"}
        ) {
            final Class<?>[] types = {String.class,String.class,String.class,String.class,String.class,String.class};
            final boolean[] canEdit = {false,false,false,false,false,false};
            public Class<?> getColumnClass(int c) { return types[c]; }
            public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        scrlAppointments.setViewportView(tblAppointments);

        btnRefreshAppointments.setFont(new Font("Yu Gothic UI", 0, 18)); btnRefreshAppointments.setText("Refresh");
        btnRefreshAppointments.addActionListener(e -> onRefreshAppointments());

        btnLogout.setFont(new Font("Yu Gothic UI", 0, 18)); btnLogout.setText("Logout");
        btnLogout.addActionListener(e -> onLogout());

        GroupLayout tab1Layout = new GroupLayout(pnlAppointmentHistory);
        pnlAppointmentHistory.setLayout(tab1Layout);
        tab1Layout.setHorizontalGroup(
            tab1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab1Layout.createSequentialGroup()
                .addGap(62,62,62)
                .addComponent(scrlAppointments, GroupLayout.PREFERRED_SIZE, 1167, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
            .addGroup(tab1Layout.createSequentialGroup()
                .addGap(602,602,602).addComponent(btnRefreshAppointments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout).addGap(78,78,78))
        );
        tab1Layout.setVerticalGroup(
            tab1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab1Layout.createSequentialGroup()
                .addGap(47,47,47)
                .addComponent(scrlAppointments, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(58,58,58)
                .addGroup(tab1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefreshAppointments).addComponent(btnLogout))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        tabPaneMain.addTab("Appointment history", pnlAppointmentHistory);

        // ── Tab 2: Modify info ──
        lblFirstname.setFont(new Font("Yu Gothic UI",0,18)); lblFirstname.setText("Firstname");
        txtFirstname.setFont(new Font("Yu Gothic UI",0,18));
        lblLastname.setFont(new Font("Yu Gothic UI",0,18)); lblLastname.setText("Lastname");
        txtLastname.setFont(new Font("Yu Gothic UI",0,18));
        lblBirthdate.setFont(new Font("Yu Gothic UI",0,18)); lblBirthdate.setText("Birthdate");
        txtBirthdate.setFont(new Font("Yu Gothic UI",0,18));
        lblGender.setFont(new Font("Yu Gothic UI",0,18)); lblGender.setText("Gender");
        cmbGender.setFont(new Font("Yu Gothic UI",0,18));
        cmbGender.setModel(new DefaultComboBoxModel<>(new String[]{"Select one","Female","Male"}));
        lblEmail.setFont(new Font("Yu Gothic UI",0,18)); lblEmail.setText("Email");
        txtEmail.setFont(new Font("Yu Gothic UI",0,18));
        lblPhone.setFont(new Font("Yu Gothic UI",0,18)); lblPhone.setText("Phone");
        txtPhone.setFont(new Font("Yu Gothic UI",0,18));
        lblAddress.setFont(new Font("Yu Gothic UI",0,18)); lblAddress.setText("Address");
        txtAddress.setFont(new Font("Yu Gothic UI",0,18));
        lblUsername.setFont(new Font("Yu Gothic UI",0,18)); lblUsername.setText("User");
        txtUsername.setFont(new Font("Yu Gothic UI",0,18));
        lblPassword.setFont(new Font("Yu Gothic UI",0,18)); lblPassword.setText("Password");
        txtPassword.setFont(new Font("Yu Gothic UI",0,18));
        lblPasswordConfirm.setFont(new Font("Yu Gothic UI",0,18)); lblPasswordConfirm.setText("Password confirmation");
        txtPasswordConfirm.setFont(new Font("Yu Gothic UI",0,18));
        btnSaveInfo.setFont(new Font("Yu Gothic UI",0,18)); btnSaveInfo.setText("Save");
        btnSaveInfo.addActionListener(e -> onSaveInfo());

        GroupLayout tab2Layout = new GroupLayout(pnlModifyInfo);
        pnlModifyInfo.setLayout(tab2Layout);
        tab2Layout.setHorizontalGroup(
            tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab2Layout.createSequentialGroup()
                .addGap(81,81,81).addComponent(lblFirstname).addGap(18,18,18)
                .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                .addGap(28,28,28).addComponent(lblLastname).addGap(18,18,18)
                .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab2Layout.createSequentialGroup()
                        .addComponent(lblPhone).addGap(18,18,18)
                        .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAddress).addGap(18,18,18)
                        .addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(0,0,Short.MAX_VALUE))
                    .addGroup(tab2Layout.createSequentialGroup()
                        .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblBirthdate).addGap(18,18,18)
                        .addComponent(txtBirthdate, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblGender)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbGender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(15,15,15).addComponent(lblEmail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                .addGap(141,141,141))
            .addGroup(tab2Layout.createSequentialGroup()
                .addGap(516,516,516)
                .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab2Layout.createSequentialGroup().addGap(60,60,60).addComponent(btnSaveInfo))
                    .addGroup(tab2Layout.createSequentialGroup()
                        .addGap(39,39,39)
                        .addComponent(txtPasswordConfirm, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblPasswordConfirm)
                    .addGroup(tab2Layout.createSequentialGroup().addGap(58,58,58).addComponent(lblPassword))
                    .addGroup(tab2Layout.createSequentialGroup().addGap(38,38,38)
                        .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                .addGroup(tab2Layout.createSequentialGroup().addGap(39,39,39).addComponent(lblUsername)))
                            .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tab2Layout.setVerticalGroup(
            tab2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab2Layout.createSequentialGroup()
                .addGap(95,95,95)
                .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFirstname)
                    .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLastname)
                    .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBirthdate)
                    .addComponent(txtBirthdate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGender).addComponent(lblEmail)
                    .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbGender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18,18,18)
                .addGroup(tab2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPhone)
                    .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddress)
                    .addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(66,66,66)
                .addComponent(lblUsername).addGap(18,18,18)
                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(lblPassword).addGap(18,18,18)
                .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18).addComponent(lblPasswordConfirm).addGap(18,18,18)
                .addComponent(txtPasswordConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(36,36,36).addComponent(btnSaveInfo)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        tabPaneMain.addTab("Modify info", pnlModifyInfo);

        // ── Tab 3: Request/Cancel ──
        lblRequestAppointment.setFont(new Font("Yu Gothic UI",0,18)); lblRequestAppointment.setText("Request medical appointment");
        rdoBySpecialty.setFont(new Font("Yu Gothic UI",0,18)); rdoBySpecialty.setText("Specialty");
        rdoBySpecialty.addActionListener(e -> onRdoSpecialty());
        rdoByDoctor.setFont(new Font("Yu Gothic UI",0,18)); rdoByDoctor.setText("Doctor");
        rdoByDoctor.addActionListener(e -> onRdoDoctor());
        sepV1.setOrientation(SwingConstants.VERTICAL);
        lblAppointmentDate.setFont(new Font("Yu Gothic UI",0,18)); lblAppointmentDate.setText("Appointment date");
        txtAppointmentDate.setFont(new Font("Yu Gothic UI",0,18));
        lblAppointmentTime.setFont(new Font("Yu Gothic UI",0,18)); lblAppointmentTime.setText("Appointment time");
        txtAppointmentTime.setFont(new Font("Yu Gothic UI",0,18));
        lblAppointmentType.setFont(new Font("Yu Gothic UI",0,18)); lblAppointmentType.setText("Appointment type");
        cmbAppointmentType.setFont(new Font("Yu Gothic UI",0,18));
        cmbAppointmentType.setModel(new DefaultComboBoxModel<>(new String[]{"Select one","Remote","In-person"}));
        lblAppointmentReason.setFont(new Font("Yu Gothic UI",0,18)); lblAppointmentReason.setText("Appointment reason");
        cmbAppointmentSelect.setFont(new Font("Yu Gothic UI",0,18));
        cmbAppointmentSelect.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        txtAppointmentReason.setColumns(20); txtAppointmentReason.setFont(new Font("Yu Gothic UI",0,18)); txtAppointmentReason.setRows(5);
        scrlAppointmentReason.setViewportView(txtAppointmentReason);
        btnCreateAppointment.setFont(new Font("Yu Gothic UI",0,18)); btnCreateAppointment.setText("Create");
        btnCreateAppointment.addActionListener(e -> onCreateAppointment());

        sepV2.setOrientation(SwingConstants.VERTICAL);
        lblRequestHospitalization.setFont(new Font("Yu Gothic UI",0,18));
        lblRequestHospitalization.setHorizontalAlignment(SwingConstants.CENTER);
        lblRequestHospitalization.setText("Request hospitalization");
        lblHospitalizationReason.setFont(new Font("Yu Gothic UI",0,18));
        lblHospitalizationReason.setHorizontalAlignment(SwingConstants.CENTER);
        lblHospitalizationReason.setText("Hospitalization reason");
        txtHospitalizationReason.setColumns(20); txtHospitalizationReason.setFont(new Font("Yu Gothic UI",0,18)); txtHospitalizationReason.setRows(5);
        scrlHospitalizationReason.setViewportView(txtHospitalizationReason);
        lblAttendingDoctor.setFont(new Font("Yu Gothic UI",0,18));
        lblAttendingDoctor.setHorizontalAlignment(SwingConstants.CENTER);
        lblAttendingDoctor.setText("Attending doctor");
        cmbHospitalizationDoctor.setFont(new Font("Yu Gothic UI",0,18));
        cmbHospitalizationDoctor.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        lblEstimatedDate.setFont(new Font("Yu Gothic UI",0,18));
        lblEstimatedDate.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstimatedDate.setText("Estimated date of admission");
        lblEstimatedDate.setHorizontalTextPosition(SwingConstants.CENTER);
        txtHospitalizationDate.setFont(new Font("Yu Gothic UI",0,18));
        lblDesiredRoom.setFont(new Font("Yu Gothic UI",0,18));
        lblDesiredRoom.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesiredRoom.setText("Desired room type");
        cmbRoomType.setFont(new Font("Yu Gothic UI",0,18));
        String[] roomTypes = new String[RoomType.values().length + 1];
        roomTypes[0] = "Select one";
        for (int i = 0; i < RoomType.values().length; i++) roomTypes[i+1] = RoomType.values()[i].toDisplayName();
        cmbRoomType.setModel(new DefaultComboBoxModel<>(roomTypes));
        lblHospitalizationObs.setFont(new Font("Yu Gothic UI",0,18));
        lblHospitalizationObs.setHorizontalAlignment(SwingConstants.CENTER);
        lblHospitalizationObs.setText("Observations");
        txtHospitalizationObs.setColumns(20); txtHospitalizationObs.setFont(new Font("Yu Gothic UI",0,18)); txtHospitalizationObs.setRows(5);
        scrlHospitalizationObs.setViewportView(txtHospitalizationObs);
        btnCreateHospitalization.setFont(new Font("Yu Gothic UI",0,18)); btnCreateHospitalization.setText("Create");
        btnCreateHospitalization.addActionListener(e -> onCreateHospitalization());

        lblCancelAppointment.setFont(new Font("Yu Gothic UI",0,18)); lblCancelAppointment.setText("Cancel appointment");
        lblCancelId.setFont(new Font("Yu Gothic UI",0,18)); lblCancelId.setText("ID appointment");
        cmbCancelAppointment.setFont(new Font("Yu Gothic UI",0,18));
        cmbCancelAppointment.setModel(new DefaultComboBoxModel<>(new String[]{"Select one"}));
        lblCancelObservations.setFont(new Font("Yu Gothic UI",0,18)); lblCancelObservations.setText("Observations");
        txtCancelObs.setColumns(20); txtCancelObs.setFont(new Font("Yu Gothic UI",0,18)); txtCancelObs.setRows(5);
        scrlCancelObs.setViewportView(txtCancelObs);
        btnCancelAppointment.setFont(new Font("Yu Gothic UI",0,18)); btnCancelAppointment.setText("Cancel");
        btnCancelAppointment.addActionListener(e -> onCancelAppointment());

        GroupLayout tab3Layout = new GroupLayout(pnlRequestCancel);
        pnlRequestCancel.setLayout(tab3Layout);
        tab3Layout.setHorizontalGroup(
            tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tab3Layout.createSequentialGroup()
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(tab3Layout.createSequentialGroup().addGap(44,44,44)
                            .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(tab3Layout.createSequentialGroup()
                                    .addComponent(rdoBySpecialty)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(rdoByDoctor))
                                .addGroup(tab3Layout.createSequentialGroup().addGap(63,63,63)
                                    .addComponent(txtAppointmentDate, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                .addGroup(tab3Layout.createSequentialGroup().addGap(47,47,47)
                                    .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblAppointmentTime).addComponent(lblAppointmentDate)
                                        .addComponent(cmbAppointmentSelect, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(tab3Layout.createSequentialGroup().addGap(63,63,63)
                                    .addComponent(txtAppointmentTime, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                                .addGroup(tab3Layout.createSequentialGroup().addGap(38,38,38).addComponent(lblAppointmentReason))
                                .addGroup(tab3Layout.createSequentialGroup().addGap(46,46,46).addComponent(lblAppointmentType))
                                .addGroup(tab3Layout.createSequentialGroup().addGap(55,55,55)
                                    .addComponent(cmbAppointmentType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(tab3Layout.createSequentialGroup().addGap(42,42,42).addComponent(lblRequestAppointment)))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(44,44,44)
                        .addComponent(scrlAppointmentReason, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(122,122,122).addComponent(btnCreateAppointment)))
                .addGap(69,69,69)
                .addComponent(sepV1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(tab3Layout.createSequentialGroup().addGap(211,211,211).addComponent(btnCreateHospitalization))
                        .addGroup(tab3Layout.createSequentialGroup().addGap(127,127,127)
                            .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblHospitalizationReason, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scrlHospitalizationReason, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(lblRequestHospitalization, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                                .addComponent(lblAttendingDoctor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(GroupLayout.Alignment.TRAILING, tab3Layout.createSequentialGroup().addGap(127,127,127)
                            .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(lblHospitalizationObs, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblEstimatedDate, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrlHospitalizationObs, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblDesiredRoom, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(190,190,190)
                        .addComponent(cmbHospitalizationDoctor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(200,200,200)
                        .addComponent(txtHospitalizationDate, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab3Layout.createSequentialGroup().addGap(191,191,191)
                        .addComponent(cmbRoomType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                .addComponent(sepV2, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                .addGap(63,63,63)
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scrlCancelObs, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
                    .addGroup(tab3Layout.createSequentialGroup()
                        .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(tab3Layout.createSequentialGroup().addGap(30,30,30).addComponent(lblCancelAppointment))
                            .addGroup(tab3Layout.createSequentialGroup().addGap(77,77,77).addComponent(btnCancelAppointment))
                            .addGroup(tab3Layout.createSequentialGroup().addGap(47,47,47)
                                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(cmbCancelAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCancelId)))
                            .addGroup(tab3Layout.createSequentialGroup().addGap(60,60,60).addComponent(lblCancelObservations)))
                        .addGap(49,49,49)))
                .addGap(81,81,81))
        );
        tab3Layout.setVerticalGroup(
            tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(sepV1).addComponent(sepV2)
            .addGroup(tab3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tab3Layout.createSequentialGroup()
                        .addComponent(lblRequestHospitalization)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(lblHospitalizationReason)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrlHospitalizationReason, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAttendingDoctor).addGap(18,18,18)
                        .addComponent(cmbHospitalizationDoctor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblEstimatedDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtHospitalizationDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(24,24,24).addComponent(lblDesiredRoom).addGap(18,18,18)
                        .addComponent(cmbRoomType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(lblHospitalizationObs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrlHospitalizationObs, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                        .addGap(18,18,18).addComponent(btnCreateHospitalization).addGap(15,15,15))
                    .addGroup(tab3Layout.createSequentialGroup()
                        .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(tab3Layout.createSequentialGroup()
                                .addComponent(lblRequestAppointment).addGap(18,18,18)
                                .addGroup(tab3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdoBySpecialty).addComponent(rdoByDoctor))
                                .addGap(18,18,18)
                                .addComponent(cmbAppointmentSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblAppointmentDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAppointmentDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(13,13,13).addComponent(lblAppointmentTime).addGap(18,18,18)
                                .addComponent(txtAppointmentTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblAppointmentReason).addGap(24,24,24)
                                .addComponent(scrlAppointmentReason, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
                            .addGroup(tab3Layout.createSequentialGroup()
                                .addComponent(lblCancelAppointment).addGap(39,39,39)
                                .addComponent(lblCancelId).addGap(18,18,18)
                                .addComponent(cmbCancelAppointment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18,18,18).addComponent(lblCancelObservations).addGap(18,18,18)
                                .addComponent(scrlCancelObs, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                .addGap(56,56,56).addComponent(btnCancelAppointment)))
                        .addGap(18,18,18).addComponent(lblAppointmentType).addGap(18,18,18)
                        .addComponent(cmbAppointmentType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(40,40,40).addComponent(btnCreateAppointment)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        tabPaneMain.addTab("Request/Cancel", pnlRequestCancel);

        GroupLayout mainLayout = new GroupLayout(pnlMain);
        pnlMain.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitleBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabPaneMain)
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

    private void loadPatientInfo() {
        txtFirstname.setText(patient.getFirstname());
        txtLastname.setText(patient.getLastname());
        txtBirthdate.setText(patient.getBirthdate() != null ? patient.getBirthdate().toString() : "");
        cmbGender.setSelectedItem(patient.isGender() ? "Male" : "Female");
        txtEmail.setText(patient.getEmail() != null ? patient.getEmail() : "");
        txtPhone.setText(patient.getPhone() != 0 ? String.valueOf(patient.getPhone()) : "");
        txtAddress.setText(patient.getAddress() != null ? patient.getAddress() : "");
        txtUsername.setText(patient.getUsername());
    }

    private void loadComboBoxes() {
        cmbHospitalizationDoctor.removeAllItems();
        cmbHospitalizationDoctor.addItem("Select one");
        userRepository.getDoctors().forEach(d ->
            cmbHospitalizationDoctor.addItem(d.getId() + " - " + d.getFirstname() + " " + d.getLastname()));

        cmbCancelAppointment.removeAllItems();
        cmbCancelAppointment.addItem("Select one");
        Response r = appointmentController.getPatientAppointments(patient.getId());
        if (r.isOk() && r.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);
                String status = a.getString("status");
                if (!status.equals("COMPLETED") && !status.equals("CANCELED")) {
                    cmbCancelAppointment.addItem(a.getString("id"));
                }
            }
        }
    }

    @Override
    public void onModelChanged() {
        loadComboBoxes();
        onRefreshAppointments();
    }

    private void onRefreshAppointments() {
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        model.setRowCount(0);
        Response r = appointmentController.getPatientAppointments(patient.getId());
        if (r.isOk() && r.getData() instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject a = arr.getJSONObject(i);
                model.addRow(new Object[]{
                    a.getString("id"), a.getString("datetime"),
                    a.getString("doctorName"), a.getString("specialty"),
                    a.getString("type"), a.getString("status")
                });
            }
        }
    }

    private void onRdoSpecialty() {
        rdoByDoctor.setSelected(false);
        rdoSpecialtySelected = true;
        cmbAppointmentSelect.removeAllItems();
        cmbAppointmentSelect.addItem("Select one");
        for (Specialty s : Specialty.values()) cmbAppointmentSelect.addItem(s.getDisplayName());
    }

    private void onRdoDoctor() {
        rdoBySpecialty.setSelected(false);
        rdoSpecialtySelected = false;
        cmbAppointmentSelect.removeAllItems();
        cmbAppointmentSelect.addItem("Select one");
        userRepository.getDoctors().forEach(d ->
            cmbAppointmentSelect.addItem(d.getId() + " - " + d.getFirstname() + " " + d.getLastname()));
    }

    private void onCreateAppointment() {
        String selected = (String) cmbAppointmentSelect.getSelectedItem();
        if (selected == null || selected.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Please select a specialty or doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String date = txtAppointmentDate.getText();
        String time = txtAppointmentTime.getText();
        String reason = txtAppointmentReason.getText();
        boolean inPerson = cmbAppointmentType.getSelectedIndex() == 2;

        Response response;
        if (rdoSpecialtySelected) {
            response = appointmentController.requestBySpecialty(patient.getId(), selected, date, time, reason, inPerson);
        } else {
            long doctorId = Long.parseLong(selected.split(" - ")[0]);
            response = appointmentController.requestByDoctor(patient.getId(), doctorId, date, time, reason, inPerson);
        }
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void onCreateHospitalization() {
        String selected = (String) cmbHospitalizationDoctor.getSelectedItem();
        if (selected == null || selected.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long doctorId = Long.parseLong(selected.split(" - ")[0]);
        String date = txtHospitalizationDate.getText();
        String reason = txtHospitalizationReason.getText();
        String roomType = (String) cmbRoomType.getSelectedItem();
        String observations = txtHospitalizationObs.getText();
        Response response = hospitalizationController.request(patient.getId(), doctorId, date, reason, roomType, observations);
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void onCancelAppointment() {
        String appointmentId = (String) cmbCancelAppointment.getSelectedItem();
        if (appointmentId == null || appointmentId.equals("Select one")) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Response response = appointmentController.cancel(appointmentId);
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (response.isOk()) loadComboBoxes();
    }

    private void onSaveInfo() {
        String genderStr = (String) cmbGender.getSelectedItem();
        Response response = patientController.update(
                patient.getId(), txtUsername.getText(), txtFirstname.getText(),
                txtLastname.getText(), txtPassword.getText(), txtPasswordConfirm.getText(),
                txtEmail.getText(), txtBirthdate.getText(), genderStr,
                txtPhone.getText(), txtAddress.getText());
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void onLogout() {
        userRepository.removeObserver(this);
        appointmentController.removeObserver(this);
        hospitalizationController.removeObserver(this);
        setVisible(false);
        dispose();
        AuthManager authManager = new AuthManager(userRepository);
        AuthController authCtrl = new AuthController(authManager);
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
