package com.liferay.sagov.login.filter.configuration;


import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

/**
 * @author marcialcalvo
 */
@Component(service = ConfigurationCategory.class)
public class LoginFilterConfigurationCategory 
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

	private static final String _CATEGORY_ICON = "filter";

	private static final String _CATEGORY_KEY = "login-filter";

	private static final String _CATEGORY_SECTION = "platform";

}