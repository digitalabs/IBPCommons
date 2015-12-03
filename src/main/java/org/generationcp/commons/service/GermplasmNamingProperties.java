package org.generationcp.commons.service;

/**
 * This bean is typically populated in Spring application context using values in crossing.properties file.
 */
public class GermplasmNamingProperties {

	private String germplasmOriginNurseriesDefault;
	private String germplasmOriginTrialsDefault;

	private String germplasmOriginNurseriesWheat;
	private String germplasmOriginTrialsWheat;

	private String germplasmOriginNurseriesMaize;
	private String germplasmOriginTrialsMaize;

	public String getGermplasmOriginNurseriesDefault() {
		return this.germplasmOriginNurseriesDefault;
	}

	public void setGermplasmOriginNurseriesDefault(String germplasmOriginNurseriesDefault) {
		this.germplasmOriginNurseriesDefault = germplasmOriginNurseriesDefault;
	}

	public String getGermplasmOriginTrialsDefault() {
		return this.germplasmOriginTrialsDefault;
	}

	public void setGermplasmOriginTrialsDefault(String germplasmOriginTrialsDefault) {
		this.germplasmOriginTrialsDefault = germplasmOriginTrialsDefault;
	}

	public String getGermplasmOriginNurseriesWheat() {
		return this.germplasmOriginNurseriesWheat;
	}

	public void setGermplasmOriginNurseriesWheat(String germplasmOriginNurseriesWheat) {
		this.germplasmOriginNurseriesWheat = germplasmOriginNurseriesWheat;
	}

	public String getGermplasmOriginTrialsWheat() {
		return this.germplasmOriginTrialsWheat;
	}

	public void setGermplasmOriginTrialsWheat(String germplasmOriginTrialsWheat) {
		this.germplasmOriginTrialsWheat = germplasmOriginTrialsWheat;
	}

	public String getGermplasmOriginNurseriesMaize() {
		return this.germplasmOriginNurseriesMaize;
	}

	public void setGermplasmOriginNurseriesMaize(String germplasmOriginNurseriesMaize) {
		this.germplasmOriginNurseriesMaize = germplasmOriginNurseriesMaize;
	}

	public String getGermplasmOriginTrialsMaize() {
		return this.germplasmOriginTrialsMaize;
	}

	public void setGermplasmOriginTrialsMaize(String germplasmOriginTrialsMaize) {
		this.germplasmOriginTrialsMaize = germplasmOriginTrialsMaize;
	}
}
