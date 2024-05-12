import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static String INPUT_CSV_FILENAME = "experiments.csv";
    private static String OUTPUT_CSV_FILENAME = "results.csv";

    static class SimParams {
        int runNumber;
        int ticksToRun;
        int numCols;
        int numRows;
        boolean toSetupFull;

        double immigrantChanceCooperateWithSame;
        double immigrantChanceCooperateWithDiff;
        int immigrantsPerDay;
        double initialPtr;
        double costOfGiving;
        double gainOfReceiving;
        double mutationRate;
        double deathRate;
    }
    public static void main(String [] args) {
        SimParams[] sps = readSimParams();
        ArrayList<Simulation.RunStats> rses = new ArrayList<Simulation.RunStats>();

        for (int i=0; i<sps.length; i++) {
            SimParams sp = sps[i];
            System.out.println("Run: " + (i+1));

            Simulation sim = new Simulation(
                sp.runNumber, 
                sp.ticksToRun, 
                sp.numRows, 
                sp.numCols,
                sp.toSetupFull,
                sp.mutationRate,
                sp.initialPtr,
                sp.deathRate,
                sp.costOfGiving,
                sp.immigrantsPerDay,
                sp.gainOfReceiving,
                sp.immigrantChanceCooperateWithSame,
                sp.immigrantChanceCooperateWithDiff
            );

            Simulation.RunStats rs = sim.run();
            rses.add(rs);
        }

        Reporter.writeStatsToCsvFile(OUTPUT_CSV_FILENAME, rses.toArray(new Simulation.RunStats[0]));
    }

    private static SimParams[] readSimParams() {
        List<SimParams> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_CSV_FILENAME))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                if (lineNumber++ == 0) {
                    continue; // skip header
                }

                String[] values = line.split(",");
                List<String> record = Arrays.asList(values);

                int col = 0;
                SimParams sp = new SimParams();
                sp.runNumber = Integer.parseInt(record.get(col++));
                sp.ticksToRun = Integer.parseInt(record.get(col++));
                sp.numCols = Integer.parseInt(record.get(col++));
                sp.numRows = Integer.parseInt(record.get(col++));
                sp.toSetupFull = Boolean.parseBoolean(record.get(col++));
        
                sp.immigrantChanceCooperateWithSame = Double.parseDouble(record.get(col++));
                sp.immigrantChanceCooperateWithDiff = Double.parseDouble(record.get(col++));
                sp.immigrantsPerDay = Integer.parseInt(record.get(col++));
                sp.initialPtr = Double.parseDouble(record.get(col++));
                sp.costOfGiving = Double.parseDouble(record.get(col++));
                sp.gainOfReceiving = Double.parseDouble(record.get(col++));
                sp.mutationRate = Double.parseDouble(record.get(col++));
                sp.deathRate = Double.parseDouble(record.get(col++));

                records.add(sp);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return records.toArray(new SimParams[0]);
    }
}
