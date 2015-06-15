package com.rzg.zombieland.cliente.meta;

import java.util.ArrayList;
import java.util.List;

import com.rzg.zombieland.comunes.comunicacion.pojo.POJOPartida;

/**
 * Almacena estado que se debe compartir entre pantallas.
 * @author nicolas
 *
 */
public class Estado {

    /**
     * Interfaz para escuchar cambios de estado del lobby.
     * @author nicolas
     *
     */
    public interface EscuchadorEstadoLobby {
        /**
         * Se dispara cuando se cambie el estado del lobby.
         * @param pojo
         */
        public void notificarLobbyActualizado(POJOPartida pojo);
    }
    
    private static Estado instancia;
    
    private POJOPartida estadoLobby;
    
    private String jugador;
    
    private List<EscuchadorEstadoLobby> escuchadores;
    
    public Estado() {
        escuchadores = new ArrayList<EscuchadorEstadoLobby>();
    }
    
    /**
     * @return la instancia de estado.
     */
    public static Estado getInstancia() {
        if (instancia == null)
            instancia = new Estado();
        return instancia;
    }
    
    /**
     * Agrega un escuchador de estado de lobby.
     * @param escuchador
     */
    public void addEscuchador(EscuchadorEstadoLobby escuchador) {
        this.escuchadores.add(escuchador);
    }
    
    /**
     * Establece el estado del lobby a partir de su POJO.
     * @param pojo
     */
    public void setEstadoLobby(POJOPartida pojo) {
        estadoLobby = pojo;
        for (EscuchadorEstadoLobby escuchador : escuchadores)
            escuchador.notificarLobbyActualizado(pojo);
    }
    
    /**
     * Establece el nombre de un jugador.
     * @param nombreJugador
     */
    public void setNombreJugador(String nombreJugador) {
        jugador = nombreJugador;
    }
    
    /**
     * @return el estado del lobby.
     */
    public POJOPartida getEstadoLobby() {
        return estadoLobby;
    }

    /**
     * @return el nombre del jugador actual.
     */
    public String getNombreJugador() {
        return jugador;
    }
}
