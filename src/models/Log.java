package models;

import java.util.*;

public class Log extends Observable {

    private List<String> logs;

    private String last;

    public Log() {
        logs = Collections.synchronizedList(new LinkedList<>());
    }

    public List<String> getAll() { return logs; }

    public void add(String msg) {
        logs.add(msg);
        last = msg;
        notifyObservers(msg);
    }

    public String getLast() {
        return last;
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }

}
