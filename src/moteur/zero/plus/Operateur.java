package moteur.zero.plus;

/**
 * Enumeration représentant les opérateurs géré par le moteur d'inférence.
 * @author etudiant
 *
 */
public enum Operateur {
	SUPERIEUR(">"),
	
	SUPERIEUR_OU_EGAL(">="),
	
	INFERIEUR("<"),
	
	INFERIEUR_OU_EGAL("<="),
	
	EGAL("="),
	
	DIFFERENT("/=");
	
	private String symbole;
	
	private Operateur(final String symbole){
		this.symbole = symbole;
	}
	
	public String getSymbole(){
		return symbole;
	}
	
	public String toString(){
		return symbole;
	}
	
	public static Operateur testOperateur(String s)
	{
		if(s.equals(">"))
			return Operateur.SUPERIEUR ;
		else if(s.equals("<"))
				return Operateur.INFERIEUR ;
			else if(s.equals(">=")) 
				return Operateur.SUPERIEUR_OU_EGAL ;			
			else if(s.equals("<=")) 
				return Operateur.INFERIEUR_OU_EGAL ;
			else if(s.equals("=")) 
				return Operateur.EGAL ;
			else
				return Operateur.DIFFERENT ;
	}
	
}

