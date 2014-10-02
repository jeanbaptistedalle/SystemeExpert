package moteur.zero.plus;

/**
 * Une classe main pour lancer quelques tests non automatique
 * 
 * @author JBD
 *
 */
public class Main {

	public static void main(String[] args) {
		final Moteur m = new Moteur(false);
		final BaseIncoherence bi = Moteur.baseIncoherence;

		bi.addIncoherence(new Incoherence(1, new Element("perteDePoint",
				Operateur.INFERIEUR, "0")));
		
		System.out.println(m);

		// m.chainageAvant();
		m.chainageArriere("perteDePermis","true");
		m.menu();
	}
}
