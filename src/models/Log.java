package models;

import java.util.*;

public class Log extends Observable {

    private List<String> logs;

    private String last;

    public Log() {
        logs = Collections.synchronizedList(new LinkedList<>());
    }

    public List<String> getAll() { return logs; }

    public void add(String log) {
        logs.add(log);
        last = log;
        notifyObservers();
    }

    public String getLast() {
        return last;
    }

    @Override
    public void notifyObservers(){
        setChanged();
        super.notifyObservers();
    }

}
