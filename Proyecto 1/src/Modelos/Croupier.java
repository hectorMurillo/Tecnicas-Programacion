package Modelos;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Croupier extends  Jugador {
    public Croupier(){
        this.setNombre("Croupier");
        Baraja.crearBaraja();
    }
    //MÉTODO QUE BARAJEA LAS CARTAS OBTENIDAS COMO MAZO PARA EL JUEGO
    public void barajear(){
        final int numeroCartas = Baraja.cartas.size();
        ArrayList<Carta> cartasBarajeadas = new ArrayList<Carta>();
        Random rn = new Random();
        for(int i=0 ; i<numeroCartas ; i++){
            int posicion = rn.nextInt(Baraja.cartas.size());
            cartasBarajeadas.add(Baraja.cartas.get(posicion));
            Baraja.cartas.remove(posicion);
        }
        Baraja.cartas = cartasBarajeadas;
    }
    //MÉTODO QUE DA LAS PRIMERAS DOS CARTAS A LOS JUGADORES Y 2 AL CROUPIER
    public void repartoInicial(Jugador[] jugadores){
        for(int i=0 ; i < 2 ; i++){
            for(int j=0; j<jugadores.length ; j++){
                jugadores[j].mano.add(obtenerCartaARepartir());
                if(j == jugadores.length-1)
                    this.mano.add(obtenerCartaARepartir());
            }
        }
    }
    //MÉTODO QUE SACA UNA CARTA DE LA BARAJA
    public Carta obtenerCartaARepartir(){
        Random rnd = new Random();
        Carta carta = new Carta();
        int indiceCarta = rnd.nextInt(Baraja.cartas.size());
        carta = Baraja.cartas.get(indiceCarta);
        Baraja.cartas.remove(indiceCarta);
        return carta;
    }
    //MÉTODO QUE VÁLIDA SI UN JUGADOR PUEDE SEGUIR EN EL JUEGO O YA PERDIÓ
    public Boolean jugadorPuedeSeguir(Jugador jugador){
        boolean puedeSeguir = false;
        this.evaluarAs(jugador);
        puedeSeguir = jugador.obtenerTotalPuntos() < 21 ? true : false;
        if(!puedeSeguir) {
            if(jugador.obtenerTotalPuntos() == 21){
                jugador.setEstatus(EstatusJugador.PLANTADO);
            }else{
                jugador.setEstatus(EstatusJugador.PERDIDO);
                System.out.println(jugador.getNombre()+" HA PERDIDO CON "+jugador.obtenerTotalPuntos()+" PUNTOS");
            }
        }
        return puedeSeguir;
    }

    //MÉTODO QUE VALIDA LA JUGADA CONTRA EL CROUPIER.
    public String validarJugada(Jugador jugador) {
        int sumaPuntosJugador=0;
        int sumaPuntosCroupier=0;
        String msjARegresar = "";
        //Si el jugador está plantado y el croupier está jugando
        if(jugador.getEstatus() == EstatusJugador.PLANTADO && this.getEstatus() == EstatusJugador.JUGANDO){
            for (Carta carta:jugador.getMano()) {
                sumaPuntosJugador += carta.valor;
            }
            for(Carta carta: this.getMano()){
                sumaPuntosCroupier += carta.valor;
            }
            if(sumaPuntosCroupier == sumaPuntosJugador){
                //Sacar si tienen 21 pues directo lo manda sino sacar otra carta
                if(sumaPuntosCroupier == 21){
                    msjARegresar = "Empate";
                }
                else{
                    this.mano.add(obtenerCartaARepartir());
                    this.validarJugada(this);
                    msjARegresar = "Empate";
                }
            }else if(sumaPuntosCroupier > sumaPuntosJugador && sumaPuntosCroupier <= 21){
                msjARegresar = "Gana Croupier";
            }//ELSE ES PORQUE EL CROUPIER VA A PERDER
            else{
                msjARegresar = "Pierde Croupier";
            }
        }else if(jugador.getEstatus() == EstatusJugador.PERDIDO){
            msjARegresar =  "Gana Croupier";
        }else{
            msjARegresar = "Pierde Croupier";
        }
        return msjARegresar;
    }
    //MÉTODO QUE VÁLIDA SI
    public Boolean tieneBlackJack(Jugador jugador) {
        if(this.tieneVeintyUno(jugador) && jugador.getMano().size() == 2){
            return true;
        }else{
            return false;
        }
    }
    //MÉTODO QUE VALIDA SI EL TOTAL DE PUNTOS ES = A 21 Y CAMBIA ESTATUS A PLANTADO
    public Boolean tieneVeintyUno(Jugador jugador) {
        this.evaluarAs(jugador);
        if(jugador.obtenerTotalPuntos() == 21){
            jugador.setEstatus(EstatusJugador.PLANTADO);
            return true;
        }else{
            return false;
        }
    }

    /*MÉTODO QUE PREGUNTA SI EL JUGADOR QUIERE OTRA CARTA, Y
    EN EL CASO DE CROUPIER TOMA UN SI POR DEFAULT*/
    public Boolean otraCarta(Jugador jugador) {
        Scanner scn = new Scanner(System.in);
        String resp = "";
        boolean quiereMas = false;
        do{
            for (Carta carta:jugador.getMano()) {
                if(carta.valor == 1 || carta.valor == 11) {
                    if(jugador.obtenerTotalPuntos() == 21){
                        continue;
                    }else if(jugador.obtenerTotalPuntos() < 21){
                        carta.valor = 11;
                    }else{
                        carta.valor =1;
                    }
                }
            }
            if(jugador instanceof Croupier){
                resp="s";
            }else{
                if(jugador.obtenerTotalPuntos()>=21){
                    break;
                }else{
                    System.out.println("#####TURNO DE "+jugador.getNombre().toUpperCase()+"#####");
                    System.out.println("¿Quiere otra carta?, tienes "+jugador.obtenerTotalPuntos()+" puntos (Respuestas: Si o No)");
                    resp = scn.next();
                }
            }
            quiereMas = resp.toLowerCase().charAt(0) == 's' ? true : false;
        }while ((resp.toLowerCase()).charAt(0) != 's' &&  (resp.toLowerCase()).charAt(0) != 'n');
        return quiereMas;
    }
    //VERIFICA SI UN JUGADOR TIENE AS Y SI ES ASÍ BUSCA LA MEJOR FORMA DE APLICARLE EL VALOR (11 U 1)
    public void evaluarAs(Jugador jugador) {
        int sumaPuntos = 0;
        int posicionAsEnMano = 0;
        int idx = 0;
        for (Carta carta: jugador.mano) {
            idx++;
            sumaPuntos += carta.valor;
            if(carta.valor == 1 || carta.valor == 11){
                posicionAsEnMano = idx;
            }
        }
        if(posicionAsEnMano > 0){
            if(jugador.obtenerTotalPuntos()>21){
                jugador.mano.get(posicionAsEnMano-1).setValor(1);
            }else{
                jugador.mano.get(posicionAsEnMano-1).setValor(11);
            }
        }
    }
}
