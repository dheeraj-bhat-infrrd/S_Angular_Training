package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the statelookup database table.
 * 
 */
@Entity
@Table(name = "statelookup")
@NamedQuery(name = "StateLookup.findAll", query = "SELECT s FROM StateLookup s")
public class StateLookup implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String statecode;
	private String statename;
	private List<ZipCodeLookup> codeLookups;

	public StateLookup() {
	}

	public StateLookup(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatecode() {
		return statecode;
	}

	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}

	public String getStatename() {
		return statename;
	}

	public void setStatename(String statename) {
		this.statename = statename;
	}

	@OneToMany(mappedBy = "stateLookup", fetch = FetchType.LAZY)
	public List<ZipCodeLookup> getCodeLookups() {
		return this.codeLookups;
	}

	public void setCodeLookups(List<ZipCodeLookup> codeLookups) {
		this.codeLookups = codeLookups;
	}

}