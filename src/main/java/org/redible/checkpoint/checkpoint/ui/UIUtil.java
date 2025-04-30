package org.redible.checkpoint.checkpoint.ui;

import org.redible.checkpoint.checkpoint.AppConfig;

import javax.swing.*;
import java.awt.*;

public class UIUtil {
    public static void showSettings(JPanel contentPanel, JFrame parentFrame) {
        contentPanel.removeAll();

        boolean isDarkModeEnabled = AppConfig.isDarkModeEnabled();

        JCheckBox darkModeToggle = new JCheckBox("Sötét mód");
        darkModeToggle.setFont(new Font("Arial", Font.BOLD, 16));
        darkModeToggle.setSelected(isDarkModeEnabled);
        darkModeToggle.addActionListener(e -> DarkModeUtil.toggleDarkMode(darkModeToggle.isSelected(), parentFrame));

        contentPanel.add(darkModeToggle, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}