import java.util.ArrayList;


public class Isik {
    private final String eesnimi;
    private String perenimi;
    private final int soovitunnid;
    private int tunnid = 0;
    private int viimanehommik = 0;
    private int viimaneohtu = 0;
    private final int[] soovihommikud;
    private final int[] sooviohtud;
    private ArrayList<Vahetus> vahetused = new ArrayList<>();

    public Isik(String surname, int soovitunnid, int[] soovihommikud, int[] sooviohtud) {
        this.eesnimi = surname;
        this.soovitunnid = soovitunnid;
        this.sooviohtud = sooviohtud;
        this.soovihommikud = soovihommikud;
    }

    public Isik(String surname, int soovitunnid, int tunnid, int[] soovihommikud, int[] sooviohtud) {
        this.eesnimi = surname;
        this.soovitunnid = soovitunnid;
        this.tunnid = tunnid;
        this.soovihommikud = soovihommikud;
        this.sooviohtud = sooviohtud;
    }

    public int getSoovitunnid() {
        return soovitunnid;
    }

    public String getEesnimi() {
        return eesnimi;
    }

    public int getTunnid() {
        return tunnid;
    }

    public void setTunnid(int tunnid) {
        this.tunnid = tunnid;
    }

    public int[] getSooviohtud() {
        return sooviohtud;
    }

    public int[] getSoovihommikud() {
        return soovihommikud;
    }

    public int getViimanehommik() {
        return viimanehommik;
    }

    public void setViimanehommik(int viimanehommik) {
        this.viimanehommik = viimanehommik;
    }

    public int getViimaneohtu() {
        return viimaneohtu;
    }

    public void setViimaneohtu(int viimaneohtu) {
        this.viimaneohtu = viimaneohtu;
    }
}