import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.regex.RegularExpression;
import org.apache.xmlbeans.impl.regex.SchemaRegularExpression;

import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static java.sql.Types.BOOLEAN;
import static java.sql.Types.NUMERIC;
import static org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType.FORMULA;
import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;

public class Peaklass {
    private static ArrayList<ArrayList<Vahetus>> graafikud = new ArrayList<>();
    private static ArrayList<ArrayList<Isik>> tootajad = new ArrayList<>();
    private static ArrayList<Isik> abimehed = new ArrayList<Isik>();
    private static ArrayList<Double> puuduTunnid = new ArrayList<>();


    public static void generate(int index, int workerIndex, ArrayList<Vahetus> vahetused, ArrayList<Isik> tootajad) {
        if (graafikud.size() > 0) {
            return;
        }
        if (vahetused.size() == index) {
            System.out.println("tere");
            double lisatunnid = 0;
            ArrayList<Vahetus> lisand = new ArrayList<>();
            for (Vahetus vahetus : vahetused) {
                lisand.add(new Vahetus(vahetus.getKuupaev(), vahetus.getAlgus(), vahetus.getKestvus(), vahetus.getTootaja()));
            }
            ArrayList<Isik> workerLisand = new ArrayList<>();
            for (Isik tootaja : tootajad) {
                workerLisand.add(new Isik(tootaja.getEesnimi(), tootaja.getSoovitunnid(), tootaja.getTunnid(), tootaja.getSoovihommikud(), tootaja.getSooviohtud()));
            }
            for (Isik tootaja : abimehed) {
                lisatunnid += tootaja.getTunnid();
                workerLisand.add(new Isik(tootaja.getEesnimi(), tootaja.getSoovitunnid(), tootaja.getTunnid(), tootaja.getSoovihommikud(), tootaja.getSooviohtud()));
            }
            Peaklass.tootajad.add(workerLisand);
            graafikud.add(lisand);
            puuduTunnid.add(lisatunnid);
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
        for (Isik i : abimehed) {
            if (i.getViimanehommik() != vahetus.getKuupaev() && i.getViimaneohtu() != vahetuseKuupaev) {
                //
                if (!(hommik && i.getViimaneohtu() == vahetuseKuupaev - 1)) {
                    vahetused.get(index).setTootaja(i);
                    i.setTunnid(i.getTunnid() + vahetus.getKestvus());
                    if (hommik) {
                        eelmineVaartus = i.getViimanehommik();
                        i.setViimanehommik(vahetuseKuupaev);
                    } else {
                        eelmineVaartus = i.getViimaneohtu();
                        i.setViimaneohtu(vahetuseKuupaev);
                    }

                    generate(index + 1, workerIndex + 1, vahetused, tootajad);

                    i.setTunnid(i.getTunnid() - vahetus.getKestvus());
                    if (hommik) {
                        i.setViimanehommik(eelmineVaartus);
                    } else {
                        i.setViimaneohtu(eelmineVaartus);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException, ParseException {
        // muudetavad
        int abimeesteArv = 5;
        int kuuPikkus = 31;
        int kuuArv = 8;
        int aasta = 2022;
        //


        long start = System.currentTimeMillis();
        // loeme sisse tootajate andmed
        ArrayList<Isik> workers = new ArrayList();
        try {
            File tootajad = new File("C:\\Users\\mihke\\Documents\\GitHub\\graphic-maker\\tootajad.txt");
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

        int a = 0;
        while (a < abimeesteArv) {
            abimehed.add(new Isik("abimees" + a));
            a++;
        }

        // loome vahetused
        ArrayList<Vahetus> vahetused = new ArrayList<>();
        int i = 1;
        while (i < 31) {
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

        int vahim = 0;

        for (int j = 1; j < puuduTunnid.size(); j++) {
            System.out.println(puuduTunnid.get(j));
            if (puuduTunnid.get(j) < puuduTunnid.get(vahim)) {
                vahim = j;
            }
        }

        for (Vahetus v : graafikud.get(vahim)) {
            System.out.println(v.getKuupaev() + "/ algus : " + v.getAlgus() + "/ kestvus:" + v.getKestvus() + "/ teeb: " + v.getTootaja().getEesnimi() + "/ hommikuvahetus: " +  (v.getAlgus()<=14));
        }
        for (Isik is : tootajad.get(vahim)) {
            System.out.println(is.getEesnimi() + ": " + is.getTunnid() + " " + is.getSoovitunnid());
        }
        System.out.println(puuduTunnid.get(vahim));



        long stop = System.currentTimeMillis();


        System.out.println(stop-start);
//
//        String paevad = "PETKNRL";
//
//        LocalDate localDate = LocalDate.of(aasta, kuuArv, 1);
//        int dayOfWeek = localDate.getDayOfWeek().getValue();
//
//
//        FileInputStream file = new FileInputStream(new File("C:\\Users\\mihke\\Documents\\GitHub\\graphic-maker\\blank.xlsx"));
//        Workbook workbook = new XSSFWorkbook(file);
//
//        Sheet sheet = workbook.getSheetAt(0);
//
//        Iterator<Row> itr = sheet.iterator();    //iterating over excel file
//        while (itr.hasNext()) {
//            Row row = itr.next();
//            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
//            while (cellIterator.hasNext()) {
//                Cell cell = cellIterator.next();
//                switch (cell.getCellType()) {
//                    case Cell.CELL_TYPE_STRING:    //field that represents string cell type
//                        cell.setCellValue("hello");
//                        System.out.print(cell.getStringCellValue() + "\t\t\t");
//                        break;
//                    case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type
//                        System.out.print(cell.getNumericCellValue() + "\t\t\t");
//                        break;
//                    default:
//                }
//            }
//            System.out.println("");
//
//
////            // kirjutame faili
//            File currDir = new File(".");
//            String path = currDir.getAbsolutePath();
//            String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
//
//            FileOutputStream outputStream = new FileOutputStream(fileLocation);
//            workbook.write(outputStream);
//            workbook.close();

//        }
    }
}
