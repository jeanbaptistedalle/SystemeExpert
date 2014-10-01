package moteur.zero.plus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author etudiant
 *
 */
public class BaseIncoherence {

	private List<Incoherence> listeIncoherence;
	
	public BaseIncoherence(){
		listeIncoherence = new ArrayList<Incoherence>();
	}

	public List<Incoherence> getListeIncoherence() {
		return listeIncoherence;
	}

	public void setListeIncoherence(List<Incoherence> listeIncoherence) {
		this.listeIncoherence = listeIncoherence;
	}
	
	public Incoherence get(final int index){
		return listeIncoherence.get(index);
	}
	
	public void addIncoherence(final Incoherence incoherence){
		listeIncoherence.add(incoherence);
	}
	
	public String toString(){
		final StringBuilder result = new StringBuilder("Liste des incoherences :\n");
		for(Incoherence incoherence : listeIncoherence){
			result.append(incoherence);
			result.append("\n");
		}
		return result.toString();
	}
}
