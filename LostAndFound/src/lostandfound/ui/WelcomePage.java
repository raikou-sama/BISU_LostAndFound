package lostandfound.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import lostandfound.dao.ItemDAO;
import lostandfound.dao.UserDAO;
import lostandfound.model.Item;
import lostandfound.util.UITheme;

public class WelcomePage extends JFrame {

    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();

    // Kit images loaded once
    private ImageIcon logoIcon   = null;
    private Image     heroBgImg  = null;

    public WelcomePage() {
        setTitle("BISU Candijay Campus — Lost & Found System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        loadKitImages();
        initUI();
    }

    // ── Load images from kit folder ────────────────────────────────────────────
    private void loadKitImages() {
        try {
            URL logoUrl = getClass().getResource("/lostandfound/kit/logobisu.png");
            if (logoUrl != null) {
                ImageIcon raw = new ImageIcon(logoUrl);
                Image scaled = raw.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("[WelcomePage] logo load: " + e.getMessage());
        }
        try {
            URL bgUrl = getClass().getResource("/lostandfound/kit/campusbg.jpg");
            if (bgUrl != null) {
                heroBgImg = new ImageIcon(bgUrl).getImage();
            }
        } catch (Exception e) {
            System.err.println("[WelcomePage] background load: " + e.getMessage());
        }
    }

    // ── Helper: make a small logo label (for sidebar / header) ────────────────
    private JLabel makeLogoLabel(int size) {
        if (logoIcon != null) {
            Image scaled = logoIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }
        // Fallback: painted circle with "B"
        JPanel circle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255,255,255,50)); g.fillOval(0,0,getWidth()-1,getHeight()-1);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.BOLD, size/2));
                FontMetrics fm = g.getFontMetrics();
                g.drawString("B", (getWidth()-fm.stringWidth("B"))/2, fm.getAscent()+size/8);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(size, size));
        // wrap in label via icon trick
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        return new JLabel(new ImageIcon(bi)); // plain placeholder if no logo
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());

        // ── HEADER ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandRow.setOpaque(false);

        // Logo image or fallback circle
        JLabel logoImg;
        if (logoIcon != null) {
            Image scaled = logoIcon.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
            logoImg = new JLabel(new ImageIcon(scaled));
        } else {
            JPanel circle = new JPanel(new GridBagLayout());
            circle.setBackground(new Color(255,255,255,40));
            circle.setPreferredSize(new Dimension(44,44));
            circle.setBorder(new LineBorder(new Color(255,255,255,60),1,true));
            JLabel bl = new JLabel("B");
            bl.setFont(new Font("Segoe UI",Font.BOLD,22)); bl.setForeground(Color.WHITE);
            circle.add(bl);
            logoImg = new JLabel();
            brandRow.add(circle);
        }
        if (logoIcon != null) brandRow.add(logoImg);

        JPanel brandText = new JPanel(new GridLayout(2,1,0,1));
        brandText.setOpaque(false);
        JLabel uniName = new JLabel("Bohol Island State University");
        uniName.setFont(new Font("Segoe UI",Font.BOLD,15)); uniName.setForeground(Color.WHITE);
        JLabel campusName = new JLabel("BISU Candijay Campus  ·  Lost & Found System");
        campusName.setFont(new Font("Segoe UI",Font.PLAIN,12)); campusName.setForeground(new Color(200,215,255));
        brandText.add(uniName); brandText.add(campusName);
        brandRow.add(brandText);

        JPanel navBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        navBtns.setOpaque(false);
        JButton signinBtn = new JButton("Sign In");
        signinBtn.setFont(UITheme.FONT_BUTTON); signinBtn.setForeground(Color.WHITE);
        signinBtn.setBackground(new Color(255,255,255,0)); signinBtn.setBorderPainted(true);
        signinBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255,255,255,120),1,true),
            BorderFactory.createEmptyBorder(6,16,6,16)));
        signinBtn.setFocusPainted(false); signinBtn.setContentAreaFilled(false);
        signinBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton signupBtn = UITheme.createAccentButton("Sign Up");
        signupBtn.setBorder(BorderFactory.createEmptyBorder(7,16,7,16));
        navBtns.add(signinBtn); navBtns.add(signupBtn);

        header.add(brandRow, BorderLayout.WEST);
        header.add(navBtns,  BorderLayout.EAST);

        // ── SCROLLABLE BODY ───────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UITheme.BG_LIGHT);

        // ── HERO — uses campusbg.jpg as background ────────────────────────────
        JPanel hero = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (heroBgImg != null) {
                    // Draw campus background image, then dark overlay for readability
                    g2.drawImage(heroBgImg, 0, 0, getWidth(), getHeight(), this);
                    g2.setColor(new Color(14, 35, 70, 195)); // navy overlay ~76% opacity
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Fallback gradient
                    GradientPaint gp = new GradientPaint(0,0,UITheme.PRIMARY_DARK,getWidth(),getHeight(),UITheme.PRIMARY_LIGHT);
                    g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                }
            }
        };
        hero.setLayout(new GridBagLayout());
        hero.setPreferredSize(new Dimension(1000, 240));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        JPanel heroContent = new JPanel(); heroContent.setOpaque(false);
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("BISU Candijay Campus — Official Lost & Found Portal");
        badge.setFont(new Font("Segoe UI",Font.PLAIN,12));
        badge.setForeground(new Color(200,220,255)); badge.setAlignmentX(CENTER_ALIGNMENT);
        badge.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255,255,255,60),1,true),
            BorderFactory.createEmptyBorder(3,14,3,14)));

        JLabel heroTitle = new JLabel("Reuniting Students with Their Belongings");
        heroTitle.setFont(new Font("Segoe UI",Font.BOLD,28));
        heroTitle.setForeground(Color.WHITE); heroTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel heroSub = new JLabel("<html><center>A campus-wide system to report, track, and recover lost and found items.<br>Submit claims with proof and let our admin team handle verification.</center></html>");
        heroSub.setFont(new Font("Segoe UI",Font.PLAIN,13));
        heroSub.setForeground(new Color(200,220,255)); heroSub.setAlignmentX(CENTER_ALIGNMENT);
        heroSub.setHorizontalAlignment(JLabel.CENTER);

        JPanel heroBtns = new JPanel(new FlowLayout(FlowLayout.CENTER,12,0));
        heroBtns.setOpaque(false);
        JButton hSignIn = new JButton("Sign In to Your Account");
        hSignIn.setBackground(Color.WHITE); hSignIn.setForeground(UITheme.PRIMARY_DARK);
        hSignIn.setFont(UITheme.FONT_BUTTON); hSignIn.setFocusPainted(false); hSignIn.setBorderPainted(false);
        hSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hSignIn.setBorder(BorderFactory.createEmptyBorder(10,22,10,22));
        JButton hSignUp = UITheme.createAccentButton("Create a New Account");
        hSignUp.setBorder(BorderFactory.createEmptyBorder(10,22,10,22));
        heroBtns.add(hSignIn); heroBtns.add(hSignUp);

        heroContent.add(badge);     heroContent.add(Box.createVerticalStrut(14));
        heroContent.add(heroTitle); heroContent.add(Box.createVerticalStrut(10));
        heroContent.add(heroSub);   heroContent.add(Box.createVerticalStrut(20));
        heroContent.add(heroBtns);
        hero.add(heroContent);

        // ── HOW IT WORKS ──────────────────────────────────────────────────────
        JPanel stepsSection = new JPanel();
        stepsSection.setLayout(new BoxLayout(stepsSection,BoxLayout.Y_AXIS));
        stepsSection.setBackground(Color.WHITE);
        stepsSection.setBorder(BorderFactory.createEmptyBorder(28,32,28,32));
        stepsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel stepsTitle = new JLabel("How It Works");
        stepsTitle.setFont(UITheme.FONT_TITLE); stepsTitle.setForeground(UITheme.TEXT_DARK);
        stepsTitle.setAlignmentX(CENTER_ALIGNMENT);
        JLabel stepsSub = new JLabel("Four simple steps to recover your belongings");
        stepsSub.setFont(UITheme.FONT_BODY); stepsSub.setForeground(UITheme.TEXT_GRAY);
        stepsSub.setAlignmentX(CENTER_ALIGNMENT);

        JPanel stepsGrid = new JPanel(new GridLayout(1,4,16,0));
        stepsGrid.setOpaque(false);
        stepsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE,140));

        Object[][] stepsData = {
            {"1","Report an Item","Submit a lost or found item with description and location.", new Color(230,240,255), UITheme.PRIMARY},
            {"2","Browse Listings","Search reported items to see if yours has been found.", new Color(220,248,235), UITheme.FOUND_COLOR},
            {"3","Submit a Claim","Describe your proof of ownership and send a claim request.", new Color(255,249,220), UITheme.PENDING_COLOR},
            {"4","Admin Approves","Admin reviews and approves pickup at the Student Affairs Office.", new Color(255,235,235), UITheme.DANGER}
        };
        for (Object[] s : stepsData) {
            JPanel card = new JPanel(new BorderLayout(0,8));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR,1), BorderFactory.createEmptyBorder(16,16,16,16)));
            JPanel numCircle = new JPanel(new GridBagLayout());
            numCircle.setBackground((Color)s[3]);
            numCircle.setPreferredSize(new Dimension(36,36));
            JLabel numLbl = new JLabel((String)s[0]);
            numLbl.setFont(new Font("Segoe UI",Font.BOLD,16)); numLbl.setForeground((Color)s[4]);
            numCircle.add(numLbl);
            JPanel numWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
            numWrapper.setOpaque(false); numWrapper.add(numCircle);
            JLabel cardTitle = new JLabel((String)s[1], JLabel.CENTER);
            cardTitle.setFont(new Font("Segoe UI",Font.BOLD,13)); cardTitle.setForeground(UITheme.TEXT_DARK);
            JLabel cardDesc = new JLabel("<html><center>"+(String)s[2]+"</center></html>", JLabel.CENTER);
            cardDesc.setFont(UITheme.FONT_SMALL); cardDesc.setForeground(UITheme.TEXT_GRAY);
            JPanel textPanel = new JPanel(new GridLayout(2,1,0,4));
            textPanel.setOpaque(false); textPanel.add(cardTitle); textPanel.add(cardDesc);
            card.add(numWrapper,BorderLayout.NORTH); card.add(textPanel,BorderLayout.CENTER);
            stepsGrid.add(card);
        }
        stepsSection.add(stepsTitle); stepsSection.add(Box.createVerticalStrut(4));
        stepsSection.add(stepsSub);   stepsSection.add(Box.createVerticalStrut(20));
        stepsSection.add(stepsGrid);

        // ── QUICK STATS ───────────────────────────────────────────────────────
        JPanel statsSection = new JPanel(new BorderLayout());
        statsSection.setBackground(UITheme.BG_LIGHT);
        statsSection.setBorder(BorderFactory.createEmptyBorder(24,32,24,32));
        statsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE,130));

        JPanel statsGrid = new JPanel(new GridLayout(1,4,16,0));
        statsGrid.setOpaque(false);
        int lost    = itemDAO.countByType("lost");
        int found   = itemDAO.countByType("found");
        int claimed = itemDAO.countByStatus("claimed") + itemDAO.countByStatus("completed");
        int studs   = userDAO.getTotalStudents();

        Object[][] statsData = {
            {lost,   "Lost Items Reported",       UITheme.LOST_COLOR},
            {found,  "Found Items Reported",       UITheme.FOUND_COLOR},
            {claimed,"Items Successfully Claimed", UITheme.CLAIMED_COLOR},
            {studs,  "Registered Students",        UITheme.PRIMARY}
        };
        for (Object[] s : statsData) {
            JPanel card = new JPanel(new BorderLayout(0,4));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR,1), BorderFactory.createEmptyBorder(14,16,14,16)));
            JLabel val = new JLabel(String.valueOf(s[0]), JLabel.CENTER);
            val.setFont(new Font("Segoe UI",Font.BOLD,30)); val.setForeground((Color)s[2]);
            JLabel lbl = new JLabel((String)s[1], JLabel.CENTER);
            lbl.setFont(UITheme.FONT_SMALL); lbl.setForeground(UITheme.TEXT_GRAY);
            card.add(val,BorderLayout.CENTER); card.add(lbl,BorderLayout.SOUTH);
            statsGrid.add(card);
        }
        statsSection.add(statsGrid, BorderLayout.CENTER);

        // ── RECENT ITEMS ──────────────────────────────────────────────────────
        JPanel recentSection = new JPanel();
        recentSection.setLayout(new BoxLayout(recentSection,BoxLayout.Y_AXIS));
        recentSection.setBackground(Color.WHITE);
        recentSection.setBorder(BorderFactory.createEmptyBorder(24,32,24,32));
        recentSection.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));

        JLabel recentTitle = new JLabel("Recent Item Reports");
        recentTitle.setFont(UITheme.FONT_SECTION); recentTitle.setForeground(UITheme.TEXT_DARK);
        recentTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel recentSub = new JLabel("Sign in to see all items and submit a claim.");
        recentSub.setFont(UITheme.FONT_SMALL); recentSub.setForeground(UITheme.TEXT_GRAY);
        recentSub.setAlignmentX(LEFT_ALIGNMENT);

        List<Item> recent = itemDAO.getAll();
        List<Item> preview = recent.subList(0, Math.min(3, recent.size()));

        JPanel cardsRow = new JPanel(new GridLayout(1,3,16,0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,120));

        for (Item it : preview) {
            JPanel iCard = new JPanel(new BorderLayout(0,6));
            iCard.setBackground(UITheme.BG_LIGHT);
            iCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR,1), BorderFactory.createEmptyBorder(12,14,12,14)));
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);
            JLabel iName = new JLabel(it.getItemName());
            iName.setFont(new Font("Segoe UI",Font.BOLD,13)); iName.setForeground(UITheme.TEXT_DARK);
            Color badgeColor = "lost".equals(it.getType()) ? UITheme.LOST_COLOR : UITheme.FOUND_COLOR;
            JLabel typeBadge = new JLabel(it.getType().toUpperCase());
            typeBadge.setFont(UITheme.FONT_SMALL); typeBadge.setForeground(badgeColor);
            typeBadge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(badgeColor,1,true), BorderFactory.createEmptyBorder(1,7,1,7)));
            topRow.add(iName,BorderLayout.WEST); topRow.add(typeBadge,BorderLayout.EAST);
            JLabel iDesc = new JLabel(it.getDescription()!=null&&it.getDescription().length()>50
                ? it.getDescription().substring(0,50)+"..." : it.getDescription());
            iDesc.setFont(UITheme.FONT_SMALL); iDesc.setForeground(UITheme.TEXT_GRAY);
            JLabel iLoc = new JLabel("📍 "+it.getLocation()+"  ·  "+
                (it.getDateReported()!=null?it.getDateReported().toString().substring(0,10):""));
            iLoc.setFont(UITheme.FONT_SMALL); iLoc.setForeground(UITheme.TEXT_GRAY);
            iCard.add(topRow,BorderLayout.NORTH);
            iCard.add(iDesc, BorderLayout.CENTER);
            iCard.add(iLoc,  BorderLayout.SOUTH);
            cardsRow.add(iCard);
        }
        while (cardsRow.getComponentCount() < 3) {
            JPanel empty = new JPanel(); empty.setOpaque(false); cardsRow.add(empty);
        }
        recentSection.add(recentTitle); recentSection.add(Box.createVerticalStrut(4));
        recentSection.add(recentSub);   recentSection.add(Box.createVerticalStrut(16));
        recentSection.add(cardsRow);

        // ── FOOTER ────────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.PRIMARY_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(18,32,12,32));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE,120));

        JPanel footerGrid = new JPanel(new GridLayout(1,3,24,0));
        footerGrid.setOpaque(false);

        String[][] footerCols = {
            {"Bohol Island State University","BISU Candijay Campus","Candijay, Bohol, Philippines","Academic Year 2025–2026"},
            {"Colleges","CBM — Business & Management","CFMS — Fisheries & Marine Sciences","CoS — Sciences  |  CTE — Teacher Education"},
            {"Lost & Found Office","Student Affairs Office","Admin Building, Ground Floor","Mon–Fri  8:00am–5:00pm  |  studentaffairs@bisu.edu.ph"}
        };
        for (String[] col : footerCols) {
            JPanel fp = new JPanel(); fp.setOpaque(false);
            fp.setLayout(new BoxLayout(fp,BoxLayout.Y_AXIS));
            JLabel fTitle = new JLabel(col[0]);
            fTitle.setFont(new Font("Segoe UI",Font.BOLD,11)); fTitle.setForeground(Color.WHITE);
            fTitle.setAlignmentX(LEFT_ALIGNMENT); fp.add(fTitle); fp.add(Box.createVerticalStrut(5));
            for (int i=1;i<col.length;i++) {
                JLabel fl = new JLabel(col[i]);
                fl.setFont(UITheme.FONT_SMALL); fl.setForeground(new Color(190,205,240));
                fl.setAlignmentX(LEFT_ALIGNMENT); fp.add(fl); fp.add(Box.createVerticalStrut(2));
            }
            footerGrid.add(fp);
        }
        JLabel footerBottom = new JLabel("© 2025–2026 Bohol Island State University Candijay Campus — Lost & Found System", JLabel.CENTER);
        footerBottom.setFont(UITheme.FONT_SMALL); footerBottom.setForeground(new Color(160,175,215));
        footer.add(footerGrid,   BorderLayout.CENTER);
        footer.add(footerBottom, BorderLayout.SOUTH);

        body.add(hero);
        body.add(stepsSection);
        body.add(statsSection);
        body.add(recentSection);
        body.add(footer);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(header,     BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        add(root);

        signinBtn.addActionListener(e -> openLogin());
        hSignIn.addActionListener(e  -> openLogin());
        signupBtn.addActionListener(e -> openSignup());
        hSignUp.addActionListener(e  -> openSignup());
    }

    private void openLogin()  { dispose(); new LoginFrame().setVisible(true); }
    private void openSignup() { dispose(); new SignupFrame().setVisible(true); }
}
