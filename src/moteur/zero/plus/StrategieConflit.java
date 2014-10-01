package moteur.zero.plus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Enum réferençant les stratégie de gestion de conflit
 * 
 * @author JBD
 *
 */
public enum StrategieConflit {
	ALEATOIRE(1, "Choix d'une règle aléatoirement"),

	PREMIERE_TROUVEE(2, "Choix de la première règle trouvée"),

	PLUS_PREMISSE(3, "Choix de la règle ayant le plus de prémisse à satisfaire");

	private int numero;
	private String description;

	private StrategieConflit(final int numero, final String description) {
		this.numero = numero;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getNumero() {
		return numero;
	}

	/**
	 * Methode renvoyant la liste des strategie de gestion de conflit
	 * 
	 * @return
	 */
	public static List<StrategieConflit> getAll() {
		final List<StrategieConflit> listeStrat = new ArrayList<StrategieConflit>();
		listeStrat.add(PREMIERE_TROUVEE);
		listeStrat.add(ALEATOIRE);
		listeStrat.add(PLUS_PREMISSE);
		Collections.sort(listeStrat, new Comparator<StrategieConflit>(){
			public int compare(final StrategieConflit arg0,
					final StrategieConflit arg1) {
				return new Integer(arg0.getNumero()).compareTo(arg1.getNumero());
			}
		});
		return listeStrat;
	}

	/**
	 * Methode renvoyant le plus haut numero parmi les strategies existantes.
	 * 
	 * @return
	 */
	public static int getNumeroMax() {
		final List<StrategieConflit> liste = getAll();
		Collections.sort(liste, new Comparator<StrategieConflit>() {
			public int compare(final StrategieConflit arg0,
					final StrategieConflit arg1) {
				return new Integer(arg0.getNumero()).compareTo(arg1.getNumero());
			}
		});
		return liste.get(liste.size() - 1).getNumero();
	}

	/**
	 * Methode retournant la stratégie de gestion des conflits pour le numero
	 * donné en parametre
	 * 
	 * @param numero
	 * @return
	 */
	public static StrategieConflit getStrategieConflit(final int numero) {
		for (StrategieConflit s : getAll()) {
			if (s.getNumero() == numero) {
				return s;
			}
		}
		return null;
	}
}