package io.github.ayaxperson.boorumassdownloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.swing.JTextArea;

public class PrintOutStream extends ByteArrayOutputStream {

    final int maxTextAreaSize = 1000;

    private JTextArea textArea;

    public PrintOutStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            String outputStr = this.toString();
            super.reset();
            if (textArea.getText().length() > maxTextAreaSize) {
                textArea.replaceRange("", 0, 100);
            }
            textArea.append(outputStr);
        }
    }
}