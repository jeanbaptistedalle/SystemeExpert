package moteur.zero.plus.test;

import junit.framework.Assert;
import moteur.zero.plus.BaseFait;
import moteur.zero.plus.BaseIncoherence;
import moteur.zero.plus.BaseRegle;
import moteur.zero.plus.Element;
import moteur.zero.plus.Incoherence;
import moteur.zero.plus.Moteur;
import moteur.zero.plus.Operateur;
import moteur.zero.plus.Regle;

import org.junit.Before;
import org.junit.Test;

/**
 * Classe de test du moteur d'inference 0+.
 * 
 * @FIXME Les tests utilisent une version obsolete d'Assert. *
 * 
 * @author JBD
 *
 */
@SuppressWarnings("deprecation")
public class MoteurTest {

	Moteur m;

	@Before
	public void init() {
		m = new Moteur(false);
		final BaseRegle br = Moteur.baseRegle;
		final BaseIncoherence bi = Moteur.baseIncoherence;
		final BaseFait bf = Moteur.baseFait;

		/*
		 * br.addRegle(new Regle(0, new Element("attributBidon", Operateur.EGAL,
		 * "true"), "test", "true"));
		 */

		br.addRegle(new Regle(1, new Element("vitesse", Operateur.SUPERIEUR,
				"110", new Element("temps", Operateur.EGAL, "pluie")),
				"perteDePoint", "2"));

		br.addRegle(new Regle(2, new Element("vitesse", Operateur.SUPERIEUR,
				"130"), "perteDePoint", "3"));

		br.addRegle(new Regle(3, new Element("perteDePoint",
				Operateur.SUPERIEUR_OU_EGAL, "12"), "perteDePermis", "true"));

		bi.addIncoherence(new Incoherence(1, new Element("perteDePoint",
				Operateur.INFERIEUR, "0")));

		bf.addFait("vitesse", "130");
		bf.addFait("temps", "pluie");
		bf.addFait("typeRoute", "autoroute");
	}

	@Test
	public void testChainageAvant() {
		m.chainageAvant();
		Assert.assertTrue(Moteur.baseFait.contains("perteDePoint"));
		Assert.assertEquals(Moteur.baseFait.get("perteDePoint"), "2");
	}

	@Test
	public void testChainageArriere() {
		Assert.assertTrue(m.chainageArriere("perteDePoint", "2"));
		Assert.assertFalse(m.chainageArriere("perteDePoint", "20"));

	}
}
