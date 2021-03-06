package com.rzg.zombieland.server.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.rzg.zombieland.comunes.comunicacion.EnviaPeticiones;
import com.rzg.zombieland.comunes.comunicacion.HiloEscucha;
import com.rzg.zombieland.comunes.comunicacion.pojo.POJOPartida;
import com.rzg.zombieland.comunes.comunicacion.respuesta.POJOListadoPartidas;
import com.rzg.zombieland.comunes.misc.Log;
import com.rzg.zombieland.comunes.misc.ZombielandException;
import com.rzg.zombieland.server.comunicacion.peticion.PeticionListadoPartidas;
import com.rzg.zombieland.server.interfaz.Principal;
import com.rzg.zombieland.server.meta.Partida.PartidaListener;

/**
 * Almancena la lista de partidas actual.
 * @author nicolas
 *
 */
public class ServicioPartidas implements PartidaListener {

    private static ServicioPartidas instancia;

    private Map<UUID, Partida> partidas;
    
    private ServicioPartidas() {
        partidas = new HashMap<UUID, Partida>();
    }
    
    /**
     * @return la instancia del servicio de partidas.
     */
    public static ServicioPartidas getInstancia() {
        if (instancia == null)
            instancia = new ServicioPartidas();
        return instancia;
    }

    /**
     * A�ade una partida.
     * @param partida
     * @throws ZombielandException 
     */
    public void addPartida(Partida partida) {
        synchronized (partidas) {
            partidas.put(partida.getId(), partida);
        }
        partida.setListener(this);
        notificarClientes();
    }

    /**
     * @return el listado de partidas actual.
     */
    public Collection<Partida> getPartidas() {
        synchronized (partidas) {
            return partidas.values();
        }
    }

    /**
     * Vuela la instancia para tests.
     */
    public static void matarInstancia() {
        instancia = null;
    }

    /**
     * @param fromString
     * @return una partida seg�n su ID.
     */
    public Partida getPartida(UUID id) {
        synchronized (partidas) {
            return partidas.get(id);
        }
    }

    /**
     * Env�a el listado de partidas a trav�s del hilo dado.
     * @param hilo
     */
    public void enviarPartidas(EnviaPeticiones hilo) {
        try {
            if (hilo != null) {
                List<POJOPartida> listado = proyectarPartidas();
                hilo.enviarPeticion(new PeticionListadoPartidas(new POJOListadoPartidas(listado)));
            }
        } catch (ZombielandException e) {
            Log.error("No se pudo enviar actualizaci�n de partida a un hilo");
        }
    }

    private List<POJOPartida> proyectarPartidas() {
        List<POJOPartida> listado = new ArrayList<POJOPartida>();
        synchronized (partidas) {
            for (Partida partidaExistente : partidas.values())
                listado.add(partidaExistente.getPOJO(null));    
        }
        return listado;
    }
    
    /**
     * Le env�a las partidas a todos los clientes.
     */
    public void notificarClientes() {
        if (Principal.getServicioEscucha() != null) {
            List<POJOPartida> partidas = proyectarPartidas();
            for (HiloEscucha hilo : Principal.getServicioEscucha().getHilos())
                try {
                    hilo.enviarPeticion(
                            new PeticionListadoPartidas(new POJOListadoPartidas(partidas)));
                } catch (ZombielandException e) {
                    Log.error("No se pudo enviar actualizaci�n de partida a un hilo");
                }
        }
    }

    @Override
    public void notificarPartidaVacia(Partida partida) {
        synchronized (partidas) {
            partidas.remove(partida.getId());
        }
        notificarClientes();
    }

    /**
     * @return una partida aleatoria a la que el jugador se puede unir.
     */
    public Partida getPartidaAleatoria() {
        synchronized (partidas) {
            List<Partida> partidasValidas = new ArrayList<Partida>();
            for (Partida partida : partidas.values()) {
                if (partida.puedenUnirseJugadores())
                    partidasValidas.add(partida);
            }
            if (partidasValidas.size() == 0)
                return null;
            if (partidasValidas.size() == 1)
                return partidasValidas.get(0);
            int i = (new Random()).nextInt(partidasValidas.size() - 1);
            return partidasValidas.get(i);
        }
    }
}
