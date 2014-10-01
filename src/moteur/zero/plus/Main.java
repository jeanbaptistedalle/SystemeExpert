package moteur.zero.plus;

public class Main {


	public static void main(String[] args) {

		final Moteur m = new Moteur();
		final BaseRegle br = Moteur.baseRegle;
		final BaseIncoherence bi = Moteur.baseIncoherence;
		final BaseFait bf = Moteur.baseFait;

		bi.addIncoherence(new Incoherence(1, new Element("perteDePoint",
				Operateur.INFERIEUR, "0")));

		m.chainageAvant();
		m.chainageArriere("perteDePermis","true");
	}
}
