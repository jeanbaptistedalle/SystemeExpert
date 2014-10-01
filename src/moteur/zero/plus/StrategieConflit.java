package moteur.zero.plus;

/**
 * Enum réferençant les stratégie de gestion de conflit
 * 
 * @author JBD
 *
 */
public enum StrategieConflit {
	ALEATOIRE("Choix d'une règle aléatoirement"),
	
	PREMIERE_TROUVEE("Choix de la première règle trouvée"),
	
	PLUS_PREMISSE("Choix de la règle ayant le plus de prémisse à satisfaire");
	
	private String description;
	
	private StrategieConflit(final String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
}
