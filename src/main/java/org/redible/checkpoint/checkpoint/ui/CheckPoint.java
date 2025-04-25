package org.redible.checkpoint.checkpoint.ui;

import javax.swing.*;

public class CheckPoint {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignIn().setVisible(true));
    }
}