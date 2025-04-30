package org.redible.checkpoint.checkpoint.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NavigationUI {

    public static void addStyledNavButton(JPanel targetPanel, String text, String iconPath, ActionListener action) {
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            JButton button = new JButton(text, icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g);
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
            };
            button.setFocusPainted(false);
            button.setBackground(new Color(30, 100, 180));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 50));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            targetPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            targetPanel.add(button);
            button.addActionListener(action);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ikon betöltése sikertelen: " + iconPath, e);
        }
    }

    public static JPanel createPortaSidebar(ActionListener onProfile, ActionListener onEvents, ActionListener onScan, ActionListener onLogout) {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(70, 130, 180));
        navPanel.setPreferredSize(new Dimension(200, 1000));

        addStyledNavButton(navPanel, "Profil", "/icons/profile-svgrepo-com.svg", onProfile);
        addStyledNavButton(navPanel, "Események", "/icons/list-svgrepo-com.svg", onEvents);
        addStyledNavButton(navPanel, "Kártyabeolvasás", "/icons/barcode-svgrepo-com.svg", onScan);
        addStyledNavButton(navPanel, "Kilépés", "/icons/logout-multimedia-ui-svgrepo-com.svg", onLogout);

        return navPanel;
    }

}
