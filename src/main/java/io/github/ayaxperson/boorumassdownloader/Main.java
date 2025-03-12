package io.github.ayaxperson.boorumassdownloader;

import com.formdev.flatlaf.FlatDarkLaf;
import me.ajax.gelbooru.Booru;
import me.ajax.gelbooru.Boorus;
import me.ajax.gelbooru.Page;
import me.ajax.gelbooru.Post;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConsoleFrame().setVisible(true);
            System.out.printf("Hello :3%n");
        });

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
        } catch (final Exception e) {
            System.err.println("Failed to show UI");
            System.err.printf("%s : %s%n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private JFrame frame;

    private JButton button;
    private JLabel statusLabel;
    private JLabel errorLabel;

    private JTextArea tagsArea;

    private JTextField booruUrlInput;
    private JTextField outputDirectoryInput;

    private JSpinner startPageInput;
    private JSpinner endPageInput;

    private JCheckBox originalQualityCheckbox, replaceFilesCheckbox;

    private final AtomicInteger errors = new AtomicInteger(0),
            warnings = new AtomicInteger(0);

    private final AtomicInteger downloadedFiles = new AtomicInteger(0);
    private final AtomicLong downloadedSize = new AtomicLong(0);

    private volatile boolean running = false;

    private void createAndShowGUI() {
        Thread.currentThread().setName("UI Thread");
        frame = new JFrame("Booru Mass Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setResizable(false);

        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        final JLabel titleLabel = new JLabel("Booru Mass Downloader", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Setup
        gbc.gridy++;
        panel.add(new JLabel("Booru URL"), gbc);
        gbc.gridy++;
        panel.add(booruUrlInput = new JTextField(), gbc);

        gbc.gridy++;
        panel.add(new JLabel("Output Directory"), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(outputDirectoryInput = new JTextField(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        final JButton folderButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));
        folderButton.setPreferredSize(new Dimension(25, 25));
        panel.add(folderButton, gbc);

        folderButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                final File selectedFolder = fileChooser.getSelectedFile();
                outputDirectoryInput.setText(selectedFolder.getAbsolutePath());
            }
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;

        // Page number
        panel.add(new JLabel("Starting Page"), gbc);
        gbc.gridy++;
        startPageInput = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        panel.add(startPageInput, gbc);

        gbc.gridy++;

        panel.add(new JLabel("Ending Page"), gbc);
        gbc.gridy++;
        endPageInput = new JSpinner(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 1));
        panel.add(endPageInput, gbc);

        // Options
        gbc.gridy++;
        panel.add(originalQualityCheckbox = new JCheckBox("Original quality"), gbc);
        gbc.gridy++;
        panel.add(replaceFilesCheckbox = new JCheckBox("Overwrite existing files"), gbc);

        // Tags label
        gbc.gridwidth = 2;
        gbc.gridy++;
        panel.add(new JLabel("Tags"), gbc);

        // Tags text are
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        tagsArea = new JTextArea();
        tagsArea.setPreferredSize(new Dimension(300, 100));
        panel.add(tagsArea, gbc);

        // Start/stop button
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(button = new JButton("Start"), gbc);
        button.addActionListener((ignored) -> this.run());

        // Status
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(statusLabel = new JLabel("Waiting to start"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(errorLabel = new JLabel("0 warnings, 0 errors"), gbc);

        frame.add(panel);
        frame.setVisible(true);

        new Thread(() -> {
            Thread.currentThread().setName("UI Update Thread");

            while (frame.isVisible()) {
                errorLabel.setText(String.format("%d warnings, %d errors", warnings.get(), errors.get()));

                final int start = (Integer) startPageInput.getValue();
                final int end = (Integer) endPageInput.getValue();
                if (start > end) {
                    endPageInput.setValue(start);
                }

                if (running) {
                    button.setText("Stop");
                } else {
                    button.setText("Start");
                }

                if (downloadedSize.get() != 0 || downloadedFiles.get() != 0) {
                    statusLabel.setText(String.format("Downloaded %d files (%d MB)", downloadedFiles.get(), downloadedSize.get() / 1000 / 1000));
                }
            }

            running = false;
        }).start();
    }

    private void run() {
        final boolean originalRunning = running;

        if (originalRunning) {
            running = false;
            return;
        }

        running = true;

        new Thread(() -> {
            final Path directory = Path.of(outputDirectoryInput.getText());

            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                System.err.println("Provided directory is not valid!");
                errors.addAndGet(1);
                running = false;
                return;
            }

            final String url = getUrl();
            final String[] tags = getTags();
            final Booru booru = new Booru(url);

            System.out.printf("Requesting with:%nURL: %s%nTags: %s%n", url, String.join(", ", tags));

            for (int pageIndex = (Integer) this.startPageInput.getValue(); pageIndex <= (Integer) this.endPageInput.getValue(); pageIndex++) {
                if (!running)
                    return;

                try {
                    final Page page = booru.getPage(pageIndex, tags);

                    for (final Post post : page.posts()) {
                        if (!running)
                            return;

                        try {
                            final Path path = Paths.get(directory.toString(), post.image());
                            final boolean replaceFiles =replaceFilesCheckbox.isSelected();

                            if (Files.exists(path)) {
                                if (!replaceFiles) {
                                    warnings.addAndGet(1);
                                    System.out.printf("Skipping file %s as it already exists%n", path);
                                    continue;
                                } else if (Files.isRegularFile(path)) {
                                    Files.delete(path);
                                    System.out.printf("Deleting file %s%n", path);
                                } else {
                                    System.out.printf("Skipping file %s as it is not a regular file%n", path);
                                    errors.addAndGet(1);
                                    continue;
                                }
                            }

                            String downloadUrlPlain = originalQualityCheckbox.isSelected() ? post.fileURL() : post.sampleURL();
                            downloadUrlPlain = downloadUrlPlain.replace("\\/", "/");

                            System.out.printf("Downloading %s to %s%n", downloadUrlPlain, path);

                            final URL downloadUrl = URI.create(downloadUrlPlain).toURL();
                            final URLConnection connection = downloadUrl.openConnection();
                            final InputStream stream = connection.getInputStream();
                            final byte[] bytes = stream.readAllBytes();
                            Files.write(path, bytes);
                            downloadedFiles.addAndGet(1);
                            downloadedSize.addAndGet(bytes.length);

                            System.out.printf("Downloaded %s to %s successfully %n", downloadUrlPlain, path);

                        } catch (final Exception e) {
                            errors.addAndGet(1);
                            System.err.printf("Failed to download %d%n", post.id());
                            System.err.printf("%s : %s%n", e.getClass().getSimpleName(), e.getMessage());
                        }
                    }
                } catch (final Exception e) {
                    this.errors.addAndGet(1);
                    System.err.println("Failed to parse page");
                    System.err.printf("%s : %s%n", e.getClass().getSimpleName(), e.getMessage());
                }
            }

            running = false;
        }).start();
    }

    private String getUrl() {
        final String urlInput = booruUrlInput.getText();

        for (final Boorus booru : Boorus.values()) {
            if (booru.name().equalsIgnoreCase(urlInput)) {
                return booru.baseUrl;
            }
        }

        final StringBuilder urlBuilder = new StringBuilder();

        if (!urlInput.startsWith("http://") && !urlInput.startsWith("https://")) {
            urlBuilder.append("https://");
        }

        urlBuilder.append(urlInput);

        return urlBuilder.toString();
    }

    private String[] getTags() {
        final String tagsInput = tagsArea.getText();

        final List<String> result = new ArrayList<>();
        final String[] lines = tagsInput.split("\n");

        for (final String line : lines) {
            result.addAll(Arrays.asList(line.split("%s")));
        }

        return result.toArray(new String[0]);
    }

}
