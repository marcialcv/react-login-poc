package com.liferay.sagov.userinfo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectProviderRegistry;
import com.liferay.sagov.userinfo.api.model.response.UserInfoResponse;
import com.liferay.sagov.userinfo.api.service.UserInfoInvoker;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Component(immediate = true, service = UserInfoInvoker.class)
public class UserInfoInvokerImpl implements UserInfoInvoker {

	@Override
	public UserInfoResponse getUserInfo(String jwt, String idProvider) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = getEntity(jwt);

		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		
		try {
			String userInfoURL = _openIdConnectProviderRegistry
					.findOpenIdConnectProvider(PortalUtil.getDefaultCompanyId(), idProvider).getOIDCProviderMetadata()
					.getUserInfoEndpointURI().toString();
			response = restTemplate.exchange(userInfoURL, HttpMethod.GET, entity, String.class);
			
			_log.debug(response);
			
		} catch (Exception e) {
			_log.error(e);
		}

		UserInfoResponse userInfoResponse = new UserInfoResponse();

		try {
			userInfoResponse = objectMapper.readValue(response.getBody(), UserInfoResponse.class);
		} catch (JsonProcessingException e) {
			_log.error(e);
		}
		return userInfoResponse;
	}
	
	private HttpEntity<String> getEntity(String jwt) {
		HttpHeaders httpHeaders = getHttpHeaders(jwt);

		return new HttpEntity<>(httpHeaders);
	}

	private HttpHeaders getHttpHeaders(String jwt) {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(jwt);

		return headers;
	}
	
	protected ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@Reference
	private OpenIdConnectProviderRegistry<OIDCClientMetadata, OIDCProviderMetadata> _openIdConnectProviderRegistry;
	
	private static final Log _log = LogFactoryUtil.getLog(UserInfoInvokerImpl.class);

}
