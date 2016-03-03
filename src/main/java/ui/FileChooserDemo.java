package ui;

import processing.Cluster;

import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * FileChooserDemo.java uses these files:
 *   images/Open16.gif
 *   images/Save16.gif
 */
public class FileChooserDemo extends JPanel
        implements ActionListener {
    static private final String newline = "\n";
    JButton openButton;
    JButton openUrlButton;
    JButton batchButton;
    JTextArea log;
    JFileChooser imageChooser;
    JFileChooser folderChooser;

    public FileChooserDemo() {
        super(new BorderLayout());

        String workDir = System.getProperty("user.dir");
        File workingDir = new File(workDir);

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(5, 15);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        imageChooser = new JFileChooser(workingDir);
        imageChooser.removeChoosableFileFilter(imageChooser.getFileFilter());

        javax.swing.filechooser.FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        imageChooser.addChoosableFileFilter(imageFilter);

        folderChooser = new JFileChooser(workingDir);
        folderChooser.removeChoosableFileFilter(folderChooser.getFileFilter());
        folderChooser.setFileFilter(new FolderFilter());
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //Uncomment one of the following lines to try a different
        //file selection mode.  The first allows just directories
        //to be selected (and, at least in the Java look and feel,
        //shown).  The second allows both files and directories
        //to be selected.  If you leave these lines commented out,
        //then the default mode (FILES_ONLY) will be used.
        //
        //imageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //imageChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //Create the open button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);

        openUrlButton = new JButton("Open URL of image");
        openUrlButton.addActionListener(this);

        batchButton = new JButton("Folder for sort");
        batchButton.addActionListener(this);

        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        buttonPanel.add(openButton);
        buttonPanel.add(openUrlButton);
        buttonPanel.add(batchButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = imageChooser.showOpenDialog(FileChooserDemo.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = imageChooser.getSelectedFile();
                //This is where a real application would open the file.
                try {
                    final BufferedImage img = ImageIO.read(file);
                    file = getValidFileByImage(file.getAbsolutePath(), img, "jpg");
                    new Cluster().segmentation(file, false);
                    log.append("Opening: " + file.getName() + "." + newline);
                    log.append("Please choose image" + newline);
                } catch (IOException ex) {
                    log.append("IOException: " + ex.getMessage() + newline);
                }
            }
            log.setCaretPosition(log.getDocument().getLength());
        } else if (e.getSource() == openUrlButton) {

            JPanel panel = new JPanel();
            UIManager.put("OptionPane.minimumSize", new Dimension(500, 80));

            String urlString = (String) (JOptionPane.showInputDialog(panel, null,
                    "Input URL", JOptionPane.PLAIN_MESSAGE,
                    null, null, null));

            if (urlString != null && !urlString.isEmpty()) {
                log.append("Opening: " + urlString + newline);
                File file = null;
                String mimeType = null;
                try {
                    String[] splittedUrl = urlString.split("/");
                    String fileName = splittedUrl[splittedUrl.length - 1];
                    final URL url = new URL(urlString);
                    final HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
                    mimeType = connection.getContentType();
                    final BufferedImage img = ImageIO.read(connection.getInputStream());
                    if (img != null) {
                        file = getValidFileByImage(fileName, img, "jpg");
                    } else {
                        log.append("Incorrect URL of image. Mime type:" + mimeType + newline);
                    }
                } catch (IOException ex) {
                    log.append("IOException: " + ex.getMessage() + newline);
                }
                if (file != null) {
                    new Cluster().segmentation(file, false);
                }
            } else {
                if (urlString != null) {
                    log.append("Empty url" + newline);
                }
            }
        } else if (e.getSource() == batchButton) {
            int returnVal = folderChooser.showOpenDialog(FileChooserDemo.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = folderChooser.getSelectedFile();
                //This is where a real application would open the file.
                final File[] files = file.listFiles();
                if (file.isDirectory() && files.length != 0) {
                    SwingWorker<Void, File> sworker = new SwingWorker<Void, File>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            for (File image : files) {
                                final BufferedImage img;
                                if (image.isDirectory()) {
                                    continue;
                                }
                                try {
                                    img = ImageIO.read(image);
                                } catch (IIOException ex) {
                                    continue;
                                }
                                if (img != null) {
                                    image = getValidFileByImage(image.getAbsolutePath(), img, "jpg");
                                    new Cluster().segmentation(image, true);
                                    publish(image);
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            super.done();
                        }

                        @Override
                        protected void process(List<File> chunks) {
                            for (File chunk : chunks) {
                                log.append("Processed " + chunk.getName() + newline);
                            }
                        }
                    };
                    sworker.execute();
                    log.append("Sorting started on " + file.getName() + "." + newline);
                } else {
                    log.append(file.getName() + "is not a directory or it is empty." + newline);
                }
            }
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    private File getValidFileByImage(String fileName, BufferedImage img, String imageFormat) throws IOException {
        File file;
        file = new File(fileName);
        ImageIO.write(img, imageFormat, file);
        return file.isFile() ? file : null;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Choose JPG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        //Add content to the window.
        frame.add(new FileChooserDemo());

        //Display the window.
        frame.setSize(204, 168);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void start() {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }

    private static class FolderFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept( File file ) {
            return file.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Folders";
        }
    }
}