package Modelos;

import javax.swing.text.html.parser.Parser;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Juego {

    Integer numeroJugadores = 0;
    ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    Croupier croupier = new Croupier();
    Scanner scn = new Scanner(System.in);
    public Juego(){
        this.iniciarJuego();
    }

    //Método para solicitar cantidad de jugadores.
    public void iniciarJuego(){
        System.out.print("*************BIENVENIDOS A BLACKJACK************ \n");
        int numeroJugadores = 0;
        do{
            System.out.print("¿Cuántos jugadores participan (1-6)?");
            try{
                scn = new Scanner(System.in);
                numeroJugadores = scn.nextInt();
            }
            catch(InputMismatchException ex) {
                System.out.println("***Atención:Utilice números, para específicar su respuesta***");
                numeroJugadores = 0;
            }
        }while (numeroJugadores < 1 || numeroJugadores > 6);

        jugar(numeroJugadores);
    }

    //MÉTODO QUE SOLICITA LOS DATOS DE LOS JUGADORES, LLEVA EL FLUJO DEL JUEGO Y SE TOMA EN CUENTA LAS VALIDACIONES DE LAS REGLAS
    private void jugar(int numeroJugadores) {
        //PIDE DATOS DE JUGADORES
        Jugador jugadores[] = new Jugador[numeroJugadores];
        for(int i=0 ; i<jugadores.length ; i++){
            System.out.println("Nombre del jugador "+(i+1));
            jugadores[i] = new Jugador(scn.next());
        }
        Croupier croupier = new Croupier();
        System.out.println("***************INICIA JUEGO*********");
        croupier.barajear();
        croupier.repartoInicial(jugadores);
        //*************IMPRESION DE LOS JUEGOS DE CADA UNO DE LOS JUGADORES
        for (int i =0;i<jugadores.length;i++){
            System.out.print(jugadores[i].getNombre()+" Tiene "
                    +jugadores[i].getMano().get(0).getNombreCarta()+" y "+jugadores[i].getMano().get(1).getNombreCarta()+"\n");
        }
        //**************IMPRESION DEL JUEGO DE CROUPIER
        System.out.print("CROUPIER tiene: "+croupier.getMano().get(0).getNombreCarta()+" y "+croupier.getMano().get(1).getNombreCarta());
        System.out.print("\n");
        //EN ESTE MÉTODO EL CLOUPIER VALIDA LAS MANOS DE CADA JUGADOR Y TOMA DECISIONES.
        for(int i=0 ; i<jugadores.length ; i++){
            System.out.println("*******CROUPIER DICE/PREGUNTA:********");
            evaluarAs(jugadores[i]);
            if(croupier.tieneBlackJack(jugadores[i])){
                System.out.println(jugadores[i].getNombre()+" Tiene Blac-Jack!");
            }else{
                if(jugadores[i].getEstatus() == EstatusJugador.JUGANDO){
                    if(croupier.jugadorPuedeSeguir(jugadores[i])) {
                        evaluarAs(jugadores[i]);
                        if(jugadores[i].obtenerTotalPuntos() == 21){
                              croupier.validarJugada(jugadores[i]);
                        }else{
                            boolean resp = false;
                            do{
                                evaluarAs(jugadores[i]);
                                if(croupier.tieneVeintyUno(jugadores[i])){
                                    System.out.println(jugadores[i].getNombre()+" Tiene 21 puntos!");
                                    break;
                                }else if(croupier.otraCarta(jugadores[i])){
                                    System.out.println("***ATENCIÓN**");
                                    System.out.println("EL JUGADOR "+jugadores[i].getNombre()+" TIENE EL JUEGO \n");
                                    Carta cartaRecibo = croupier.obtenerCartaARepartir();
                                    jugadores[i].mano.add(cartaRecibo);
                                    for (Carta carta:jugadores[i].getMano()) {
                                        System.out.println(carta.getNombreCarta());
                                    }
                                }else{
                                    jugadores[i].setEstatus(EstatusJugador.PLANTADO);
                                    break;
                                }
                            }while (croupier.jugadorPuedeSeguir(jugadores[i]));
                        }
                    }
                }
            }
            //CUANDO SEA LA ULTIMA CARTA CHECAR CONTRA QUIEN GANÓ O PERDIÓ EL CROUPIER Y MUESTRA LOS RESULTADOS
            if(i+1  == jugadores.length){
                System.out.println("-------TABLA DE RESULTADOS-------");
                evaluarAs(croupier);
                while(croupier.obtenerTotalPuntos()<17 && !hayJugadoresJugando(jugadores)){
                    croupier.otraCarta(croupier);
                    Carta cartaRecibo = croupier.obtenerCartaARepartir();
                    croupier.mano.add(cartaRecibo);
                };
                System.out.println("Puntos del croupier "+croupier.obtenerTotalPuntos());
                for(int j=0;j<jugadores.length;j++){
                    if(jugadores[j].obtenerTotalPuntos()<=21){
                        System.out.println(croupier.validarJugada(jugadores[j])+" con "+jugadores[j].getNombre().toUpperCase());
                    }else if(jugadores[j].obtenerTotalPuntos()>21){
                        System.out.println("Gana Croupier con "+jugadores[j].getNombre().toUpperCase());
                    }else{
                        if(croupier.obtenerTotalPuntos()<=21)
                        {
                            System.out.println("Gana Croupier con "+jugadores[j].getNombre().toUpperCase());
                        }
                        else{
                            System.out.println("Pierde Croupier con "+jugadores[j].getNombre().toUpperCase());
                        }
                    }
                }
            }
        }
    }

    private boolean hayJugadoresJugando(Jugador [] jugadores) {
        boolean hayJugando = false;
        int idx = 0;
        for(int i = 0; i<jugadores.length;i++){
            if(jugadores[i].getEstatus() == EstatusJugador.PERDIDO)
            {
                idx++;
            }
        }
        if(idx == jugadores.length){
            hayJugando = false;
        }else{
            hayJugando = true;
        }
        return hayJugando;
    }

    //VERIFICA SI UN JUGADOR TIENE AS Y SI ES ASÍ BUSCA LA MEJOR FORMA DE APLICARLE EL VALOR (11 U 1)
    private void evaluarAs(Jugador jugador) {
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

    public int getNumeroJugadores() {
        return numeroJugadores;
    }
    public void setNumeroJugadores(Integer numeroJugadores) {
        this.numeroJugadores = numeroJugadores;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }
    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Croupier getCroupier() {
        return croupier;
    }
    public void setCroupier(Croupier croupier) {
        this.croupier = croupier;
    }

}
