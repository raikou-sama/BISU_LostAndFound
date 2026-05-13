package lostandfound.ui;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import lostandfound.dao.UserDAO;
import lostandfound.model.User;
import lostandfound.util.UITheme;

public class LoginFrame extends JFrame {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private final UserDAO  userDAO = new UserDAO();

    // Kit images
    private ImageIcon logoIcon  = null;
    private Image     heroBgImg = null;

    public LoginFrame() {
        setTitle("BISU Lost & Found — Sign In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        loadKitImages();
        initUI();
    }

    private void loadKitImages() {
        try {
            URL logoUrl = getClass().getResource("/lostandfound/kit/logobisu.png");
            if (logoUrl != null) {
                Image scaled = new ImageIcon(logoUrl).getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaled);
            }
        } catch (Exception e) { System.err.println("[LoginFrame] logo: " + e.getMessage()); }
        try {
            URL bgUrl = getClass().getResource("/lostandfound/kit/campusbg.jpg");
            if (bgUrl != null) heroBgImg = new ImageIcon(bgUrl).getImage();
        } catch (Exception e) { System.err.println("[LoginFrame] bg: " + e.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());

        // ── LEFT — background image + dark overlay ────────────────────────────
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (heroBgImg != null) {
                    g2.drawImage(heroBgImg, 0, 0, getWidth(), getHeight(), this);
                    g2.setColor(new Color(14, 35, 70, 205));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setPaint(new GradientPaint(0,0,UITheme.PRIMARY_DARK,getWidth(),getHeight(),UITheme.PRIMARY));
                    g2.fillRect(0,0,getWidth(),getHeight());
                }
            }
        };
        left.setPreferredSize(new Dimension(360, 560));
        left.setLayout(new GridBagLayout());

        JPanel brand = new JPanel(); brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        // Logo image or fallback
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoRow.setOpaque(false);
        if (logoIcon != null) {
            JLabel logoLbl = new JLabel(logoIcon);
            logoRow.add(logoLbl);
        } else {
            JPanel circle = new JPanel(new GridBagLayout());
            circle.setBackground(new Color(255,255,255,50));
            circle.setPreferredSize(new Dimension(52,52));
            JLabel bl = new JLabel("B"); bl.setFont(new Font("Segoe UI",Font.BOLD,26)); bl.setForeground(Color.WHITE);
            circle.add(bl); logoRow.add(circle);
        }

        JPanel logoText = new JPanel(); logoText.setOpaque(false);
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        logoText.setBorder(BorderFactory.createEmptyBorder(0,12,0,0));
        JLabel uniLbl  = new JLabel("Bohol Island State University");
        uniLbl.setFont(new Font("Segoe UI",Font.BOLD,14)); uniLbl.setForeground(Color.WHITE);
        JLabel campLbl = new JLabel("BISU Candijay Campus");
        campLbl.setFont(new Font("Segoe UI",Font.PLAIN,12)); campLbl.setForeground(new Color(200,215,255));
        logoText.add(uniLbl); logoText.add(campLbl);
        logoRow.add(logoText);

        JLabel sysLbl = new JLabel("Lost & Found System");
        sysLbl.setFont(new Font("Segoe UI",Font.BOLD,22)); sysLbl.setForeground(UITheme.ACCENT);
        sysLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tagLbl = new JLabel("<html><i>Reuniting students<br>with their belongings</i></html>");
        tagLbl.setFont(new Font("Segoe UI",Font.ITALIC,13)); tagLbl.setForeground(new Color(180,200,240));
        tagLbl.setAlignmentX(LEFT_ALIGNMENT);

        JPanel featPanel = new JPanel(); featPanel.setOpaque(false);
        featPanel.setLayout(new BoxLayout(featPanel, BoxLayout.Y_AXIS));
        featPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,new Color(255,255,255,40)),
            BorderFactory.createEmptyBorder(14,0,0,0)));
        for (String f : new String[]{"📌  Report lost & found items","🔍  Browse the item listings","📩  Submit a claim with proof","✅  Admin verifies and approves"}) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI Emoji",Font.PLAIN,12)); fl.setForeground(new Color(200,220,255));
            fl.setAlignmentX(LEFT_ALIGNMENT);
            featPanel.add(fl); featPanel.add(Box.createVerticalStrut(8));
        }

        brand.add(logoRow);  brand.add(Box.createVerticalStrut(16));
        brand.add(sysLbl);   brand.add(Box.createVerticalStrut(8));
        brand.add(tagLbl);   brand.add(Box.createVerticalStrut(22));
        brand.add(featPanel);
        left.add(brand);

        // ── RIGHT FORM ────────────────────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(32,48,32,48));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(UITheme.FONT_SMALL); backBtn.setForeground(UITheme.PRIMARY);
        backBtn.setBorderPainted(false); backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Sign In");
        formTitle.setFont(new Font("Segoe UI",Font.BOLD,26)); formTitle.setForeground(UITheme.TEXT_DARK);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel formSub = new JLabel("Enter your credentials to continue");
        formSub.setFont(UITheme.FONT_BODY); formSub.setForeground(UITheme.TEXT_GRAY);
        formSub.setAlignmentX(LEFT_ALIGNMENT);

        emailField = UITheme.createTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));

        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL); statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Demo hint
        JPanel demoHint = new JPanel(); demoHint.setLayout(new BoxLayout(demoHint,BoxLayout.Y_AXIS));
        demoHint.setBackground(new Color(245,248,255));
        demoHint.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        demoHint.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        demoHint.setAlignmentX(LEFT_ALIGNMENT);
        JLabel demoTitle = new JLabel("Demo Accounts:");
        demoTitle.setFont(new Font("Segoe UI",Font.BOLD,11)); demoTitle.setForeground(UITheme.TEXT_DARK);
        JLabel d1 = new JLabel("Admin: admin@bisu.edu.ph  /  admin123");
        d1.setFont(UITheme.FONT_SMALL); d1.setForeground(UITheme.TEXT_GRAY);
        JLabel d2 = new JLabel("Student: juan@bisu.edu.ph  /  pass123");
        d2.setFont(UITheme.FONT_SMALL); d2.setForeground(UITheme.TEXT_GRAY);
        demoHint.add(demoTitle); demoHint.add(d1); demoHint.add(d2);

        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        signupRow.setOpaque(false);
        JLabel noAcct = new JLabel("Don't have an account?  ");
        noAcct.setFont(UITheme.FONT_BODY); noAcct.setForeground(UITheme.TEXT_GRAY);
        JButton signupLink = new JButton("Sign Up");
        signupLink.setFont(new Font("Segoe UI",Font.BOLD,13)); signupLink.setForeground(UITheme.PRIMARY);
        signupLink.setBorderPainted(false); signupLink.setContentAreaFilled(false);
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupRow.add(noAcct); signupRow.add(signupLink);

        form.add(backBtn);   form.add(Box.createVerticalStrut(8));
        form.add(formTitle); form.add(Box.createVerticalStrut(4));
        form.add(formSub);   form.add(Box.createVerticalStrut(24));
        form.add(UITheme.createLabel("Email Address")); form.add(Box.createVerticalStrut(6));
        form.add(emailField); form.add(Box.createVerticalStrut(14));
        form.add(UITheme.createLabel("Password"));      form.add(Box.createVerticalStrut(6));
        form.add(passwordField); form.add(Box.createVerticalStrut(6));
        form.add(statusLabel);   form.add(Box.createVerticalStrut(14));
        form.add(loginBtn);      form.add(Box.createVerticalStrut(16));
        form.add(demoHint);      form.add(Box.createVerticalStrut(16));
        form.add(signupRow);

        right.add(form);
        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        add(root);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        signupLink.addActionListener(e -> { dispose(); new SignupFrame().setVisible(true); });
        backBtn.addActionListener(e    -> { dispose(); new WelcomePage().setVisible(true); });
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword()).trim();
        if (email.isEmpty() || pass.isEmpty()) { statusLabel.setText("Please enter email and password."); return; }
        User user = userDAO.login(email, pass);
        if (user != null) {
            dispose();
            if ("admin".equals(user.getRole())) new AdminDashboard(user).setVisible(true);
            else                                new StudentDashboard(user).setVisible(true);
        } else {
            statusLabel.setText("Invalid email or password.");
            passwordField.setText("");
        }
    }
}
