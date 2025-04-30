package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import org.redible.checkpoint.checkpoint.ui.DarkModeUtil;
import org.redible.checkpoint.checkpoint.ui.UIUtil;
import org.redible.checkpoint.checkpoint.ui.UserManagementUI;
import org.redible.checkpoint.checkpoint.util.ProfileUtil;
import org.redible.checkpoint.checkpoint.util.EventsUtil;
import org.redible.checkpoint.checkpoint.ui.NavigationUI;
import org.redible.checkpoint.checkpoint.util.ApiUtil;
import org.redible.checkpoint.checkpoint.util.SearchUtil;

import javax.swing.*;
import java.awt.*;

public class AdminPage extends JFrame {
    private final String accessToken;
    private final JPanel contentPanel = new JPanel();
    private boolean isDarkModeEnabled = false;
    private JTable eventsTable;

    public AdminPage(String accessToken) {
        this.accessToken = accessToken;

        setTitle("CheckPoint - Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navigationPanel = NavigationUI.createAdminSidebar(
                e -> showProfile(),
                e -> showSearch(),
                e -> showUsersManagement(),
                e -> showSettings(),
                e -> showEvents(),
                e -> logout()
        );
        add(navigationPanel, BorderLayout.WEST);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void showProfile() {
        contentPanel.removeAll();
        JPanel profilePanel = ProfileUtil.buildProfilePanel(accessToken);
        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSearch() {
        SearchUtil.showSearch(contentPanel, accessToken);
    }

    private void showUsersManagement() {
        contentPanel.removeAll();

        JTable usersTable = new JTable();

        contentPanel.add(UserManagementUI.createUserManagementPanel(usersTable, accessToken, this), BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSettings() {
        UIUtil.showSettings(contentPanel, this);
    }

    private void showEvents() {
        contentPanel.removeAll();
        JPanel eventsPanel = EventsUtil.buildEventsPanel(accessToken);
        contentPanel.add(eventsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadEvents(String filter) {
        EventsUtil.loadEvents(eventsTable, filter, accessToken);
    }

    private void toggleDarkMode(boolean enable) {
        DarkModeUtil.toggleDarkMode(enable, this);
    }

    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(this, "Sikeres kijelentkezés!", "Kijelentkezés", JOptionPane.INFORMATION_MESSAGE);
        new org.redible.checkpoint.checkpoint.ui.SignIn().setVisible(true);
    }

    private String makeApiCall(String apiUrl, String method, String payload) throws Exception {
        return ApiUtil.makeApiCall(apiUrl, method, payload, accessToken);
    }
}