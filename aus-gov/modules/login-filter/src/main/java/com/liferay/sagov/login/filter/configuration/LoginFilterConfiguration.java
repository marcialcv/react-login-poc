package com.liferay.sagov.login.filter.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author marcialcalvo
 */
@ExtendedObjectClassDefinition(
	category = "login-filter",
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.sagov.login.filter.configuration.LoginFilterConfiguration",
	localization = "content/Language", name = "login-filter"
)
public interface LoginFilterConfiguration {

	@Meta.AD(
		deflt = "true", description = "enable-or-disable-login-filter",
		required = false
	)
	public boolean filterEnabled();
	
	@Meta.AD(
		deflt = "https://test-dpc-liferay-supported.apps.npe.ocp.sa.gov.au/", description = "id-verification-app",
		required = false
	)
	public String idVerificationApp();

}
