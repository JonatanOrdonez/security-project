package logica;

import java.util.ArrayList;

public class Utilidades {
	private ArrayList<Integer> numerosPrimos;
	public final static int CANTIDAD_PRIMOS_GENERADOS = 1000;
	public final static int MAXIMO_NUMERO_ALEATORIO = 100;

	public Utilidades() {
		numerosPrimos = new ArrayList<Integer>();
		generarPrimos();
	}
	
	/**
	 * Este m�todo genera un arreglo de primos con tama�io definido por la constante CANTIDAD_PRIMOS_GENERADOS
	 */
	public void generarPrimos() {
		int i = 0;
		int num = 0;
		for (i = 1; i <= CANTIDAD_PRIMOS_GENERADOS; i++) {
			int counter = 0;
			for (num = i; num >= 1; num--) {
				if (i % num == 0) {
					counter = counter + 1;
				}
			}
			if (counter == 2) {
				// Appended the Prime number to the String
				numerosPrimos.add(i);
			}
		}
	}

	/**
	 * Este m�todo obtiene un n�mero primo aleatorio del arreglo de n�meros primos
	 * @return int: N�mero primo aleatorio del arreglo
	 */
	public int obtenerNumeroPrimo() {
		int maximo = numerosPrimos.size();
		int aleatorio = (int) Math.floor(Math.random() * (maximo + 1));
		return numerosPrimos.get(aleatorio);
	}

	/**
	 * Este m�todo genera un n�mero aleatorio que se encuentra entre o y el valor de la constante MAXIMO_NUMERO_ALEATORIO
	 * @return int: N�mero aleatorio
	 */
	public int obtenerNumeroAleatorio() {
		return (int) Math.floor(Math.random() * (MAXIMO_NUMERO_ALEATORIO + 1));
	}
}
