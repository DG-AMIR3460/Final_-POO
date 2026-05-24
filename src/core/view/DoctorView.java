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
public class DoctorView extends javax.swing.JFrame implements ModelObserver {

    private final AppContext context = AppContext.getInstance();
    private int x, y;
    private User user;
    private ArrayList<User> users;
    private ArrayList<Hospitalization>hospitalizations;
    private ArrayList<Appointment>appointments;
    private Doctor doctor;
    private Patient patient;
    public DoctorView(User user,Doctor doc, ArrayList<User> users,ArrayList<Hospitalization> hospitalizations,ArrayList<Appointment> appointments) {
        initComponents();
        this.setTitle("Doctor View");
        this.user = user;
        this.users =users;
        this.doctor = doc;
        this.hospitalizations = hospitalizations;
        this.appointments = appointments;
        if (user instanceof Administrator)
            backToAdminButton.setVisible(true);
        else    
            backToAdminButton.setVisible(false);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        registerObservers();
        loadDoctorCombos();
        refreshDoctorAppointmentsTable(false);
        refreshPrescriptionsTable();
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

    private void loadDoctorCombos() {
        acceptAppointmentComboBox.removeAllItems();
        rescheduleAppointmentComboBox.removeAllItems();
        completeAppointmentComboBox.removeAllItems();
        patientAppointmentsPatientComboBox.removeAllItems();
        hospitalizationRequestComboBox.removeAllItems();
        prescriptionAppointmentComboBox.removeAllItems();
        hospitalizationPatientComboBox.removeAllItems();

        acceptAppointmentComboBox.addItem("Select one");
        rescheduleAppointmentComboBox.addItem("Select one");
        completeAppointmentComboBox.addItem("Select one");
        patientAppointmentsPatientComboBox.addItem("Select one");
        hospitalizationRequestComboBox.addItem("Select one");
        prescriptionAppointmentComboBox.addItem("Select one");
        hospitalizationPatientComboBox.addItem("Select one");

        for (Appointment appointment : context.getAppointmentRepository().getByDoctorSortedDesc(doctor.getId())) {
            rescheduleAppointmentComboBox.addItem(appointment.getId());
            completeAppointmentComboBox.addItem(appointment.getId());
            prescriptionAppointmentComboBox.addItem(appointment.getId());
            if (appointment.getStatus() == AppointmentStatus.REQUESTED) {
                acceptAppointmentComboBox.addItem(appointment.getId());
            }
        }
        for (Patient patient : context.getUserRepository().getPatients()) {
            patientAppointmentsPatientComboBox.addItem(String.valueOf(patient.getId()));
            hospitalizationPatientComboBox.addItem(String.valueOf(patient.getId()));
        }
        for (Hospitalization hospitalization : context.getHospitalizationRepository().getByDoctor(doctor.getId())) {
            if (hospitalization.getStatus() == HospitalizationStatus.REQUESTED) {
                hospitalizationRequestComboBox.addItem(hospitalization.getId());
            }
        }
    }

    @Override
    public void onModelChanged() {
        SwingUtilities.invokeLater(() -> {
            users = new ArrayList<>(context.getUserRepository().getAll());
            appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
            hospitalizations = new ArrayList<>(context.getHospitalizationRepository().getAll());
            loadDoctorCombos();
            refreshDoctorAppointmentsTable(pendingAppointmentsRadioButton.isSelected());
            refreshSelectedPatientAppointmentsTable(false);
            refreshPrescriptionsTable();
        });
    }

    private void refreshDoctorAppointmentsTable(boolean onlyPending) {
        DefaultTableModel model = (DefaultTableModel) doctorAppointmentsTable.getModel();
        model.setRowCount(0);
        Response response = context.getAppointmentController().getDoctorAppointments(doctor.getId(), onlyPending);
        if (!response.isOk() || response.getData() == null) return;

        JSONArray appointmentsData = (JSONArray) response.getData();
        for (int i = 0; i < appointmentsData.length(); i++) {
            JSONObject appointment = appointmentsData.getJSONObject(i);
            model.addRow(new Object[]{
                appointment.getString("id"),
                appointment.getString("datetime"),
                appointment.getString("patientName"),
                appointment.getString("specialty"),
                appointment.getString("type"),
                appointment.getString("status")
            });
        }
    }

    private void refreshSelectedPatientAppointmentsTable(boolean notifyMissingSelection) {
        String selectedPatientId = patientAppointmentsPatientComboBox.getItemAt(patientAppointmentsPatientComboBox.getSelectedIndex());
        if ("Select one".equals(selectedPatientId)) {
            if (notifyMissingSelection) {
                JOptionPane.showMessageDialog(this, "Patient is required.");
            }
            return;
        }

        DefaultTableModel model = (DefaultTableModel) patientAppointmentsTable.getModel();
        model.setRowCount(0);
        Response response = context.getAppointmentController().getPatientAppointments(Long.parseLong(selectedPatientId));
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

    private void refreshPrescriptionsTable() {
        DefaultTableModel model = (DefaultTableModel) prescriptionsTable.getModel();
        model.setRowCount(0);
        Response response = context.getPrescriptionController().getAllPrescriptionsForDoctor(doctor.getId());
        if (!response.isOk() || response.getData() == null) return;

        JSONArray prescriptionsData = (JSONArray) response.getData();
        for (int i = 0; i < prescriptionsData.length(); i++) {
            JSONObject prescription = prescriptionsData.getJSONObject(i);
            model.addRow(new Object[]{
                prescription.getString("appointmentId"),
                prescription.getString("medicationName"),
                String.valueOf(prescription.getDouble("dose")),
                prescription.getString("administrationRoute"),
                String.valueOf(prescription.getInt("treatmentDuration")),
                prescription.getString("additionalInstructions"),
                String.valueOf(prescription.getInt("frequency"))
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
        doctorTitleLabel = new javax.swing.JLabel();
        backToAdminButton = new javax.swing.JButton();
        doctorTabbedPane = new javax.swing.JTabbedPane();
        doctorAppointmentsPanel = new javax.swing.JPanel();
        totalAppointmentsRadioButton = new javax.swing.JRadioButton();
        doctorAppointmentsTableScrollPane = new javax.swing.JScrollPane();
        doctorAppointmentsTable = new javax.swing.JTable();
        pendingAppointmentsRadioButton = new javax.swing.JRadioButton();
        logoutButton = new javax.swing.JButton();
        patientAppointmentsPanel = new javax.swing.JPanel();
        patientAppointmentsPatientComboBox = new javax.swing.JComboBox<>();
        patientSelectorLabel = new javax.swing.JLabel();
        patientAppointmentsTableScrollPane = new javax.swing.JScrollPane();
        patientAppointmentsTable = new javax.swing.JTable();
        searchPatientAppointmentsButton = new javax.swing.JButton();
        profilePanel = new javax.swing.JPanel();
        firstnameLabel = new javax.swing.JLabel();
        firstnameField = new javax.swing.JTextField();
        lastnameLabel = new javax.swing.JLabel();
        lastnameField = new javax.swing.JTextField();
        specialtyLabel = new javax.swing.JLabel();
        licenseLabel = new javax.swing.JLabel();
        licenseField = new javax.swing.JTextField();
        officeLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        officeField = new javax.swing.JTextField();
        passwordField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordConfirmLabel = new javax.swing.JLabel();
        passwordConfirmField = new javax.swing.JTextField();
        specialtyComboBox = new javax.swing.JComboBox<>();
        saveDoctorButton = new javax.swing.JButton();
        appointmentManagementPanel = new javax.swing.JPanel();
        acceptAppointmentIdLabel = new javax.swing.JLabel();
        acceptAppointmentTitleLabel = new javax.swing.JLabel();
        acceptAppointmentComboBox = new javax.swing.JComboBox<>();
        profileSeparator = new javax.swing.JSeparator();
        acceptAppointmentButton = new javax.swing.JButton();
        rescheduleAppointmentTitleLabel = new javax.swing.JLabel();
        rescheduleAppointmentIdLabel = new javax.swing.JLabel();
        rescheduleAppointmentComboBox = new javax.swing.JComboBox<>();
        rescheduleAppointmentButton = new javax.swing.JButton();
        newAppointmentTimeLabel = new javax.swing.JLabel();
        newAppointmentTimeField = new javax.swing.JTextField();
        rescheduleReasonLabel = new javax.swing.JLabel();
        rescheduleReasonField = new javax.swing.JTextField();
        appointmentActionSeparator = new javax.swing.JSeparator();
        completeAppointmentTitleLabel = new javax.swing.JLabel();
        completeAppointmentIdLabel = new javax.swing.JLabel();
        completeAppointmentComboBox = new javax.swing.JComboBox<>();
        diagnosisLabel = new javax.swing.JLabel();
        appointmentObservationsLabel = new javax.swing.JLabel();
        recommendedTreatmentLabel = new javax.swing.JLabel();
        followUpLabel = new javax.swing.JLabel();
        completeAppointmentButton = new javax.swing.JButton();
        hospitalizationSelectorLabel = new javax.swing.JLabel();
        hospitalizationReasonLabel = new javax.swing.JLabel();
        entryDateLabel = new javax.swing.JLabel();
        entryDateField = new javax.swing.JTextField();
        estimatedDurationLabel = new javax.swing.JLabel();
        estimatedDurationField = new javax.swing.JTextField();
        hospitalizationObservationsLabel = new javax.swing.JLabel();
        hospitalizationObservationsScrollPane = new javax.swing.JScrollPane();
        hospitalizationObservationsArea = new javax.swing.JTextArea();
        generateHospitalizationButton = new javax.swing.JButton();
        hospitalizationRequestComboBox = new javax.swing.JComboBox<>();
        hospitalizationRequestsRadioButton = new javax.swing.JRadioButton();
        hospitalizationPatientRadioButton = new javax.swing.JRadioButton();
        diagnosisScrollPane = new javax.swing.JScrollPane();
        diagnosisArea = new javax.swing.JTextArea();
        appointmentObservationsScrollPane = new javax.swing.JScrollPane();
        appointmentObservationsArea = new javax.swing.JTextArea();
        recommendedTreatmentScrollPane = new javax.swing.JScrollPane();
        recommendedTreatmentArea = new javax.swing.JTextArea();
        followUpScrollPane = new javax.swing.JScrollPane();
        followUpArea = new javax.swing.JTextArea();
        hospitalizationSeparator = new javax.swing.JSeparator();
        processHospitalizationButton = new javax.swing.JButton();
        hospitalizationPatientComboBox = new javax.swing.JComboBox<>();
        hospitalizationReasonScrollPane = new javax.swing.JScrollPane();
        hospitalizationReasonArea = new javax.swing.JTextArea();
        prescriptionsPanel = new javax.swing.JPanel();
        prescriptionAppointmentIdLabel = new javax.swing.JLabel();
        medicationNameLabel = new javax.swing.JLabel();
        medicationNameField = new javax.swing.JTextField();
        doseLabel = new javax.swing.JLabel();
        doseField = new javax.swing.JTextField();
        administrationRouteLabel = new javax.swing.JLabel();
        administrationRouteField = new javax.swing.JTextField();
        frequencyLabel = new javax.swing.JLabel();
        frequencyField = new javax.swing.JTextField();
        treatmentDurationLabel = new javax.swing.JLabel();
        treatmentDurationField = new javax.swing.JTextField();
        additionalInstructionsLabel = new javax.swing.JLabel();
        additionalInstructionsField = new javax.swing.JTextField();
        prescriptionsTableScrollPane = new javax.swing.JScrollPane();
        prescriptionsTable = new javax.swing.JTable();
        addPrescriptionButton = new javax.swing.JButton();
        clearPrescriptionTableButton = new javax.swing.JButton();
        prescriptionAppointmentComboBox = new javax.swing.JComboBox<>();

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

        doctorTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        doctorTitleLabel.setText("DOCTOR VIEW");

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
                .addContainerGap()
                .addComponent(doctorTitleLabel)
                .addGap(32, 32, 32)
                .addComponent(backToAdminButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addGap(19, 19, 19))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(doctorTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(backToAdminButton))
        );

        totalAppointmentsRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        totalAppointmentsRadioButton.setText("Total appointments");
        totalAppointmentsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        doctorAppointmentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Date", "Patient", "Specialty", "Type", "Status"
            }
        ));
        doctorAppointmentsTableScrollPane.setViewportView(doctorAppointmentsTable);

        pendingAppointmentsRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        pendingAppointmentsRadioButton.setText("Pending appointments");
        pendingAppointmentsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        logoutButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(doctorAppointmentsPanel);
        doctorAppointmentsPanel.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(logoutButton)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(totalAppointmentsRadioButton)
                            .addGap(18, 18, 18)
                            .addComponent(pendingAppointmentsRadioButton))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(108, 108, 108)
                            .addComponent(doctorAppointmentsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1035, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalAppointmentsRadioButton)
                    .addComponent(pendingAppointmentsRadioButton))
                .addGap(18, 18, 18)
                .addComponent(doctorAppointmentsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(logoutButton)
                .addGap(23, 23, 23))
        );

        doctorTabbedPane.addTab("Appointments visualization", doctorAppointmentsPanel);

        patientAppointmentsPatientComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        patientAppointmentsPatientComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        patientSelectorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        patientSelectorLabel.setText("Patient");

        patientAppointmentsTable.setModel(new javax.swing.table.DefaultTableModel(
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
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        patientAppointmentsTableScrollPane.setViewportView(patientAppointmentsTable);

        searchPatientAppointmentsButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        searchPatientAppointmentsButton.setText("Search");
        searchPatientAppointmentsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPatientAppointmentsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(patientAppointmentsPanel);
        patientAppointmentsPanel.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(patientSelectorLabel)
                        .addGap(18, 18, 18)
                        .addComponent(patientAppointmentsPatientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(patientAppointmentsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(searchPatientAppointmentsButton)
                .addGap(601, 601, 601))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(patientSelectorLabel)
                    .addComponent(patientAppointmentsPatientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(patientAppointmentsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(searchPatientAppointmentsButton)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        doctorTabbedPane.addTab("History Appointments of a patient", patientAppointmentsPanel);

        firstnameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        firstnameLabel.setText("Firstname");

        firstnameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        lastnameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        lastnameLabel.setText("Lastname");

        lastnameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        specialtyLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        specialtyLabel.setText("Specialty");

        licenseLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        licenseLabel.setText("License Number");

        licenseField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        officeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        officeLabel.setText("Assigned office");

        usernameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        usernameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setText("User");

        officeField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        passwordField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        passwordLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        passwordLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        passwordLabel.setText("Password");

        passwordConfirmLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        passwordConfirmLabel.setText("Password confirmation");

        passwordConfirmField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        specialtyComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        specialtyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one", "General Medicine", "Cardiology", "Pediatrics", "Neurology", "Traumatology & Orthopedics", "Gynecology & Obstetrics", "Dermatology", "Psychiatry", "Oncology", "Ophthalmology", "Internal Medicine" }));

        saveDoctorButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        saveDoctorButton.setText("Save");
        saveDoctorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDoctorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(profilePanel);
        profilePanel.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(firstnameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(firstnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lastnameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(lastnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(specialtyLabel)
                        .addGap(18, 18, 18)
                        .addComponent(specialtyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(licenseLabel)
                        .addGap(18, 18, 18)
                        .addComponent(licenseField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(officeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(officeField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(558, 558, 558)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addComponent(usernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(passwordLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(521, 521, 521)
                        .addComponent(passwordConfirmLabel))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(576, 576, 576)
                        .addComponent(saveDoctorButton))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(561, 561, 561)
                        .addComponent(passwordConfirmField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstnameLabel)
                    .addComponent(firstnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastnameLabel)
                    .addComponent(lastnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(specialtyLabel)
                    .addComponent(specialtyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(licenseLabel)
                    .addComponent(licenseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(officeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(officeLabel))
                .addGap(30, 30, 30)
                .addComponent(usernameLabel)
                .addGap(18, 18, 18)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordLabel)
                .addGap(27, 27, 27)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordConfirmLabel)
                .addGap(18, 18, 18)
                .addComponent(passwordConfirmField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(saveDoctorButton)
                .addContainerGap(161, Short.MAX_VALUE))
        );

        doctorTabbedPane.addTab("Modify info", profilePanel);

        acceptAppointmentIdLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        acceptAppointmentIdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        acceptAppointmentIdLabel.setText("Appointment ID");

        acceptAppointmentTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        acceptAppointmentTitleLabel.setText("Accept medical appointment");

        acceptAppointmentComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        acceptAppointmentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        profileSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        acceptAppointmentButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        acceptAppointmentButton.setText("Accept");
        acceptAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptAppointmentButtonActionPerformed(evt);
            }
        });

        rescheduleAppointmentTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rescheduleAppointmentTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rescheduleAppointmentTitleLabel.setText("Reschedule medical appointment");

        rescheduleAppointmentIdLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rescheduleAppointmentIdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rescheduleAppointmentIdLabel.setText("Appointment");

        rescheduleAppointmentComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rescheduleAppointmentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        rescheduleAppointmentButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rescheduleAppointmentButton.setText("Accept");
        rescheduleAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rescheduleAppointmentButtonActionPerformed(evt);
            }
        });

        newAppointmentTimeLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        newAppointmentTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newAppointmentTimeLabel.setText("New time appointment");

        newAppointmentTimeField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        rescheduleReasonLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        rescheduleReasonLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rescheduleReasonLabel.setText("Reason for appointment");

        rescheduleReasonField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        appointmentActionSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        completeAppointmentTitleLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        completeAppointmentTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        completeAppointmentTitleLabel.setText("Complete medical appointment");

        completeAppointmentIdLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        completeAppointmentIdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        completeAppointmentIdLabel.setText("Appointment");

        completeAppointmentComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        completeAppointmentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        diagnosisLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        diagnosisLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        diagnosisLabel.setText("Diagnosis");

        appointmentObservationsLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentObservationsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        appointmentObservationsLabel.setText("Observations");

        recommendedTreatmentLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        recommendedTreatmentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recommendedTreatmentLabel.setText("Recommended treatment");

        followUpLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        followUpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        followUpLabel.setText("Follow-up indication");

        completeAppointmentButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        completeAppointmentButton.setText("Complete");
        completeAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeAppointmentButtonActionPerformed(evt);
            }
        });

        hospitalizationSelectorLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationSelectorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hospitalizationSelectorLabel.setText("Hospitalization");

        hospitalizationReasonLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationReasonLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hospitalizationReasonLabel.setText("Reason for hospitalization");

        entryDateLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        entryDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        entryDateLabel.setText("Date of entry");

        entryDateField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        estimatedDurationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        estimatedDurationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        estimatedDurationLabel.setText("Estimated duration");

        estimatedDurationField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        hospitalizationObservationsLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationObservationsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hospitalizationObservationsLabel.setText("Observations");

        hospitalizationObservationsArea.setColumns(20);
        hospitalizationObservationsArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationObservationsArea.setRows(5);
        hospitalizationObservationsScrollPane.setViewportView(hospitalizationObservationsArea);

        generateHospitalizationButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        generateHospitalizationButton.setText("Generate");
        generateHospitalizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateHospitalizationButtonActionPerformed(evt);
            }
        });

        hospitalizationRequestComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationRequestComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        hospitalizationRequestsRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationRequestsRadioButton.setText("Requests");

        hospitalizationPatientRadioButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationPatientRadioButton.setText("Patient ID");

        diagnosisArea.setColumns(20);
        diagnosisArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        diagnosisArea.setRows(5);
        diagnosisScrollPane.setViewportView(diagnosisArea);

        appointmentObservationsArea.setColumns(20);
        appointmentObservationsArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        appointmentObservationsArea.setRows(5);
        appointmentObservationsScrollPane.setViewportView(appointmentObservationsArea);

        recommendedTreatmentArea.setColumns(20);
        recommendedTreatmentArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        recommendedTreatmentArea.setRows(5);
        recommendedTreatmentScrollPane.setViewportView(recommendedTreatmentArea);

        followUpArea.setColumns(20);
        followUpArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        followUpArea.setRows(5);
        followUpScrollPane.setViewportView(followUpArea);

        hospitalizationSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        processHospitalizationButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        processHospitalizationButton.setText("Cancel");
        processHospitalizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processHospitalizationButtonActionPerformed(evt);
            }
        });

        hospitalizationPatientComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationPatientComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        hospitalizationReasonArea.setColumns(20);
        hospitalizationReasonArea.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hospitalizationReasonArea.setRows(5);
        hospitalizationReasonScrollPane.setViewportView(hospitalizationReasonArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(appointmentManagementPanel);
        appointmentManagementPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(acceptAppointmentButton)
                                        .addGap(87, 87, 87))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(acceptAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(67, 67, 67))))
                            .addComponent(acceptAppointmentIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(profileSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(acceptAppointmentTitleLabel)
                        .addGap(22, 22, 22)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rescheduleAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rescheduleAppointmentIdLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(newAppointmentTimeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rescheduleReasonLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(90, 90, 90)
                                    .addComponent(rescheduleAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(99, 99, 99)
                                    .addComponent(newAppointmentTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(98, 98, 98)
                                    .addComponent(rescheduleReasonField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(112, 112, 112)
                                    .addComponent(rescheduleAppointmentButton)))
                            .addGap(91, 91, 91))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appointmentActionSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(completeAppointmentButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(completeAppointmentIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(completeAppointmentTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(99, 99, 99)
                                        .addComponent(completeAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 25, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(diagnosisLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(appointmentObservationsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(followUpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(recommendedTreatmentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(diagnosisScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(appointmentObservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(recommendedTreatmentScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(followUpScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(hospitalizationSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hospitalizationSelectorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(entryDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(estimatedDurationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(hospitalizationObservationsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(entryDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(estimatedDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(processHospitalizationButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(generateHospitalizationButton))
                            .addComponent(hospitalizationObservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(56, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(hospitalizationRequestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(hospitalizationRequestsRadioButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(hospitalizationPatientRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(hospitalizationPatientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hospitalizationReasonLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(hospitalizationReasonScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(profileSeparator)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appointmentActionSeparator)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(completeAppointmentTitleLabel)
                        .addGap(10, 10, 10)
                        .addComponent(completeAppointmentIdLabel)
                        .addGap(18, 18, 18)
                        .addComponent(completeAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(diagnosisLabel)
                        .addGap(18, 18, 18)
                        .addComponent(diagnosisScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(appointmentObservationsLabel)
                        .addGap(18, 18, 18)
                        .addComponent(appointmentObservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(recommendedTreatmentLabel)
                        .addGap(18, 18, 18)
                        .addComponent(recommendedTreatmentScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(followUpLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(followUpScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(completeAppointmentButton)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(acceptAppointmentTitleLabel)
                                .addGap(18, 18, 18)
                                .addComponent(acceptAppointmentIdLabel)
                                .addGap(18, 18, 18)
                                .addComponent(acceptAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(acceptAppointmentButton))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(rescheduleAppointmentTitleLabel)
                                .addGap(18, 18, 18)
                                .addComponent(rescheduleAppointmentIdLabel)
                                .addGap(18, 18, 18)
                                .addComponent(rescheduleAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(newAppointmentTimeLabel)
                                .addGap(18, 18, 18)
                                .addComponent(newAppointmentTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rescheduleReasonLabel)
                                .addGap(18, 18, 18)
                                .addComponent(rescheduleReasonField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(rescheduleAppointmentButton)))
                        .addGap(18, 18, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(hospitalizationSelectorLabel)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hospitalizationRequestsRadioButton)
                    .addComponent(hospitalizationPatientRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hospitalizationRequestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hospitalizationPatientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(hospitalizationReasonLabel)
                .addGap(16, 16, 16)
                .addComponent(hospitalizationReasonScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(entryDateLabel)
                .addGap(18, 18, 18)
                .addComponent(entryDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(estimatedDurationLabel)
                .addGap(18, 18, 18)
                .addComponent(estimatedDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(hospitalizationObservationsLabel)
                .addGap(18, 18, 18)
                .addComponent(hospitalizationObservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateHospitalizationButton)
                    .addComponent(processHospitalizationButton))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(hospitalizationSeparator, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        doctorTabbedPane.addTab("Request/Appointments", appointmentManagementPanel);

        prescriptionAppointmentIdLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        prescriptionAppointmentIdLabel.setText("Appointment ID");

        medicationNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        medicationNameLabel.setText("Medication name");

        medicationNameField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        doseLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        doseLabel.setText("Dose");

        doseField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        administrationRouteLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        administrationRouteLabel.setText("Administration route");

        administrationRouteField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        frequencyLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        frequencyLabel.setText("Frecuency");

        frequencyField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        treatmentDurationLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        treatmentDurationLabel.setText("Treatment duration");

        treatmentDurationField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        additionalInstructionsLabel.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        additionalInstructionsLabel.setText("Additional instructions");

        additionalInstructionsField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        prescriptionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Appointment ID", "Medication name", "Dose", "Administration route", "Treatment duration", "Additional instructions", "Frecuency"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        prescriptionsTableScrollPane.setViewportView(prescriptionsTable);

        addPrescriptionButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        addPrescriptionButton.setText("Add");
        addPrescriptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPrescriptionButtonActionPerformed(evt);
            }
        });

        clearPrescriptionTableButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        clearPrescriptionTableButton.setText("Prescribe");
        clearPrescriptionTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearPrescriptionTableButtonActionPerformed(evt);
            }
        });

        prescriptionAppointmentComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        prescriptionAppointmentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select one" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(prescriptionsPanel);
        prescriptionsPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(prescriptionsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(prescriptionAppointmentIdLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(prescriptionAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9)
                                        .addComponent(medicationNameLabel))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(treatmentDurationLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(treatmentDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(additionalInstructionsLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(additionalInstructionsField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(frequencyLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(frequencyField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(medicationNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(doseLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(doseField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(administrationRouteLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(administrationRouteField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(addPrescriptionButton))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(583, 583, 583)
                        .addComponent(clearPrescriptionTableButton)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prescriptionAppointmentIdLabel)
                    .addComponent(medicationNameLabel)
                    .addComponent(medicationNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(doseLabel)
                    .addComponent(doseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(administrationRouteLabel)
                    .addComponent(administrationRouteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addPrescriptionButton)
                    .addComponent(prescriptionAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(treatmentDurationLabel)
                    .addComponent(treatmentDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(additionalInstructionsLabel)
                    .addComponent(additionalInstructionsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frequencyLabel)
                    .addComponent(frequencyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(prescriptionsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(clearPrescriptionTableButton)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        doctorTabbedPane.addTab("Prescribe medications", prescriptionsPanel);

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(doctorTabbedPane))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doctorTabbedPane))
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

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        totalAppointmentsRadioButton.setSelected(false);
        refreshDoctorAppointmentsTable(true);
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void saveDoctorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDoctorButtonActionPerformed
        Response response = context.getDoctorController().update(
                doctor.getId(),
                usernameField.getText(),
                firstnameField.getText(),
                lastnameField.getText(),
                passwordField.getText(),
                passwordConfirmField.getText(),
                specialtyComboBox.getItemAt(specialtyComboBox.getSelectedIndex()),
                licenseField.getText(),
                officeField.getText());
        JOptionPane.showMessageDialog(this, response.getMessage());
        users = new ArrayList<>(context.getUserRepository().getAll());
    }//GEN-LAST:event_saveDoctorButtonActionPerformed

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        unregisterObservers();
        LoginView login = new LoginView();
        this.setVisible(false);
        login.setVisible(true);
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void backToAdminButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToAdminButtonActionPerformed
        unregisterObservers();
        AdminView admin = new AdminView(user,users,hospitalizations, appointments);
        this.setVisible(false);
        admin.setVisible(true);
    }//GEN-LAST:event_backToAdminButtonActionPerformed

    private void processHospitalizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processHospitalizationButtonActionPerformed
        String hospitalizationId = hospitalizationRequestComboBox.getItemAt(hospitalizationRequestComboBox.getSelectedIndex());
        Response response;
        if (hospitalizationRequestsRadioButton.isSelected()) {
            response = context.getHospitalizationController().deny(hospitalizationId);
        } else {
            response = context.getHospitalizationController().approve(hospitalizationId);
        }
        JOptionPane.showMessageDialog(this, response.getMessage());
        hospitalizations = new ArrayList<>(context.getHospitalizationRepository().getAll());
        loadDoctorCombos();
    }//GEN-LAST:event_processHospitalizationButtonActionPerformed

    private void generateHospitalizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateHospitalizationButtonActionPerformed
        if (hospitalizationPatientRadioButton.isSelected()) {
            Long patientId = null;
            String selectedPatient = hospitalizationPatientComboBox.getItemAt(hospitalizationPatientComboBox.getSelectedIndex());
            for (User use : context.getUserRepository().getAll()) {
                if (use instanceof Patient p
                        && (String.valueOf(p.getId()).equals(selectedPatient)
                        || (p.getFirstname() + " " + p.getLastname()).equals(selectedPatient))) {
                    patientId = p.getId();
                }
            }
            if (patientId == null) {
                JOptionPane.showMessageDialog(this, "Patient is required.");
                return;
            }
            Response response = context.getHospitalizationController().requestDirect(
                    patientId,
                    doctor.getId(),
                    entryDateField.getText(),
                    hospitalizationReasonArea.getText(),
                    RoomType.STANDARD.toDisplayName(),
                    hospitalizationObservationsArea.getText());
            JOptionPane.showMessageDialog(this, response.getMessage());
            hospitalizations = new ArrayList<>(context.getHospitalizationRepository().getAll());
            loadDoctorCombos();
        }
    }//GEN-LAST:event_generateHospitalizationButtonActionPerformed

    private void searchPatientAppointmentsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPatientAppointmentsButtonActionPerformed
        refreshSelectedPatientAppointmentsTable(true);
    }//GEN-LAST:event_searchPatientAppointmentsButtonActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        pendingAppointmentsRadioButton.setSelected(false);
        refreshDoctorAppointmentsTable(false);
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void acceptAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptAppointmentButtonActionPerformed
        String idAppointment = acceptAppointmentComboBox.getItemAt(acceptAppointmentComboBox.getSelectedIndex());
        Response response = context.getAppointmentController().accept(idAppointment);
        JOptionPane.showMessageDialog(this, response.getMessage());
        appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
        loadDoctorCombos();
    }//GEN-LAST:event_acceptAppointmentButtonActionPerformed

    private void completeAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeAppointmentButtonActionPerformed
        String idAppointment = completeAppointmentComboBox.getItemAt(completeAppointmentComboBox.getSelectedIndex());
        Response response = context.getAppointmentController().complete(
                idAppointment,
                diagnosisArea.getText(),
                appointmentObservationsArea.getText(),
                recommendedTreatmentArea.getText(),
                followUpArea.getText());
        JOptionPane.showMessageDialog(this, response.getMessage());
        appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
        loadDoctorCombos();
    }//GEN-LAST:event_completeAppointmentButtonActionPerformed

    private void clearPrescriptionTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearPrescriptionTableButtonActionPerformed
        refreshPrescriptionsTable();
    }//GEN-LAST:event_clearPrescriptionTableButtonActionPerformed

    private void addPrescriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPrescriptionButtonActionPerformed
        String appointmentId = prescriptionAppointmentComboBox.getItemAt(prescriptionAppointmentComboBox.getSelectedIndex());
        Response response = context.getPrescriptionController().prescribe(
                appointmentId,
                medicationNameField.getText(),
                doseField.getText(),
                administrationRouteField.getText(),
                treatmentDurationField.getText(),
                additionalInstructionsField.getText(),
                frequencyField.getText());
        JOptionPane.showMessageDialog(this, response.getMessage());
        if (response.isOk()) {
            refreshPrescriptionsTable();
        }
    }//GEN-LAST:event_addPrescriptionButtonActionPerformed

    private void rescheduleAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rescheduleAppointmentButtonActionPerformed
        String appointmentId = rescheduleAppointmentComboBox.getItemAt(rescheduleAppointmentComboBox.getSelectedIndex());
        Response response = context.getAppointmentController().reschedule(
                appointmentId,
                newAppointmentTimeField.getText(),
                rescheduleReasonField.getText());
        JOptionPane.showMessageDialog(this, response.getMessage());
        appointments = new ArrayList<>(context.getAppointmentRepository().getAll());
        loadDoctorCombos();
    }//GEN-LAST:event_rescheduleAppointmentButtonActionPerformed




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton clearPrescriptionTableButton;
    private javax.swing.JButton backToAdminButton;
    private javax.swing.JButton logoutButton;
    private javax.swing.JButton processHospitalizationButton;
    private javax.swing.JButton acceptAppointmentButton;
    private javax.swing.JButton rescheduleAppointmentButton;
    private javax.swing.JButton completeAppointmentButton;
    private javax.swing.JButton generateHospitalizationButton;
    private javax.swing.JButton addPrescriptionButton;
    private javax.swing.JButton searchPatientAppointmentsButton;
    private javax.swing.JButton saveDoctorButton;
    private javax.swing.JComboBox<String> specialtyComboBox;
    private javax.swing.JComboBox<String> acceptAppointmentComboBox;
    private javax.swing.JComboBox<String> rescheduleAppointmentComboBox;
    private javax.swing.JComboBox<String> completeAppointmentComboBox;
    private javax.swing.JComboBox<String> patientAppointmentsPatientComboBox;
    private javax.swing.JComboBox<String> hospitalizationRequestComboBox;
    private javax.swing.JComboBox<String> prescriptionAppointmentComboBox;
    private javax.swing.JComboBox<String> hospitalizationPatientComboBox;
    private javax.swing.JLabel doctorTitleLabel;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel passwordConfirmLabel;
    private javax.swing.JLabel acceptAppointmentTitleLabel;
    private javax.swing.JLabel acceptAppointmentIdLabel;
    private javax.swing.JLabel rescheduleAppointmentTitleLabel;
    private javax.swing.JLabel rescheduleAppointmentIdLabel;
    private javax.swing.JLabel newAppointmentTimeLabel;
    private javax.swing.JLabel rescheduleReasonLabel;
    private javax.swing.JLabel completeAppointmentTitleLabel;
    private javax.swing.JLabel firstnameLabel;
    private javax.swing.JLabel completeAppointmentIdLabel;
    private javax.swing.JLabel diagnosisLabel;
    private javax.swing.JLabel appointmentObservationsLabel;
    private javax.swing.JLabel recommendedTreatmentLabel;
    private javax.swing.JLabel followUpLabel;
    private javax.swing.JLabel hospitalizationSelectorLabel;
    private javax.swing.JLabel hospitalizationReasonLabel;
    private javax.swing.JLabel entryDateLabel;
    private javax.swing.JLabel estimatedDurationLabel;
    private javax.swing.JLabel lastnameLabel;
    private javax.swing.JLabel hospitalizationObservationsLabel;
    private javax.swing.JLabel prescriptionAppointmentIdLabel;
    private javax.swing.JLabel medicationNameLabel;
    private javax.swing.JLabel doseLabel;
    private javax.swing.JLabel administrationRouteLabel;
    private javax.swing.JLabel frequencyLabel;
    private javax.swing.JLabel treatmentDurationLabel;
    private javax.swing.JLabel additionalInstructionsLabel;
    private javax.swing.JLabel patientSelectorLabel;
    private javax.swing.JLabel specialtyLabel;
    private javax.swing.JLabel licenseLabel;
    private javax.swing.JLabel officeLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JPanel appointmentManagementPanel;
    private javax.swing.JPanel prescriptionsPanel;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JPanel doctorAppointmentsPanel;
    private javax.swing.JPanel patientAppointmentsPanel;
    private javax.swing.JRadioButton totalAppointmentsRadioButton;
    private javax.swing.JRadioButton pendingAppointmentsRadioButton;
    private javax.swing.JRadioButton hospitalizationRequestsRadioButton;
    private javax.swing.JRadioButton hospitalizationPatientRadioButton;
    private javax.swing.JScrollPane hospitalizationObservationsScrollPane;
    private javax.swing.JScrollPane hospitalizationReasonScrollPane;
    private javax.swing.JScrollPane prescriptionsTableScrollPane;
    private javax.swing.JScrollPane doctorAppointmentsTableScrollPane;
    private javax.swing.JScrollPane patientAppointmentsTableScrollPane;
    private javax.swing.JScrollPane diagnosisScrollPane;
    private javax.swing.JScrollPane appointmentObservationsScrollPane;
    private javax.swing.JScrollPane recommendedTreatmentScrollPane;
    private javax.swing.JScrollPane followUpScrollPane;
    private javax.swing.JSeparator profileSeparator;
    private javax.swing.JSeparator appointmentActionSeparator;
    private javax.swing.JSeparator hospitalizationSeparator;
    private javax.swing.JTabbedPane doctorTabbedPane;
    private javax.swing.JTable prescriptionsTable;
    private javax.swing.JTable doctorAppointmentsTable;
    private javax.swing.JTable patientAppointmentsTable;
    private javax.swing.JTextArea hospitalizationObservationsArea;
    private javax.swing.JTextArea diagnosisArea;
    private javax.swing.JTextArea appointmentObservationsArea;
    private javax.swing.JTextArea recommendedTreatmentArea;
    private javax.swing.JTextArea followUpArea;
    private javax.swing.JTextArea hospitalizationReasonArea;
    private javax.swing.JTextField firstnameField;
    private javax.swing.JTextField passwordConfirmField;
    private javax.swing.JTextField newAppointmentTimeField;
    private javax.swing.JTextField rescheduleReasonField;
    private javax.swing.JTextField lastnameField;
    private javax.swing.JTextField entryDateField;
    private javax.swing.JTextField estimatedDurationField;
    private javax.swing.JTextField medicationNameField;
    private javax.swing.JTextField doseField;
    private javax.swing.JTextField administrationRouteField;
    private javax.swing.JTextField frequencyField;
    private javax.swing.JTextField treatmentDurationField;
    private javax.swing.JTextField additionalInstructionsField;
    private javax.swing.JTextField licenseField;
    private javax.swing.JTextField usernameField;
    private javax.swing.JTextField officeField;
    private javax.swing.JTextField passwordField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel headerPanel;
    // End of variables declaration//GEN-END:variables
}
