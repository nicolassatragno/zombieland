package com.rzg.zombieland.server.interfaz;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import com.rzg.zombieland.comunes.misc.EscuchaLog;
import com.rzg.zombieland.comunes.misc.Log;
import com.rzg.zombieland.comunes.misc.ZombielandException;
import com.rzg.zombieland.server.comunicacion.ServicioEscucha;
import com.rzg.zombieland.server.comunicacion.ServicioJuego;
import com.rzg.zombieland.server.meta.ServicioPartidas;
import com.rzg.zombieland.server.persistencia.HibernateSingleton;
import com.rzg.zombieland.server.sesion.ServicioSesion;

import net.miginfocom.swing.MigLayout;

/**
 * Interfaz principal del servidor.
 * @author nicolas
 *
 */
public class Principal implements EscuchaLog {
	// Constantes de texto de botones.
	private static final String TEXTO_DETENER_SERVIDOR = "Detener servidor";
	private static final String TEXTO_INICIAR_SERVIDOR = "Iniciar servidor";
	
	// Frame principal.
	private JFrame frame;
	
	// Servicio de escucha. Es null mientras no haya arrancado.
	private static ServicioEscucha servicio;
	
	// El nivel de log para mostrar en la interfaz.
	private int nivelLog = Log.DEBUG;
	
	// TextArea que muestra el log.
	private JTextArea log;
	
	// Entrada para el puerto.
	private JTextField puerto;
	
	// Con este bot�n se arranca y detiene el servidor.
	private JButton botonIniciar;
	
	// Scroll para el TextArea de log.
	private JScrollPane scroll;

	/**
	 * Launch the application.
	 * @param args - argumentos de la aplicaci�n.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principal window = new Principal();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Principal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		botonIniciar = new JButton(TEXTO_INICIAR_SERVIDOR);
		botonIniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleServidor();
			}
		});
		
		JComboBox<String> lista = new JComboBox<String>(new String[] {"Debug", "Info", "Error"});
		lista.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> combo = (JComboBox<String>)e.getSource();
				switch ((String)combo.getSelectedItem()) {
					case "Debug":
						nivelLog = Log.DEBUG;
						break;
					case "Info":
						nivelLog = Log.INFO;
						break;
					case "Error":
						nivelLog = Log.ERROR;
						break;
				}
			}
		});
		
		log = new JTextArea();
		log.setEditable(false);
		log.setRows(40);
		log.setColumns(200);
	    DefaultCaret caret = (DefaultCaret)log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	
		Log.agregarEscuchador(this);
		
		scroll = new JScrollPane(log);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		Label titulo = new Label("Servidor Zombieland");
		titulo.setFont(new Font("tahoma", Font.BOLD,20));
		
		puerto = new JTextField("2048");
		puerto.setColumns(10);
		
		frame = new JFrame();
		frame.setTitle("Servidor ZombieLand");
		frame.setBounds(100, 100, 900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(new MigLayout());
		frame.getContentPane().add(titulo, "span 2, wrap, growx");
		frame.getContentPane().add(new Label("Nivel de log"));
		frame.getContentPane().add(lista);
		frame.getContentPane().add(new Label("Puerto"));
		frame.getContentPane().add(puerto, "wrap");
		frame.getContentPane().add(scroll, "wrap, span 4");
		frame.getContentPane().add(botonIniciar, "align center, span 4");
		
		JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Servidor");
        JMenuItem menuSalir = new JMenuItem("Salir");
        menuSalir.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (servicio != null) 
                    toggleServidor();
                frame.dispose();
            }
        });
        menu.add(menuSalir);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
	}
	
	/**
	 * Arranca y detiene al servidor.
	 */
	private synchronized void toggleServidor() {
		if (servicio == null) {
		    botonIniciar.setEnabled(false);
			new Thread() {
                @Override
                public void run() {
                    // Arrancamos el servidor.
                    try {
                        int numeroPuerto = Integer.parseInt(puerto.getText());
                        servicio = new ServicioEscucha(numeroPuerto);
                        servicio.start();
                        puerto.setEditable(false);
                        HibernateSingleton.prepararDB();
                        botonIniciar.setText(TEXTO_DETENER_SERVIDOR);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(frame, "El puerto no es v�lido");
                    } catch (ZombielandException e) {
                        servicio = null;
                    } finally {
                        botonIniciar.setEnabled(true);
                    }
                }
            }.start();
		} else {
			// Detenemos el servidor.
			servicio.cerrar();
			ServicioJuego.getInstancia().matarBucles();
			ServicioPartidas.matarInstancia();
			ServicioSesion.matarInstancia();
			try {
				servicio.join();
				Log.info("Servidor cerrado con �xito");
				servicio = null;
				botonIniciar.setText(TEXTO_INICIAR_SERVIDOR);
				this.puerto.setEditable(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLog(final String mensaje, int nivel) {
		if (nivel >= nivelLog) {
		    EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    log.append(mensaje + "\n");
                }
            });
		}
	}

	/**
	 * @return el servicio de escucha.
	 */
    public static ServicioEscucha getServicioEscucha() {
        return servicio;
    }
}
