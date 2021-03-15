package com.liferay.sagov.oidc.refreshtoken;

import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ACCESS_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CLIENT_ID_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ID_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.REFRESH_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.SESSION_ATTR_JOINER_CHAR;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sagov.oidc.client.configuration.ClientSPAConfiguration;
import com.liferay.sagov.oidc.providerinfo.service.impl.OIDCProviderInfoServiceImpl;
import com.liferay.sagov.oidc.service.api.OIDCTokenService;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(
		configurationPid = "com.liferay.sagov.oidc.client.configuration.ClientSPAConfiguration",
		immediate = true,
		property = {
			"osgi.http.whiteboard.context.path=/",
			"osgi.http.whiteboard.servlet.pattern=/oidc/refresh_token/*"
		},
		service = Servlet.class
	)
public class OIDCRefreshTokenServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9164023656269894196L;

	@Override
	public void init() throws ServletException {
		if (_log.isInfoEnabled()) {
			_log.info("OIDC RefreshToken Servlet init");
		}

		super.init();
	}
 
	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {
		_log.info("OIDC RefreshToken Servlet GET");
		
		_log.info("User ID: " + PortalUtil.getUserId(httpServletRequest));
		_log.info("Company ID: " + PortalUtil.getCompanyId(httpServletRequest));
		
		
		String access_token = refreshToken(httpServletRequest, httpServletResponse);
		
		_writeJSONResponse(httpServletRequest, httpServletResponse, access_token);
	}


	private String refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		String access_token = "";
		try {
			HttpSession session = PortalUtil.getOriginalServletRequest(httpServletRequest).getSession(false);
			
			String clientId = (String) session.getAttribute(CLIENT_ID_ATTR);
			
			_log.info("ClientId retrieved from session for refresh_token -->"+ clientId);
			
			String refreshToken = "";
			
			if(Validator.isNotNull(clientId)) {
				refreshToken = (String) session.getAttribute(REFRESH_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId);
				String client_secret = getClientSecretFromConfiguration();
				
				Map<String, String> tokens = _OIDCTokenService.refreshToken(clientId, refreshToken, client_secret);
				
				_log.info("Token refreshed");
				
				access_token = tokens.get(ACCESS_TOKEN);
				
				_log.info("Acces_token "+access_token);
				_log.info("Token_refresh "+tokens.get(REFRESH_TOKEN));
				_log.info("Id_token "+tokens.get(ID_TOKEN));
				
				session.setAttribute(ACCESS_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId, tokens.get(ACCESS_TOKEN));
				session.setAttribute(REFRESH_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId, tokens.get(REFRESH_TOKEN));
				session.setAttribute(ID_TOKEN+SESSION_ATTR_JOINER_CHAR+clientId, tokens.get(REFRESH_TOKEN));
			}
			
			
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_PRECONDITION_FAILED);
		}
		
		return access_token;
	}
	
	private void _writeJSONResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String access_token) {
		
		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		try {
			
			ServletResponseUtil.write(
				httpServletResponse, _generateJSON(access_token));
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_PRECONDITION_FAILED);
		}
	}
	
	private String _generateJSON(String access_token) {

		JSONObject jsonObject =  JSONFactoryUtil.createJSONObject();
		jsonObject.put(ACCESS_TOKEN, access_token);
		
		return jsonObject.toJSONString();
		
	}	

	private static final Log _log = LogFactoryUtil.getLog(OIDCProviderInfoServiceImpl.class);

	@Reference
	protected OIDCTokenService _OIDCTokenService;
	
	public String getClientSecretFromConfiguration() {
		return _configuration.clientSecret();
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(ClientSPAConfiguration.class, properties);
	}
	
	private volatile ClientSPAConfiguration _configuration;

}