package model.cards;

/**
 * 
 * @author EisfreieEleven
 * 
 *         enthaelt alle Ressourcen
 *
 */
public class ResourceCard extends Card {

	private ResourceType resourceType;
	private boolean isVisible;
	
	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

}
