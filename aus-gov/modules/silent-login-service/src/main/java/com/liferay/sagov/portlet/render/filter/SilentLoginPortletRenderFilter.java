package com.liferay.sagov.portlet.render.filter;

import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ACCESS_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CHARS_CODE_VERIFIER;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CLIENT_ID_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CODE_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CODE_VERIFIER_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ID_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ORIGIN_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.PORTAL_LOGIN_URL_WITH_REDIRECT_PARAM;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.REFRESH_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.S256_ALGORITHM;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.SESSION_ATTR_JOINER_CHAR;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sagov.oidc.client.configuration.ClientSPAConfiguration;
import com.liferay.sagov.oidc.providerinfo.service.api.OIDCProviderInfoService;
import com.liferay.sagov.oidc.service.api.OIDCTokenService;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(configurationPid = "com.liferay.sagov.oidc.client.configuration.ClientSPAConfiguration", immediate = true, property = {
		"javax.portlet.name=" + "silentloginangularapp", // note that you can add more than one portlet to activate the
															// filtering
		"service.ranking:Integer=1" }, service = PortletFilter.class)
public class SilentLoginPortletRenderFilter implements RenderFilter {

	@Override
	public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain)
			throws IOException, PortletException {

		if (isFilterEnabled()) {

			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			String redirect = PortalUtil.getPortalURL(request) + PortalUtil.getCurrentURL(request);
			HttpServletResponse httpservletResponse = PortalUtil.getHttpServletResponse(response);

			if (!themeDisplay.isSignedIn()) {
				httpservletResponse.sendRedirect(PORTAL_LOGIN_URL_WITH_REDIRECT_PARAM + redirect);
			}

			_log.info("Portlet Filter execution");

			String clientId = getClientIdFromConfiguration();

			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));

			HttpSession session = originalServletRequest.getSession(false);

			if (Validator.isNull(session.getAttribute(ACCESS_TOKEN))
					&& Validator.isNull(session.getAttribute(CODE_VERIFIER_ATTR))) {
				_log.info("Retrieving OIDC ProviderInfo");

				OIDCProviderMetadata oidcProviderMetadata = getOIDCProviderInfo();
				String authURL = oidcProviderMetadata.getAuthorizationEndpointURI().toString();
				String tokenURL = oidcProviderMetadata.getTokenEndpointURI().toString();

				if (_log.isDebugEnabled()) {
					_log.debug("AuthURL retrieved: " + authURL);
					_log.debug("TokenURL retrieved: " + tokenURL);
				}

				_log.debug("Redirect --> " + redirect);

				String callbackRedirectUri = redirect;
				String callbackEncodedRedirectUri = URLCodec.encodeURL(callbackRedirectUri);
				String requestURL = authURL + "?response_type=code&state=silent&client_id=" + clientId
						+ "&scope=openid&redirect_uri=" + callbackEncodedRedirectUri + "&prompt=none";
				String codeVerifier = RandomUtil.shuffle(CHARS_CODE_VERIFIER);

				_log.debug("Generated codeVerifier --> " + codeVerifier);

				_log.debug("Stored codeVerifier in session");

				session.setAttribute(CODE_VERIFIER_ATTR, codeVerifier);
				session.setAttribute(ORIGIN_ATTR, redirect);
				session.setAttribute(CLIENT_ID_ATTR, clientId);

				String challenge = base64(codeVerifier);
				_log.debug("Code challenge: " + challenge);

				requestURL = requestURL + "&code_challenge_method=S256&code_challenge=" + challenge;

				_log.debug("RequestURL --> " + requestURL);

				httpservletResponse.sendRedirect(requestURL);

			}

			else {
				Map<String, String> tokens = new HashMap<String, String>();

				String codeVerifier = (String) session.getAttribute(CODE_VERIFIER_ATTR);
				String origin = (String) session.getAttribute(ORIGIN_ATTR);

				_log.info("ClientId from session in SPA server side --> " + clientId);

				String accessToken = (String) session.getAttribute(ACCESS_TOKEN + SESSION_ATTR_JOINER_CHAR + clientId);
				String refreshToken = (String) session
						.getAttribute(REFRESH_TOKEN + SESSION_ATTR_JOINER_CHAR + clientId);
				String idToken = (String) session.getAttribute(ID_TOKEN + SESSION_ATTR_JOINER_CHAR + clientId);

				if (Validator.isNull(accessToken)) {

					String authorizationCode = ParamUtil.get(originalServletRequest, CODE_ATTR, "");

					_log.info("Code Verifier retrieved from session " + codeVerifier);

					_log.info("Athorization code retrieved from url param " + authorizationCode);

					_log.info("Origin from SPA---> " + origin);

					String client_secret = getClientSecretFromConfiguration();

					tokens = oidcTokenService.getTokens(clientId, origin, codeVerifier, authorizationCode,
							client_secret);

					accessToken = tokens.get(ACCESS_TOKEN);
					refreshToken = tokens.get(REFRESH_TOKEN);
					idToken = tokens.get(ID_TOKEN);

					session.setAttribute(ACCESS_TOKEN + SESSION_ATTR_JOINER_CHAR + clientId, accessToken);
					session.setAttribute(REFRESH_TOKEN + SESSION_ATTR_JOINER_CHAR + clientId, refreshToken);
					session.setAttribute(ID_TOKEN + SESSION_ATTR_JOINER_CHAR + clientId, idToken);
					session.removeAttribute(CODE_VERIFIER_ATTR);
				}

				_log.info(accessToken);
				_log.info(refreshToken);
				_log.info(idToken);

				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {

	}

	@Override
	public void destroy() {

	}

	public static String base64(final String clearText) {
		String result = "";
		try {
			result = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(MessageDigest.getInstance(S256_ALGORITHM).digest(clearText.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	protected OIDCProviderMetadata getOIDCProviderInfo() {
		// TODO: Change defaultCompanyIdMethod
		return oidcProviderInfoService.getProviderMetadata(PortalUtil.getDefaultCompanyId());
	}

	private static final Log _log = LogFactoryUtil.getLog(SilentLoginPortletRenderFilter.class);

	@Reference
	OIDCProviderInfoService oidcProviderInfoService;

	public String getClientIdFromConfiguration() {
		return _configuration.clientId();
	}

	public String getClientSecretFromConfiguration() {
		return _configuration.clientSecret();
	}

	public boolean isFilterEnabled() {
		return _configuration.filterEnabled();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(ClientSPAConfiguration.class, properties);
	}

	@Reference
	protected OIDCTokenService oidcTokenService;

	private volatile ClientSPAConfiguration _configuration;
}