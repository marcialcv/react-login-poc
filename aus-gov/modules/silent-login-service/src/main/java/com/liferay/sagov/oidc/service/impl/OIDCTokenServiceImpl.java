package com.liferay.sagov.oidc.service.impl;

import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ACCESS_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.AUTHORIZATION_CODE;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CLIENT_ID_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CLIENT_SECRET_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CODE_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.CODE_VERIFIER_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ENCODE_TYPE_FORM_URLENCODED;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.GRANT_TYPE_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.ID_TOKEN;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.REDIRECT_URI_ATTR;
import static com.liferay.sagov.oidc.constants.OIDCTokenServiceConstants.REFRESH_TOKEN;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.sagov.oidc.providerinfo.service.api.OIDCProviderInfoService;
import com.liferay.sagov.oidc.service.api.OIDCTokenService;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import rest.consumer.api.RestConsumer;

@Component(
		immediate = true,
		property = {
		        "service.ranking:Integer=1"
		    },
		service = OIDCTokenService.class
	)
public class OIDCTokenServiceImpl implements OIDCTokenService {
	
	@Override
	public Map<String, String> getTokens(String clientId, String origin, String codeVerifier, String authorization_code, String client_secret) {
		
		_log.info("---GetToken--> OIDCTokenServiceImpl");
		
		_log.info("Retrieving OIDC ProviderInfo");
		OIDCProviderMetadata oidcProviderMetadata = getOIDCProviderInfo();
		String tokenURL = oidcProviderMetadata.getTokenEndpointURI().toString();
		
		Map<String, String> mapattrs = new HashMap<String, String>();
		mapattrs.put(GRANT_TYPE_ATTR, AUTHORIZATION_CODE);
		mapattrs.put(CODE_ATTR, authorization_code);
		mapattrs.put(REDIRECT_URI_ATTR, origin);
		mapattrs.put(CLIENT_ID_ATTR, clientId);
		mapattrs.put(CODE_VERIFIER_ATTR, codeVerifier);
		mapattrs.put(CLIENT_SECRET_ATTR, client_secret);

		Map< String, String> tokens = new HashMap<String, String>();
		try {
			String httpPostResponse = restConsumer.post(tokenURL, ENCODE_TYPE_FORM_URLENCODED, mapattrs);
			
			readAndGetTokens(httpPostResponse, tokens);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return tokens;
	}
	
	@Override
	public Map<String, String> refreshToken(String clientId, String refresh_token, String client_secret) {
		_log.info("**RefreshToken** OIDCTokenServiceImpl");
		
		_log.info("Retrieving OIDC ProviderInfo");
		OIDCProviderMetadata oidcProviderMetadata = getOIDCProviderInfo();
		String tokenURL = oidcProviderMetadata.getTokenEndpointURI().toString();
		
		Map<String, String> mapattrs = new HashMap<String, String>();
		mapattrs.put(GRANT_TYPE_ATTR, REFRESH_TOKEN);
		mapattrs.put(CLIENT_ID_ATTR, clientId);
		mapattrs.put(REFRESH_TOKEN, refresh_token);
		mapattrs.put(CLIENT_SECRET_ATTR, client_secret);

		Map< String, String> tokens = new HashMap<String, String>();
		try {
			String httpPostResponse = restConsumer.post(tokenURL, ENCODE_TYPE_FORM_URLENCODED, mapattrs);
			
			readAndGetTokens(httpPostResponse, tokens);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return tokens;
	}
	
	protected void readAndGetTokens(String httpPostResponse,Map<String, String> tokens){
		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(httpPostResponse);
			String accessToken = jsonObject.getString(ACCESS_TOKEN);
			String refreshToken = jsonObject.getString(REFRESH_TOKEN);
			String idToken = jsonObject.getString(ID_TOKEN);
			
			if(_log.isDebugEnabled()) {
				_log.debug("Access_token --> "+accessToken);
				_log.debug("Refresh_token --> "+refreshToken);
				_log.debug("Id_token --> "+idToken);
			}
			
			tokens.put(ACCESS_TOKEN, accessToken);
			tokens.put(REFRESH_TOKEN, refreshToken);
			tokens.put(ID_TOKEN, idToken);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected OIDCProviderMetadata getOIDCProviderInfo() {
		// TODO: Change defaultCompanyIdMethod
		return oidcProviderInfoService.getProviderMetadata(PortalUtil.getDefaultCompanyId());
	}
	
	private static final Log _log = LogFactoryUtil.getLog(OIDCTokenServiceImpl.class);

	@Reference
	OIDCProviderInfoService oidcProviderInfoService;
	
	@Reference
	RestConsumer restConsumer;
}
