package com.liferay.sagov.oidc.client.configuration;


import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author marcialcalvo
 */
@ExtendedObjectClassDefinition(
	category = "client-spa-configuration",
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.sagov.oidc.client.configuration.ClientSPAConfiguration",
	localization = "content/Language", name = "client-spa-configuration"
)
public interface ClientSPAConfiguration {
	
	@Meta.AD(
			deflt = "true", description = "enable-or-disable-client-spa-portlet-filter",
			required = false
	)
	public boolean filterEnabled();

	@Meta.AD(
		deflt = "medfile-react-client-app", description = "set-app-client-id-registered-in-keycloak",
		required = false
	)
	public String clientId();
	
	@Meta.AD(
			deflt = "", description = "set-app-client-secret-registered-in-keycloak",
			required = false
	)
	public String clientSecret();
}
