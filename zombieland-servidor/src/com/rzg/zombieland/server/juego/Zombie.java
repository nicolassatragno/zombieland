package com.rzg.zombieland.server.juego;

import com.rzg.zombieland.comunes.misc.Avatar;
import com.rzg.zombieland.comunes.misc.Coordenada;
import com.rzg.zombieland.server.sesion.Jugador;

/**
 * Personaje cuyo �nico deseo en el mundo es comer cerebros. RAWR!
 * 
 * @author nicolas
 *
 */
public class Zombie extends Personaje {

	// TODO definir sprite.
	private final String SPRITE = "zombie.jpg";

	public Zombie(Jugador jugador, Coordenada posicion, Tablero tablero) {
	    super(jugador, posicion, tablero);
    }

	@Override
	public void colisionar(EntidadTablero entidad) {
		if (entidad.esPersonaje()) {
		    Personaje personaje = (Personaje)entidad;
		    if (!personaje.esZombie()) {
				// Cambio al humano por un nuevo zombie.
		        Coordenada posicion = entidad.getPosicion();
		        Zombie zombie = new Zombie(personaje.getJugador(), posicion, getTablero()); 
		        getTablero().remplazarEntidadEn(posicion, zombie);
		        getTablero().addPersonajeConvertido(zombie);
			}
		}
	}

	public boolean esPersonaje() {
		return true;
	}
	
	@Override
	public boolean esZombie() {
	    return true;
	}

    @Override
    public Coordenada[] getRectanguloVision() {
        return new Coordenada[] { new Coordenada(0, 0), getTablero().getEsquinaInferiorDerecha() };
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((SPRITE == null) ? 0 : SPRITE.hashCode());
        return result;
    }

    @Override
    public Avatar getAvatar() {
        return Avatar.ZOMBIE;
    }
}
