package org.generationcp.commons.service;

/**
 * This bean is typically populated in Spring application context using values in crossing.properties file.
 */
public class GermplasmNamingProperties {

	private String germplasmOriginNurseriesDefault;
	private String germplasmOriginNurseriesWheat;
	private String germplasmOriginNurseriesMaize;

	public String getGermplasmOriginNurseriesDefault() {
		return this.germplasmOriginNurseriesDefault;
	}

	public void setGermplasmOriginNurseriesDefault(String germplasmOriginNurseriesDefault) {
		this.germplasmOriginNurseriesDefault = germplasmOriginNurseriesDefault;
	}

	public String getGermplasmOriginNurseriesWheat() {
		return this.germplasmOriginNurseriesWheat;
	}

	public void setGermplasmOriginNurseriesWheat(String germplasmOriginNurseriesWheat) {
		this.germplasmOriginNurseriesWheat = germplasmOriginNurseriesWheat;
	}

	public String getGermplasmOriginNurseriesMaize() {
		return this.germplasmOriginNurseriesMaize;
	}

	public void setGermplasmOriginNurseriesMaize(String germplasmOriginNurseriesMaize) {
		this.germplasmOriginNurseriesMaize = germplasmOriginNurseriesMaize;
	}
}
