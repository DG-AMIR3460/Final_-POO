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
import main.model.entities.Patient;
import main.model.entities.User;
import main.model.repositories.IUserRepository;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private int dragX, dragY;

    private final AuthController authController;
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
    private JTabbedPane tabPaneMain;

    // Login tab
    private PanelRound pnlLoginContent;
    private JLabel lblTitle;
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblPassword;
    private JTextField txtPassword;
    private JButton btnLogin;

    // Patient register tab
    private JPanel pnlRegisterPatient;
    private JLabel lblFirstname;
    private JTextField txtFirstname;
    private JLabel lblLastname;
    private JTextField txtLastname;
    private JLabel lblId;
    private JTextField txtId;
    private JLabel lblGender;
    private JComboBox<String> cmbGender;
    private JLabel lblPhone;
    private JTextField txtPhone;
    private JLabel lblEmail;
    private JTextField txtEmail;
    private JLabel lblRegUsername;
    private JTextField txtRegUsername;
    private JTextField txtRegPassword;
    private JLabel lblRegPassword;
    private JLabel lblRegPasswordConfirm;
    private JTextField txtRegPasswordConfirm;
    private JLabel lblAddress;
    private JTextField txtAddress;
    private JLabel lblBirthdate;
    private JTextField txtBirthdate;
    private JButton btnRegisterPatient;

    public LoginView(AuthController authController, PatientController patientController,
                     DoctorController doctorController, AppointmentController appointmentController,
                     HospitalizationController hospitalizationController,
                     PrescriptionController prescriptionController,
                     IUserRepository userRepository) {
        this.authController = authController;
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        this.hospitalizationController = hospitalizationController;
        this.prescriptionController = prescriptionController;
        this.userRepository = userRepository;
        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        pnlMain = new PanelRound();
        pnlTitleBar = new PanelRound();
        btnClose = new JButton();
        tabPaneMain = new JTabbedPane();
        pnlLoginContent = new PanelRound();
        lblTitle = new JLabel();
        lblUsername = new JLabel();
        txtUsername = new JTextField();
        lblPassword = new JLabel();
        txtPassword = new JTextField();
        btnLogin = new JButton();
        pnlRegisterPatient = new JPanel();
        lblFirstname = new JLabel();
        txtFirstname = new JTextField();
        lblLastname = new JLabel();
        txtLastname = new JTextField();
        lblId = new JLabel();
        txtId = new JTextField();
        lblGender = new JLabel();
        cmbGender = new JComboBox<>();
        lblPhone = new JLabel();
        txtPhone = new JTextField();
        lblEmail = new JLabel();
        txtEmail = new JTextField();
        lblRegUsername = new JLabel();
        txtRegUsername = new JTextField();
        txtRegPassword = new JTextField();
        lblRegPassword = new JLabel();
        lblRegPasswordConfirm = new JLabel();
        txtRegPasswordConfirm = new JTextField();
        lblAddress = new JLabel();
        txtAddress = new JTextField();
        lblBirthdate = new JLabel();
        txtBirthdate = new JTextField();
        btnRegisterPatient = new JButton();

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                dragX = evt.getX(); dragY = evt.getY();
            }
        });

        btnClose.setFont(new Font("Yu Gothic UI", 0, 18));
        btnClose.setText("X");
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        btnClose.setFocusable(false);
        btnClose.addActionListener(e -> System.exit(0));

        GroupLayout titleBarLayout = new GroupLayout(pnlTitleBar);
        pnlTitleBar.setLayout(titleBarLayout);
        titleBarLayout.setHorizontalGroup(
            titleBarLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, titleBarLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose).addGap(19, 19, 19))
        );
        titleBarLayout.setVerticalGroup(
            titleBarLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(btnClose, GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        lblTitle.setFont(new Font("Yu Gothic UI", Font.BOLD, 24));
        lblTitle.setText("LOGIN");

        txtUsername.setFont(new Font("Yu Gothic UI", 0, 18));
        lblUsername.setFont(new Font("Yu Gothic UI", 0, 18));
        lblUsername.setText("USERNAME");

        txtPassword.setFont(new Font("Yu Gothic UI", 0, 18));
        lblPassword.setFont(new Font("Yu Gothic UI", 0, 18));
        lblPassword.setText("PASSWORD");

        btnLogin.setFont(new Font("Yu Gothic UI", 0, 18));
        btnLogin.setText("ENTER");
        btnLogin.addActionListener(e -> onLogin());

        GroupLayout loginLayout = new GroupLayout(pnlLoginContent);
        pnlLoginContent.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                .addContainerGap(475, Short.MAX_VALUE).addComponent(lblTitle).addGap(481, 481, 481))
            .addGroup(loginLayout.createSequentialGroup()
                .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(loginLayout.createSequentialGroup().addGap(431, 431, 431)
                        .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                                .addGroup(loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(lblPassword, GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblUsername, GroupLayout.Alignment.TRAILING))
                                .addGap(24, 24, 24))
                            .addGroup(GroupLayout.Alignment.TRAILING,
                                loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE))))
                    .addGroup(loginLayout.createSequentialGroup().addGap(471, 471, 471).addComponent(btnLogin)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(loginLayout.createSequentialGroup().addGap(63, 63, 63)
                .addComponent(lblTitle).addGap(74, 74, 74)
                .addComponent(lblUsername).addGap(18, 18, 18)
                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18).addComponent(lblPassword).addGap(18, 18, 18)
                .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31).addComponent(btnLogin).addContainerGap(137, Short.MAX_VALUE))
        );

        tabPaneMain.addTab("Login", pnlLoginContent);

        lblFirstname.setFont(new Font("Yu Gothic UI", 0, 18)); lblFirstname.setText("Firstname");
        txtFirstname.setFont(new Font("Yu Gothic UI", 0, 18));
        lblLastname.setFont(new Font("Yu Gothic UI", 0, 18)); lblLastname.setText("Lastname");
        txtLastname.setFont(new Font("Yu Gothic UI", 0, 18));
        lblId.setFont(new Font("Yu Gothic UI", 0, 18)); lblId.setText("ID");
        txtId.setFont(new Font("Yu Gothic UI", 0, 18));
        lblGender.setFont(new Font("Yu Gothic UI", 0, 18)); lblGender.setText("Gender");
        lblPhone.setFont(new Font("Yu Gothic UI", 0, 18)); lblPhone.setText("Phone");
        txtPhone.setFont(new Font("Yu Gothic UI", 0, 18));
        lblEmail.setFont(new Font("Yu Gothic UI", 0, 18)); lblEmail.setText("Email");
        txtEmail.setFont(new Font("Yu Gothic UI", 0, 18));
        lblRegUsername.setFont(new Font("Yu Gothic UI", 0, 18)); lblRegUsername.setText("User");
        txtRegUsername.setFont(new Font("Yu Gothic UI", 0, 18));
        txtRegPassword.setFont(new Font("Yu Gothic UI", 0, 18));
        lblRegPassword.setFont(new Font("Yu Gothic UI", 0, 18)); lblRegPassword.setText("Password");
        lblRegPasswordConfirm.setFont(new Font("Yu Gothic UI", 0, 18)); lblRegPasswordConfirm.setText("Password confirmation");
        txtRegPasswordConfirm.setFont(new Font("Yu Gothic UI", 0, 18));
        lblAddress.setFont(new Font("Yu Gothic UI", 0, 18)); lblAddress.setText("Address");
        txtAddress.setFont(new Font("Yu Gothic UI", 0, 18));
        lblBirthdate.setFont(new Font("Yu Gothic UI", 0, 18)); lblBirthdate.setText("Birthdate");
        txtBirthdate.setFont(new Font("Yu Gothic UI", 0, 18));
        cmbGender.setFont(new Font("Yu Gothic UI", 0, 18));
        cmbGender.setModel(new DefaultComboBoxModel<>(new String[]{"Select one", "Female", "Male"}));
        btnRegisterPatient.setFont(new Font("Yu Gothic UI", 0, 18));
        btnRegisterPatient.setText("Save");
        btnRegisterPatient.addActionListener(e -> onRegisterPatient());

        GroupLayout regLayout = new GroupLayout(pnlRegisterPatient);
        pnlRegisterPatient.setLayout(regLayout);
        regLayout.setHorizontalGroup(
            regLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(regLayout.createSequentialGroup()
                .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(regLayout.createSequentialGroup().addGap(450, 450, 450).addComponent(lblRegPassword))
                    .addGroup(regLayout.createSequentialGroup().addGap(434, 434, 434)
                        .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtRegPassword, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRegPasswordConfirm, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(regLayout.createSequentialGroup().addGap(473, 473, 473).addComponent(lblRegUsername))
                    .addGroup(regLayout.createSequentialGroup().addGap(432, 432, 432)
                        .addComponent(txtRegUsername, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                    .addGroup(regLayout.createSequentialGroup().addGap(456, 456, 456).addComponent(btnRegisterPatient))
                    .addGroup(regLayout.createSequentialGroup().addGap(396, 396, 396).addComponent(lblRegPasswordConfirm))
                    .addGroup(regLayout.createSequentialGroup().addGap(91, 91, 91)
                        .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addGroup(regLayout.createSequentialGroup().addComponent(lblBirthdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtBirthdate, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
                            .addGroup(regLayout.createSequentialGroup().addComponent(lblFirstname)
                                .addGap(34, 34, 34)
                                .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblLastname).addComponent(lblAddress))
                        .addGap(18, 18, 18)
                        .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(regLayout.createSequentialGroup()
                                .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18).addComponent(lblId).addGap(30, 30, 30)
                                .addComponent(txtId, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18).addComponent(lblGender).addGap(26, 26, 26)
                                .addComponent(cmbGender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(regLayout.createSequentialGroup()
                                .addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18).addComponent(lblPhone).addGap(18, 18, 18)
                                .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblEmail).addGap(18, 18, 18)
                                .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        regLayout.setVerticalGroup(
            regLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(regLayout.createSequentialGroup().addGap(19, 19, 19)
                .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFirstname)
                    .addComponent(txtFirstname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLastname)
                    .addComponent(txtLastname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId)
                    .addComponent(txtId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGender)
                    .addComponent(cmbGender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblBirthdate)
                        .addComponent(txtBirthdate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblAddress)
                        .addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPhone)
                        .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(regLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblEmail)
                        .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addComponent(lblRegUsername).addGap(18, 18, 18)
                .addComponent(txtRegUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRegPassword).addGap(18, 18, 18)
                .addComponent(txtRegPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18).addComponent(lblRegPasswordConfirm).addGap(18, 18, 18)
                .addComponent(txtRegPasswordConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37).addComponent(btnRegisterPatient).addGap(42, 42, 42))
        );

        tabPaneMain.addTab("Patient register", pnlRegisterPatient);

        GroupLayout mainLayout = new GroupLayout(pnlMain);
        pnlMain.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlTitleBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(tabPaneMain, GroupLayout.PREFERRED_SIZE, 1028, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(pnlTitleBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPaneMain).addContainerGap())
        );

        GroupLayout contentLayout = new GroupLayout(getContentPane());
        getContentPane().setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }

    private void onLogin() {
        Response response = authController.login(txtUsername.getText(), txtPassword.getText());
        if (!response.isOk()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        org.json.JSONObject data = (org.json.JSONObject) response.getData();
        long userId = data.getLong("id");
        String type = data.getString("type");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        setVisible(false);
        switch (type) {
            case "Administrator" -> {
                AdminView adminView = new AdminView((Administrator) user, doctorController,
                        patientController, appointmentController, hospitalizationController,
                        prescriptionController, userRepository);
                adminView.setVisible(true);
            }
            case "Doctor" -> {
                DoctorView doctorView = new DoctorView(user, (Doctor) user, patientController,
                        doctorController, appointmentController, hospitalizationController,
                        prescriptionController, userRepository);
                doctorView.setVisible(true);
            }
            default -> {
                PatientView patientView = new PatientView(user, (Patient) user, patientController,
                        doctorController, appointmentController, hospitalizationController,
                        prescriptionController, userRepository);
                patientView.setVisible(true);
            }
        }
    }

    private void onRegisterPatient() {
        String genderStr = (String) cmbGender.getSelectedItem();
        Response response = patientController.register(
                txtId.getText(), txtRegUsername.getText(), txtFirstname.getText(),
                txtLastname.getText(), txtRegPassword.getText(), txtRegPasswordConfirm.getText(),
                txtEmail.getText(), txtBirthdate.getText(), genderStr,
                txtPhone.getText(), txtAddress.getText());
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isOk() ? "Success" : "Error",
                response.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (response.isOk()) clearRegisterFields();
    }

    private void clearRegisterFields() {
        txtFirstname.setText(""); txtLastname.setText(""); txtId.setText("");
        cmbGender.setSelectedIndex(0); txtPhone.setText(""); txtEmail.setText("");
        txtRegUsername.setText(""); txtRegPassword.setText(""); txtRegPasswordConfirm.setText("");
        txtAddress.setText(""); txtBirthdate.setText("");
    }
}
