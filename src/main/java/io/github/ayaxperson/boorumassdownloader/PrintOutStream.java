package io.github.ayaxperson.boorumassdownloader;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class PrintOutStream extends ByteArrayOutputStream {

    private final int maxTextAreaSize = 1000;

    private final PrintStream original;
    private final JTextArea textArea;

    public PrintOutStream(final PrintStream original, final JTextArea textArea) {
        this.textArea = textArea;
        this.original = original;
    }

    @Override
    public void write(final int b) {
        original.write(b);
        super.write(b);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        original.write(b, off, len);
        super.write(b, off, len);
    }


    public void flush() throws IOException {
        synchronized (this) {
            original.flush();
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