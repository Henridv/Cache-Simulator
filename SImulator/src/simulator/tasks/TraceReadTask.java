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

    public TraceReadTask(File trace) {
        super(SimulatorApp.getApplication());
        this.trace = trace;
        simulator = SimulatorApp.getApplication().getSimulator();
        simulatorView = (SimulatorView) SimulatorApp.getApplication().getMainView();
    }

    @Override
    protected Boolean doInBackground() throws IOException {
        // Count lines
        setMessage("Counting lines");
        BufferedReader in = new BufferedReader(new FileReader(trace.getAbsolutePath()));
        long total = 0;
        while (in.readLine() != null) {
            total++;
        }
        in.close();

        // Read accesses
        in = new BufferedReader(new FileReader(trace.getAbsolutePath()));
        String str;
        long count = 0;
        float  progress = 0;
        while ((str = in.readLine()) != null) {
            if (!str.equals("")) {
                count++;
                simulator.memoryAccess(Integer.parseInt(str, 16));
                progress = (float) count/total;
                setMessage("Reading accesses");
                setProgress(progress);
            }
        }
        setMessage("Finished");
        in.close();
        return true;
    }

    @Override
    protected void finished() {
        simulatorView.updateHitMisses();
    }
}
