package in.maru.pathify.model;

import java.util.HashMap;


public class Game {
    private Boolean ready;
    private Boolean moved;
    private HashMap<String, String> host;
    private HashMap<String, String> joiner;

    public Game() {
    }

    public Game(HashMap<String, String> host) {
        this.ready = false;
        this.moved = false;
        this.host = host;
        this.joiner = null;
    }

    public Boolean isReady() {
        return ready;
    }

    public Boolean isMoved() {
        return moved;
    }

    public HashMap<String, String> getHost() {
        return host;
    }

    public HashMap<String, String> getJoiner() {
        return joiner;
    }
}
