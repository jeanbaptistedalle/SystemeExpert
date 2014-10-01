package moteur.zero.plus;

public class Main {


	public static void main(String[] args) {
		final Moteur m = new Moteur();
		final BaseRegle br = Moteur.baseRegle;
		final BaseIncoherence bi = Moteur.baseIncoherence;
		final BaseFait bf = Moteur.baseFait;

		/*
		 * br.addRegle(new Regle(0, new Element("attributBidon", Operateur.EGAL,
		 * "true"), "test", "true"));
		 */

		/*br.addRegle(new Regle(1, new Element("vitesse", Operateur.SUPERIEUR,
				"110", new Element("temps", Operateur.EGAL, "pluie")),
				"perteDePoint", "2"));

		br.addRegle(new Regle(2, new Element("vitesse", Operateur.SUPERIEUR,
				"130"), "perteDePoint", "3"));

		br.addRegle(new Regle(3, new Element("perteDePoint",
				Operateur.SUPERIEUR_OU_EGAL, "12"), "perteDePermis", "true"));*/

		bi.addIncoherence(new Incoherence(1, new Element("perteDePoint",
				Operateur.INFERIEUR, "0")));

		bf.addFait("vitesse", "120");
		bf.addFait("temps", "pluie");
		bf.addFait("typeRoute", "autoroute");
		m.chainageAvant();
		m.chainageArriere("perteDePermis","true");
	}
}
