package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import org.redible.checkpoint.checkpoint.ui.NavigationUI;
import org.redible.checkpoint.checkpoint.ui.UIUtil;
import org.redible.checkpoint.checkpoint.ui.scanner.ScanPanelBehavior;
import org.redible.checkpoint.checkpoint.ui.scanner.ScanPanelBuilder;
import org.redible.checkpoint.checkpoint.util.EventsUtil;
import org.redible.checkpoint.checkpoint.util.ProfileUtil;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("ALL")
public class PortaPage extends JFrame {
    private final String accessToken;
    private final JPanel contentPanel = new JPanel();

    public PortaPage(String accessToken) {
        this.accessToken = accessToken;

        setTitle("CheckPoint - Porta");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navigationPanel = NavigationUI.createPortaSidebar(
                e -> showProfile(),
                e -> showEvents(),
                e -> showScanPanel(),
                e -> showSettings(),
                e -> logout()
        );
        add(navigationPanel, BorderLayout.WEST);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void showScanPanel() {
        contentPanel.removeAll();

        JTextField cardField = new JTextField(20);
        DefaultListModel<String> eventLogModel = new DefaultListModel<>();

        JPanel scanPanel = ScanPanelBuilder.createScanPanel(cardField, eventLogModel);
        contentPanel.add(scanPanel, BorderLayout.CENTER);

        ScanPanelBehavior.attachDebouncedScanHandler(cardField, accessToken, eventLogModel);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        contentPanel.removeAll();
        JPanel profilePanel = ProfileUtil.buildProfilePanel(accessToken);
        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEvents() {
        contentPanel.removeAll();
        contentPanel.add(EventsUtil.buildEventsPanel(accessToken), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSettings() {
        UIUtil.showSettings(contentPanel, this);
    }

    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(this, "Sikeres kijelentkezés!", "Kijelentkezés", JOptionPane.INFORMATION_MESSAGE);
        new org.redible.checkpoint.checkpoint.ui.SignIn().setVisible(true);
    }
}
