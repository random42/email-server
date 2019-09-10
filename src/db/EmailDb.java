package db;

import models.*;

import java.io.*;
import java.util.*;

public class EmailDb {
    private static final String path = "data/emails";
    private static final File file = new File(path);
    private static EmailDb instance;

    private ObjectOutputStream out;
    private Map<String,EmailAccount> accounts;

    public static EmailDb getInstance() {
        if (instance == null)
            instance = new EmailDb();
        return instance;
    }

    private boolean isInitialized() {
        return file.exists() && file.isFile();
    }

    private void loadDataFromFile() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            try {
                Object o = in.readObject();
                while (o != null) {
                    Email e = (Email)o;
                    addEmailToAccounts(e);
                    o = in.readObject();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if (!isInitialized()) {
            File dir = file.getParentFile();
            deleteDir(dir);
            dir.mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        accounts = Collections.synchronizedMap(new HashMap<>());
        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadDataFromFile();
    }

    private EmailDb() {
    }

    private synchronized void addEmailToAccounts(Email e) {
        for (String r : e.getReceivers()) {
            if (!accounts.containsKey(r)) {
                accounts.put(r, new EmailAccount(r));
            }
            EmailAccount account = accounts.get(r);
            account.addEmail(e);
        }
    }

    public synchronized void saveEmail(Email e) {
        addEmailToAccounts(e);
        try {
            out.writeObject(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public synchronized List<Email> getUserEmailsAfter(String user, Date last) {
        List<Email> req = new LinkedList<>();
        EmailAccount a = getAccount(user);
        if (a != null) {
            List<Email> emails = a.getInbox();
            for (Email e : emails) {
                if (e.getDate().compareTo(last) > 0) {
                    req.add(e);
                }
            }
        }
        return req;
    }

    private synchronized EmailAccount getAccount(String user) {
        return accounts.get(user);
    }

    // delete a non-empty directory
    private static void deleteDir(File dir) {
        if (dir.isFile()) {
            dir.delete();
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDir(f);
            }
        }
        dir.delete();
    }
}
