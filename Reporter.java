import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reporter {
    public static void writeStatsToCsvFile(String csvFilename, Simulation.RunStats[] rses) {
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[] {});
        dataLines.add(new String[] {});
        dataLines.add(new String[] {});
        dataLines.add(new String[] {});
        dataLines.add(new String[] {});
        dataLines.add(new String[] {});

        dataLines.add(new String[] 
            { 
                "[run number]",
                "gain-of-receiving",
                "initial-ptr",
                "immigrants-per-day",
                "immigrant-chance-cooperate-with-same",
                "mutation-rate",
                "cost-of-giving",
                "immigrant-chance-cooperate-with-different",
                "death-rate",
                "max-pxcor",
                "max-pycor",
                "[step]",
                "coopown-percent",
                "defother-percent",
                "consist-ethno-percent",
                "meetown-percent",
                "coop-percent",
                "last100coopown-percent",
                "last100defother-percent",
                "last100consist-ethno-percent",
                "last100meetown-percent",
                "last100coop-percent",
                "cc-percent",
                "cd-percent",
                "dc-percent",
                "dd-percent" 
            });

        for(int i=0; i<rses.length; i++) {
            Simulation.RunStats rs = rses[i];

            dataLines.add(new String[] 
            { 
                String.valueOf(rs.runNumber),
                String.valueOf(rs.gainOfReceiving),
                String.valueOf(rs.initialPtr ),
                String.valueOf(rs.immigrantsPerDay),
                String.valueOf(rs.immigrantChanceCooperateWithSame),
                String.valueOf(rs.mutationRate),
                String.valueOf(rs.costOfGiving),
                String.valueOf(rs.immigrantChanceCooperateWithDifferent),
                String.valueOf(rs.deathRate),
                String.valueOf(rs.maxPxcor),
                String.valueOf(rs.maxPycor),
                String.valueOf(rs.step),
                String.valueOf(rs.coopownPercent),
                String.valueOf(rs.defotherPercent),
                String.valueOf(rs.consistEthnoPercent),
                String.valueOf(rs.meetownPercent),
                String.valueOf(rs.coopPercent),
                String.valueOf(rs.last100coopownPercent),
                String.valueOf(rs.last100defotherPercent),
                String.valueOf(rs.last100consistEthnoPercent),
                String.valueOf(rs.last100meetownPercent),
                String.valueOf(rs.last100coopPercent),
                String.valueOf(rs.ccPercent),
                String.valueOf(rs.cdPercent),
                String.valueOf(rs.dcPercent),
                String.valueOf(rs.ddPercent)
            });
        }
        

        File csvOutputFile = new File(csvFilename);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
            .map(Reporter::convertToCSV)
            .forEach(pw::println);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertToCSV(String[] data) {
        return Stream.of(data)
          .collect(Collectors.joining(","));
    }
}