package Modelos;

import java.util.ArrayList;

public class Jugador {
    private String nombre;
    ArrayList<Carta> mano  = new ArrayList<Carta>();
    public Jugador(){
        //defaul public
        nombre="";
    }
    public Jugador(String nombre){
        this.setNombre(nombre);
    }
    private EstatusJugador estatus = EstatusJugador.JUGANDO;

    public EstatusJugador getEstatus() {
        return estatus;
    }

    public void setEstatus(EstatusJugador estatus) {
        this.estatus = estatus;
    }

    public String getNombre() {
        return nombre.toUpperCase();
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Carta> getMano() {
        return mano;
    }

    public void setMano(ArrayList<Carta> mano) {
        this.mano = mano;
    }

    public int obtenerTotalPuntos(){
        int sumaTotal = 0;
        for (Carta carta: this.getMano()) {
            sumaTotal += carta.getValor();
        }
        return  sumaTotal;
    }
}
