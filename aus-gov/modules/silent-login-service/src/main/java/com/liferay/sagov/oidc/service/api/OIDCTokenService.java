package com.liferay.sagov.oidc.service.api;

import java.util.Map;

public interface OIDCTokenService {
	
	public Map<String, String> getTokens(String clientId, String origin, String codeVerifier, String authorization_code, String client_secret);
	
	public Map<String, String> refreshToken(String clientId,String refresh_token, String client_secret );

}
