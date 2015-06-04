
package org.generationcp.commons.pojo;

public class GermplasmParents {

	private Integer gid;
	private String femaleParentName;
	private String maleParentName;
	private Integer fgid;
	private Integer mgid;

	public GermplasmParents(Integer gid, String femaleParentName, String maleParentName, Integer fgid, Integer mgid) {
		super();
		this.gid = gid;
		this.femaleParentName = femaleParentName;
		this.maleParentName = maleParentName;
		this.fgid = fgid;
		this.mgid = mgid;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getFemaleParentName() {
		return this.femaleParentName;
	}

	public void setFemaleParentName(String femaleParentName) {
		this.femaleParentName = femaleParentName;
	}

	public String getMaleParentName() {
		return this.maleParentName;
	}

	public void setMaleParentName(String maleParentName) {
		this.maleParentName = maleParentName;
	}

	public Integer getFgid() {
		return this.fgid;
	}

	public void setFgid(Integer fgid) {
		this.fgid = fgid;
	}

	public Integer getMgid() {
		return this.mgid;
	}

	public void setMgid(Integer mgid) {
		this.mgid = mgid;
	}
}
