import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private static boolean DEBUG_PRINT = false;

    private Grid grid;

    private Random rand;

    private int runNumber;
    private int ticksToRun = 5;
    private int numCols = 10;
    private int numRows = 10;

    private double immigrantChanceCooperateWithSame = 0.5;
    private double immigrantChanceCooperateWithDiff = 0.5;
    private int immigrantsPerDay = 1;
    private double initialPtr = 0.12;
    private double costOfGiving = 0.01;
    private double gainOfReceiving = 0.03;
    private double mutationRate = 0.005;
    private double deathRate = 0.1;

    private int meet;                                   // how many interactions occurred this turn
    private int meetAgg;                                // how many interactions occurred through the run
    private ArrayList<Integer> last100meet;             // meet for the last 100 ticks
    private int meetown;                                // what number of individuals met someone of their own color this turn
    private int meetownAgg;                             // what number of individuals met someone of their own color throughout the run
    private ArrayList<Integer> last100meetown;          // meetown for the last 100 ticks
    private int meetother;                              // what number of individuals met someone of a different color this turn
    private int meetotherAgg;                           // what number of individuals met someone of a different color throughout the run
    private ArrayList<Integer> last100meetother;        // meetother for the last 100 ticks
    private int coopown;                                // how many interactions this turn were cooperating with the same color
    private int coopownAgg;                             // how many interactions throughout the run were cooperating with the same color
    private ArrayList<Integer> last100coopown;          // coopown for the last 100 ticks
    private int coopother;                              // how many interactions this turn were cooperating with a different color
    private int coopotherAgg;                           // how many interactions throughout the run were cooperating with a different color
    private int defother;                               // how many interactions this turn were defecting with a different color
    private int defotherAgg;                            // how many interactions throughout the run were defecting with a different color
    private ArrayList<Integer> last100defother;         // defother for the last 100 ticks
    private ArrayList<Integer> last100cc;               // how many cooperate-cooperate genotypes have there been in the last 100 ticks
    private ArrayList<Integer> last100cd;               // how many cooperate-defect genotypes have there been in the last 100 ticks
    private ArrayList<Integer> last100dc;               // how many defect-cooperate genotypes have there been in the last 100 ticks
    private ArrayList<Integer> last100dd;               // how many defect-defect genotypes have there been in the last 100 ticks
    private ArrayList<Integer> last100consistEthno;     // how many interactions consistent with ethnocentrism in the last 100 ticks
    private ArrayList<Integer> last100coop;             // how many interactions have been cooperation in the last 100 ticks

    public class RunStats {
        int runNumber;
        double gainOfReceiving;
        double initialPtr;
        int immigrantsPerDay;
        double immigrantChanceCooperateWithSame;
        double mutationRate;
        double costOfGiving;
        double immigrantChanceCooperateWithDifferent;
        double deathRate;
        int maxPxcor; 
        int maxPycor;
        int step;
        double coopownPercent;
        double defotherPercent;
        double consistEthnoPercent;
        double meetownPercent;
        double coopPercent; 
        double last100coopownPercent;
        double last100defotherPercent;
        double last100consistEthnoPercent;
        double last100meetownPercent;
        double last100coopPercent;
        double ccPercent;
        double cdPercent;
        double dcPercent;
        double ddPercent;
    }

    public Simulation(
        int runNumber, 
        int ticksToRun, 
        int numRows, 
        int numCols,
        boolean toSetupFull,
        double mutationRate,
        double initialPtr,
        double deathRate,
        double costOfGiving,
        int immigrantsPerDay,
        double gainOfReceiving,
        double immigrantChanceCooperateWithSame,
        double immigrantChanceCooperateWithDiff
        ) {

        this.runNumber = runNumber;
        this.ticksToRun = ticksToRun;
        this.numRows = numRows;
        this.numCols = numCols;
        this.mutationRate = mutationRate;
        this.initialPtr = initialPtr;
        this.deathRate = deathRate;
        this.costOfGiving = costOfGiving;
        this.immigrantsPerDay = immigrantsPerDay;
        this.gainOfReceiving = gainOfReceiving;
        this.immigrantChanceCooperateWithSame = immigrantChanceCooperateWithSame;
        this.immigrantChanceCooperateWithDiff = immigrantChanceCooperateWithDiff;

        this.rand = new Random();

        if (toSetupFull) {
            setupFull();
        }
        else {
            setupEmpty();
        }
    }

    public RunStats run() {
        for (int tick=0; tick<this.ticksToRun; tick++) {
            go();
        }

        return getRunStats();
    }

    private void setupEmpty() {
        clearAll();
        initializeVariables();
    }

    private void setupFull() {
        clearAll();
        initializeVariables();
        grid.askPatches((agent, coord) -> grid.setAgentAt(createAgent(), coord));
    }

    private void clearAll() {
        this.grid = new Grid(numRows, numCols);
    }

    private void initializeVariables() {
        this.meetown = 0;
        this.meetownAgg = 0;
        this.meet = 0;
        this.meetAgg = 0;
        this.coopown = 0;
        this.coopownAgg = 0;
        this.defother = 0;
        this.defotherAgg = 0;
        this.meetother = 0;
        this.meetotherAgg = 0;
        this.coopother = 0;
        this.coopotherAgg = 0;
        this.last100dd = new ArrayList<Integer>();
        this.last100cd = new ArrayList<Integer>();
        this.last100cc = new ArrayList<Integer>();
        this.last100dc = new ArrayList<Integer>();
        this.last100coopown = new ArrayList<Integer>();
        this.last100defother = new ArrayList<Integer>();
        this.last100consistEthno = new ArrayList<Integer>();
        this.last100meetown = new ArrayList<Integer>();
        this.last100meetother = new ArrayList<Integer>();
        this.last100meet = new ArrayList<Integer>();
        this.last100coop = new ArrayList<Integer>();
    }

    private Agent createAgent() {
        int colour = getRandomColour();
        boolean same = rand.nextDouble() < immigrantChanceCooperateWithSame;
        boolean diff = rand.nextDouble() < immigrantChanceCooperateWithDiff;

        return new Agent(colour, same, diff, initialPtr);
    }

    private int getRandomColour() {
        return rand.nextInt(0, 4);
    }

    private void clearStats() {
        this.meetown = 0;
        this.meet = 0;
        this.coopown = 0;
        this.defother = 0;
        this.meetother = 0;
        this.coopother = 0;
    }

    private void go() {
        clearStats(); // clear the turn based stats
        immigrate(); // new agents immigrate into the world

        // reset the probability to reproduce
        grid.askAgents((agent, coord) -> agent.setPtr(initialPtr));

        // have all of the agents interact with other agents if they can
        grid.askAgents((agent, coord) -> interact(agent, coord));

        // now they reproduce
        grid.askAgents((agent, coord) -> reproduce(agent, coord));

        // kill some of the agents
        grid.askAgents((agent, coord) -> death(agent, coord));

        // update the states for the aggregate and last 100 ticks
        updateStats();

        if (DEBUG_PRINT) this.grid.print();
    }

    // random individuals enter the world on empty cells
    private void immigrate() {
        int newImmigrants = 0;
        Coord[] coords = grid.getRandomizedCoords();

        // we can't have more immigrants than there are empty patches and immigrantsPerDay
        for (int i=0; i<coords.length && newImmigrants < this.immigrantsPerDay; i++) {
            Coord coord = coords[i];

            if (grid.isEmptyAt(coord)) {
                Agent newAgent = createAgent();
                grid.setAgentAt(newAgent, coord);

                debugPrint("immigrate " + newAgent + " " + coord + " immiCount=" + newImmigrants);
                newImmigrants++;
            }
        }
    }

    // turtle procedure
    private void interact(Agent agent, Coord coord) {
        Agent[] neighbouringAgents = grid.getRandomizedNeighbourAgentsAt(coord);

        for (int i=0; i<neighbouringAgents.length; i++) {
            Agent neighbouringAgent = neighbouringAgents[i];

            this.meet++;
            this.meetAgg++;

            // do one thing if the individual interacting is the same color as me
            if (agent.getColour() == neighbouringAgent.getColour()) {
                // record the fact the agent met someone of the own color
                this.meetown++;
                this.meetownAgg++;

                // if I cooperate then I reduce my PTR and increase my neighbors
                if (agent.getCooperateWithSame()) {
                    this.coopown++;
                    this.coopownAgg++;

                    agent.setPtr(agent.getPtr() - costOfGiving);
                    neighbouringAgent.setPtr(neighbouringAgent.getPtr() + gainOfReceiving);
                }
            }
            // if we are different colors we take a different strategy
            else {
                // record stats on encounters
                this.meetother++;
                this.meetotherAgg++;

                // if we cooperate with different colors then reduce our PTR and increase our neighbors
                if (agent.getCooperateWithDiff()) {
                    this.coopother++;
                    this.coopotherAgg++;

                    agent.setPtr(agent.getPtr() - costOfGiving);
                    neighbouringAgent.setPtr(neighbouringAgent.getPtr() + gainOfReceiving);
                }
                else {
                    this.defother++;
                    this.defotherAgg++;
                }
            }
        }
    }

    // use PTR to determine if the agent gets to reproduce
    private void reproduce(Agent agent, Coord coord) {
        // if a random variable is less than the PTR the agent can reproduce
        if (rand.nextDouble() < agent.getPtr()) {
            Coord[] neighbourCoords = grid.getRandomizedNeighbourCoordsAt(coord);

            for (int i=0; i<neighbourCoords.length; i++) {
                Coord neighbourCoord = neighbourCoords[i];

                // if the location exists hatch a copy of the current turtle in the new location
                if (grid.isEmptyAt(neighbourCoord)) {
                    Agent childAgent = agent.clone();
                    debugPrint("reproduce " + childAgent + " " + neighbourCoord);

                    mutate(childAgent, neighbourCoord);
                    grid.setAgentAt(childAgent, neighbourCoord);
                    break;
                }
            }
        }
    }

    // modify the children of agents according to the mutation rate
    private void mutate(Agent agent, Coord coord) {
        // mutate the color
        if (rand.nextDouble() < mutationRate) {
            int oldColour = agent.getColour();

            while (agent.getColour() == oldColour) {
                agent.setColour(getRandomColour());
                debugPrint(agent.getColour() + " " + oldColour);
            }

            debugPrint("mutate colour " + agent + " " + coord + " oldColour=" + oldColour);
        }

        // mutate the strategy flags
        if (rand.nextDouble() < mutationRate) {
            agent.setCooperateWithSame(!agent.getCooperateWithSame());
            debugPrint("mutate same " + agent + " " + coord);
        }

        if (rand.nextDouble() < mutationRate) {
            agent.setCooperateWithDiff(!agent.getCooperateWithDiff());
            debugPrint("mutate diff " + agent + " " + coord);
        }
    }

    // check to see if a random variable is less than the death rate for each agent
    private void death(Agent agent, Coord coord) {
        if (rand.nextDouble() < deathRate) {
            grid.setAgentAt(null, coord);
            debugPrint("death " + coord);
        }
    }

    // this routine calculates a moving average of some stats over the last 100 ticks
    private void updateStats() {
        shorten(last100dd, countAgentsWith(false, false));
        shorten(last100cc, countAgentsWith(true, true));
        shorten(last100cd, countAgentsWith(true, false));
        shorten(last100dc, countAgentsWith(false, true));
        shorten(last100coopown, coopown);
        shorten(last100defother, defother);
        shorten(last100meetown, meetown);
        shorten(last100coop, (coopown + coopother));
        shorten(last100meet, meet);
        shorten(last100meetother, meetother);
    }

    // this is used to keep all of the last100 lists the right length
    private void shorten(ArrayList<Integer> list, int insertValue) {
        list.add(insertValue);
        if (list.size() > 100) {
            list.removeFirst();
        }
    }

    private int countAgentsWith(boolean same, boolean diff) {
        int[] count = { 0 }; // using an array so the lambda can access the local variable

        grid.askAgents((agent, coord) -> {
            if (agent.getCooperateWithSame() == same && agent.getCooperateWithDiff() == diff) {
                count[0]++;
            }
        });

        return count[0];
    }

    private double meetownPercent() {
        return meetown / Math.max(1.0, meet * 1.0);
    }

    private double meetownAggPercent() {
        return meetownAgg / Math.max(1.0, meetAgg * 1.0);
    }
        
    private double coopownPercent() {
        return coopown / Math.max(1.0, meetown * 1.0);
    }

    private double coopownAggPercent() {
        return coopownAgg / Math.max(1.0, meetownAgg * 1.0);
    }
    
    private double defotherPercent() {
        return defother / Math.max(1.0, meetother * 1.0);
    }
    
    private double defotherAggPercent() {
        return defotherAgg / Math.max(1.0, meetotherAgg * 1.0);
    }
    
    private double consistEthnoPercent() {
        return (defother + coopown) / (Math.max(1.0, meet * 1.0));
    }

    private double consistEthnoAggPercent() {
        return (defotherAgg + coopownAgg) / (Math.max(1.0, meetAgg * 1.0));
    }

    private double coopPercent() {
        return (coopown + coopother) / (Math.max(1.0, meet * 1.0));
    }

    private double coopAggPercent() {
        return (coopownAgg + coopotherAgg) / (Math.max(1.0, meetAgg * 1.0));
    }

    private double ccCount() {
        return sumList(last100cc) / Math.max(1.0, last100cc.size() * 1.0);
    }

    private double cdCount() {
        return sumList(last100cd) / Math.max(1.0, last100cd.size() * 1.0);
    }

    private double dcCount() {
        return sumList(last100dc) / Math.max(1.0, last100dc.size() * 1.0);
    }

    private double ddCount() {
        return sumList(last100dd) / Math.max(1.0, last100dd.size() * 1.0);
    }

    private double ccPercent() {
        return ccCount() / (Math.max(1.0, ccCount() + cdCount() + dcCount() + ddCount() * 1.0));
    }

    private double cdPercent() {
        return cdCount() / (Math.max(1.0, ccCount() + cdCount() + dcCount() + ddCount() * 1.0));
    }

    private double dcPercent() {
        return dcCount() / (Math.max(1.0, ccCount() + cdCount() + dcCount() + ddCount() * 1.0));
    }

    private double ddPercent() {
        return ddCount() / (Math.max(1.0, ccCount() + cdCount() + dcCount() + ddCount() * 1.0));
    }

    private double last100coopownPercent() {
        return sumList(last100coopown) / Math.max(1.0, sumList(last100meetown) * 1.0);
    }

    private double last100defotherPercent() {
        return sumList(last100defother) / Math.max(1.0, sumList(last100meetother) * 1.0);
    }

    private double last100consistEthnoPercent() {
        return (sumList(last100defother) + sumList(last100coopown)) / Math.max(1.0, sumList(last100meet) * 1.0);
    }

    private double last100meetownPercent() {
        return sumList(last100meetown) / Math.max(1.0, sumList(last100meet) * 1.0);
    }

    private double last100coopPercent() {
        return sumList(last100coop) / Math.max(1.0, sumList(last100meet) * 1.0);
    }

    private int sumList(ArrayList<Integer> list) {
        return list.stream().reduce((a, b) -> a + b).get();
    }

    private RunStats getRunStats() {
        RunStats rs = new RunStats();
        rs.runNumber = this.runNumber;
        rs.gainOfReceiving = this.gainOfReceiving;
        rs.initialPtr = this.initialPtr;
        rs.immigrantsPerDay = this.immigrantsPerDay;
        rs.immigrantChanceCooperateWithSame = this.immigrantChanceCooperateWithSame;
        rs.mutationRate = this.mutationRate;
        rs.costOfGiving = this.costOfGiving;
        rs.immigrantChanceCooperateWithDifferent = this.immigrantChanceCooperateWithDiff;
        rs.deathRate = this.deathRate;
        rs.maxPxcor = this.numCols; 
        rs.maxPycor = this.numRows;
        rs.step = this.ticksToRun;
        rs.coopownPercent = coopownPercent();
        rs.defotherPercent = defotherPercent();
        rs.consistEthnoPercent = consistEthnoPercent();
        rs.meetownPercent = meetownPercent();
        rs.coopPercent = coopPercent(); 
        rs.last100coopownPercent = last100coopownPercent();
        rs.last100defotherPercent = last100defotherPercent();
        rs.last100consistEthnoPercent = last100consistEthnoPercent();
        rs.last100meetownPercent = last100meetownPercent();
        rs.last100coopPercent = last100coopPercent();
        rs.ccPercent = ccPercent();
        rs.cdPercent = cdPercent();
        rs.dcPercent = dcPercent();
        rs.ddPercent = ddPercent();

        return rs;
    }

    private void debugPrint(String s) {
        if (DEBUG_PRINT) System.out.println(s);
    }
}
