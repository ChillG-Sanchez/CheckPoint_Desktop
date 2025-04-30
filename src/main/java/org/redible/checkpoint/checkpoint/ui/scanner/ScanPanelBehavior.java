package org.redible.checkpoint.checkpoint.ui.scanner;

import org.redible.checkpoint.checkpoint.util.CardScannerUtil;
import org.redible.checkpoint.checkpoint.util.SmokingStatusUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScanPanelBehavior {
    public static void attachDebouncedScanHandler(JTextField cardField, String accessToken, DefaultListModel<String> eventLogModel) {
        final Timer[] debounceTimer = {null};
        final int idleThreshold = 300;

        cardField.getDocument().addDocumentListener(new DocumentListener() {
            private void restartDebounce() {
                if (debounceTimer[0] != null) {
                    debounceTimer[0].stop();
                }

                debounceTimer[0] = new Timer(idleThreshold, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String cardNumber = cardField.getText().trim();
                        if (!cardNumber.isEmpty()) {
                            CardScannerUtil.processCardScan(cardNumber, accessToken, eventLogModel);
                            SmokingStatusUtil.fetchSmokingStatuses(eventLogModel, accessToken);
                            cardField.setText("");
                        }
                    }
                });

                debounceTimer[0].setRepeats(false);
                debounceTimer[0].start();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                restartDebounce();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                restartDebounce();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                restartDebounce();
            }
        });
    }
}
