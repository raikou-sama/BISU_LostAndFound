package lostandfound;

import lostandfound.ui.LoginFrame;
import lostandfound.util.UITheme;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        UITheme.applyGlobalTheme();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}