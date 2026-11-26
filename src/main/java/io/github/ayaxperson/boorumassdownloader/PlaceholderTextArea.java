package io.github.ayaxperson.boorumassdownloader;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextArea extends JTextArea {

    private String placeholder;

    public PlaceholderTextArea(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty()) {
            final Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(Color.GRAY);
            g2.setFont(getFont());

            final Insets insets = getInsets();

            g2.drawString(
                placeholder,
                insets.left + 5,
                insets.top + g2.getFontMetrics().getAscent()
            );

            g2.dispose();
        }
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
}