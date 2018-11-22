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

	public final static int PUERTO = 3456;

	public Cliente() {
		utilidades = new Utilidades();
		encripcion = new Encripcion();
		iniciarCliente();
		diffieHellman();
	}

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
            
            byte[] archivoEncriptado = encripcion.encriptarArchivo("descarga.jpg", key);
            //String enviar = encripcion.convertirByteArrayAString(archivoEncriptado);
            dos.writeInt(archivoEncriptado.length);
            dos.write(archivoEncriptado);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Cliente cliente = new Cliente();
	}
}
