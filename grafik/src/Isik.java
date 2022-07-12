import java.util.ArrayList;


public class Isik {
    private String surname;
    private String famname;
    private String code;
    private int soovitunnid;
    private int tunnid = 0;
    private int viimanehommik = 0;
    private int viimaneohtu = 0;
    private int[] sooviohtud;
    private int[] soovihommikud;
    private ArrayList<Vahetus> vahetused = new ArrayList<>();

    public Isik(String surname, int soovitunnid) {
        this.surname = surname;
        this.soovitunnid = soovitunnid;
    }

    public Isik(String surname, int soovitunnid, int[] sooviohtud, int[] soovihommikud) {
        this.surname = surname;
        this.soovitunnid = soovitunnid;
        this.sooviohtud = sooviohtud;
        this.soovihommikud = soovihommikud;
    }

    public Isik(String surname, int soovitunnid, int tunnid) {
        this.surname = surname;
        this.soovitunnid = soovitunnid;
        this.tunnid = tunnid;
    }

    public int getSoovitunnid() {
        return soovitunnid;
    }

    public String getSurname() {
        return surname;
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