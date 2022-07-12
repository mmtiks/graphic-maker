import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Peaklass {
    private static ArrayList<ArrayList<Vahetus>> graafikud = new ArrayList<>();
    private static ArrayList<ArrayList<Isik>> tootajad = new ArrayList<>();


    public static void generate(int index, int workerIndex, ArrayList<Vahetus> vahetused, ArrayList<Isik> tootajad) {
        if (graafikud.size() > 0) {
            return;
        }
        if (vahetused.size() == index) {
            ArrayList<Vahetus> lisand = new ArrayList<>();
            for (Vahetus vahetus : vahetused) {
                lisand.add(new Vahetus(vahetus.getKuupaev(), vahetus.getAlgus(), vahetus.getKestvus(), vahetus.getTootaja()));
            }
            ArrayList<Isik> workerLisand = new ArrayList<>();
            for (Isik tootaja : tootajad) {
                workerLisand.add(new Isik(tootaja.getEesnimi(), tootaja.getSoovitunnid(), tootaja.getTunnid(), tootaja.getSoovihommikud(),tootaja.getSooviohtud()));
            }
            Peaklass.tootajad.add(workerLisand);
            graafikud.add(lisand);
            return;
        }
        int eelmineVaartus;
        Vahetus vahetus = vahetused.get(index);
        int vahetuseKuupaev = vahetus.getKuupaev();
        boolean hommik = vahetus.getAlgus() <= 14;
        for (int k = workerIndex; k < workerIndex + tootajad.size(); k++) {
            Isik tootaja = tootajad.get(k % tootajad.size());
            int tootajaTunnid = tootaja.getTunnid();
            //tunnid pole veel tais
            if (tootajaTunnid + vahetus.getKestvus() < tootaja.getSoovitunnid()) {
                // pole hommikuvahetus voi siis pole soovihommik
                if (!(hommik && Arrays.stream(tootaja.getSoovihommikud()).anyMatch(i -> i == vahetuseKuupaev))) {
                    // pole ohtuvahetus voi siis pole sooviohtu
                    if (!(!hommik && Arrays.stream(tootaja.getSooviohtud()).anyMatch(i -> i == vahetuseKuupaev))) {
                        // pole juba sellel paeval tool
                        if (tootaja.getViimanehommik() != vahetus.getKuupaev() && tootaja.getViimaneohtu() != vahetuseKuupaev) {
                            //
                            if (!(hommik && tootaja.getViimaneohtu() == vahetuseKuupaev - 1)) {
                                vahetused.get(index).setTootaja(tootaja);
                                tootaja.setTunnid(tootajaTunnid + vahetus.getKestvus());
                                if (hommik) {
                                    eelmineVaartus = tootaja.getViimanehommik();
                                    tootaja.setViimanehommik(vahetuseKuupaev);
                                } else {
                                    eelmineVaartus = tootaja.getViimaneohtu();
                                    tootaja.setViimaneohtu(vahetuseKuupaev);
                                }

                                generate(index + 1, workerIndex + 1, vahetused, tootajad);

                                tootaja.setTunnid(tootaja.getTunnid() - vahetus.getKestvus());
                                if (hommik) {
                                    tootaja.setViimanehommik(eelmineVaartus);
                                } else {
                                    tootaja.setViimaneohtu(eelmineVaartus);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // loeme sisse tootajate andmed
        ArrayList<Isik> workers = new ArrayList();
        try {
            File tootajad = new File("C:\\Users\\Mihkel\\Documents\\GitHub\\graphic-maker\\tootajad.txt");
            Scanner myReader = new Scanner(tootajad);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(";");
                int[] soovihommikud = Stream.of(data[2].split(",")).mapToInt(Integer::parseInt).toArray();
                int[] sooviohtud = Stream.of(data[3].split(",")).mapToInt(Integer::parseInt).toArray();
                workers.add(new Isik(data[0], Integer.parseInt(data[1]), soovihommikud, sooviohtud));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // loome vahetused
        ArrayList<Vahetus> vahetused = new ArrayList<>();
        int i = 1;
        while (i < 29) {
            switch (i % 7) {
                case 2 -> {
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 18, 7));
                    vahetused.add(new Vahetus(i, 18, 7));
                    vahetused.add(new Vahetus(i, 17, 6));
                }
                case 3 -> {
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 18, 6));
                    vahetused.add(new Vahetus(i, 18, 6));
                }
                case 4 -> {
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 10, 8));
                    vahetused.add(new Vahetus(i, 18, 8));
                    vahetused.add(new Vahetus(i, 18, 8));
                    vahetused.add(new Vahetus(i, 18, 5));
                }
                case 5, 6 -> {
                    vahetused.add(new Vahetus(i, 17, 10));
                    vahetused.add(new Vahetus(i, 15, 5));
                    vahetused.add(new Vahetus(i, 17, 10));
                    vahetused.add(new Vahetus(i, 10, 10));
                    vahetused.add(new Vahetus(i, 19, 9));
                    vahetused.add(new Vahetus(i, 20, 8));
                    vahetused.add(new Vahetus(i, 21, 8));
                    vahetused.add(new Vahetus(i, 18, 10));
                    vahetused.add(new Vahetus(i, 10, 10));
                }
            }
            i++;
        }

        generate(0,0, vahetused, workers);
        for (Vahetus v : graafikud.get(0)) {
            System.out.println(v.getKuupaev() + "/ algus : " + v.getAlgus() + "/ kestvus:" + v.getKestvus() + "/ teeb: " + v.getTootaja().getEesnimi() + "/ hommikuvahetus: " +  (v.getAlgus()<=14));
        }
//        for (Isik is : tootajad.get(0)) {
//            System.out.println(is.getSurname() + ": " + is.getTunnid() + " " + is.getSoovitunnid());
//        }
        long stop = System.currentTimeMillis();
        System.out.println(stop-start);
    }
}
