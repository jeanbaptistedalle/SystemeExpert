package moteur.zero.plus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un moteur d'inference 0+
 * 
 * @author JBD
 *
 */
public class Moteur {

	public static BaseFait baseFait = new BaseFait();
	public static BaseFait demandable = new BaseFait();
	public static BaseRegle baseRegle = new BaseRegle();
	public static BaseIncoherence baseIncoherence = new BaseIncoherence();
	public static List<String> stackTrace = new ArrayList<String>();

	public StrategieConflit strategieConflit;

	public Moteur() {
		strategieConflit = StrategieConflit.PREMIERE_TROUVEE;
		generate();
	}

	public String toString() {
		final StringBuilder result = new StringBuilder(
				"Moteur d'inférence 0+\n");
		result.append(baseRegle);
		result.append("\n");
		result.append(baseIncoherence);
		result.append("\n");
		result.append(baseFait);
		return result.toString();
	}


	/**
	 * Genere le moteur d'inference à l'aide des fichiers BaseFait.txt,
	 * BaseRegle.txt et BaseIncoherence.txt 
	 */
	public void generate() {
		
		int numRegle  ;
		Element tmp ;
		List<Element> listElement = new ArrayList<Element>() ;
		String ligne , resultat, element , nomElement , valElement ;
		String[] tabLigne , tabElement, tabRegle , tabResultat;
		String[] tabFait , tabFaitFinaux ;
		BufferedReader Lecteur = null , lecteurFait = null ;
		Operateur operateur ;
		
		
		try
		{
			Lecteur = new BufferedReader(new FileReader("Regles.txt")) ;
			lecteurFait = new BufferedReader(new FileReader("Fait.txt")) ;
			
		Regle r = null ;
		while((ligne = Lecteur.readLine()) != null )
		{
			tabLigne = ligne.split("[:]") ;
			numRegle = Integer.parseInt(tabLigne[0]);
			tabResultat = tabLigne[2].split("[ ]") ;
			tabElement = tabLigne[1].split("[&]") ;
			if(tabElement.length == 1)
			{
				tabRegle = tabElement[0].split("[ ]") ;
				nomElement = tabRegle[0] ;
				operateur = Operateur.testOperateur(tabRegle[1]) ;
				valElement = tabRegle[2] ;
				r = new Regle(numRegle, new Element(nomElement, operateur , valElement),tabResultat[0],tabResultat[2]);
				baseRegle.addRegle(r);
			}
			else
			{
				for(int i=0 ; i<tabElement.length ; i++)
				{
					tabRegle = tabElement[i].split("[ ]") ;
					nomElement = tabRegle[i] ;
					operateur = Operateur.testOperateur(tabRegle[i+1]) ;
					valElement = tabRegle[i+2] ;
					tmp = new Element(nomElement, operateur, valElement) ;
					listElement.add(tmp);
				}
				r.addElements(listElement);
			}
		}
		
		while((ligne = lecteurFait.readLine()) != null)
		{
			tabFait = ligne.split("[:]") ;
			for(int i=0 ; i<tabFait.length ; i++)
			{
				tabFaitFinaux = tabFait[i].split("[ ]") ;
				baseFait.addFait(tabFaitFinaux[0], tabFaitFinaux[1]);
			}
		}
			
		}
		catch(FileNotFoundException fI)
		{
			fI.printStackTrace();
		}
		catch(IOException i)
		{
			i.printStackTrace();
		}

	}


	/**
	 * Methode executant un chainage avant
	 */
	public void chainageAvant() {
		/* Avant de lancer le chainage, on vide l'ancienne stacktrace */
		stackTrace.clear();
		int nbInference = 0;
		List<Regle> listeRegle;
		final List<Regle> listeRegleModifie = new ArrayList<Regle>(
				baseRegle.getRegles());
		boolean inference = true;
		while (inference) {
			inference = false;
			listeRegle = new ArrayList<Regle>(listeRegleModifie);
			for (Regle r : listeRegle) {
				/* Pour chaque règle, on vérifie si la condition est respectée */
				if (r.evaluate()) {
					/*
					 * Si elle est respectée, on applique la conclusion de la
					 * règle et on la retire des règles sur lesquelles itérer
					 */
					baseFait.addFait(r.getNomResultat(), r.getValeurResultat());
					listeRegleModifie.remove(r);
					inference = true;
					nbInference++;
					stackTrace.add(nbInference + " : " + r);
				}
			}
			/*
			 * Decommenter le code ci-dessous pour ajouter un délai entre chaque
			 * itération du chainage
			 */
			/*
			 * try { Thread.sleep(10000); } catch (final InterruptedException e)
			 * { throw new RuntimeException(e); }
			 */
		}
	}

	public boolean chainageArriere(final String nomBut, final String valeurBut) {
		return chainageArriere(nomBut, Operateur.EGAL, valeurBut);
	}

	/**
	 * Methode executant un chainage arrière
	 * 
	 * @param but
	 */
	public boolean chainageArriere(final String nomBut,
			final Operateur operateur, final String valeurBut) {
		/* 1er cas : le but se trouve déjà dans la base de fait */
		if (baseFait.contains(nomBut)) {
			if (evaluateElement(nomBut, operateur, valeurBut)) {
				return true;
			}
		}
		/* 2eme cas : le but est deductible */
		final List<Regle> reglesEnConflit = new ArrayList<Regle>();
		for (Regle r : baseRegle.getRegles()) {
			/*
			 * actuellement, les conflits ne sont géré que par la premiere règle
			 * trouvée
			 */
			if (r.getNomResultat().equals(nomBut)
					&& r.getValeurResultat().equals(valeurBut)) {
				reglesEnConflit.add(r);
			}
		}
		if (reglesEnConflit.size() == 0) {
			// Aucune regle ne correspond, donc non atteignable
			return false;
		}
		if (reglesEnConflit.size() == 1) {
			// Une regle correspond donc on vérifie pour cette règle
			return execRegle(reglesEnConflit.get(0));
		} else {
			return execRegle(choixRegle(reglesEnConflit));
		}
	}

	private Regle choixRegle(final List<Regle> regleEnConflit) {
		/*
		 * On choisit une des règles valable.Actuellement, on choisit simplement
		 * la première
		 */
		switch (strategieConflit) {
		case ALEATOIRE:
			return regleEnConflit.get((new Double(Math.random()
					* (regleEnConflit.size() - 1))).intValue());
		case PREMIERE_TROUVEE:
			return regleEnConflit.get(0);
		case PLUS_PREMISSE:
			Regle choix = null;
			for (final Regle r : regleEnConflit) {
				/*
				 * On prend la premiere règle trouvée, puis on la remplace à
				 * chaque fois qu'on trouve une règle avec plus de prémisse
				 */
				if (choix == null) {
					choix = r;
				} else {
					if (choix.getNbPremisse() < r.getNbPremisse()) {
						choix = r;
					}
				}
			}
		default:
			/* Stratégie inconnue */
			throw new IllegalArgumentException(
					"Stratégie de gestion de conflit inconnue");
		}

	}

	private boolean execRegle(final Regle r) {
		boolean result = false;
		Element elt = r.getElement();
		while (elt != null) {
			result = chainageArriere(elt.getNom(), elt.getOperateur(),
					elt.getValeur());
			if (!result) {
				break;
			} else {
				elt = elt.getNext();
			}
		}
		if (result) {
			return true;
		}
		return false;
	}

	/**
	 * Methode permettant d'évaluer un élément (donc un nom, un operateur et une valeur)
	 * 
	 * @param nom
	 * @param operateur
	 * @param valeur
	 * @return
	 */
	private boolean evaluateElement(final String nom,
			final Operateur operateur, final String valeur) {
		final String valeurBaseFait = Moteur.baseFait.get(nom);
		Integer valeurInteger = null;
		Integer valeurBaseFaitInteger = null;
		boolean result = false;
		try {
			valeurInteger = Integer.parseInt(valeur);
		} catch (final NumberFormatException e) {
			valeurInteger = null;
		}
		/*
		 * Si la valeur attendue est un entier, on cast aussi la valeur à
		 * tester. Si elle ne peut etre casté, elle ne correspond pas, donc on
		 * retourne faux
		 */
		if (valeurInteger != null) {
			try {
				valeurBaseFaitInteger = Integer.parseInt(valeurBaseFait);
			} catch (final NumberFormatException e) {
				return false;
			}
		}
		switch (operateur) {
		case EGAL:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.equals(valeurInteger);
			} else {
				result = valeur.equals(valeurBaseFait);
			}
			break;
		case DIFFERENT:
			if (valeurBaseFaitInteger != null) {
				result = !valeurBaseFaitInteger.equals(valeurInteger);
			} else {
				result = !valeur.equals(valeurBaseFait);
			}
			break;
		case SUPERIEUR:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) > 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) > 0;
			}
			break;
		case SUPERIEUR_OU_EGAL:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) >= 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) >= 0;
			}
			break;
		case INFERIEUR:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) < 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) < 0;
			}
			break;
		case INFERIEUR_OU_EGAL:
			if (valeurBaseFaitInteger != null) {
				result = valeurBaseFaitInteger.compareTo(valeurInteger) <= 0;
			} else {
				result = valeur.compareTo(valeurBaseFait) <= 0;
			}
			break;
		default:
			throw new IllegalArgumentException("Opérateur inconnu");
		}
		return result;
	}

	/**
	 * Methode permettant de tester chaque incoherences définies dans la liste
	 * des incoherences. Si au moins l'une d'entre elle n'est pas validé, alors
	 * la base de fait n'est plus coherente
	 * 
	 * @return
	 */
	public boolean verifCoherence() {
		for (Incoherence incoherence : baseIncoherence.getListeIncoherence()) {
			if (incoherence.evaluate()) {
				return false;
			}
		}
		return true;
	}
}