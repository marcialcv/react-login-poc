package silent.login.angular.app.portlet;

import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ACCESS_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CLIENT_ID_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ID_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.REFRESH_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.SESSION_ATTR_JOINER_CHAR;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import silent.login.angular.app.constants.SilentLoginAngularAppPortletKeys;

/**
 * @author roselainedefaria
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + SilentLoginAngularAppPortletKeys.SilentLoginAngularApp,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false"
	},
	service = Portlet.class
)
public class SilentLoginAngularAppPortlet extends MVCPortlet {
	
	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
		
		// Get tokens
		String accessToken = getTokens(renderRequest).get(ACCESS_TOKEN);

		_log.info("AccessToken -> " + accessToken);

		renderRequest.setAttribute("accessToken", accessToken);
		renderRequest.setAttribute("heroName", "Windstorm");
		 
		//TODO send tokens to Angular piece

		renderRequest.setAttribute(
			"mainRequire",
			_npmResolver.resolveModuleName("silent-login-angular-app") + " as main");

		super.doView(renderRequest, renderResponse);
	}
	
	
	protected Map<String, String> getTokens(RenderRequest renderRequest) {
		Map<String, String> tokens = new HashMap<String, String>();
		HttpSession session = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)).getSession(false);
		
		String clientId = (String) session.getAttribute(CLIENT_ID_ATTR);

		_log.info("ClientId from session in SPA server side --> "+ clientId);
	
		String accessToken = "";
		String refreshToken = "";
		String idToken = "";
		if(Validator.isNotNull(clientId)) {
			accessToken = (String) session.getAttribute(ACCESS_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId);
			refreshToken = (String) session.getAttribute(REFRESH_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId);
			idToken = (String) session.getAttribute(ID_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId);
			
			tokens.put(ACCESS_TOKEN, accessToken);
			tokens.put(REFRESH_TOKEN, refreshToken);
			tokens.put(ID_TOKEN, idToken);
		}
		
		_log.info("Access_token ---> " +accessToken);
		_log.info("Refresh_token --> "+refreshToken);
		_log.info("Id_token -------> "+idToken);
		
		return tokens;
	}

	@Reference
	private NPMResolver _npmResolver;
	
	private static final Log _log = LogFactoryUtil.getLog(SilentLoginAngularAppPortlet.class);

}