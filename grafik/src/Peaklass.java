import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Peaklass {
    private static ArrayList<ArrayList<Vahetus>> graafikud = new ArrayList<>();
    private static ArrayList<ArrayList<Isik>> tootajad = new ArrayList<>();


    public static void generate(int index, int workerIndex, ArrayList<Vahetus> vahetused, ArrayList<Isik> workers) {
        if (graafikud.size() > 0) {
            return;
        }
        if (vahetused.size() == index) {
            ArrayList<Vahetus> lisand = new ArrayList<>();
            for (Vahetus vahetus : vahetused) {
                lisand.add(new Vahetus(vahetus.getDay(), vahetus.getAlgus(), vahetus.getKestvus(), vahetus.getTootaja()));
            }
            ArrayList<Isik> workerLisand = new ArrayList<>();
            for (Isik worker : workers) {
                workerLisand.add(new Isik(worker.getSurname(), worker.getSoovitunnid(), worker.getTunnid()));
            }
            tootajad.add(workerLisand);
            graafikud.add(lisand);
            return;
        }
        int placeholder;
        Vahetus vahetus = vahetused.get(index);
        boolean hommik = vahetus.getAlgus() <= 14;
        for (Isik worker : workers) {
            if (hommik) {
                placeholder = worker.getViimanehommik();
            } else {
                placeholder = worker.getViimaneohtu();
            }
            //tunnid pole veel tais
            if (worker.getTunnid() + vahetus.getKestvus() < worker.getSoovitunnid()) {
                // pole hommikuvahetus voi siis pole soovihommik
                if (!hommik || Arrays.stream(worker.getSoovihommikud()).noneMatch(i -> i == vahetus.getDay())) {
                    // pole ohtuvahetus voi siis pole sooviohtu
                    if (hommik || Arrays.stream(worker.getSoovihommikud()).noneMatch(i -> i == vahetus.getDay())) {
                        // pole juba sellel paeval tool
                        if (worker.getViimanehommik() != vahetus.getDay() && worker.getViimaneohtu() != vahetus.getDay()) {
                            //
                            if (!(hommik && worker.getViimaneohtu() == vahetus.getDay() - 1)) {
                                vahetused.get(index).setTootaja(worker);
                                worker.setTunnid(worker.getTunnid() + vahetus.getKestvus());
                                if (hommik) {
                                    worker.setViimanehommik(vahetus.getDay());
                                } else {
                                    worker.setViimaneohtu(vahetus.getDay());
                                }
                                generate(index + 1, workerIndex, vahetused, workers);

                                worker.setTunnid(worker.getTunnid() - vahetus.getKestvus());
                                if (hommik) {
                                    worker.setViimanehommik(placeholder);
                                } else {
                                    worker.setViimaneohtu(placeholder);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) {

        // loeme sisse tootajate andmed
        ArrayList<Isik> workers = new ArrayList();
        try {
            File tootajad = new File("tootajad.txt");
            Scanner myReader = new Scanner(tootajad);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(";");
                int[] soovihommikud = Stream.of(data[1].split(",")).mapToInt(Integer::parseInt).toArray();
                int[] sooviohtud = Stream.of(data[2].split(",")).mapToInt(Integer::parseInt).toArray();
                String[] muu = data[0].split(" ");
                workers.add(new Isik(muu[0], Integer.parseInt(muu[1]), soovihommikud, sooviohtud));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        // loome vahetused
        ArrayList<Vahetus> vahetused = new ArrayList<Vahetus>();
        int i = 1;
        while (i < 8) {
            switch (i % 7) {
                case 2:
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i,18,7));
                    vahetused.add(new Vahetus(i,18,7));
                    vahetused.add(new Vahetus(i,17,6));
                    break;
                case 3:
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 18, 6));
                    vahetused.add(new Vahetus(i,18,6));
                    break;
                case 4:
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i,18,8));
                    vahetused.add(new Vahetus(i,18,8));
                    vahetused.add(new Vahetus(i,18,5));
                    break;
                case 5:
                    vahetused.add(new Vahetus(i, 17, 10));
                    vahetused.add(new Vahetus(i, 15, 5));
                    vahetused.add(new Vahetus(i, 17, 10));
                    vahetused.add(new Vahetus(i,10,10));
                    vahetused.add(new Vahetus(i,19,9));
                    vahetused.add(new Vahetus(i,20,8));
                    vahetused.add(new Vahetus(i,21,8));
                    vahetused.add(new Vahetus(i,18,10));
                    vahetused.add(new Vahetus(i,10,10));
                    break;
                case 6:
                    vahetused.add(new Vahetus(i, 17, 10));
                    vahetused.add(new Vahetus(i, 15, 5));
                    vahetused.add(new Vahetus(i,17,10));
                    vahetused.add(new Vahetus(i,10,10));
                    vahetused.add(new Vahetus(i,19,9));
                    vahetused.add(new Vahetus(i,20,8));
                    vahetused.add(new Vahetus(i,21,8));
                    vahetused.add(new Vahetus(i,18,10));
                    vahetused.add(new Vahetus(i,10,10));
                    break;
            }
            i++;
        }

        generate(0,0, vahetused, workers);
        for (Vahetus v : graafikud.get(0)) {
            System.out.println(v.getDay() + "/ algus : " + v.getAlgus() + "/ kestvus:" + v.getKestvus() + "/ teeb: " + v.getTootaja().getSurname());
        }
        for (Isik is : tootajad.get(0)) {
            System.out.println(is.getSurname() + ": " + is.getTunnid() + " " + is.getSoovitunnid());
        }
    }
}
