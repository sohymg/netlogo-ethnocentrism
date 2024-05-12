public class Agent {
    private int colour;

    private double ptr;

    private boolean cooperateWithSame;
    private boolean cooperateWithDiff;

    public Agent(int colour, boolean cooperateWithSame, boolean cooperateWithDiff, double ptr) {
        this.colour = colour;
        this.cooperateWithSame = cooperateWithSame;
        this.cooperateWithDiff = cooperateWithDiff;
        this.ptr = ptr;
    }

    public double getPtr() {
        return this.ptr;
    }

    public void setPtr(double ptr) {
        this.ptr = ptr;
    }

    public int getColour() {
        return this.colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public boolean getCooperateWithSame() {
        return this.cooperateWithSame;
    }

    public void setCooperateWithSame(boolean same) {
        this.cooperateWithSame = same;
    }

    public boolean getCooperateWithDiff() {
        return this.cooperateWithDiff;
    }

    public void setCooperateWithDiff(boolean diff) {
        this.cooperateWithDiff = diff;
    }

    public Agent clone() {
        return new Agent(colour, cooperateWithSame, cooperateWithDiff, ptr);
    }

    public String toString() {
        if (cooperateWithSame) {
            return cooperateWithDiff ? colour + "A" : colour + "T";
        }
        else {
            return cooperateWithDiff ? colour + "C" : colour + "G";
        }
    }
}
