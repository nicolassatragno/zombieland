package com.rzg.zombieland.comunes.comunicacion.pojo;

import java.util.ArrayList;
import java.util.List;

import com.rzg.zombieland.comunes.comunicacion.respuesta.POJOCreacionPartida;

/**
 * Representa el estado actual de un lobby: cantidad de jugadores, nombres, etc.
 * @author nicolas
 *
 */
public class POJOPartida {
    
    /**
     * El administrador de la partida.
     */
    private String administrador;
    
    /**
     * Listado de nombres de jugadores, incluyendo al administrador.
     */
    private List<String> jugadores;

    /**
     * Listado de nombres de espectadores.
     */
    private List<String> espectadores;
    
    private int cantidadRondas;
    
    private int cantidadMaximaJugadores;
    
    private String nombre;
    
    /**
     * Construye un POJO de lobby.
     * @param administrador - incluye al administrador.
     * @param jugadores
     * @param espectadores
     * @param cantidadRondas 
     * @param cantidadJugadores 
     * @param nombre 
     * @param estado 
     */
    public POJOPartida(String administrador, List<String> jugadores, List<String> espectadores,
                     int cantidadRondas, int cantidadJugadores, String nombre, String estado) {
        this.administrador = administrador;
        this.jugadores = jugadores;
        this.espectadores = espectadores;
        this.cantidadRondas = cantidadRondas;
        this.cantidadMaximaJugadores = cantidadJugadores;
        this.nombre = nombre;
    }
    
    /**
     * Construye un pojo de lobby a partir del pojo de creaci�n de partida.
     * @param pojo
     * @param nombreAdministrador 
     */
    public POJOPartida(POJOCreacionPartida pojo, String nombreAdministrador) {
        administrador = nombreAdministrador;
        jugadores = new ArrayList<String>();
        espectadores = new ArrayList<String>();
        cantidadRondas = pojo.getCantidadRondas();
        cantidadMaximaJugadores = pojo.getCantidadMaximaJugadores();
        nombre = pojo.getNombre();
        jugadores.add(nombreAdministrador);
    }

    /**
     * @return el administrador.
     */
    public String getAdministrador() {
        return administrador;
    }

    /**
     * @return los jugadores.
     */
    public List<String> getJugadores() {
        return jugadores;
    }

    /**
     * @return los espectadores.
     */
    public List<String> getEspectadores() {
        return espectadores;
    }

    /**
     * @return the cantidadRondas
     */
    public int getCantidadRondas() {
        return cantidadRondas;
    }

    /**
     * @return the cantidadJugadores
     */
    public int getCantidadMaximaJugadores() {
        return cantidadMaximaJugadores;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(getClass().isAssignableFrom(obj.getClass())))
            return false;
        POJOPartida otro = (POJOPartida)obj;
        return otro.administrador.equals(administrador) &&
               otro.cantidadMaximaJugadores == cantidadMaximaJugadores &&
               otro.cantidadRondas == cantidadRondas &&
               otro.espectadores.equals(espectadores) &&
               otro.jugadores.equals(jugadores) &&
               otro.nombre.equals(nombre);
    }
    
    @Override
    public String toString() {
        return "POJOPartida [administrador=" + administrador + ", jugadores=" + jugadores
                + ", espectadores=" + espectadores + ", cantidadRondas=" + cantidadRondas
                + ", cantidadMaximaJugadores=" + cantidadMaximaJugadores + ", nombre=" + nombre
                + "]";
    }


}