package logica;

import java.io.File;
import java.nio.file.Files;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encripcion {

	private Cipher cipher;

	public Encripcion() {
		try {
			cipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Este método agrega los bytes faltantes para convertir un arreglo de n bytes en uno de 16 bytes
	 * @param arreglo: Arreglo de bytes que van a ser completados
	 * @return byte[]: Arreglo con un tamaño de 16 bytes
	 */
	public byte[] rellenarArregloPara16(byte[] arreglo) {
		int mod = arreglo.length % 16;
		if (mod == 0) {
			return arreglo;
		}
		int falta = 16 - mod;
		byte[] nuevo = new byte[arreglo.length + falta];
		System.arraycopy(arreglo, 0, nuevo, 0, arreglo.length);
		return nuevo;
	}
	
	public Key generarClave(byte[] clave) {
		byte[] byteKey = new byte[16];
		clave = rellenarArregloPara16(clave);
		try {
			System.arraycopy(clave, 0, byteKey, 0, 16);
			Key key = new SecretKeySpec(byteKey, "AES");
			return key;
		} catch (Exception e) {
			System.err.println("Error en la creación de la clave: " + e);
		}
		return null;
	}
	
	/**
	 * Este método encripta un archivo a partir de su ruta dentro del proyecto y la clave generada a partir del diffie hellman
	 * @param ruta: Ruta del archivo que va a ser encriptado
	 * @param clave: Clave con la cual se va a encriptar en modalidad AES 128
	 * @return byte[]: Arreglo de bytes de un archivo que ha sido encriptado
	 */
	public byte[] encriptarArchivo(String ruta, Key clave) {
		try {
			byte[] bytesArchivo = Files.readAllBytes(new File(ruta).toPath());
			cipher.init(Cipher.ENCRYPT_MODE, clave);
			byte[] archivoEncriptado = cipher.doFinal(bytesArchivo);
			System.out.println("Archivo encriptado correctamente");
			return archivoEncriptado;
		} catch (Exception e) {
			System.err.println("Error encriptando el archivo: " + e);
		}
		return null;
	}

	/**
	 * Este método desencripta un arreglo de bytes utilizando una clave generada a partir del diffie hellman
	 * @param archivo: Arreglo de bytes del archivo encriptado
	 * @param clave: Clave con la cual se va a encriptar en modalidad AES 128@param clave: 
	 * @param nombreArchivo: Nombre del archivo con el cual se va a guardar dentro del proyecto
	 */
	public void desencriptarArchivo(byte[] archivo, Key clave, String nombreArchivo) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, clave);
			byte[] decryptedByte = cipher.doFinal(archivo);
			Files.write(new File(nombreArchivo).toPath(), decryptedByte);
			System.out.println("Archivo desencriptado correctamente");
		} catch (Exception e) {
			System.err.println("Error desencriptando el archivo: " + e);
		}
	}
}
