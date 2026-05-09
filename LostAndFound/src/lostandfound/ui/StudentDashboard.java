package lostandfound.ui;

import lostandfound.dao.ClaimDAO;
import lostandfound.dao.ItemDAO;
import lostandfound.model.Claim;
import lostandfound.model.Item;
import lostandfound.model.User;
import lostandfound.util.DropdownData;
import lostandfound.util.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final User student;
    private final ItemDAO  itemDAO  = new ItemDAO();
    private final ClaimDAO claimDAO = new ClaimDAO();

    private JPanel contentArea;
    private JLabel pageTitle;

    public StudentDashboard(User student) {
        this.student = student;
        setTitle("Lost & Found — Student Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);
        initUI();
    }

    // ─────────────────────────────────────────────────────────────
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());

        JPanel sidebar = buildSidebar();

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_LIGHT);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(14,22,14,22)));
        pageTitle = new JLabel("My Dashboard");
        pageTitle.setFont(new Font("Segoe UI",Font.BOLD,20));
        pageTitle.setForeground(UITheme.TEXT_DARK);

        // Student info badge
        String badge = student.getCourse()!=null
            ? student.getCourse()+" | "+DropdownData.intToYear(student.getYear())+" Sec "+student.getSection()
            : student.getEmail();
        JLabel infoBadge = new JLabel(badge);
        infoBadge.setFont(UITheme.FONT_SMALL); infoBadge.setForeground(UITheme.TEXT_GRAY);
        topBar.add(pageTitle, BorderLayout.WEST);
        topBar.add(infoBadge, BorderLayout.EAST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_LIGHT);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        main.add(topBar,      BorderLayout.NORTH);
        main.add(contentArea, BorderLayout.CENTER);
        root.add(sidebar, BorderLayout.WEST);
        root.add(main,    BorderLayout.CENTER);
        add(root);

        showDashboard();
    }

    // ─────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(new Color(15,60,35));
        sb.setPreferredSize(new Dimension(220,680));
        sb.setLayout(new BoxLayout(sb,BoxLayout.Y_AXIS));

        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT,18,18));
        logo.setOpaque(false);
        JLabel ll = new JLabel("🎒  Student Portal");
        ll.setFont(new Font("Segoe UI",Font.BOLD,15)); ll.setForeground(Color.WHITE);
        logo.add(ll); sb.add(logo);

        JPanel uInfo = new JPanel(new FlowLayout(FlowLayout.LEFT,18,6));
        uInfo.setOpaque(false);
        String deptShort = student.getCollege()!=null
            ? student.getCollege().replace("College of ","") : "Student";
        JLabel ul = new JLabel("<html><b style='color:#90EE90'>"+student.getFullName()+"</b>" +
            "<br><span style='color:#aaa;font-size:10px'>"+student.getStudentId()+"  |  "+deptShort+"</span></html>");
        uInfo.add(ul); sb.add(uInfo);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(220,1)); sep.setForeground(new Color(40,100,60));
        sb.add(sep); sb.add(Box.createVerticalStrut(8));

        String[][] navs = {
            {"🏠","My Dashboard","Dashboard"},
            {"➕","Report Item","Report"},
            {"📋","My Reports","My Reports"},
            {"🔍","Browse Found Items","Browse Found"},
            {"🔴","Browse Lost Items","Browse Lost"},
            {"📩","My Claims","My Claims"}
        };
        for (String[] n : navs) {
            JButton btn = navBtn(n[0]+"  "+n[1]);
            String key = n[2];
            btn.addActionListener(e -> navigate(key));
            sb.add(btn);
        }
        sb.add(Box.createVerticalGlue());
        JButton logout = navBtn("🚪  Logout");
        logout.setForeground(new Color(255,130,110));
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        sb.add(logout); sb.add(Box.createVerticalStrut(20));
        return sb;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.FONT_NAV); b.setForeground(new Color(200,240,215));
        b.setBackground(new Color(15,60,35)); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(220,42));
        b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        b.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(new Color(10,100,50)); }
            public void mouseExited(java.awt.event.MouseEvent e){ b.setBackground(new Color(15,60,35)); }
        });
        return b;
    }

    // ─────────────────────────────────────────────────────────────
    private void navigate(String key) {
        pageTitle.setText(switch(key){
            case "Dashboard" -> "My Dashboard";
            case "Report"    -> "Report Item";
            default          -> key;
        });
        contentArea.removeAll();
        switch(key) {
            case "Dashboard"   -> showDashboard();
            case "Report"      -> showReportForm();
            case "My Reports"  -> showMyReports();
            case "Browse Found"-> showBrowse("found");
            case "Browse Lost" -> showBrowse("lost");
            case "My Claims"   -> showMyClaims();
        }
        contentArea.revalidate(); contentArea.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    private void showDashboard() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setOpaque(false);

        // Welcome banner
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(15,60,35));
        banner.setBorder(BorderFactory.createEmptyBorder(18,22,18,22));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE,90));
        JLabel wl = new JLabel("Welcome back, "+student.getFullName()+"! 👋");
        wl.setFont(new Font("Segoe UI",Font.BOLD,20)); wl.setForeground(Color.WHITE);
        JLabel ws = new JLabel(student.getCourse()+" | "+DropdownData.intToYear(student.getYear())+" — Section "+student.getSection());
        ws.setFont(UITheme.FONT_LABEL); ws.setForeground(new Color(180,230,200));
        JPanel bText = new JPanel(new BorderLayout()); bText.setOpaque(false);
        bText.add(wl,BorderLayout.NORTH); bText.add(ws,BorderLayout.CENTER);
        banner.add(bText,BorderLayout.WEST);
        p.add(banner); p.add(Box.createVerticalStrut(18));

        // Stats
        List<Item> myItems = itemDAO.getByUser(student.getId());
        List<Claim> myClaims = claimDAO.getClaimsByUser(student.getId());
        long myLost    = myItems.stream().filter(i->"lost".equals(i.getType())).count();
        long myFound   = myItems.stream().filter(i->"found".equals(i.getType())).count();
        long pending   = myClaims.stream().filter(c->"pending".equals(c.getStatus())).count();
        long approved  = myClaims.stream().filter(c->"approved".equals(c.getStatus())).count();

        JPanel stats = new JPanel(new GridLayout(1,4,14,0));
        stats.setOpaque(false); stats.setMaximumSize(new Dimension(Integer.MAX_VALUE,100));
        stats.add(statCard("My Lost Reports",  (int)myLost,   UITheme.LOST_COLOR,    "🔴"));
        stats.add(statCard("My Found Reports", (int)myFound,  UITheme.FOUND_COLOR,   "🟢"));
        stats.add(statCard("Pending Claims",   (int)pending,  UITheme.PENDING_COLOR, "⏳"));
        stats.add(statCard("Approved Claims",  (int)approved, UITheme.CLAIMED_COLOR, "✅"));
        p.add(stats); p.add(Box.createVerticalStrut(20));

        JLabel rl = new JLabel("My Recent Reports");
        rl.setFont(UITheme.FONT_TITLE); rl.setForeground(UITheme.TEXT_DARK);
        p.add(rl); p.add(Box.createVerticalStrut(10));

        JScrollPane sc = new JScrollPane(buildItemTable(myItems.subList(0,Math.min(6,myItems.size()))));
        sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        p.add(sc);
        contentArea.add(p,BorderLayout.CENTER);
    }

    private JPanel statCard(String label, int val, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(14,18,14,18)));
        JPanel bar = new JPanel(); bar.setBackground(color); bar.setPreferredSize(new Dimension(4,50));
        JLabel il = new JLabel(icon+"  "+label);
        il.setFont(new Font("Segoe UI Emoji",Font.PLAIN,11)); il.setForeground(UITheme.TEXT_GRAY);
        JLabel vl = new JLabel(String.valueOf(val));
        vl.setFont(new Font("Segoe UI",Font.BOLD,32)); vl.setForeground(color);
        JPanel inner = new JPanel(new BorderLayout()); inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        inner.add(il,BorderLayout.NORTH); inner.add(vl,BorderLayout.CENTER);
        card.add(bar,BorderLayout.WEST); card.add(inner,BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────────
    private void showReportForm() {
        JPanel wrap = new JPanel(new GridBagLayout()); wrap.setOpaque(false);
        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(26,34,26,34)));

        JLabel ft = new JLabel("Report a Lost or Found Item");
        ft.setFont(new Font("Segoe UI",Font.BOLD,18)); ft.setForeground(UITheme.TEXT_DARK);
        ft.setAlignmentX(LEFT_ALIGNMENT);
        form.add(ft); form.add(Box.createVerticalStrut(18));

        JTextField nameF = af(form,"Item Name *",UITheme.createTextField());
        JTextField locF  = af(form,"Location *",UITheme.createTextField());
        JTextField contF = af(form,"Contact Info",UITheme.createTextField());
        JComboBox<String> typeBox = afcb(form,"Report Type *",new String[]{"lost","found"});
        JComboBox<String> catBox  = afcb(form,"Category *",new String[]{"Electronics","Clothing","Books/Notes","ID/Cards","Keys","Bags","Jewelry","Sports Equipment","Documents","Personal Accessories","Other"});

        JTextArea desc = new JTextArea(3,20); desc.setFont(UITheme.FONT_BODY);
        desc.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR),BorderFactory.createEmptyBorder(7,11,7,11)));
        JLabel dl = UITheme.createLabel("Description"); dl.setAlignmentX(LEFT_ALIGNMENT);
        form.add(dl); form.add(Box.createVerticalStrut(5));
        JScrollPane ds = new JScrollPane(desc); ds.setMaximumSize(new Dimension(Integer.MAX_VALUE,80)); ds.setAlignmentX(LEFT_ALIGNMENT);
        form.add(ds); form.add(Box.createVerticalStrut(14));

        JLabel sl = new JLabel(" "); sl.setFont(UITheme.FONT_SMALL); sl.setAlignmentX(LEFT_ALIGNMENT);
        form.add(sl); form.add(Box.createVerticalStrut(8));
        JButton sub = UITheme.createAccentButton("📤 Submit Report");
        sub.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); sub.setAlignmentX(LEFT_ALIGNMENT);
        form.add(sub);

        sub.addActionListener(e -> {
            if(nameF.getText().trim().isEmpty()||locF.getText().trim().isEmpty()){
                sl.setForeground(UITheme.DANGER); sl.setText("Item name and location are required."); return; }
            Item item = new Item(0,nameF.getText().trim(),desc.getText(),(String)catBox.getSelectedItem(),
                locF.getText().trim(),"active",(String)typeBox.getSelectedItem(),contF.getText().trim(),
                student.getId(),student.getFullName(),null);
            if(itemDAO.addItem(item)){
                sl.setForeground(UITheme.SUCCESS); sl.setText("✅ Report submitted successfully!");
                nameF.setText(""); locF.setText(""); contF.setText(""); desc.setText("");
            } else { sl.setForeground(UITheme.DANGER); sl.setText("Submission failed. Try again."); }
        });

        wrap.add(form);
        contentArea.add(wrap,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    private void showMyReports() {
        JPanel p = new JPanel(new BorderLayout(0,10)); p.setOpaque(false);
        List<Item> items = itemDAO.getByUser(student.getId());
        JLabel cnt = new JLabel("Your Reports: "+items.size());
        cnt.setFont(UITheme.FONT_LABEL); cnt.setForeground(UITheme.TEXT_GRAY);
        JScrollPane sc = new JScrollPane(buildItemTable(items));
        sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        p.add(cnt,BorderLayout.NORTH); p.add(sc,BorderLayout.CENTER);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    /** Browse found/lost items with a "Claim This Item" button */
    private void showBrowse(String type) {
        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        // Search
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); searchRow.setOpaque(false);
        JTextField kw = UITheme.createTextField(); kw.setPreferredSize(new Dimension(260,36));
        JButton srch = UITheme.createPrimaryButton("🔍 Search");
        searchRow.add(kw); searchRow.add(Box.createHorizontalStrut(8)); searchRow.add(srch);

        List<Item> items = "found".equals(type) ? itemDAO.getActiveFoundItems() : itemDAO.getByType(type);
        JTable table = buildItemTable(items);
        JScrollPane sc = new JScrollPane(table); sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // Claim button (only for found items)
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); btns.setOpaque(false);
        if("found".equals(type)) {
            JButton claimBtn = UITheme.createSuccessButton("📩 Claim This Item");
            btns.add(claimBtn);

            claimBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if(row<0){ JOptionPane.showMessageDialog(this,"Please select an item first."); return; }
                int itemId = (int)table.getValueAt(row,0);
                String itemName = (String)table.getValueAt(row,1);

                // Proof dialog
                JTextArea proofArea = new JTextArea(5,30);
                proofArea.setLineWrap(true); proofArea.setWrapStyleWord(true);
                proofArea.setFont(UITheme.FONT_BODY);
                JScrollPane ps = new JScrollPane(proofArea);
                JPanel dialog = new JPanel(new BorderLayout(0,8));
                dialog.add(new JLabel("<html><b>Item:</b> "+itemName+"<br>Describe proof of ownership:<br><i>(e.g. unique marks, name inside, receipt, etc.)</i></html>"),BorderLayout.NORTH);
                dialog.add(ps,BorderLayout.CENTER);

                int result = JOptionPane.showConfirmDialog(this,dialog,"Submit Claim",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
                if(result!=JOptionPane.OK_OPTION) return;
                String proof = proofArea.getText().trim();
                if(proof.isEmpty()){ JOptionPane.showMessageDialog(this,"Please describe your proof of ownership."); return; }

                if(claimDAO.submitClaim(itemId,student.getId(),proof)){
                    JOptionPane.showMessageDialog(this,
                        "<html><b>Claim submitted successfully!</b><br><br>" +
                        "Your claim is now <b>pending admin review</b>.<br>" +
                        "You will see the status in <b>My Claims</b>.<br>" +
                        "If approved, visit the office with your Student ID.</html>",
                        "Claim Submitted", JOptionPane.INFORMATION_MESSAGE);
                    navigate("My Claims");
                } else {
                    JOptionPane.showMessageDialog(this,"You already have a pending claim for this item.","Duplicate",JOptionPane.WARNING_MESSAGE);
                }
            });
        }

        srch.addActionListener(e -> {
            String txt = kw.getText().trim();
            List<Item> res = txt.isEmpty() ? ("found".equals(type)?itemDAO.getActiveFoundItems():itemDAO.getByType(type)) : itemDAO.search(txt);
            DefaultTableModel m = (DefaultTableModel)table.getModel();
            m.setRowCount(0);
            for(Item it:res){ String d=it.getDateReported()!=null?it.getDateReported().toString().substring(0,10):"N/A";
                m.addRow(new Object[]{it.getId(),it.getItemName(),it.getCategory(),it.getLocation(),it.getStatus(),it.getContactInfo(),d});}
        });

        p.add(searchRow,BorderLayout.NORTH);
        p.add(sc,BorderLayout.CENTER);
        p.add(btns,BorderLayout.SOUTH);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    /** My Claims — student sees status of all their claim requests */
    private void showMyClaims() {
        List<Claim> claims = claimDAO.getClaimsByUser(student.getId());
        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        String[] cols = {"Claim ID","Item","Status","Admin Notes","Date Filed","Date Reviewed"};
        DefaultTableModel model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        for(Claim cl : claims){
            String filed    = cl.getDateClaimed()!=null?cl.getDateClaimed().toString().substring(0,10):"N/A";
            String reviewed = cl.getDateReviewed()!=null?cl.getDateReviewed().toString().substring(0,10):"Pending";
            model.addRow(new Object[]{cl.getId(),cl.getItemName(),cl.getStatus().toUpperCase(),
                cl.getAdminNotes()!=null?cl.getAdminNotes():"—", filed, reviewed});
        }

        JTable table = new JTable(model); UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                Component c = super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if(!sel){
                    String st = (String)t.getValueAt(row,2);
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

        // Status legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,14,4)); legend.setOpaque(false);
        for(String[] lg : new String[][]{{"⏳ PENDING","pending review"},{"✅ APPROVED","visit office"},{"❌ REJECTED","see notes"},{"📦 COMPLETED","item received"}}){
            JLabel ll = new JLabel(lg[0]+" — "+lg[1]);
            ll.setFont(UITheme.FONT_SMALL); ll.setForeground(UITheme.TEXT_GRAY);
            legend.add(ll);
        }

        JScrollPane sc = new JScrollPane(table); sc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        p.add(legend,BorderLayout.NORTH);
        p.add(sc,BorderLayout.CENTER);
        contentArea.add(p,BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    private JTable buildItemTable(List<Item> items) {
        String[] cols = {"ID","Item Name","Category","Location","Status","Contact","Date"};
        DefaultTableModel m = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        for(Item it:items){
            String d = it.getDateReported()!=null?it.getDateReported().toString().substring(0,10):"N/A";
            m.addRow(new Object[]{it.getId(),it.getItemName(),it.getCategory(),it.getLocation(),it.getStatus(),it.getContactInfo(),d});
        }
        JTable t = new JTable(m); UITheme.styleTable(t);
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable tb,Object v,boolean sel,boolean foc,int row,int col){
                Component c = super.getTableCellRendererComponent(tb,v,sel,foc,row,col);
                if(!sel){
                    String st=(String)tb.getValueAt(row,4);
                    if("claimed".equalsIgnoreCase(st)) c.setBackground(new Color(235,248,255));
                    else c.setBackground(Color.WHITE);
                }
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); return c;
            }
        });
        return t;
    }

    private <T extends JTextField> T af(JPanel p,String label,T f){
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); f.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l=UITheme.createLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(f); p.add(Box.createVerticalStrut(12));
        return f;
    }
    private JComboBox<String> afcb(JPanel p,String label,String[] opts){
        JComboBox<String> cb=UITheme.createComboBox(opts);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); cb.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l=UITheme.createLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(cb); p.add(Box.createVerticalStrut(12));
        return cb;
    }
}