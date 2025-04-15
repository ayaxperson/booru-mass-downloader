package io.github.ayaxperson.boorumassdownloader;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

public class ConsoleFrame extends JFrame {

    public ConsoleFrame() {
        super("Console");

        final JPanel panel = new JPanel();
        final JScrollPane scrollPane = new JScrollPane();
        final JTextArea consoleTextArea = new JTextArea();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());

        consoleTextArea.setFocusable(false);
        consoleTextArea.setColumns(20);
        consoleTextArea.setRows(5);
        scrollPane.setViewportView(consoleTextArea);

        final GroupLayout layout = new GroupLayout(panel);

        panel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 835, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 378, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
        );

        getContentPane().add(panel);

        pack();

        final PrintOutStream printOutStream = new PrintOutStream(System.out, consoleTextArea);
        System.setErr(new PrintStream(printOutStream, true));
        System.setOut(new PrintStream(printOutStream, true));
    }
}