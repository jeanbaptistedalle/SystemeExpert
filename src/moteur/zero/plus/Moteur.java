package moteur.zero.plus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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
	public static Scanner scan = new Scanner(System.in);

	public StrategieConflit strategieConflit;

	/**
	 * Crée le moteur en générant les base à l'aide des fichiers .txt
	 * correspondants
	 */
	public Moteur() {
		this(true);
	}

	/**
	 * Crée le moteur en générant les base à l'aide des fichiers .txt
	 * correspondants si generer = true
	 * 
	 * @param generer
	 */
	public Moteur(final boolean generer) {
		strategieConflit = StrategieConflit.PREMIERE_TROUVEE;
		if (generer) {
			generate();
		}
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
	 * Génère la base de règle grâce au fichier Regle.txt<br/>
	 * La syntaxe est : (les espaces sont représentés par des '_' )<br/>
	 * <numeroRegle>:<variable>_<operateur>_<valeur>[&<variable>_<operateur>_<
	 * valeur>]:<nomVariable>=<valeur>
	 * 
	 */
	public void rempliBaseRegle() {
		BufferedReader Lecteur = null;
		String ligne, nomElement, valElement;
		String[] tabLigne, tabResultat, tabElement, tabRegle;
		int numRegle;
		Operateur operateur;
		Element tmp;
		List<Element> listElement = new ArrayList<Element>();

		try {

			Lecteur = new BufferedReader(new FileReader("Regles.txt"));

			Regle r = null;
			while ((ligne = Lecteur.readLine()) != null) {
				tabLigne = ligne.split("[:]");
				numRegle = Integer.parseInt(tabLigne[0]);
				tabResultat = tabLigne[2].split("[ ]");
				tabElement = tabLigne[1].split("[&]");
				if (tabElement.length == 1) {
					tabRegle = tabElement[0].split("[ ]");
					nomElement = tabRegle[0];
					operateur = Operateur.testOperateur(tabRegle[1]);
					valElement = tabRegle[2];
					r = new Regle(numRegle, new Element(nomElement, operateur,
							valElement), tabResultat[0], tabResultat[2]);
				} else {
					r = new Regle(numRegle, tabResultat[0], tabResultat[2]);
					listElement.clear();
					for (int i = 0; i < tabElement.length; i++) {
						tabRegle = tabElement[i].split("[ ]");
						nomElement = tabRegle[0];
						operateur = Operateur.testOperateur(tabRegle[1]);
						valElement = tabRegle[2];
						tmp = new Element(nomElement, operateur, valElement);
						listElement.add(tmp);
					}
					r.addElements(listElement);
				}
				baseRegle.addRegle(r);
			}
		} catch (FileNotFoundException f) {
			f.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}

	}

	/**
	 * Génère la base de fait grâce au fichier Fait.txt<br/>
	 * La syntaxe est : (les espaces sont représentés par des '_' )<br/>
	 * <variable>_=_<valeur>
	 * 
	 */
	public void rempliBaseFait() {
		BufferedReader lecteurFait = null;
		String ligne;
		String[] tabFait;

		try {
			lecteurFait = new BufferedReader(new FileReader("Fait.txt"));

			while ((ligne = lecteurFait.readLine()) != null) {
				tabFait = ligne.split("[=]");
				baseFait.addFait(tabFait[0], tabFait[1]);

			}
		} catch (FileNotFoundException f) {
			f.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	/**
	 * Génère la base d'incohérence grâce au fichier Incoherence.txt La syntaxe
	 * est : (les espaces sont représentés par des '_' )<br/>
	 * <variable>_<operateur>_<valeur>
	 * 
	 * 
	 */
	public void rempliBaseIncoherence() {
		BufferedReader lecteurIncoherence = null;
		String ligne, nomInc, valInc;
		String[] tabInco;
		int numInc;
		Element elem;
		Operateur oper;

		try {

			lecteurIncoherence = new BufferedReader(new FileReader(
					"Incoherence.txt"));

			while ((ligne = lecteurIncoherence.readLine()) != null) {
				tabInco = ligne.split("[ ]");
				numInc = Integer.parseInt(tabInco[0]);
				nomInc = tabInco[1];
				oper = Operateur.testOperateur(tabInco[2]);
				valInc = tabInco[3];
				elem = new Element(nomInc, oper, valInc);
				baseIncoherence.addIncoherence(new Incoherence(numInc, elem));
			}

		} catch (FileNotFoundException fI) {
			fI.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	/**
	 * Genere le moteur d'inference à l'aide des fichiers Fait.txt, Regles.txt
	 * et Incoherence.txt
	 */
	public void generate() {

		rempliBaseRegle();
		rempliBaseFait();
		rempliBaseIncoherence();

	}

	/**
	 * Methode executant un chainage avant
	 */
	public void chainageAvant() {
		final Incoherence incoherence = verifCoherence();
		if (incoherence != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Le moteur n'est pas cohérent, la règle \"");
			stringBuilder.append(incoherence);
			stringBuilder
					.append("\" n'est pas respecté, il est donc impossible de lancer le chainage avant");
			System.out.println(stringBuilder.toString());
			return;
		}
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
					System.out.println("La règle \"" + r + "\" a été executée");
					baseFait.addFait(r.getNomResultat(), r.getValeurResultat());
					listeRegleModifie.remove(r);
					inference = true;
					nbInference++;
					stackTrace.add("Inference n°" + nbInference
							+ " : Règle executée : " + r);
				}
			}
			sleep(500);
		}
		System.out
				.println("Fin du chainage avant, les règles suivantes n'ont pas été executée");
		stackTrace.add("Liste des règles non executées :");
		for (Regle r : listeRegleModifie) {
			stackTrace.add(r.toString());
			System.out.println("\"" + r + "\"");
		}
		waitForInput();
	}

	public void waitForInput() {
		System.out.println(System.getProperty("line.separator"));
		System.out.println("Appuyez sur \"Entrer\" pour continuer");
		try {
			System.in.read();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.out.println(System.getProperty("line.separator"));
	}

	/**
	 * Methode mettant en pause le programme pendant un nombre de temps en ms.
	 * 
	 * @param time
	 */
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Cette methode permet de saisir le but que l'utilisateur souhaite
	 * atteindre, puis affiche si celui-ci est atteignable ou non.
	 */
	public void initChainageArrière() {
		final Incoherence incoherence = verifCoherence();
		if (incoherence != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Le moteur n'est pas cohérent, la règle \"");
			stringBuilder.append(incoherence);
			stringBuilder
					.append("\" n'est pas respecté, il est donc impossible de lancer le chainage arrière");
			System.out.println(stringBuilder.toString());
			return;
		}
		System.out.println("Entrez le nom de la variable but :\n");
		String nomBut = null;
		while (nomBut == null) {
			nomBut = scan.next();
		}
		System.out.println("Entrez la valeur de la variable but : \n");
		String valeurBut = null;
		while (valeurBut == null) {
			valeurBut = scan.next();
		}
		if (chainageArriere(nomBut, valeurBut)) {
			System.out.println("");
			System.out.println("Le but saisi peut être atteint !\n\n");
		} else {
			System.out.println("");
			System.out.println("Le but saisi ne peut être atteint !\n\n");
		}
	}

	public boolean chainageArriere(final String nomBut, final String valeurBut) {
		return chainageArriere(nomBut, Operateur.EGAL, valeurBut, true);
	}

	/**
	 * Methode executant un chainage arrière
	 * 
	 * @param but
	 */
	public boolean chainageArriere(final String nomBut,
			final Operateur operateur, final String valeurBut,
			final boolean premiereIteration) {
		System.out.println("On cherche à savoir si le fait \"" + nomBut
				+ "\" pour la valeur \"" + valeurBut + "\" est atteignable.");
		/* 1er cas : le but se trouve déjà dans la base de fait */
		if (baseFait.contains(nomBut)) {
			if (evaluateElement(nomBut, operateur, valeurBut)) {
				System.out.println("Le fait \"" + nomBut
						+ "\" pour la valeur \"" + valeurBut
						+ "\" se trouve dans la base de fait.");
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
			if (premiereIteration) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("\"");
				stringBuilder.append(nomBut);
				stringBuilder.append("\" pour la valeur \"");
				stringBuilder.append(valeurBut);
				stringBuilder
						.append("\" n'appartient pas à la base de fait et n'est produit par aucune règle.");
				System.out.println(stringBuilder.toString());
				// Aucune regle ne correspond, donc non atteignable
				return false;
			} else {
				if (!baseFait.contains(nomBut)) {
					System.out
							.println("Il n'existe aucune valeur pour la variable \""
									+ nomBut + "\". Veuillez en entrer une.");
					String nouvelleValeur = null;
					while (nouvelleValeur == null) {
						nouvelleValeur = scan.next();
					}
					baseFait.put(nomBut, nouvelleValeur);
					System.out.println("Relancement du chainage arrière avec les nouvelles valeurs ...");
					return chainageArriere(nomBut, operateur, valeurBut, premiereIteration);
				}else{
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("\"");
					stringBuilder.append(nomBut);
					stringBuilder.append("\" pour la valeur \"");
					stringBuilder.append(valeurBut);
					stringBuilder
							.append("\" n'appartient pas à la base de fait et n'est produit par aucune règle.");
					System.out.println(stringBuilder.toString());
					// Aucune regle ne correspond, donc non atteignable
					return false;
				}
			}
		}
		if (reglesEnConflit.size() == 1) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("La règle \"");
			stringBuilder.append(reglesEnConflit.get(0));
			stringBuilder.append("\" permet de produire \"");
			stringBuilder.append(nomBut);
			stringBuilder.append("\" pour la valeur \"");
			stringBuilder.append(valeurBut);
			stringBuilder
					.append("\", on lance un chainage arrière sur chacune de ses prémisses.");
			System.out.println(stringBuilder.toString());
			// Une regle correspond donc on vérifie pour cette règle
			return execRegle(reglesEnConflit.get(0));
		} else {
			System.out.println("Plusieurs règles permettent de produire \""
					+ nomBut + "\" pour la valeur \"" + valeurBut + "\".");
			return execRegle(choixRegle(reglesEnConflit));
		}
	}

	private Regle choixRegle(final List<Regle> regleEnConflit) {
		/*
		 * On choisit une des règles valable.Actuellement, on choisit simplement
		 * la première
		 */
		Regle regle = null;
		switch (strategieConflit) {
		case ALEATOIRE:
			regle = regleEnConflit.get((new Double(Math.random()
					* (regleEnConflit.size() - 1))).intValue());
			System.out.println("On choisit aléatoirement la règle \"" + regle
					+ "\"");
			return regle;
		case PREMIERE_TROUVEE:
			regle = regleEnConflit.get(0);
			System.out
					.println("On choisit la première règle \"" + regle + "\"");
			return regle;
		case PLUS_PREMISSE:
			for (final Regle r : regleEnConflit) {
				/*
				 * On prend la premiere règle trouvée, puis on la remplace à
				 * chaque fois qu'on trouve une règle avec plus de prémisse
				 */
				if (regle == null) {
					regle = r;
				} else {
					if (regle.getNbPremisse() < r.getNbPremisse()) {
						regle = r;
					}
				}
			}
			System.out
					.println("On choisit la règle avec le plus de prémisse \""
							+ regle + "\"");
			return regle;
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
					elt.getValeur(), false);
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
	 * Methode permettant d'évaluer un élément (donc un nom, un operateur et une
	 * valeur)
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
	public Incoherence verifCoherence() {
		for (Incoherence incoherence : baseIncoherence.getListeIncoherence()) {
			if (incoherence.evaluate()) {
				return incoherence;
			}
		}
		return null;
	}

	public void menu() {
		Integer choice = null;
		while (choice == null || choice != 0) {
			StringBuilder affich = new StringBuilder("Menu du moteur 0+\n");
			affich.append("Entrez le numero de l'option que vous voulez utiliser\n");
			affich.append("0. Quitter\n");
			affich.append("1. Affichage\n");
			affich.append("2. Chainage avant\n");
			affich.append("3. Trace d'execution\n");
			affich.append("4. Chainage arrière\n");
			affich.append("5. Modifier stratégie de gestion des conflits\n");
			System.out.println(affich.toString());
			choice = null;
			while (choice == null || (choice < 0 && choice > 5)) {
				if (choice != null && (choice < 0 && choice > 5)) {
					System.out.println("!! Choix invalide !!\n");
				}
				try {
					choice = scan.nextInt();
				} catch (final InputMismatchException e) {
					System.out.println("!! Choix invalide !!\n");
					scan.nextLine();
				}
			}
			switch (choice) {
			case 1:
				System.out.println(toString() + "\n");
				waitForInput();
				break;
			case 2:
				chainageAvant();
				break;
			case 3:
				if (stackTrace.size() > 0) {
					printStackTrace();
				} else {
					System.out
							.println("Aucune trace d'execution disponible, veuillez executer le chainage avant en premier lieu.\n\n");
				}
				waitForInput();
				break;
			case 4:
				initChainageArrière();
				waitForInput();
				break;
			case 5:
				modifyGestionConflit();
				break;
			default:
				if (choice != 0) {
					throw new IllegalArgumentException(
							"Choix de menu incorrect");
				}
			}
		}
	}

	public void printStackTrace() {
		for (String s : stackTrace) {
			System.out.println(s);
		}
	}

	/**
	 * Cette methode permet de modifier le mode de gestion des conflits parmis
	 * ceux existants
	 */
	public void modifyGestionConflit() {
		Integer choice = null;
		while (choice == null || choice != 0) {
			StringBuilder sb = new StringBuilder(
					"Choississez le nouveau mode de gestion des conflits :\n");
			sb.append("0. Retour au menu\n");
			for (StrategieConflit s : StrategieConflit.getAll()) {
				sb.append(s.getNumero());
				sb.append(". ");
				sb.append(s.getDescription());
				sb.append("\n");
			}
			System.out.println(sb.toString());
			while (choice == null || (choice < 0 && choice > 5)) {
				try {
					choice = scan.nextInt();
				} catch (final InputMismatchException e) {
					System.out.println("!! Choix invalide !!\n");
				}
				final StrategieConflit s = StrategieConflit
						.getStrategieConflit(choice);
				if (s == null) {
					System.out.println("!! Choix invalide !!\n");
				} else {
					this.strategieConflit = s;
					return;
				}
			}
		}
	}
}