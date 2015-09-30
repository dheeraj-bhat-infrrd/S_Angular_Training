package com.realtech.socialsurvey.core.entities;

/**
 * Dot loop participant entity
 */
public class DotLoopParticipant {

	private String name;
	private String email;
	private String role;
	private long participantId;
	private String memberOfMyTeam;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getParticipantId() {
		return participantId;
	}

	public void setParticipantId(long participantId) {
		this.participantId = participantId;
	}

	public String getMemberOfMyTeam() {
		return memberOfMyTeam;
	}

	public void setMemberOfMyTeam(String memberOfMyTeam) {
		this.memberOfMyTeam = memberOfMyTeam;
	}

}
