package lostandfound.ui;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import lostandfound.dao.UserDAO;
import lostandfound.model.User;
import lostandfound.util.DropdownData;
import lostandfound.util.UITheme;

public class SignupFrame extends JFrame {

    private JTextField nameField, emailField, studentIdField, sectionField;
    private JPasswordField passwordField, confirmField;
    private JComboBox<String> collegeBox, programBox, yearBox;
    private JLabel statusLabel;
    private final UserDAO userDAO = new UserDAO();
    private ImageIcon logoIcon = null;
    private Image heroBgImg = null;

    public SignupFrame() {
        loadKitImages();
        setTitle("BISU Lost & Found — Student Registration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void loadKitImages() {
        try {
            URL logoUrl = getClass().getResource("/lostandfound/kit/logobisu.png");
            if (logoUrl != null) {
                Image scaled = new ImageIcon(logoUrl).getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("[SignupFrame] logo: " + e.getMessage());
        }
        try {
            URL bgUrl = getClass().getResource("/lostandfound/kit/campusbg.jpg");
            if (bgUrl != null) heroBgImg = new ImageIcon(bgUrl).getImage();
        } catch (Exception e) {
            System.err.println("[SignupFrame] bg: " + e.getMessage());
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());

        // ── LEFT ──────────────────────────────────────────────────────────────
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                if (heroBgImg != null) {
                    g2.drawImage(heroBgImg, 0, 0, getWidth(), getHeight(), this);
                    g2.setColor(new Color(10,55,30,220));
                    g2.fillRect(0,0,getWidth(),getHeight());
                } else {
                    g2.setPaint(new GradientPaint(0,0,new Color(10,55,30),getWidth(),getHeight(),new Color(25,120,70)));
                    g2.fillRect(0,0,getWidth(),getHeight());
                }
            }
        };
        left.setPreferredSize(new Dimension(320,680)); left.setLayout(new GridBagLayout());

        JPanel brand = new JPanel(); brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JPanel lr = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); lr.setOpaque(false);
        JPanel lc = new JPanel(new GridBagLayout());
        lc.setBackground(new Color(255,255,255,40)); lc.setPreferredSize(new Dimension(44,44));
        if (logoIcon != null) {
            JLabel logoLbl = new JLabel(logoIcon);
            lc.add(logoLbl);
        } else {
            JLabel bl = new JLabel("B"); bl.setFont(new Font("Segoe UI",Font.BOLD,22)); bl.setForeground(Color.WHITE);
            lc.add(bl);
        }
        JPanel lt = new JPanel(); lt.setOpaque(false); lt.setLayout(new BoxLayout(lt,BoxLayout.Y_AXIS));
        lt.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        JLabel ln1 = new JLabel("Bohol Island State University");
        ln1.setFont(new Font("Segoe UI",Font.BOLD,12)); ln1.setForeground(Color.WHITE);
        JLabel ln2 = new JLabel("BISU Candijay Campus");
        ln2.setFont(new Font("Segoe UI",Font.PLAIN,11)); ln2.setForeground(new Color(180,230,200));
        lt.add(ln1); lt.add(ln2); lr.add(lc); lr.add(lt);

        JLabel t = new JLabel("Student Registration");
        t.setFont(new Font("Segoe UI",Font.BOLD,22)); t.setForeground(Color.WHITE);
        t.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = new JLabel("Create your Lost & Found account");
        s.setFont(new Font("Segoe UI",Font.PLAIN,12)); s.setForeground(new Color(180,230,200));
        s.setAlignmentX(LEFT_ALIGNMENT);

        JPanel colleges = new JPanel(); colleges.setOpaque(false);
        colleges.setLayout(new BoxLayout(colleges,BoxLayout.Y_AXIS));
        colleges.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,new Color(255,255,255,40)),
            BorderFactory.createEmptyBorder(14,0,0,0)));
        JLabel colTitle = new JLabel("BISU Candijay Colleges:");
        colTitle.setFont(new Font("Segoe UI",Font.BOLD,11)); colTitle.setForeground(new Color(200,240,210));
        colTitle.setAlignmentX(LEFT_ALIGNMENT);
        colleges.add(colTitle); colleges.add(Box.createVerticalStrut(8));
        for (String[] c : new String[][]{
            {"CBM","Business & Management → BSHM, BSOAD"},
            {"CFMS","Fisheries & Marine Sciences → BSF, BSMB"},
            {"CoS","Sciences → BSCS, BSES"},
            {"CTE","Teacher Education → BEED, BSED"}}) {
            JLabel cl = new JLabel("<html><b style='color:#90EE90'>"+c[0]+"</b> — "+c[1]+"</html>");
            cl.setFont(new Font("Segoe UI",Font.PLAIN,11)); cl.setForeground(new Color(180,230,200));
            cl.setAlignmentX(LEFT_ALIGNMENT);
            colleges.add(cl); colleges.add(Box.createVerticalStrut(6));
        }

        brand.add(lr); brand.add(Box.createVerticalStrut(14));
        brand.add(t); brand.add(Box.createVerticalStrut(4));
        brand.add(s); brand.add(Box.createVerticalStrut(20));
        brand.add(colleges);
        left.add(brand);

        // ── RIGHT FORM ────────────────────────────────────────────────────────
        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));
        rightContent.setBackground(Color.WHITE);
        rightContent.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));

        JScrollPane scrollPane = new JScrollPane(rightContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(UITheme.FONT_SMALL); backBtn.setForeground(UITheme.PRIMARY);
        backBtn.setBorderPainted(false); backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(LEFT_ALIGNMENT);

        JLabel rTitle = new JLabel("Student Registration");
        rTitle.setFont(new Font("Segoe UI",Font.BOLD,22)); rTitle.setForeground(UITheme.TEXT_DARK);
        rTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel rSub = new JLabel("All fields marked * are required");
        rSub.setFont(UITheme.FONT_BODY); rSub.setForeground(UITheme.TEXT_GRAY);
        rSub.setAlignmentX(LEFT_ALIGNMENT);

        rightContent.add(backBtn); rightContent.add(Box.createVerticalStrut(4));
        rightContent.add(rTitle); rightContent.add(Box.createVerticalStrut(3));
        rightContent.add(rSub); rightContent.add(Box.createVerticalStrut(16));

        sectionDivider(rightContent, "Personal Information");
        nameField = addField(rightContent, "Full Name *", UITheme.createTextField());
        studentIdField = addField(rightContent, "Student ID *", UITheme.createTextField());
        emailField = addField(rightContent, "Email Address *", UITheme.createTextField());

        rightContent.add(Box.createVerticalStrut(4));
        sectionDivider(rightContent, "Academic Information");

        collegeBox = UITheme.createComboBox(DropdownData.COLLEGES);
        collegeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        addField(rightContent, "College *", collegeBox);

        programBox = UITheme.createComboBox(new String[]{"-- Select Program --"});
        programBox.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        addField(rightContent, "Program *", programBox);

        // Year + Section side by side
        JLabel ysLbl = UITheme.createLabel("Year Level & Section *");
        ysLbl.setAlignmentX(LEFT_ALIGNMENT);
        rightContent.add(ysLbl); rightContent.add(Box.createVerticalStrut(5));
        JPanel ysRow = new JPanel(new GridLayout(1,2,12,0));
        ysRow.setOpaque(false); ysRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        ysRow.setAlignmentX(LEFT_ALIGNMENT);
        yearBox = UITheme.createComboBox(DropdownData.YEAR_LEVELS);
        sectionField = UITheme.createTextField();
        sectionField.setToolTipText("e.g. A, B, C");
        ysRow.add(yearBox); ysRow.add(sectionField);
        rightContent.add(ysRow); rightContent.add(Box.createVerticalStrut(14));

        rightContent.add(Box.createVerticalStrut(2));
        sectionDivider(rightContent, "Account Security");
        passwordField = addPwField(rightContent, "Password * (minimum 6 characters)");
        confirmField  = addPwField(rightContent, "Confirm Password *");

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL); statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        rightContent.add(statusLabel); rightContent.add(Box.createVerticalStrut(10));

        JButton regBtn = UITheme.createSuccessButton("Create Account");
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); regBtn.setAlignmentX(LEFT_ALIGNMENT);
        rightContent.add(regBtn); rightContent.add(Box.createVerticalStrut(14));

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        loginRow.setOpaque(false); loginRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel has = new JLabel("Already have an account?  ");
        has.setFont(UITheme.FONT_BODY); has.setForeground(UITheme.TEXT_GRAY);
        JButton loginLink = new JButton("Sign In");
        loginLink.setFont(new Font("Segoe UI",Font.BOLD,13)); loginLink.setForeground(UITheme.PRIMARY);
        loginLink.setBorderPainted(false); loginLink.setContentAreaFilled(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginRow.add(has); loginRow.add(loginLink);
        rightContent.add(loginRow); rightContent.add(Box.createVerticalStrut(10));

        root.add(left, BorderLayout.WEST);
        root.add(scrollPane, BorderLayout.CENTER);
        add(root);

        collegeBox.addActionListener(e -> {
            String col = (String)collegeBox.getSelectedItem();
            programBox.removeAllItems();
            for (String p : DropdownData.PROGRAMS_BY_COLLEGE.getOrDefault(col, new String[]{"-- Select Program --"}))
                programBox.addItem(p);
        });

        regBtn.addActionListener(e -> doRegister());
        loginLink.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        backBtn.addActionListener(e -> { dispose(); new WelcomePage().setVisible(true); });
    }

    private void sectionDivider(JPanel p, String text) {
        JLabel h = new JLabel(text.toUpperCase());
        h.setFont(new Font("Segoe UI",Font.BOLD,10)); h.setForeground(UITheme.TEXT_GRAY);
        h.setAlignmentX(LEFT_ALIGNMENT);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(0,0,5,0)));
        h.setMaximumSize(new Dimension(Integer.MAX_VALUE,20));
        p.add(h); p.add(Box.createVerticalStrut(8));
    }

    private <T extends JComponent> T addField(JPanel p, String label, T field) {
        JLabel l = UITheme.createLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5));
        p.add(field); p.add(Box.createVerticalStrut(12));
        return field;
    }

    private JPasswordField addPwField(JPanel p, String label) {
        JPasswordField pf = UITheme.createPasswordField();
        return (JPasswordField) addField(p, label, pf);
    }

    private void doRegister() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String sid     = studentIdField.getText().trim();
        String college = (String) collegeBox.getSelectedItem();
        String program = (String) programBox.getSelectedItem();
        String yearLbl = (String) yearBox.getSelectedItem();
        String section = sectionField.getText().trim().toUpperCase();
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty()||email.isEmpty()||sid.isEmpty()||section.isEmpty()||pass.isEmpty()) {
            statusLabel.setText("All fields marked * are required."); return; }
        if (!email.contains("@")) { statusLabel.setText("Please enter a valid email address."); return; }
        if (college==null||college.startsWith("--")) { statusLabel.setText("Please select your college."); return; }
        if (program==null||program.startsWith("--")) { statusLabel.setText("Please select your program."); return; }
        if (yearLbl==null||yearLbl.startsWith("--")) { statusLabel.setText("Please select your year level."); return; }
        if (pass.length()<6) { statusLabel.setText("Password must be at least 6 characters."); return; }
        if (!pass.equals(confirm)) { statusLabel.setText("Passwords do not match."); return; }
        if (userDAO.emailExists(email)) { statusLabel.setText("This email is already registered."); return; }

        User u = new User(0,name,email,pass,"student",sid,college,program,
            DropdownData.yearToInt(yearLbl),section);
        if (userDAO.register(u)) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nPlease sign in with your credentials.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose(); new LoginFrame().setVisible(true);
        } else {
            statusLabel.setText("Registration failed. Please try again.");
        }
    }
}