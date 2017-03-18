package de.sebpas.replay.filesystem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileManager {
    private String fileContent = "";

    public FileManager() {

    }

    public synchronized boolean save() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YY-MM-D-H-m-s");
        File file = new File("plugins/Replays/" + format.format(date) + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        String[] content = fileContent.split("§§§");
        for (String s : content) {
            File f = new File("plugins/Replays/" + format.format(date) + "/", s);
            Integer i = 0;
            while (f.exists()) {
                f = new File("plugins/Replays/" + format.format(date) + "/", s + i);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public synchronized List<String> readFile(String name) {
        List<String> rtn = new ArrayList<String>();
        File f = new File("plugins/Replays/" + name + "/");
        if (!f.exists()) {
            f.mkdirs();
        }
        for (File ff : f.listFiles()) {
            String[] n = ff.getName().split("");
            try {
                int i = Integer.parseInt(n[n.length - 1]);
                String con = "";
                int size = 0;
                for (String s : n) {
                    if (size != n.length - 1) {
                        con += s;
                    }
                    size++;
                }
                rtn.add(con);
            } catch (NumberFormatException ex) {
                rtn.add(ff.getName());
            }
        }
        return rtn;
    }

    public void reset() {
        this.fileContent = "";
    }

    public void appendString(String s) {
        fileContent += s + "§§§";
    }
}
