package com.liferay.samples.fbo.oidc.providerinfo;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectProviderRegistry;
import com.liferay.samples.fbo.oidc.callback.OIDCCallbackServlet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property = {
			"osgi.http.whiteboard.context.path=/",
			"osgi.http.whiteboard.servlet.pattern=/oidc/providerinfo/*"
		},
		service = Servlet.class
	)
public class OIDCProviderInfoServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		if (_log.isInfoEnabled()) {
			_log.info("OIDC Provider Info Servlet init");
		}

		super.init();
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		_log.info("User ID: " + PortalUtil.getUserId(httpServletRequest));
		
		if (_log.isInfoEnabled()) {
			_log.info("doGet");
		}

		_writeProviderInfo(httpServletRequest, httpServletResponse);
	}

	/**
	 * OpenID Provider info
	 *
	 * @return OpenID Provider Auth URL
	 */
	private String _generateJSON(String clientId, String idProvider, long companyId) {
		
		StringBuffer sb = new StringBuffer();

		String authURL;
		String tokenURL;
		try {
			authURL = _openIdConnectProviderRegistry.findOpenIdConnectProvider(companyId,idProvider).getOIDCProviderMetadata().getAuthorizationEndpointURI().toString();
			tokenURL = _openIdConnectProviderRegistry.findOpenIdConnectProvider(companyId,idProvider).getOIDCProviderMetadata().getTokenEndpointURI().toString();
			
			_log.info("authURL: "+authURL);
			_log.info("tokenURL: "+tokenURL);
			
		} catch (Exception e) {
			_log.error("Failed to get OIDC Provider Authorization Endpoint URI");
			sb.append("{");
			sb.append("  error: 'failed to get authorization endpoint'");
			sb.append("}");
			return new String(sb);
		}

		sb.append("{");
		sb.append("  \"auth_url\": \"" + authURL + "\",");
		sb.append("  \"token_url\": \"" + tokenURL + "\"");
		sb.append("}");
		return new String(sb);
	}

	/**
	 * Write JSON
	 *
	 * @param httpServletResponse
	 */
	private void _writeProviderInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		try {

			String[] split = httpServletRequest.getPathInfo().split("/");
			
			String clientId = split[2];//"medfile-react-client-app";
			String idProvider = split[1];//"oi";

			_log.info("Client ID: " + clientId);
			_log.info("ID Provider: " + idProvider);
			
			long companyId = PortalUtil.getCompanyId(httpServletRequest);
			
			_log.info("Company ID: " + companyId);
			
			ServletResponseUtil.write(
				httpServletResponse, _generateJSON(clientId, idProvider, companyId));
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_PRECONDITION_FAILED);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(OIDCCallbackServlet.class);

	private static final long serialVersionUID = 1L;

	
	@Reference
	private OpenIdConnectProviderRegistry<OIDCClientMetadata, OIDCProviderMetadata> _openIdConnectProviderRegistry;

}