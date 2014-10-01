package moteur.zero.plus;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant la base de fait. Les éléments de celle-ci sont stockés
 * dans une Map avec comme clef le nom de l'élément et comme valeur sa valeur.
 * 
 * @author JBD
 *
 */
public class BaseFait {

	private Map<String, String> faits;

	public BaseFait() {
		faits = new HashMap<String, String>();
	}

	public boolean contains(final String key) {
		return faits.containsKey(key);
	}
	
	public void addFait(final String key, final String value){
		put(key,value);
	}

	public void put(final String key, final String value) {
		if (key != null && value != null) {
			if (faits.containsKey(key)) {
				try{
					int nb = Integer.parseInt(faits.get(key));
					int newNb = Integer.parseInt(value);
					faits.put(key, new Integer(nb+newNb).toString());
					return;
				}catch(final NumberFormatException e){
					//pas un int, on écrase la valeur
				}
				// Dans le cas ou la clef existe déjà, on additionne ?
			}
			faits.put(key, value);
		}
	}

	public String get(final String key) {
		return faits.get(key);
	}
	
	public String toString(){
		final StringBuilder result = new StringBuilder("Base de fait :\n");
		for(String key : faits.keySet()){
			result.append("[");
			result.append(key);
			result.append(" = ");
			result.append(faits.get(key));
			result.append("]");
			result.append("\n");
		}
		return result.toString();
	}
}
