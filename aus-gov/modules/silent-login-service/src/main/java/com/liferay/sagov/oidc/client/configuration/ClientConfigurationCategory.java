package com.liferay.sagov.oidc.client.configuration;



import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

/**
 * @author marcialcalvo
 */
@Component(service = ConfigurationCategory.class)
public class ClientConfigurationCategory 
	implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return _CATEGORY_ICON;
	}

	@Override
	public String getCategoryKey() {
		return _CATEGORY_KEY;
	}

	@Override
	public String getCategorySection() {
		return _CATEGORY_SECTION;
	}

	private static final String _CATEGORY_ICON = "lock";

	private static final String _CATEGORY_KEY = "client-spa-configuration";

	private static final String _CATEGORY_SECTION = "platform";

}