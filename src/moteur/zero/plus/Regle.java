package moteur.zero.plus;

import java.util.List;

/**
 * Classe représentant une règle. La syntaxe d'une règle est : <br/>
 * A = 2 & B = 3 : C = 4 <br/>
 * Dans le cas des booleans, il est nécessaire d'écrire true ou false
 * 
 * @author etudiant
 *
 */
public class Regle {

	private Integer numero;
	private Element element;
	private String nomResultat;
	private String valeurResultat;

	public Regle() {}
	
	public Regle(final Integer numero, final Element element, final String nomResultat, final String valeurResultat){
		this.numero = numero;
		this.element = element;
		this.nomResultat = nomResultat;
		this.valeurResultat = valeurResultat;
	}
	
	public Regle(final Integer numero, final String nomResultat, final String valeurResultat){
		this(numero, null, nomResultat, valeurResultat);
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(final Integer numero) {
		this.numero = numero;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(final Element element) {
		this.element = element;
	}

	public String getNomResultat() {
		return nomResultat;
	}

	public void setNomResultat(final String nomResultat) {
		this.nomResultat = nomResultat;
	}

	public String getValeurResultat() {
		return valeurResultat;
	}

	public void setValeurResultat(final String valeurResultat) {
		this.valeurResultat = valeurResultat;
	}
	
	public boolean evaluate(){
		return element.evaluate();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Regle other = (Regle) obj;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuilder result = new StringBuilder("R");
		result.append(numero);
		result.append(" : ");
		result.append(element);
		result.append(" ->- ");
		result.append(nomResultat);
		result.append(" = ");
		result.append(valeurResultat);
		return result.toString();
	}
	
	/**
	 * Permet d'ajouter une liste d'élément à la règle. Si une liste existe
	 * déjà, on les ajoute aux éléments existants
	 * 
	 * @param elements
	 */
	public void addElements(final List<Element> elements) {
		Element existing = this.element;
		while (existing.getNext() != null) {
			existing = existing.getNext();
		}
		for (final Element elt : elements) {
			existing.setNext(elt);
			existing = existing.getNext();
		}
	}
}
