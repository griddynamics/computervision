package job;

import org.opencv.core.Core;
import ui.FileChooserDemo;

public class Launcher {

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Throwable ex) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Check opencv dynamic libraries path '" + libraryPath + "'");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileChooserDemo fc = new FileChooserDemo();
        fc.start();
    }
}
