package com.liferay.sagov.userinfo.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {

	private boolean emailVerified = false;

	@JsonProperty("IPLevel")
	private String ipLevel = null;

	public String sub = null;

	public String birthdate = null;

	public String name = null;

	public String preferred_username = null;

	public String given_name = null;

	public String family_name = null;

	public String email = null;

	public boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	@JsonProperty("IPLevel")
	public String getIPLevel() {
		return ipLevel;
	}

	@JsonProperty("IPLevel")
	public void setIPLevel(String ipLevel) {
		this.ipLevel = ipLevel;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreferred_username() {
		return preferred_username;
	}

	public void setPreferred_username(String preferred_username) {
		this.preferred_username = preferred_username;
	}

	public String getGiven_name() {
		return given_name;
	}

	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}