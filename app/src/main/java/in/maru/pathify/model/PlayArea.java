package in.maru.pathify.model;

import java.util.HashMap;


public class PlayArea {
    private String start;
    private String end;
    private int hostPercentComplete;
    private int joinerPercentComplete;

    public PlayArea() {
    }

    public PlayArea(String start, String end) {
        this.start = start;
        this.end = end;
        this.hostPercentComplete = 0;
        this.joinerPercentComplete = -1;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getHostPercentComplete() {
        return hostPercentComplete;
    }

    public int getJoinerPercentComplete() {
        return joinerPercentComplete;
    }
}
