package logica;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.Key;

public class Cliente {
	private Socket sClient;
	private java.io.DataInputStream dis;
	private DataOutputStream dos;
	private Utilidades utilidades;
	private Encripcion encripcion;
	private CheckSum checkSum;

	public final static int PUERTO = 3456;

	public Cliente() {
		utilidades = new Utilidades();
		encripcion = new Encripcion();
		checkSum = new CheckSum();
		iniciarCliente();
		diffieHellman();
	}

	/**
	 * Este método inicializa un socket que se conecta a un servidor para enviarle
	 * archivos También se crea un canal de conexión con los clientes que se
	 * conectan al servidor por medio de Sockets
	 */
	public void iniciarCliente() {
		try {
			// Mensaje para mostrar que el cliente se ha conectado con el servidor
			System.out.println("Cliente conectado al servidor...");
			sClient = new Socket("localhost", PUERTO);
			File archivo = new File("imagenes/juanmanuelmadrid.jpg");
			// Se obtienen los datos del cliente a través de un DataInputStream
			dis = new DataInputStream(sClient.getInputStream());
			// Se envían los datos al cliente a través de un DataOutputStream
			dos = new DataOutputStream(sClient.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Este método hace el proceso de definición de claves públicas y privadas a
	 * través de diffie hellman
	 */
	public void diffieHellman() {
		try {
			// Calculamos un p primo aleatorio
			BigInteger pPrimo = new BigInteger(utilidades.obtenerNumeroPrimo() + "");
			// Enviamos el número primo p al servidor
			dos.writeUTF(pPrimo.toString());

			// Calculamos un g primo alteatorio
			BigInteger gPrimo = new BigInteger(utilidades.obtenerNumeroPrimo() + "");
			// Enviamos el número primo g al servidor
			dos.writeUTF(gPrimo.toString());

			// Calculamos un a aleatorio
			BigInteger aAleatorio = new BigInteger(utilidades.obtenerNumeroAleatorio() + "");
			// Calculamos la operación A = (g^a) mod p
			BigInteger A = gPrimo.modPow(aAleatorio, pPrimo);
			// Enviamos A al servidor
			dos.writeUTF(A.toString());

			// Lectura del B enviado por el servidor
			String BValue = dis.readUTF();
			// Creación de un entero con el BValue
			BigInteger B = new BigInteger(BValue + "");

			// Creamos la clave del servidor con la operación K = (B^a) mod p
			BigInteger clave = B.modPow(aAleatorio, pPrimo);
			System.out.println("Clave generada para el cliente...");

			// Clave generada con AES
			Key key = encripcion.generarClave(clave.toByteArray());
			System.out.println("La clave del cliente se ha generado correctamente...");

			// Se almacena el nombre del archivo en una variable
			String nombreArchivo = "juanmanuelmadrid.jpg";

			// Se envia el nombre del archivo al servidor
			dos.writeUTF(nombreArchivo);

			// Se encripta el archivo y se obtiene su valor en un arreglo de bytes
			byte[] archivoEncriptado = encripcion.encriptarArchivo(nombreArchivo, key);

			// Se envía al servidor la cantidad de bytes que componen el archivo encriptado
			dos.writeInt(archivoEncriptado.length);

			// Se envía al servidor el arreglo de bytes
			dos.write(archivoEncriptado);

			// Se calcula el checksum del archivo
			String csum = checkSum.CheckSumMD5(nombreArchivo);
			// Se envía el checksum al servidor
			dos.writeUTF(csum);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Cliente cliente = new Cliente();
	}
}
