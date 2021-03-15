package com.liferay.sagov.login.filter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.sagov.login.filter.configuration.LoginFilterConfiguration;
import com.liferay.sagov.userinfo.api.model.response.UserInfoResponse;
import com.liferay.sagov.userinfo.api.service.UserInfoInvoker;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(configurationPid = "com.liferay.sagov.login.filter.configuration.LoginFilterConfiguration", immediate = true, service = AutoLogin.class, property = {
		"service.ranking:Integer=-100" })
public class FilterAutologin extends BaseAutoLogin {

	private static final String IPLEVEL_1 = "IP1";

	public boolean isLoginFilterEnabled() {
		return _configuration.filterEnabled();
	}

	public String getIDVerificationAppURL() {
		return _configuration.idVerificationApp();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(LoginFilterConfiguration.class, properties);
	}

	@Override
	protected String[] doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (isLoginFilterEnabled()) {
			try {
				HttpSession session = PortalUtil.getOriginalServletRequest(request).getSession();

				if (session
						.getAttribute(OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION) instanceof OpenIdConnectSession) {

					OpenIdConnectSession openIdConnectSession = (OpenIdConnectSession) session
							.getAttribute(OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION);

					String jwtToken = openIdConnectSession.getAccessTokenValue();
					String idProvider = openIdConnectSession.getOpenIdProviderName();

					if(_log.isDebugEnabled()) {
						_log.debug("Token " + jwtToken);
						_log.debug("IDProvider_name " + idProvider);
					}

					UserInfoResponse userInfo = userInfoInvoker.getUserInfo(jwtToken, idProvider);

					if(_log.isDebugEnabled()) {
						_log.debug("IPLevel --> "+userInfo.getIPLevel());
					}
					if (IPLEVEL_1.equals(userInfo.getIPLevel())) {
						if(_log.isDebugEnabled()) {
							_log.debug("Sending redirect to --> "+getIDVerificationAppURL());
							
						}
						
						request.getSession().invalidate();
						response.sendRedirect(getIDVerificationAppURL());
					}

				}

			} catch (Exception e) {
				_log.error(e);
			}
		}

		return null;

	}

	@Reference
	protected UserInfoInvoker userInfoInvoker;

	private static final Log _log = LogFactoryUtil.getLog(FilterAutologin.class);

	private volatile LoginFilterConfiguration _configuration;
}