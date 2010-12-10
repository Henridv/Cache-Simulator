package simulator.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.jdesktop.application.Task;
import simulator.SimulatorApp;
import simulator.Simulator;
import simulator.SimulatorView;

/**
 *
 * @author Ruben Verhack
 */
public class TraceReadTask extends Task<Boolean, Integer> {

    private File trace;
    private Simulator simulator;
    private SimulatorView simulatorView;

    /**
     * 
     * @param trace
     */
    public TraceReadTask(File trace) {
        super(SimulatorApp.getApplication());
        this.trace = trace;
        this.simulator = SimulatorApp.getApplication().getSimulator();
        this.simulatorView = SimulatorApp.getApplication().getSimulatorView();
    }

    /**
     * Dit is de functie die de traces uitleest. Indien de filechooser een directory
     * meegaf, dan zullen alle traces in die folder apart ingelezen worden. Indien
     * één file geselecteerd werd, wordt enkel die file genomen.
     * @return
     * @throws IOException
     */
    @Override
    protected Boolean doInBackground() throws IOException {
        File[] files;

        // Hack voor als er maar één file geselecteerd werd
        if (trace.isDirectory()) {
            files = trace.listFiles();
        } else {
            files = new File[1];
            files[0] = trace;
        }

        for (File file : files) {

            // Enkel .out files bekijken. Nog niet gegarandeerd dat het memtraces zijn
            // maar is op zich niet zo erg
            if (file.getName().endsWith(".out")) {

                // Count lines
                setMessage("Counting lines");
                BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
                long total = 0;
                while (in.readLine() != null) {
                    total++;
                }
                in.close();

                // Read accesses
                in = new BufferedReader(new FileReader(file.getAbsolutePath()));
                String str;
                long count = 0;
                long address = 0;
                long programCounter = 0;
                int indexOfColon = 0;
                float progress = 0;
                while ((str = in.readLine()) != null) {
                    if (!str.equals("") && !str.equals("#eof")) {
                        try {
                            indexOfColon = str.indexOf(':');
                            if (indexOfColon > 0) {
                                address = Long.parseLong(str.substring(indexOfColon+1));
                                programCounter = Long.parseLong(str.substring(0, indexOfColon));
                                simulator.memoryAccess(address, programCounter);
                            } else {
                                address = Long.parseLong(str);
                                simulator.memoryAccess(address);
                            }
                            count++;
                            progress = (float) count / total;
                            setMessage("Reading accesses");
                            setProgress(progress);
                        } catch (NumberFormatException ex) {
                            System.err.println("WARNING: Could not parse long: '" + str + "'");

                        }
                    }
                }
                setMessage("Finished file: " + file.getName());
                in.close();
                simulatorView.updateHitMisses(file);
            } else {
                System.err.println("WARNING: This is not a trace-file: " + file.getName());
            }
        }
        return true;
    }
}
