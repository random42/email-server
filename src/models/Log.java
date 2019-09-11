package models;

import java.util.*;

public class Log extends Observable {

    private List<String> logs;

    public Log() {
        logs = Collections.synchronizedList(new LinkedList<>());
    }

    public List<String> get() { return logs; }

    public void add(String log) {
        logs.add(log);
        notifyObservers();
    }

}
