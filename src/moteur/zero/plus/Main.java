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
		m.generate();
		//final BaseIncoherence bi = Moteur.baseIncoherence;
		
		System.out.println(m);

		// m.chainageAvant();
		//m.chainageArriere("perteDePermis","true");
		m.menu();
	}
}
