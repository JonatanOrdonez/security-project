package logica;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;

public class Servidor {

	private ServerSocket sServer;
	private Socket sClient;
	private java.io.DataInputStream dis;
	private DataOutputStream dos;
	private Utilidades utilidades;
	private Encripcion encripcion;
	private CheckSum checkSum;

	public final static int PUERTO = 3456;

	public Servidor() {
		utilidades = new Utilidades();
		encripcion = new Encripcion();
		checkSum = new CheckSum();
		iniciarServidor();
		diffieHellman();
	}

	/**
	 * Este m�todo inicializa un ServerSocket al cual se env�an los archivos encriptados.
	 * Tambi�n se crea un canal de conexi�n con los clientes que se conectan al servidor por medio de Sockets
	 */
	public void iniciarServidor() {
		try {
			// Se inicializa un ServerSocket donde se recebiran los archivos encriptados
			sServer = new ServerSocket(PUERTO);
			// Mensaje de espera mientras llega una nueva conexi�n al servidor
			System.out.println("Servidor esperando conexiones entrantes...");
			// Una conexi�n nueva ha llegado al servidor y es aceptada en un socket
			sClient = sServer.accept();
			// Se obtienen los datos del cliente a trav�s de un DataInputStream
			dis = new DataInputStream(sClient.getInputStream());
			// Se env�an los datos al cliente a trav�s de un DataOutputStream
			dos = new DataOutputStream(sClient.getOutputStream());
			// Mensaje cuando la conexi�n ha sido aceptada
			System.out.println("Nueva conexi�n aceptada...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Este m�todo hace el proceso de definici�n de claves p�blicas y privadas a trav�s de diffie hellman
	 */
	public void diffieHellman() {
		try {
			// Lectura del primo p enviado por el cliente
			String pValue = dis.readUTF();
			// Creaci�n de un entero primo grande con el pValue
			BigInteger p = new BigInteger(pValue);

			// Lectura del primo g enviado por el cliente
			String gValue = dis.readUTF();
			// Creaci�n de un entero primo grande con el gValue
			BigInteger g = new BigInteger(gValue);

			// Lectura del primo a enviado por el cliente
			String AValue = dis.readUTF();
			// Creaci�n de un entero con el AValue
			BigInteger A = new BigInteger(AValue);

			// Calculamos un b aleatorio
			BigInteger b = new BigInteger(utilidades.obtenerNumeroAleatorio() + "");
			// Calculamos la operaci�n B = (g^b) mod p
			BigInteger B = g.modPow(b, p);
			// Enviamos B al cliente
			dos.writeUTF(B.toString() + "");

			// Creamos la clave del servidor con la operaci�n K = (A^b) mod p
			BigInteger clave = A.modPow(b, p);
			System.out.println("Clave generada para el servidor...");

			// Clave generada con AES
			Key key = encripcion.generarClave(clave.toByteArray());
            System.out.println("La clave del servidor se ha generado correctamente...");
            
            // Se lee el nombre del archivo enviado desde el cliente
            String nombreArchivo = dis.readUTF();
            
            // Se obtiene la cantidad de bytes que componen el archivo encriptado
			int largo = dis.readInt();
			
			// Se inicializa un arrelgo de bytes con el tama�o del archivo encriptado
			byte[] contenido = new byte[largo];
			
			// Se lee el arreglo de bytes encriptados que vienen del cliente
			dis.readFully(contenido, 0, contenido.length);
			
			// Se desencripta el arreglo de bytes y se crea el archivo dentro del proyecto
			encripcion.desencriptarArchivo(contenido, key, nombreArchivo);
			
			// Se lee el checksum enviado desde el cliente
            String csum = dis.readUTF();
            
            // Se calcula el ckecsum del archivo generado
            String csumCalculado = checkSum.CheckSumMD5(nombreArchivo);
            checkSum.validarCheckSum(csum, csumCalculado);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Servidor servidor = new Servidor();
	}
}
