public class Vahetus {
    private final int kuupaev;
    private final int algus;
    private final int kestvus;
    private Isik tootaja = null;

    public Vahetus(int day, int algus, int kestvus) {
        this.kuupaev = day;
        this.algus = algus;
        this.kestvus = kestvus;
    }

    public Vahetus(int day, int algus, int kestvus, Isik tootaja) {
        this.kuupaev = day;
        this.algus = algus;
        this.kestvus = kestvus;
        this.tootaja = tootaja;
    }
    public int getKuupaev() {
        return kuupaev;
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
