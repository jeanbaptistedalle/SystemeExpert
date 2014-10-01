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
 * @author etudiant
 *
 */
public class Moteur {

	public static BaseFait baseFait = new BaseFait();
	public static BaseFait demandable = new BaseFait();
	public static BaseRegle baseRegle = new BaseRegle();
	public static BaseIncoherence baseIncoherence = new BaseIncoherence();
	public static List<String> stackTrace = new ArrayList<String>();

	public Moteur() {
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
	 * @throws IOException 
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
					nomElement = tabRegle[0] ;
					operateur = Operateur.testOperateur(tabRegle[1]) ;
					valElement = tabRegle[2] ;
					tmp = new Element(nomElement, operateur, valElement) ;
					listElement.add(tmp);
				}
				r.addElements(listElement);
			}
		}
		
		while((ligne = lecteurFait.readLine()) != null)
		{
			tabFait = ligne.split("[=]") ;
			
			baseFait.addFait(tabFait[0], tabFait[1]);
		
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
			System.out.println(this);
			/*
			 * try { Thread.sleep(10000); } catch (final InterruptedException e)
			 * { throw new RuntimeException(e); }
			 */
		}
	}

	/**
	 * Methode executant un chainage arrière
	 * 
	 * @param but
	 */
	public boolean chainageArriere(final String nomBut, final String valeurBut) {
		/* 1er cas : le but se trouve déjà dans la base de fait */
		if (baseFait.contains(nomBut)) {
			if (valeurBut.equals(baseFait.get(nomBut))) {
				return true;
			}
		}
		/* 2eme cas : le but est deductible */
		for (Regle r : baseRegle.getRegles()) {
			/*
			 * actuellement, les conflits ne sont géré que par la premiere règle
			 * trouvée
			 */
			if (r.getNomResultat().equals(nomBut)
					&& r.getValeurResultat().equals(valeurBut)) {
				Element elt = r.getElement();
				boolean result = false;
				while (elt != null) {
					result = chainageArriere(elt.getNom(),
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
			}
		}
		return false;
	}
}
