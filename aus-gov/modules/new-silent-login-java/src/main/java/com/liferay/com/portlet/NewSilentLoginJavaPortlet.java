package com.liferay.com.portlet;
import com.liferay.com.constants.NewSilentLoginJavaPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import javax.portlet.*;
import org.osgi.service.component.annotations.Component;
/**
 * @author roselainedefaria
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=NewSilentLoginJava",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + NewSilentLoginJavaPortletKeys.NEWSILENTLOGINJAVA,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class NewSilentLoginJavaPortlet extends MVCPortlet {

}