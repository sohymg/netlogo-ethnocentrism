import java.util.ArrayList;
import java.util.Collections;

public class Grid {
    private int numRows;
    private int numCols;


    private Agent[][] agents;

    private ArrayList<Coord> memonizedCoords;

    interface PatchFunctionInterface {
        public void call(Agent agent, Coord coord);
    }

    interface AgentFunctionInterface {
        public void call(Agent agent, Coord coord);
    }

    public Grid(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;

        this.agents = new Agent[this.numRows][this.numCols];


        this.memonizedCoords = new ArrayList<Coord>();
        for (int r=0; r<numRows; r++) {
            for (int c=0; c<numCols; c++) {
                this.memonizedCoords.add(new Coord(r, c));
            }
        }
    }

    public void setAgentAt(Agent agent, Coord coord) {
        this.agents[coord.getRow()][coord.getCol()] = agent;
    }

    public boolean isEmptyAt(Coord coord) {
        return getAgentAt(coord) == null;
    }

    public Agent getAgentAt(Coord coord) {
        return this.agents[coord.getRow()][coord.getCol()];
    }

    public Agent[] getRandomizedNeighbourAgentsAt(Coord coord) {
        ArrayList<Agent> neighbourAgents = new ArrayList<Agent>();
        Coord[] coords = getRandomizedNeighbourCoordsAt(coord);

        for (int i=0; i<coords.length; i++) {
            int r = coords[i].getRow();
            int c = coords[i].getCol();
            
            if (agents[r][c] != null) {
                neighbourAgents.add(agents[r][c]);
            }
        }
        
        return neighbourAgents.toArray(new Agent[0]);
    }

    public Coord[] getRandomizedNeighbourCoordsAt(Coord coord) {
        int row = coord.getRow();
        int col = coord.getCol();

        ArrayList<Coord> neighbourCoords = new ArrayList<Coord>();
        if (row - 1 >= 0)               neighbourCoords.add(new Coord(row - 1, col));
        if (row + 1 < this.numRows)     neighbourCoords.add(new Coord(row + 1, col));
        if (col - 1 >= 0)               neighbourCoords.add(new Coord(row, col - 1));
        if (col + 1 < this.numCols)     neighbourCoords.add(new Coord(row, col + 1));

        Collections.shuffle(neighbourCoords);

        return neighbourCoords.toArray(new Coord[0]);
    }

    public void askPatches(PatchFunctionInterface fn) {
        Coord[] coords = getRandomizedCoords();

        for (int i=0; i<coords.length; i++) {
            Coord coord = coords[i];
            Agent agent = getAgentAt(coord);

            fn.call(agent, coord);
        }
    }

    public void askAgents(AgentFunctionInterface fn) {
        Coord[] coords = getRandomizedCoords();

        for (int i=0; i<coords.length; i++) {
            Coord coord = coords[i];
            Agent agent = getAgentAt(coord);

            if (agent != null) {
                fn.call(agent, coord);
            }
        }
    }

    public Coord[] getRandomizedCoords() {
        Collections.shuffle(this.memonizedCoords);
        
        return this.memonizedCoords.toArray(new Coord[0]);
    }

    public void print() {
        for (int r=0; r<this.agents.length; r++) {
            for (int c=0; c<this.agents[r].length; c++) {
                Agent agent = this.agents[r][c];

                if (agent == null) {
                    System.out.print(" ");
                }
                else {
                    System.out.print(agent);
                }
            }
            System.out.println();
        }
        System.out.println("---------------");
    }
}
