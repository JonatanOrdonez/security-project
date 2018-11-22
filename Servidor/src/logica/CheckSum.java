package logica;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSum {

	private MessageDigest md5;
	
	public CheckSum() {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Este método llama a la función checksum para calcular el valor de un archivo en modalidad MD5
	 * @param rutaArchivo: Ruta del archivo al cual se le calculará el checksum
	 * @return String: Cadena con el checksum del archivo
	 */
	public String CheckSumMD5(String rutaArchivo) {
		try {
			return checksum(rutaArchivo, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Este método calcula el checksum de un archvio en diferentes modalidades
	 * @param filepath: Ruta del archivo al cual se le calculará el checksum
	 * @param md: Modalidad del checksum
	 * @return String: Cadena con el checksum del archivo
	 * @throws IOException: Excepción si no se logra calcular el checksum
	 */
	private String checksum(String filepath, MessageDigest md) throws IOException {
		// DigestInputStream is better, but you also can hash file like this.
		try (InputStream fis = new FileInputStream(filepath)) {
			byte[] buffer = new byte[1024];
			int nread;
			while ((nread = fis.read(buffer)) != -1) {
				md.update(buffer, 0, nread);
			}
		}
		// bytes to hex
		StringBuilder result = new StringBuilder();
		for (byte b : md.digest()) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}
	
	/**
	 * Este método calcula si el checksum de un archivo coincide con el que es enviado a través del socket
	 * @param csumCliente: Valor del checksum que se recibe del cliente
	 * @param csumCalculado: Valor del checksum calculado del archivo recibido
	 */
	public void validarCheckSum(String csumCliente, String csumCalculado) {
		if (csumCliente.equals(csumCalculado)) {
			System.out.println("La integridad del archivo es correcta...");
		}
		else {
			System.out.println("El archivo se encuentra dañado. El checksum no pasó la prueba...");
		}
	}
}
