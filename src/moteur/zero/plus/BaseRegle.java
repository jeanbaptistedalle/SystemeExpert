package moteur.zero.plus;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant la base de règle du systeme expert.
 * 
 * @author JBD
 *
 */
public class BaseRegle {

	private List<Regle> regles;

	public BaseRegle() {
		regles = new ArrayList<Regle>();
	}

	public List<Regle> getRegles() {
		return regles;
	}

	public void setRegles(final List<Regle> regles) {
		this.regles = regles;
	}

	public void addRegle(final Regle regle) {
		if (regle != null) {
			regles.add(regle);
		}
	}
	
	/**
	 * Retourne la nieme regle de la base de regle
	 * @param index
	 * @return
	 */
	public Regle getRegle(final int index){
		return regles.get(index);
	}
	
	public String toString(){
		final StringBuilder result = new StringBuilder("Base de règle :\n");
		for(Regle regle : regles){
			result.append(regle);
			result.append("\n");
		}
		return result.toString();
	}

}
