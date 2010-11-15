/*
 * SImulatorApp.java
 */

package simulator;

import javax.swing.UIManager;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class SimulatorApp extends SingleFrameApplication {

    /**
     *
     */
    protected Simulator simulator;

    /**
     * Get the value of simulator
     *
     * @return the value of simulator
     */
    public Simulator getSimulator() {
        return simulator;
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {

        this.simulator = new Simulator();
        SimulatorView simulatorView = new SimulatorView(this);
        show(simulatorView);

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SImulatorApp
     */
    public static SimulatorApp getApplication() {
        return Application.getInstance(SimulatorApp.class);
    }

    /**
     * Main method launching the application.
     * @param args 
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Niets doen
            System.out.println(ex);
        }

        launch(SimulatorApp.class, args);
        
    }
}
