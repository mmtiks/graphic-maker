public class Vahetus {
    private int day;
    private int algus;
    private int kestvus;
    private Isik tootaja = null;

    public Vahetus(int day, int algus, int kestvus) {
        this.day = day;
        this.algus = algus;
        this.kestvus = kestvus;
    }

    public Vahetus(int day, int algus, int kestvus, Isik tootaja) {
        this.day = day;
        this.algus = algus;
        this.kestvus = kestvus;
        this.tootaja = tootaja;
    }
    public int getDay() {
        return day;
    }
    public int getAlgus() {
        return algus;
    }
    public int getKestvus() {
        return kestvus;
    }
    public Isik getTootaja() {
        return tootaja;
    }
    public void setTootaja(Isik tootaja) {
        this.tootaja = tootaja;
    }

}
