/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package core.view;

import core.AppContext;
import core.controller.Response;
import core.model.entities.Administrator;
import core.model.entities.Appointment;
import core.model.entities.Doctor;
import core.model.entities.Hospitalization;
import core.model.entities.Patient;
import core.model.entities.Prescription;
import core.model.entities.User;
import core.model.enums.AppointmentStatus;
import core.model.enums.HospitalizationStatus;
import core.model.enums.RoomType;
import core.model.enums.Specialty;
import core.model.observers.ModelObserver;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jjlora
 * @author edangulo
 */
public class PatientView extends javax.swing.JFrame implements ModelObserver {

    private final AppContext context = AppContext.getInstance();
    private int x, y;
    private User user;
    private ArrayList<User> users;
    private Patient patient;
    private ArrayList<Appointment> appointments;
    private ArrayList<Hospitalization> hospitalizations;

    public PatientView(User user,Patient patient, ArrayList<User> users, ArrayList<Appointment>appointments, ArrayList<Hospitalization> hospitalizations) {
        initComponents();
        this.setTitle("Patient View");
        this.user = user;
        this.users = users;
        this.patient = patient;
        this.hospitalizations = hospitalizations;
        this.appointments = appointments;
        if (user instanceof Administrator) {
            backToAdminButton.setVisible(true);
        } else {
            backToAdminButton.setVisible(false);
        }
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        registerObservers();
        loadPatientCombos();
        refreshAppointmentsTable();
    }

    private void registerObservers() {
        context.getAppointmentController().addObserver(this);
        context.getHospitalizationController().addObserver(this);
        context.getUserRepository().addObserver(this);
    }

    private void unregisterObservers() {
        context.getAppointmentController().removeObserver(this);
        context.getHospitalizationController().removeObserver(this);
        context.getUserRepository().removeObserver(this);
    }

    private void loadPatientCombos() {
        hospitalizationDoctorComboBox.removeAllItems();
        hospitalizationDoctorComboBox.addItem("Select one");
        for (Doctor doctor : context.getUserRepository().getDoctors()) {
            hospitalizationDoctorComboBox.addItem(String.valueOf(doctor.getId()));
        }

        roomTypeComboBox.removeAllItems();
        roomTypeComboBox.addItem("Select one");
        for (RoomType roomType : RoomType.values()) {
            roomTypeComboBox.addItem(roomType.toDisplayName());
        }

        cancelAppointmentComboBox.removeAllItems();
        cancelAppointmentComboBox.addItem("Select one");
        for (Appointment appointment : context.getAppointmentRepository().getByPatientSortedDesc(patient.getId())) {
            cancelAppointmentComboBox.addItem(appointment.getId());
        }
    }

    @Override
    public void onModelChanged() {
        SwingUtilities.invokeLater(() -> {
            users = new ArrayList<>(context.getUserRepository().getAll());
            appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
            hospitalizations = new ArrayList<>(context.getHospitalizationRepository().getAll());
            loadPatientCombos();
            refreshAppointmentsTable();
        });
    }

    private void refreshAppointmentsTable() {
        DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();
        model.setRowCount(0);
        Response response = context.getAppointmentController().getPatientAppointments(patient.getId());
        if (!response.isOk() || response.getData() == null) return;

        JSONArray appointmentsData = (JSONArray) response.getData();
        for (int i = 0; i < appointmentsData.length(); i++) {
            JSONObject appointment = appointmentsData.getJSONObject(i);
            model.addRow(new Object[]{
                appointment.getString("id"),
                appointment.getString("datetime"),
                appointment.getString("doctorName"),
                appointment.getString("specialty"),
                appointment.getString("type"),
                appointment.getString("status")
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        patientTitleLabel = new javax.swing.JLabel();
        backToAdminButton = new javax.swing.JButton();
        patientTabbedPane = new javax.swing.JTabbedPane();
        appointmentsPanel = new javax.swing.JPanel();
        appointmentsTableScrollPane = new javax.swing.JScrollPane();
        appointmentsTable = new javax.swing.JTable();
        refreshAppointmentsButton = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();
        profilePanel = new javax.swing.JPanel();
        firstnameLabel = new javax.swing.JLabel();
        firstnameField = new javax.swing.JTextField();
        lastnameLabel = new javax.swing.JLabel();
        lastnameField = new javax.swing.JTextField();
        birthdateLabel = new javax.swing.JLabel();
        birthdateField = new javax.swing.JTextField();
        genderLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        phoneLabel = new javax.swing.JLabel();
        phoneField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        passwordField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordConfirmLabel = new javax.swing.JLabel();
        passwordConfirmField = new javax.swing.JTextField();
        savePatientButton = new javax.swing.JButton();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        genderComboBox = new javax.swing.JComboBox<>();
        requestsPanel = new javax.swing.JPanel();
        appointmentRequestTitleLabel = new javax.swing.JLabel();
        specialtyRadioButton = new javax.swing.JRadioButton();
        doctorRadioButton = new javax.swing.JRadioButton();
        requestSeparator = new javax.swing.JSeparator();
        appointmentDateLabel = new javax.swing.JLabel();
        appointmentDateField = new javax.swing.JTextField();
        appointmentTimeField = new javax.swing.JTextField();
        appointmentTimeLabel = new javax.swing.JLabel();
        appointmentTypeLabel = new javax.swing.JLabel();
        appointmentReasonLabel = new javax.swing.JLabel();
        appointmentTypeComboBox = new javax.swing.JComboBox<>();
        createAppointmentButton = new javax.swing.JButton();
        cancelSeparator = new javax.swing.JSeparator();
        hospitalizationRequestTitleLabel = new javax.swing.JLabel();
        hospitalizationReasonLabel = new javax.swing.JLabel();
        attendingDoctorLabel = new javax.swing.JLabel();
        hospitalizationDoctorComboBox = new javax.swing.JComboBox<>();
        estimatedAdmissionDateField = new javax.swing.JTextField();
        estimatedAdmissionDateLabel = new javax.swing.JLabel();
        roomTypeLabel = new javax.swing.JLabel();
        roomTypeComboBox = new javax.swing.JComboBox<>();
        hospitalizationObservationsLabel = new javax.swing.JLabel();
        hospitalizationObservationsScrollPane = new javax.swing.JScrollPane();
        hospitalizationObservationsArea = new javax.swing.JTextArea();
        createHospitalizationButton = new javax.swing.JButton();
        cancelAppointmentTitleLabel = new javax.swing.JLabel();
        cancelAppointmentIdLabel = new javax.swing.JLabel();
        cancelObservationsLabel = new javax.swing.JLabel();
        cancelObservationsScrollPane = new javax.swing.JScrollPane();
        cancelObservationsArea = new javax.swing.JTextArea();
        cancelAppointmentButton = new javax.swing.JButton();
        hospitalizationReasonScrollPane = new javax.swing.JScrollPane();
        hospitalizationReasonArea = new javax.swing.JTextArea();
        appointmentReasonScrollPane = new javax.swing.JScrollPane();
        appointmentReasonArea = new javax.swing.JTextArea();
        cancelAppointmentComboBox = new javax.swing.JComboBox<>();
        appointmentTargetComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        headerPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelRound2MouseDragged(evt);
            }
        });
        headerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelRound2MousePressed(evt);
            }
        });

        closeButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        closeButton.setText("X");
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        closeButton.setFocusable(false);
        closeButton.setRequestFocusEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        patientTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        patientTitleLabel.setText("PATIENT VIEW");

        backToAdminButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        backToAdminButton.setText("Back");
        backToAdminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToAdminButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(patientTitleLabel)
                .addGap(29, 29, 29)
                .addComponent(backToAdminButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addGap(19, 19, 19))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(closeButton))
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backToAdminButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(patientTitleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        appointmentsTable.setAutoCreateRowSorter(true);
        appointmentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Date", "Doctor", "Specialty", "Type", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        appointmentsTableScrollPane.setViewportView(appointmentsTable);

        refreshAppointmentsButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        refreshAppointmentsButton.setText("Refresh");
        refreshAppointmentsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshAppointmentsButtonActionPerformed(evt);
            }
        });

        logoutButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(appointmentsPanel);
        appointmentsPanel.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(appointmentsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(602, 602, 602)
                .addComponent(refreshAppointmentsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutButton)
                .addGap(78, 78, 78))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(appointmentsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refreshAppointmentsButton)
                    .addComponent(logoutButton))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        patientTabbedPane.addTab("Appointment history", appointmentsPanel);

        firstnameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        firstnameLabel.setText("Firstname");

        firstnameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lastnameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lastnameLabel.setText("Lastname");

        lastnameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        birthdateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        birthdateLabel.setText("Birthdate");

        birthdateField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        genderLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        genderLabel.setText("Gender");

        emailLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        emailLabel.setText("Email");

        emailField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        phoneLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        phoneLabel.setText("Phone");

        phoneField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        addressLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        addressLabel.setText("Address");

        addressField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        passwordField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        passwordLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        passwordLabel.setText("Password");

        passwordConfirmLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        passwordConfirmLabel.setText("Password confirmation");

        passwordConfirmField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        savePatientButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        savePatientButton.setText("Save");
        savePatientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePatientButtonActionPerformed(evt);
            }
        });

        usernameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        usernameLabel.setText("User");

        usernameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        genderComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        genderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "Female", "Male" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(profilePanel);
        profilePanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(firstnameLabel)
                .addGap(18, 18, 18)
                .addComponent(firstnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(lastnameLabel)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(phoneLabel)
                        .addGap(18, 18, 18)
                        .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addressLabel)
                        .addGap(18, 18, 18)
                        .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lastnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(birthdateLabel)
                        .addGap(18, 18, 18)
                        .addComponent(birthdateField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(genderLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(emailLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(141, 141, 141))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(516, 516, 516)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(savePatientButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(passwordConfirmField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(passwordConfirmLabel)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(passwordLabel))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(39, 39, 39)
                                    .addComponent(usernameLabel)))
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstnameLabel)
                    .addComponent(firstnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastnameLabel)
                    .addComponent(lastnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(birthdateLabel)
                    .addComponent(birthdateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genderLabel)
                    .addComponent(emailLabel)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneLabel)
                    .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLabel)
                    .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66)
                .addComponent(usernameLabel)
                .addGap(18, 18, 18)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordLabel)
                .addGap(18, 18, 18)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordConfirmLabel)
                .addGap(18, 18, 18)
                .addComponent(passwordConfirmField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(savePatientButton)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        patientTabbedPane.addTab("Modify info", profilePanel);

        appointmentRequestTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentRequestTitleLabel.setText("Request medical appointment");

        specialtyRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        specialtyRadioButton.setText("Specialty");
        specialtyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        doctorRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        doctorRadioButton.setText("Doctor");
        doctorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        requestSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        appointmentDateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentDateLabel.setText("Appointment date");

        appointmentDateField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        appointmentTimeField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        appointmentTimeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentTimeLabel.setText("Appointment time");

        appointmentTypeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentTypeLabel.setText("Appointment type");

        appointmentReasonLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentReasonLabel.setText("Appointment reason");

        appointmentTypeComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "Remote", "In-person" }));

        createAppointmentButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        createAppointmentButton.setText("Create");
        createAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAppointmentButtonActionPerformed(evt);
            }
        });

        cancelSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        hospitalizationRequestTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationRequestTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hospitalizationRequestTitleLabel.setText("Request hospitalization");

        hospitalizationReasonLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationReasonLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hospitalizationReasonLabel.setText("Hospitalization reason");

        attendingDoctorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        attendingDoctorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        attendingDoctorLabel.setText("Attending doctor");

        hospitalizationDoctorComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationDoctorComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        estimatedAdmissionDateField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        estimatedAdmissionDateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        estimatedAdmissionDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        estimatedAdmissionDateLabel.setText("Estimated date of admission");
        estimatedAdmissionDateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        roomTypeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        roomTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roomTypeLabel.setText("Desired room type");

        roomTypeComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        roomTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        hospitalizationObservationsLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationObservationsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hospitalizationObservationsLabel.setText("Observations");

        hospitalizationObservationsArea.setColumns(20);
        hospitalizationObservationsArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationObservationsArea.setRows(5);
        hospitalizationObservationsScrollPane.setViewportView(hospitalizationObservationsArea);

        createHospitalizationButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        createHospitalizationButton.setText("Create");
        createHospitalizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createHospitalizationButtonActionPerformed(evt);
            }
        });

        cancelAppointmentTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cancelAppointmentTitleLabel.setText("Cancel appointment");

        cancelAppointmentIdLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cancelAppointmentIdLabel.setText("ID appointment");

        cancelObservationsLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cancelObservationsLabel.setText("Observations");

        cancelObservationsArea.setColumns(20);
        cancelObservationsArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cancelObservationsArea.setRows(5);
        cancelObservationsScrollPane.setViewportView(cancelObservationsArea);

        cancelAppointmentButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cancelAppointmentButton.setText("Cancel");
        cancelAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAppointmentButtonActionPerformed(evt);
            }
        });

        hospitalizationReasonArea.setColumns(20);
        hospitalizationReasonArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationReasonArea.setRows(5);
        hospitalizationReasonScrollPane.setViewportView(hospitalizationReasonArea);

        appointmentReasonArea.setColumns(20);
        appointmentReasonArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentReasonArea.setRows(5);
        appointmentReasonScrollPane.setViewportView(appointmentReasonArea);

        cancelAppointmentComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        cancelAppointmentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        appointmentTargetComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentTargetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(requestsPanel);
        requestsPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(44, 44, 44)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(specialtyRadioButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(doctorRadioButton))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(63, 63, 63)
                                    .addComponent(appointmentDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(47, 47, 47)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(appointmentTimeLabel)
                                        .addComponent(appointmentDateLabel)
                                        .addComponent(appointmentTargetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(63, 63, 63)
                                    .addComponent(appointmentTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(38, 38, 38)
                                    .addComponent(appointmentReasonLabel))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(46, 46, 46)
                                    .addComponent(appointmentTypeLabel))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(55, 55, 55)
                                    .addComponent(appointmentTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(appointmentRequestTitleLabel)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(appointmentReasonScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(createAppointmentButton)))
                .addGap(69, 69, 69)
                .addComponent(requestSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(211, 211, 211)
                            .addComponent(createHospitalizationButton))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(hospitalizationReasonLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(hospitalizationReasonScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(hospitalizationRequestTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                                .addComponent(attendingDoctorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(hospitalizationObservationsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(estimatedAdmissionDateLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(hospitalizationObservationsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(roomTypeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(hospitalizationDoctorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(estimatedAdmissionDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(roomTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                .addComponent(cancelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelObservationsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(cancelAppointmentTitleLabel))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(cancelAppointmentButton))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cancelAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cancelAppointmentIdLabel)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(cancelObservationsLabel)))
                        .addGap(49, 49, 49)))
                .addGap(81, 81, 81))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(requestSeparator)
            .addComponent(cancelSeparator)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(hospitalizationRequestTitleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(hospitalizationReasonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hospitalizationReasonScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(attendingDoctorLabel)
                        .addGap(18, 18, 18)
                        .addComponent(hospitalizationDoctorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(estimatedAdmissionDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(estimatedAdmissionDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(roomTypeLabel)
                        .addGap(18, 18, 18)
                        .addComponent(roomTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(hospitalizationObservationsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(hospitalizationObservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(createHospitalizationButton)
                        .addGap(15, 15, 15))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(appointmentRequestTitleLabel)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(specialtyRadioButton)
                                    .addComponent(doctorRadioButton))
                                .addGap(18, 18, 18)
                                .addComponent(appointmentTargetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(appointmentDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(appointmentDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(appointmentTimeLabel)
                                .addGap(18, 18, 18)
                                .addComponent(appointmentTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(appointmentReasonLabel)
                                .addGap(24, 24, 24)
                                .addComponent(appointmentReasonScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cancelAppointmentTitleLabel)
                                .addGap(39, 39, 39)
                                .addComponent(cancelAppointmentIdLabel)
                                .addGap(18, 18, 18)
                                .addComponent(cancelAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cancelObservationsLabel)
                                .addGap(18, 18, 18)
                                .addComponent(cancelObservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56)
                                .addComponent(cancelAppointmentButton)))
                        .addGap(18, 18, 18)
                        .addComponent(appointmentTypeLabel)
                        .addGap(18, 18, 18)
                        .addComponent(appointmentTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(createAppointmentButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        patientTabbedPane.addTab("Request/Cancel", requestsPanel);

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(patientTabbedPane)
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(patientTabbedPane))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panelRound2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_panelRound2MousePressed

    private void panelRound2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_panelRound2MouseDragged

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void cancelAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAppointmentButtonActionPerformed
        String idAppointment = cancelAppointmentComboBox.getItemAt(cancelAppointmentComboBox.getSelectedIndex());
        Response response = context.getAppointmentController().cancel(idAppointment);
        JOptionPane.showMessageDialog(this, response.getMessage());
        appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
        loadPatientCombos();
    }//GEN-LAST:event_cancelAppointmentButtonActionPerformed

    private void savePatientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePatientButtonActionPerformed
        String gender = genderComboBox.getSelectedIndex() == 1 ? "Male"
                : genderComboBox.getSelectedIndex() == 2 ? "Female" : "Select one";
        Response response = context.getPatientController().update(
                patient.getId(),
                usernameField.getText(),
                firstnameField.getText(),
                lastnameField.getText(),
                passwordField.getText(),
                passwordConfirmField.getText(),
                emailField.getText(),
                birthdateField.getText(),
                gender,
                phoneField.getText(),
                addressField.getText());
        JOptionPane.showMessageDialog(this, response.getMessage());
        users = new ArrayList<>(context.getUserRepository().getAll());

    }//GEN-LAST:event_savePatientButtonActionPerformed

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        unregisterObservers();
        LoginView login = new LoginView();
        this.setVisible(false);
        login.setVisible(true);
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void backToAdminButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToAdminButtonActionPerformed
        unregisterObservers();
        AdminView admin = new AdminView(user, users,hospitalizations, appointments);
        this.setVisible(false);
        admin.setVisible(true);
    }//GEN-LAST:event_backToAdminButtonActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        if (doctorRadioButton.isSelected()) {
            doctorRadioButton.setSelected(false);
        }

        appointmentTargetComboBox.removeAllItems();

        appointmentTargetComboBox.addItem("Select one");
        for (Specialty spec : Specialty.values()) {
            appointmentTargetComboBox.addItem(spec.getDisplayName());
        }
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        if (specialtyRadioButton.isSelected()) {
            specialtyRadioButton.setSelected(false);
        }
        appointmentTargetComboBox.removeAllItems();

        appointmentTargetComboBox.addItem("Select one");
        for (User doc : this.users) {
            if (doc instanceof Doctor) {
                appointmentTargetComboBox.addItem(doc.getFirstname() + " " + doc.getLastname());
            }
        }
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void createAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAppointmentButtonActionPerformed
        boolean appointmentType = appointmentTypeComboBox.getSelectedIndex() == 2;
        Response response;
        if (specialtyRadioButton.isSelected()) {
            response = context.getAppointmentController().requestBySpecialty(
                    patient.getId(),
                    appointmentTargetComboBox.getItemAt(appointmentTargetComboBox.getSelectedIndex()),
                    appointmentDateField.getText(),
                    appointmentTimeField.getText(),
                    appointmentReasonArea.getText(),
                    appointmentType);
        } else {
            Long doctorId = null;
            String selectedDoctor = appointmentTargetComboBox.getItemAt(appointmentTargetComboBox.getSelectedIndex());
            for (User use : context.getUserRepository().getAll()) {
                if (use instanceof Doctor d
                        && (String.valueOf(d.getId()).equals(selectedDoctor)
                        || (d.getFirstname() + " " + d.getLastname()).equals(selectedDoctor))) {
                    doctorId = d.getId();
                }
            }
            if (doctorId == null) {
                JOptionPane.showMessageDialog(this, "Doctor is required.");
                return;
            }
            response = context.getAppointmentController().requestByDoctor(
                    patient.getId(),
                    doctorId,
                    appointmentDateField.getText(),
                    appointmentTimeField.getText(),
                    appointmentReasonArea.getText(),
                    appointmentType);
        }
        JOptionPane.showMessageDialog(this, response.getMessage());
        if (response.isOk()) {
            appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
            appointmentDateField.setText("");
            appointmentTimeField.setText("");
            appointmentReasonArea.setText("");
            loadPatientCombos();
        }
    }//GEN-LAST:event_createAppointmentButtonActionPerformed


    private void refreshAppointmentsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshAppointmentsButtonActionPerformed
        refreshAppointmentsTable();
    }//GEN-LAST:event_refreshAppointmentsButtonActionPerformed

    private void createHospitalizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createHospitalizationButtonActionPerformed
        Long doctorId = null;
        String selectedDoctor = hospitalizationDoctorComboBox.getItemAt(hospitalizationDoctorComboBox.getSelectedIndex());
        for (User use : context.getUserRepository().getAll()) {
            if (use instanceof Doctor d
                    && (String.valueOf(d.getId()).equals(selectedDoctor)
                    || (d.getFirstname() + " " + d.getLastname()).equals(selectedDoctor))) {
                doctorId = d.getId();
            }
        }
        if (doctorId == null) {
            JOptionPane.showMessageDialog(this, "Doctor is required.");
            return;
        }
        Response response = context.getHospitalizationController().request(
                patient.getId(),
                doctorId,
                estimatedAdmissionDateField.getText(),
                hospitalizationReasonArea.getText(),
                roomTypeComboBox.getItemAt(roomTypeComboBox.getSelectedIndex()),
                hospitalizationObservationsArea.getText());
        JOptionPane.showMessageDialog(this, response.getMessage());
        if (response.isOk()) {
            hospitalizations = new ArrayList<>(context.getHospitalizationRepository().getAll());
            estimatedAdmissionDateField.setText("");
            hospitalizationReasonArea.setText("");
            hospitalizationObservationsArea.setText("");
            loadPatientCombos();
        }
    }//GEN-LAST:event_createHospitalizationButtonActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton createAppointmentButton;
    private javax.swing.JButton createHospitalizationButton;
    private javax.swing.JButton cancelAppointmentButton;
    private javax.swing.JButton refreshAppointmentsButton;
    private javax.swing.JButton backToAdminButton;
    private javax.swing.JButton logoutButton;
    private javax.swing.JButton savePatientButton;
    private javax.swing.JComboBox<String> appointmentTypeComboBox;
    private javax.swing.JComboBox<String> hospitalizationDoctorComboBox;
    private javax.swing.JComboBox<String> roomTypeComboBox;
    private javax.swing.JComboBox<String> cancelAppointmentComboBox;
    private javax.swing.JComboBox<String> appointmentTargetComboBox;
    private javax.swing.JComboBox<String> genderComboBox;
    private javax.swing.JLabel patientTitleLabel;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel passwordConfirmLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JLabel appointmentRequestTitleLabel;
    private javax.swing.JLabel appointmentDateLabel;
    private javax.swing.JLabel appointmentTimeLabel;
    private javax.swing.JLabel appointmentTypeLabel;
    private javax.swing.JLabel appointmentReasonLabel;
    private javax.swing.JLabel hospitalizationRequestTitleLabel;
    private javax.swing.JLabel hospitalizationReasonLabel;
    private javax.swing.JLabel firstnameLabel;
    private javax.swing.JLabel attendingDoctorLabel;
    private javax.swing.JLabel estimatedAdmissionDateLabel;
    private javax.swing.JLabel roomTypeLabel;
    private javax.swing.JLabel hospitalizationObservationsLabel;
    private javax.swing.JLabel cancelAppointmentTitleLabel;
    private javax.swing.JLabel cancelAppointmentIdLabel;
    private javax.swing.JLabel cancelObservationsLabel;
    private javax.swing.JLabel lastnameLabel;
    private javax.swing.JLabel birthdateLabel;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel phoneLabel;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JPanel requestsPanel;
    private javax.swing.JPanel appointmentsPanel;
    private javax.swing.JRadioButton specialtyRadioButton;
    private javax.swing.JRadioButton doctorRadioButton;
    private javax.swing.JScrollPane hospitalizationObservationsScrollPane;
    private javax.swing.JScrollPane cancelObservationsScrollPane;
    private javax.swing.JScrollPane appointmentsTableScrollPane;
    private javax.swing.JScrollPane hospitalizationReasonScrollPane;
    private javax.swing.JScrollPane appointmentReasonScrollPane;
    private javax.swing.JSeparator requestSeparator;
    private javax.swing.JSeparator cancelSeparator;
    private javax.swing.JTabbedPane patientTabbedPane;
    private javax.swing.JTable appointmentsTable;
    private javax.swing.JTextArea hospitalizationObservationsArea;
    private javax.swing.JTextArea cancelObservationsArea;
    private javax.swing.JTextArea hospitalizationReasonArea;
    private javax.swing.JTextArea appointmentReasonArea;
    private javax.swing.JTextField firstnameField;
    private javax.swing.JTextField passwordConfirmField;
    private javax.swing.JTextField usernameField;
    private javax.swing.JTextField appointmentDateField;
    private javax.swing.JTextField appointmentTimeField;
    private javax.swing.JTextField estimatedAdmissionDateField;
    private javax.swing.JTextField lastnameField;
    private javax.swing.JTextField birthdateField;
    private javax.swing.JTextField emailField;
    private javax.swing.JTextField phoneField;
    private javax.swing.JTextField addressField;
    private javax.swing.JTextField passwordField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel headerPanel;
    // End of variables declaration//GEN-END:variables
}
