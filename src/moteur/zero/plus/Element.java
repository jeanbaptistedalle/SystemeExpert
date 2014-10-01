package moteur.zero.plus;
/**
 * Correspond à une prémisse d'une règle. Si la règle contient plusieurs
 * prémisse, alors elles sont chainées grâce à next.
 * 
 * @author JBD
 *
 */
public class Element {

	private String nom;
	private Operateur operateur;
	private String valeur;
	private Element next;

	public Element() {}
	
	public Element(final String nom, final Operateur operateur, final String valeur, final Element next){
		this.nom = nom;
		this.operateur = operateur;
		this.valeur = valeur;
		this.next = next;
	}
	
	public Element(final String nom, final Operateur operateur, final String valeur){
		this(nom, operateur, valeur, null);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(final String nom) {
		this.nom = nom;
	}

	public Operateur getOperateur() {
		return operateur;
	}

	public void setOperateur(final Operateur operateur) {
		this.operateur = operateur;
	}

	public String getValeur() {
		return valeur;
	}

	public void setValeur(final String valeur) {
		this.valeur = valeur;
	}

	public Element getNext() {
		return next;
	}

	public void setNext(final Element next) {
		this.next = next;
	}

	/**
	 * Methode evaluant l'élément ainsi que les éléments suivant si ils existent
	 * 
	 * @return
	 */
	public boolean evaluate() {
		if (!Moteur.baseFait.contains(nom)) {
			return false;
		}
		boolean result = evaluateElement();
		/* Inutile de tester le suivant si l'élément actuel est déjà faux */
		if (result && next != null) {
			result = result && next.evaluate();
		}
		return result;
	}

	/**
	 * Methode évaluant l'élément seul. Par défaut, on teste sur des String,
	 * mais il est possible d'étendre le comportement pour d'autre type. Les
	 * types actuellement gérés sont : Integer
	 * 
	 * @return boolean
	 */
	private boolean evaluateElement() {
		final String valeurBaseFait = Moteur.baseFait.get(nom);
		Integer valeurInteger = null;
		Integer valeurBaseFaitInteger = null;
		boolean result = false;
		try {
			valeurInteger = Integer.parseInt(valeur);
		} catch (final NumberFormatException e) {
			valeurInteger = null;
		}
		/*
		 * Si la valeur attendue est un entier, on cast aussi la valeur à
		 * tester. Si elle ne peut etre casté, elle ne correspond pas, donc on
		 * retourne faux
		 */
		if (valeurInteger != null) {
			try {
				valeurBaseFaitInteger = Integer.parseInt(valeurBaseFait);
			} catch (final NumberFormatException e) {
				return false;
			}
		}
		switch (operateur) {
		case EGAL:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.equals(valeurInteger);
			} else {
				result = valeur.equals(valeurBaseFait);
			}
			break;
		case DIFFERENT:
			if (valeurBaseFaitInteger != null) {
				result = !valeurBaseFaitInteger.equals(valeurInteger);
			} else {
				result = !valeur.equals(valeurBaseFait);
			}
			break;
		case SUPERIEUR:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) > 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) > 0;
			}
			break;
		case SUPERIEUR_OU_EGAL:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) >= 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) >= 0;
			}
			break;
		case INFERIEUR:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) < 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) < 0;
			}
			break;
		case INFERIEUR_OU_EGAL:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) <= 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) <= 0;
			}
			break;
		default:
			throw new IllegalArgumentException("Opérateur inconnu");
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder result = new StringBuilder("");
		result.append(nom);
		result.append(operateur);
		result.append(valeur);
		if(next != null){
			result.append(" & [");
			result.append(next);
			result.append("]");
		}
		return result.toString();
	}
}
