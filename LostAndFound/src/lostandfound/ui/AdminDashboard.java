package lostandfound.ui;

import lostandfound.dao.ClaimDAO;
import lostandfound.dao.ItemDAO;
import lostandfound.dao.UserDAO;
import lostandfound.model.Claim;
import lostandfound.model.Item;
import lostandfound.model.User;
import lostandfound.util.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final User admin;
    private final ItemDAO  itemDAO  = new ItemDAO();
    private final UserDAO  userDAO  = new UserDAO();
    private final ClaimDAO claimDAO = new ClaimDAO();

    private JPanel contentArea;
    private JLabel pageTitle;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Lost & Found — Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 700);
        setLocationRelativeTo(null);
        initUI();
    }

    // ─────────────────────────────────────────────────────────────
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());

        // SIDEBAR
        JPanel sidebar = buildSidebar();

        // MAIN AREA
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_LIGHT);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16,24,16,24)));
        pageTitle = new JLabel("Dashboard Overview");
        pageTitle.setFont(new Font("Segoe UI",Font.BOLD,20));
        pageTitle.setForeground(UITheme.TEXT_DARK);
        JLabel adminBadge = new JLabel("Administrator  |  " + admin.getFullName());
        adminBadge.setFont(UITheme.FONT_SMALL); adminBadge.setForeground(UITheme.TEXT_GRAY);
        topBar.add(pageTitle,  BorderLayout.WEST);
        topBar.add(adminBadge, BorderLayout.EAST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_LIGHT);
        contentArea.setBorder(BorderFactory.createEmptyBorder(22,22,22,22));

        main.add(topBar,     BorderLayout.NORTH);
        main.add(contentArea,BorderLayout.CENTER);

        root.add(sidebar, BorderLayout.WEST);
        root.add(main,    BorderLayout.CENTER);
        add(root);

        showDashboard();
    }

    // ─────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(UITheme.SIDEBAR_ADMIN);
        sb.setPreferredSize(new Dimension(230,700));
        sb.setLayout(new BoxLayout(sb,BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT,18,18));
        logo.setOpaque(false);
        JLabel logoLbl = new JLabel("🔍  L&F Admin");
        logoLbl.setFont(new Font("Segoe UI",Font.BOLD,16)); logoLbl.setForeground(Color.WHITE);
        logo.add(logoLbl);
        sb.add(logo);

        JPanel uInfo = new JPanel(new FlowLayout(FlowLayout.LEFT,18,6));
        uInfo.setOpaque(false);
        JLabel uLbl = new JLabel("<html><span style='color:#FFD700;font-size:12px;font-weight:bold'>Admin Account</span>" +
            "<br><span style='color:#aaa;font-size:10px'>" + admin.getFullName() + "</span></html>");
        uInfo.add(uLbl); sb.add(uInfo);
        sb.add(hline());

        int pending = claimDAO.countPending();
        String[][] nav = {
            {"📊","Dashboard","Dashboard"},
            {"📋","All Items","All Items"},
            {"🔴","Lost Items","Lost Items"},
            {"🟢","Found Items","Found Items"},
            {"⏳","Pending Claims" + (pending>0?" ("+pending+")":""),"Pending Claims"},
            {"✅","Claim History","Claim History"},
            {"👥","Students","Students"},
            {"➕","Add Item","Add Item"}
        };
        for (String[] n : nav) {
            JButton btn = navBtn(n[0]+"  "+n[1]);
            String key = n[2];
            btn.addActionListener(e -> navigate(key));
            sb.add(btn);
        }
        sb.add(Box.createVerticalGlue());

        JButton logout = navBtn("🚪  Logout");
        logout.setForeground(new Color(255,120,100));
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        sb.add(logout);
        sb.add(Box.createVerticalStrut(20));
        return sb;
    }

    private JSeparator hline() {
        JSeparator s = new JSeparator();
        s.setMaximumSize(new Dimension(230,1));
        s.setForeground(new Color(60,80,120));
        return s;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.FONT_NAV); b.setForeground(new Color(200,210,240));
        b.setBackground(UITheme.SIDEBAR_ADMIN); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(230,42));
        b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(UITheme.PRIMARY); }
            public void mouseExited(java.awt.event.MouseEvent e){ b.setBackground(UITheme.SIDEBAR_ADMIN); }
        });
        return b;
    }

    // ─────────────────────────────────────────────────────────────
    private void navigate(String key) {
        pageTitle.setText(key.equals("Dashboard") ? "Dashboard Overview" : key);
        contentArea.removeAll();
        switch (key) {
            case "Dashboard"     -> showDashboard();
            case "All Items"     -> showItemsPanel(itemDAO.getAll(), true);
            case "Lost Items"    -> showItemsPanel(itemDAO.getByType("lost"), true);
            case "Found Items"   -> showItemsPanel(itemDAO.getByType("found"), true);
            case "Pending Claims"-> showPendingClaims();
            case "Claim History" -> showClaimHistory();
            case "Students"      -> showStudents();
            case "Add Item"      -> showAddItemForm();
        }
        contentArea.revalidate(); contentArea.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    private void showDashboard() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setOpaque(false);

        // Stats row
        JPanel stats = new JPanel(new GridLayout(1,5,14,0));
        stats.setOpaque(false); stats.setMaximumSize(new Dimension(Integer.MAX_VALUE,110));
        stats.add(statCard("Lost Reported",  itemDAO.countByType("lost"),       UITheme.LOST_COLOR,    "🔴"));
        stats.add(statCard("Found Reported", itemDAO.countByType("found"),      UITheme.FOUND_COLOR,   "🟢"));
        stats.add(statCard("Claimed",        itemDAO.countByStatus("claimed"),  UITheme.CLAIMED_COLOR, "✅"));
        stats.add(statCard("Pending Claims", claimDAO.countPending(),           UITheme.PENDING_COLOR, "⏳"));
        stats.add(statCard("Students",       userDAO.getTotalStudents(),         UITheme.PRIMARY,       "👥"));
        p.add(stats); p.add(Box.createVerticalStrut(24));

        JLabel lbl = new JLabel("Recent Item Reports");
        lbl.setFont(UITheme.FONT_TITLE); lbl.setForeground(UITheme.TEXT_DARK);
        p.add(lbl); p.add(Box.createVerticalStrut(10));

        List<Item> items = itemDAO.getAll();
        JScrollPane sc = new JScrollPane(buildItemTable(items.subList(0,Math.min(8,items.size())), false));
        sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        p.add(sc);
        contentArea.add(p, BorderLayout.CENTER);
    }

    private JPanel statCard(String label, int val, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16,18,16,18)));
        JPanel bar = new JPanel(); bar.setBackground(color); bar.setPreferredSize(new Dimension(4,60));
        JLabel iconLbl = new JLabel(icon+"  "+label);
        iconLbl.setFont(new Font("Segoe UI Emoji",Font.PLAIN,12)); iconLbl.setForeground(UITheme.TEXT_GRAY);
        JLabel valLbl  = new JLabel(String.valueOf(val));
        valLbl.setFont(new Font("Segoe UI",Font.BOLD,34)); valLbl.setForeground(color);
        JPanel inner = new JPanel(new BorderLayout()); inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0,12,0,0));
        inner.add(iconLbl,BorderLayout.NORTH); inner.add(valLbl,BorderLayout.CENTER);
        card.add(bar,BorderLayout.WEST); card.add(inner,BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────────
    private void showItemsPanel(List<Item> items, boolean withActions) {
        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); searchBar.setOpaque(false);
        JTextField kw = UITheme.createTextField(); kw.setPreferredSize(new Dimension(260,36));
        JButton srchBtn = UITheme.createPrimaryButton("🔍 Search");
        searchBar.add(kw); searchBar.add(Box.createHorizontalStrut(8)); searchBar.add(srchBtn);

        JTable table = buildItemTable(items, withActions);
        JScrollPane sc = new JScrollPane(table); sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // Action row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); actions.setOpaque(false);
        JButton delBtn = UITheme.createDangerButton("🗑 Delete Item");
        actions.add(delBtn);

        srchBtn.addActionListener(e -> {
            String txt = kw.getText().trim();
            List<Item> res = txt.isEmpty() ? itemDAO.getAll() : itemDAO.search(txt);
            refreshItemTable((DefaultTableModel)table.getModel(), res);
        });

        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row<0){info("Select an item first.");return;}
            int id = (int)table.getValueAt(row,0);
            if(JOptionPane.showConfirmDialog(this,"Delete this item and all its claims?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION
               && itemDAO.deleteItem(id)) { info("Item deleted."); navigate("All Items"); }
        });

        p.add(searchBar,BorderLayout.NORTH);
        p.add(sc,BorderLayout.CENTER);
        p.add(actions,BorderLayout.SOUTH);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    /** PENDING CLAIMS — core admin approval screen */
    private void showPendingClaims() {
        List<Claim> claims = claimDAO.getPendingClaims();

        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        // Info banner
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(255,245,220));
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,180,60)),
            BorderFactory.createEmptyBorder(10,16,10,16)));
        JLabel bannerLbl = new JLabel("⏳  " + claims.size() + " pending claim(s) waiting for your review.");
        bannerLbl.setFont(new Font("Segoe UI",Font.BOLD,13)); bannerLbl.setForeground(new Color(120,80,0));
        banner.add(bannerLbl);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        p.add(banner,BorderLayout.NORTH);

        // Table
        String[] cols = {"Claim ID","Item","Claimant","Student ID","Dept","Course","Yr-Sec","Date Filed","Proof (click row)"};
        DefaultTableModel model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        for (Claim cl : claims) {
            String date = cl.getDateClaimed()!=null ? cl.getDateClaimed().toString().substring(0,10) : "N/A";
            model.addRow(new Object[]{cl.getId(),cl.getItemName(),cl.getClaimerName(),cl.getClaimerStudentId(),
                abbrev(cl.getClaimerDept()),cl.getClaimerCourse(),cl.getClaimerYearSection(),date,
                cl.getProofDetails()!=null&&cl.getProofDetails().length()>30
                    ? cl.getProofDetails().substring(0,30)+"…" : cl.getProofDetails()});
        }
        JTable table = new JTable(model); UITheme.styleTable(table);
        table.setRowHeight(38);
        JScrollPane sc = new JScrollPane(table); sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); btns.setOpaque(false);
        JButton viewBtn    = UITheme.createPrimaryButton("👁 View Proof");
        JButton approveBtn = UITheme.createSuccessButton("✅ Approve");
        JButton rejectBtn  = UITheme.createDangerButton("❌ Reject");
        btns.add(viewBtn); btns.add(approveBtn); btns.add(rejectBtn);

        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0){info("Select a claim first.");return;}
            Claim cl = claims.get(row);
            JTextArea ta = new JTextArea(cl.getProofDetails()); ta.setLineWrap(true); ta.setWrapStyleWord(true);
            ta.setEditable(false); ta.setRows(6); ta.setColumns(40);
            JOptionPane.showMessageDialog(this,new JScrollPane(ta),
                "Proof from: "+cl.getClaimerName()+" — "+cl.getItemName(), JOptionPane.INFORMATION_MESSAGE);
        });

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0){info("Select a claim first.");return;}
            Claim cl = claims.get(row);
            String notes = JOptionPane.showInputDialog(this,
                "Enter note for student (e.g. pickup instructions):\n[Leave blank if none]",
                "Approve Claim", JOptionPane.QUESTION_MESSAGE);
            if(notes==null) return; // cancelled
            if(claimDAO.approveClaim(cl.getId(),cl.getItemId(),admin.getId(),notes)) {
                JOptionPane.showMessageDialog(this,
                    "Claim APPROVED!\nStudent: "+cl.getClaimerName()+"\nItem: "+cl.getItemName()+
                    "\n\nItem is now marked 'claimed'. Notify student to visit the office.",
                    "Approved",JOptionPane.INFORMATION_MESSAGE);
                navigate("Pending Claims");
            }
        });

        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0){info("Select a claim first.");return;}
            Claim cl = claims.get(row);
            String reason = JOptionPane.showInputDialog(this,
                "Enter reason for rejection (required):","Reject Claim",JOptionPane.WARNING_MESSAGE);
            if(reason==null||reason.trim().isEmpty()){info("Rejection reason is required.");return;}
            if(claimDAO.rejectClaim(cl.getId(),admin.getId(),reason)) {
                JOptionPane.showMessageDialog(this,"Claim REJECTED.\nStudent: "+cl.getClaimerName(),"Rejected",JOptionPane.WARNING_MESSAGE);
                navigate("Pending Claims");
            }
        });

        p.add(sc,BorderLayout.CENTER);
        p.add(btns,BorderLayout.SOUTH);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    private void showClaimHistory() {
        List<Claim> claims = claimDAO.getAllClaims();
        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        String[] cols = {"ID","Item","Claimant","Student ID","Status","Reviewed By","Notes","Date"};
        DefaultTableModel model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        for (Claim cl : claims) {
            String date = cl.getDateClaimed()!=null ? cl.getDateClaimed().toString().substring(0,10):"N/A";
            model.addRow(new Object[]{cl.getId(),cl.getItemName(),cl.getClaimerName(),cl.getClaimerStudentId(),
                cl.getStatus().toUpperCase(), cl.getReviewerName()!=null?cl.getReviewerName():"—",
                cl.getAdminNotes()!=null?cl.getAdminNotes():"—", date});
        }
        JTable table = new JTable(model); UITheme.styleTable(table);

        // Color rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                Component c = super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel) {
                    String st = (String)t.getValueAt(row,4);
                    c.setBackground(switch(st){
                        case "APPROVED"  -> new Color(240,255,245);
                        case "REJECTED"  -> new Color(255,243,243);
                        case "PENDING"   -> new Color(255,249,235);
                        case "COMPLETED" -> new Color(240,245,255);
                        default          -> Color.WHITE;
                    });
                }
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                return c;
            }
        });

        // Complete pickup button
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); btns.setOpaque(false);
        JButton completeBtn = UITheme.createAccentButton("📦 Mark Pickup Completed");
        btns.add(completeBtn);

        completeBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0){info("Select a claim first.");return;}
            String status = (String)table.getValueAt(row,4);
            if(!"APPROVED".equals(status)){info("Only APPROVED claims can be completed.");return;}
            int cid = (int)table.getValueAt(row,0);
            // find itemId from claims list
            Claim cl = claims.get(row);
            if(claimDAO.completeClaim(cid, cl.getItemId(), admin.getId())) {
                info("Claim marked as COMPLETED. Item archived."); navigate("Claim History");
            }
        });

        JScrollPane sc = new JScrollPane(table); sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        p.add(sc,BorderLayout.CENTER);
        p.add(btns,BorderLayout.SOUTH);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    private void showStudents() {
        List<User> students = userDAO.getAllStudents();
        String[] cols = {"ID","Full Name","Student ID","Department","Course","Year","Section","Email"};
        DefaultTableModel model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        for (User u : students)
            model.addRow(new Object[]{u.getId(),u.getFullName(),u.getStudentId(),
                abbrev(u.getDepartment()),u.getCourse(),
                u.getYear()>0?u.getYear()+"":"-", u.getSection()!=null?u.getSection():"-", u.getEmail()});

        JTable table = new JTable(model); UITheme.styleTable(table);
        JScrollPane sc = new JScrollPane(table); sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        JLabel cnt = new JLabel("Total Students: "+students.size());
        cnt.setFont(UITheme.FONT_LABEL); cnt.setForeground(UITheme.TEXT_GRAY);
        JPanel p = new JPanel(new BorderLayout(0,10)); p.setOpaque(false);
        p.add(cnt,BorderLayout.NORTH); p.add(sc,BorderLayout.CENTER);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    private void showAddItemForm() {
        JPanel wrap = new JPanel(new GridBagLayout()); wrap.setOpaque(false);
        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(28,36,28,36)));

        JLabel ftitle = new JLabel("Report a Lost / Found Item");
        ftitle.setFont(new Font("Segoe UI",Font.BOLD,18)); ftitle.setForeground(UITheme.TEXT_DARK);
        ftitle.setAlignmentX(LEFT_ALIGNMENT);
        form.add(ftitle); form.add(Box.createVerticalStrut(18));

        JTextField nameF = af(form,"Item Name *",UITheme.createTextField());
        JTextField locF  = af(form,"Location *",UITheme.createTextField());
        JTextField contF = af(form,"Contact Info",UITheme.createTextField());
        JComboBox<String> typeBox = afcb(form,"Report Type *",new String[]{"lost","found"});
        JComboBox<String> catBox  = afcb(form,"Category *",new String[]{"Electronics","Clothing","Books/Notes","ID/Cards","Keys","Bags","Jewelry","Sports Equipment","Documents","Personal Accessories","Other"});
        JTextArea desc = new JTextArea(3,20); desc.setFont(UITheme.FONT_BODY);
        desc.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR),BorderFactory.createEmptyBorder(7,11,7,11)));
        JLabel dLabel = UITheme.createLabel("Description"); dLabel.setAlignmentX(LEFT_ALIGNMENT);
        form.add(dLabel); form.add(Box.createVerticalStrut(5));
        JScrollPane dsc = new JScrollPane(desc); dsc.setMaximumSize(new Dimension(Integer.MAX_VALUE,85)); dsc.setAlignmentX(LEFT_ALIGNMENT);
        form.add(dsc); form.add(Box.createVerticalStrut(14));

        JLabel stLbl = new JLabel(" "); stLbl.setFont(UITheme.FONT_SMALL); stLbl.setAlignmentX(LEFT_ALIGNMENT);
        form.add(stLbl); form.add(Box.createVerticalStrut(10));
        JButton sub = UITheme.createAccentButton("📤 Submit Report");
        sub.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); sub.setAlignmentX(LEFT_ALIGNMENT);
        form.add(sub);

        sub.addActionListener(e -> {
            if(nameF.getText().trim().isEmpty()||locF.getText().trim().isEmpty()){
                stLbl.setForeground(UITheme.DANGER); stLbl.setText("Item Name and Location are required."); return;
            }
            Item item = new Item(0,nameF.getText().trim(),desc.getText(),(String)catBox.getSelectedItem(),
                locF.getText().trim(),"active",(String)typeBox.getSelectedItem(),contF.getText().trim(),
                admin.getId(),admin.getFullName(),null);
            if(itemDAO.addItem(item)){
                stLbl.setForeground(UITheme.SUCCESS); stLbl.setText("✅ Item reported successfully!");
                nameF.setText(""); locF.setText(""); contF.setText(""); desc.setText("");
            } else { stLbl.setForeground(UITheme.DANGER); stLbl.setText("Failed. Try again."); }
        });

        wrap.add(form);
        contentArea.add(wrap,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    private JTable buildItemTable(List<Item> items, boolean withActions) {
        String[] cols = {"ID","Item Name","Type","Category","Location","Status","Reporter","Date"};
        DefaultTableModel model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        refreshItemTable(model,items);
        JTable table = new JTable(model); UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                Component c = super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel){
                    String type=(String)t.getValueAt(row,2), status=(String)t.getValueAt(row,5);
                    if("claimed".equalsIgnoreCase(status)||"completed".equalsIgnoreCase(status))
                        c.setBackground(new Color(235,248,255));
                    else if("lost".equalsIgnoreCase(type)) c.setBackground(new Color(255,245,245));
                    else c.setBackground(new Color(245,255,248));
                }
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                return c;
            }
        });
        return table;
    }

    private void refreshItemTable(DefaultTableModel model, List<Item> items) {
        model.setRowCount(0);
        for (Item it : items) {
            String date = it.getDateReported()!=null?it.getDateReported().toString().substring(0,10):"N/A";
            model.addRow(new Object[]{it.getId(),it.getItemName(),it.getType(),it.getCategory(),
                it.getLocation(),it.getStatus(),it.getReporterName(),date});
        }
    }

    private <T extends JTextField> T af(JPanel p, String label, T f) {
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); f.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l = UITheme.createLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(f); p.add(Box.createVerticalStrut(12));
        return f;
    }
    private JComboBox<String> afcb(JPanel p, String label, String[] opts) {
        JComboBox<String> cb = UITheme.createComboBox(opts);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); cb.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l = UITheme.createLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(cb); p.add(Box.createVerticalStrut(12));
        return cb;
    }

    private String abbrev(String dept) {
        if(dept==null) return "—";
        return dept.replace("College of ","").replace("Administration","Admin");
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this,msg);
    }
}