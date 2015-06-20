package com.rzg.zombieland.server.juego;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.rzg.zombieland.comunes.comunicacion.ProyeccionTablero;
import com.rzg.zombieland.comunes.comunicacion.ProyeccionTablero.POJOEntidad;
import com.rzg.zombieland.comunes.misc.Avatar;
import com.rzg.zombieland.comunes.misc.Coordenada;
import com.rzg.zombieland.server.meta.ResultadoRonda;
import com.rzg.zombieland.server.sesion.Jugador;

/**
 * El tablero de juego, que contiene obst�culos, humanos y zombies.
 * 
 * @author Nicolas, Manuel
 *
 */
public class Tablero {
    // El tablero en s�, con todos sus actores. Incluidos obst�culos.
    private EntidadTablero[][] matriz;

    // Jugadores que participan - Humanos solamente.
    private List<Personaje> personajes;

    // Posici�n en el listado de personajes del que se mover� primero.
    private int primerPersonaje;

    // Personaje de la ronda que arranca como zombi.
    private Personaje zombi;

    /**
     * Constructor por defecto. Aqu� se generar�n los obst�culos en forma
     * 'aleatoria'
     */
    public Tablero(int casilleros, List<Jugador> jugadores, Jugador zombi) {
        personajes = new ArrayList<Personaje>();
        Random rnd = new Random(); // Que quede claro que va a ser una cuesti�n
                                   // de suerte
        boolean resuelto = false; // Flag que me indica si ya posicione o no a
                                  // la entidad.

        Coordenada c;

        Zombie personajeZombie = new Zombie(zombi);
        personajes.add(personajeZombie);

        matriz = new EntidadTablero[casilleros][casilleros];
        // Ponemos al zombi - primero le asignamos el nombre.
        personajeZombie.setPosicion(new Coordenada(casilleros / 2, casilleros / 2));
        // Siempre arranca en el medio.
        matriz[casilleros / 2][casilleros / 2] = personajeZombie; // Lo ponemos
                                                                  // en la
                                                                  // matriz.

        // Ponemos los obstaculos. Si la matriz es de 10x10, son 100 casilleros.
        // Con 25 obst�culos estariamos bien -- Ser�a el 25%
        // Con el 30%, usando el algoritmo aleatorio, puede terminar en un bucle
        // infinito. Cosas que pasan.

        // Totalmente aleatorio - Resultado: En 10 corridas,
        // 4 veces encerr� a un jugador.
        // Verificando que en las diagonales tampoco haya obstaculos -
        // Resultado: Luz verde. En 100000 corridas, luz verde.

        for (int i = 0; i < Math.pow(casilleros, 2) * 0.25; i++) {
            resuelto = false;
            while (!resuelto) {
                c = new Coordenada(Math.abs(rnd.nextInt()) % casilleros, Math.abs(rnd.nextInt())
                        % casilleros);
                if (matriz[c.getX()][c.getY()] == null
                        && matriz[c.getX() + 1 >= casilleros ? c.getX() : c.getX() + 1][c.getY() + 1 >= casilleros ? c
                                .getY() : c.getY() + 1] == null
                        && matriz[c.getX() - 1 < 0 ? c.getX() : c.getX() - 1][c.getY() + 1 >= casilleros ? c
                                .getY() : c.getY() + 1] == null
                        && matriz[c.getX() + 1 >= casilleros ? c.getX() : c.getX() + 1][c.getY() - 1 < 0 ? c
                                .getY() : c.getY() - 1] == null
                        && matriz[c.getX() - 1 < 0 ? c.getX() : c.getX() - 1][c.getY() - 1 < 0 ? c
                                .getY() : c.getY() - 1] == null) {
                    matriz[c.getX()][c.getY()] = new Obstaculo(c);
                    resuelto = true;
                }
            }
        }

        // Ponemos a los humanos
        // Los humanos se crean a partir de los nombres de los jugadores.
        for (Jugador jugador : jugadores) {
            resuelto = false;
            while (!resuelto) {
                c = new Coordenada(Math.abs(rnd.nextInt()) % casilleros, Math.abs(rnd.nextInt())
                        % casilleros);
                if (matriz[c.getX()][c.getY()] == null) {
                    Humano h = new Humano(jugador);
                    h.setPosicion(c);
                    matriz[c.getX()][c.getY()] = h;
                    resuelto = true;
                    personajes.add(h);
                }
            }

        }

    }

    /**
     * @param superiorIzquierda
     * @param inferiorDerecha
     * @return la proyecci�n del tablero entre las dos esquinas dadas.
     */
    public ProyeccionTablero getProyeccion(Coordenada superiorIzquierda, Coordenada inferiorDerecha) {
        List<POJOEntidad> entidades = new ArrayList<POJOEntidad>();
        // Recorro mi matriz de entidades en los limites indicados por el
        // metodo.
        for (int i = superiorIzquierda.getX(); i < inferiorDerecha.getX(); i++) {
            for (int j = superiorIzquierda.getY(); j < inferiorDerecha.getY(); i++) {
                if (matriz[i][j] != null) {
                    // Agrego las entidades que encuentre a la lista de la
                    // proyeccion
                    entidades.add(new POJOEntidad("Elemento" + i + j, new Coordenada(i, j), // Cada
                                                                                            // entidad
                                                                                            // ya
                                                                                            // tiene
                                                                                            // su
                                                                                            // posicion
                            Avatar.HOMBRE)); // Ac� iria el avatar
                                             // correspondiente.
                }
            }
        }
        // Devuelvo la proyecci�n. Chiche bomb�n.
        return new ProyeccionTablero(matriz.length, superiorIzquierda, inferiorDerecha, entidades);
    }

    /**
     * Obtiene la entidad por coordenada.
     * 
     * @param coordenada
     * @return la entidad en la coordenada dada, o null si no hay ninguna.
     */
    public EntidadTablero getEntidadEn(Coordenada coordenada) {
        return matriz[coordenada.getX()][coordenada.getY()];
    }

    /**
     * Mueve una entidad.
     * 
     * @param desde
     *            - coordenada donde la entidad original se encuentra.
     * @param hasta
     *            - coordenada de destino. Debe estar vac�a.
     */

    // Se supone que estos datos los validamos antes de mandarselos al tablero.
    // De no ser as�, avisen que tengo que validar todo para no irme del mismo.
    public Coordenada moverEntidad(Coordenada desde, Coordenada hasta) {
        // Primero pregunto si a la posici�n a la cual quiere desplazarse no hay
        // nada
        if (fueraDeLaMatriz(desde)) {
            throw new InvalidParameterException(
                    "La coordenada desde est� fuera de los l�mites de la matriz");
        }
        if (fueraDeLaMatriz(hasta)) {
            // No se mueva nada.
            return desde;
        }
                if (matriz[hasta.getX()][hasta.getY()] == null) {
            matriz[hasta.getX()][hasta.getY()] = matriz[desde.getX()][desde.getY()];
            matriz[desde.getX()][desde.getY()] = null;
            return hasta;
        } // Cambio los valores de la matriz.
          // Si no es null, hay una colisi�n.
        else {
            getEntidadEn(desde).colisionar(getEntidadEn(hasta), matriz);
            return desde;
        }
    }

    /**
     * @param coordenada
     * @return true si la coordenada est� fuera de la matriz, false de lo contrario.
     */
    private boolean fueraDeLaMatriz(Coordenada coordenada) {
        return (coordenada.getX() >= matriz.length || coordenada.getX() < 0 ||
                coordenada.getY() >= matriz.length || coordenada.getY() < 0);
    }

    /**
     * @return el resultado de la partida.
     */
    public ResultadoRonda getResultado() {
        // TODO implementar.
        return null;
    }

    /**
     * Mueve a todos los personajes.
     */
    public void moverTodos() {
        // Ordenamos los personajes de acuerdo al orden en el que realizaron los
        // movimientos.
        personajes.sort(new Comparator<Personaje>() {

            @Override
            public int compare(Personaje p1, Personaje p2) {
                return p1.compareTo(p2);
            }
        });
        for (Personaje personaje : personajes)
            personaje.mover();
    }
}
