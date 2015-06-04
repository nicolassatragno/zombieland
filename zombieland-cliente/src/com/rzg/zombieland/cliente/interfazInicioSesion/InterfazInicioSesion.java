package com.rzg.zombieland.cliente.interfazInicioSesion;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.rzg.zombieland.cliente.Main;

/**
 * Interfaz completa de inicio de sesi�n.
 * @author Manuel
 */

public class InterfazInicioSesion extends JPanel {

	private static final long serialVersionUID = 1L;
	private JFrame frmZombielandV;
	private JTextField textField;
	private JPasswordField passwordField;
	private JTextField userField;
	private JPasswordField passwordField_1;
	private static final int intentos = 0; // Se incrementar� cuando el password sea incorrecto
	private Main main;

	/**
	 * Create the application.
	 */
	public InterfazInicioSesion(Main main) {
	    this.main = main;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(100, 100, 450, 325);
		setLayout(null);
		
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(10, 161, 92, 14);
		add(lblUsuario);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 192, 92, 14);
		add(lblPassword);
		
		JLabel lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		lblError.setBounds(252, 192, 172, 14);
		add(lblError);
		
		JButton btnIngresar = new JButton("Ingresar");
		btnIngresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Aqu� se validar�n los datos (Usuario - Contrase�a)
				// Si son correctos, se enviar� al lobby de partidas.
				// Si son incorrectos, se enviar� un mensaje de error
				// en lblError, y se incrementar� en 1 el contador de 'intentos'.
				// Si 'intentos' = 3, se tomar�n las medidas propuestas
				// de la pregunta y respuesta de seguridad.
			}
		});
		btnIngresar.setBounds(60, 217, 139, 34);
		add(btnIngresar);
		
		userField = new JTextField();
		userField.setBounds(112, 158, 115, 20);
		add(userField);
		userField.setColumns(10);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(112, 189, 115, 20);
		add(passwordField_1);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(InterfazInicioSesion.class.getResource("/com/rzg/zombieland/cliente/interfazInicioSesion/Zombieland.png")));
		lblNewLabel.setBounds(252, 0, 159, 179);
		add(lblNewLabel);
		
		JLabel lblMsg = new JLabel("No tenes un usuario?");
		lblMsg.setBounds(27, 11, 200, 37);
		add(lblMsg);
		
		JButton btnRegistrarse = new JButton("Registrarse");
		btnRegistrarse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
			    main.irARegistro();
			}
		});
		btnRegistrarse.setBounds(98, 69, 116, 30);
		add(btnRegistrarse);
		
		JLabel lblRzg = new JLabel("RZG - 2015");
		lblRzg.setForeground(SystemColor.controlShadow);
		lblRzg.setBounds(369, 248, 110, 14);
		add(lblRzg);
		
		JLabel lblUniteAZombieland = new JLabel("Unite a Zombieland!");
		lblUniteAZombieland.setBounds(27, 30, 154, 37);
		add(lblUniteAZombieland);
		
		JButton btnO = new JButton("Olvid\u00F3 su clave?");
		btnO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Se direccionar� a la pantalla de recuperaci�n de clave
				// haciendo uso de la pregunta de seguridad.
			}
		});
		btnO.setBounds(209, 217, 126, 34);
		add(btnO);
	}
}
