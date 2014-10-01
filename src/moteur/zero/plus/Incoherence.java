package moteur.zero.plus;

/**
 * Classe représentant les incoherences qu'il faut pouvoir detecter dans le
 * moteur
 * 
 * @author JBD
 *
 */
public class Incoherence {
	private Integer numero;
	private Element element;

	public Incoherence() {}
	
	public Incoherence(final Integer numero, final Element element){
		this.numero = numero;
		this.element = element;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public boolean evaluate(){
		return element.evaluate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuilder result = new StringBuilder("INC");
		result.append(numero);
		result.append(" : ");
		result.append(element);
		return result.toString();
	}
}
