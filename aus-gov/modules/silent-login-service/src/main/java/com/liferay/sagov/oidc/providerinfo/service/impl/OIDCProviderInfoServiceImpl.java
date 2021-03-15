package com.liferay.sagov.oidc.providerinfo.service.impl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectProviderRegistry;
import com.liferay.sagov.oidc.providerinfo.service.api.OIDCProviderInfoService;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property = {
		        "service.ranking:Integer=1"
		    },
		service = OIDCProviderInfoService.class
	)
public class OIDCProviderInfoServiceImpl implements OIDCProviderInfoService {


	/**
	 * OpenID Provider info
	 *
	 * @return OpenID ProviderMetadata
	 */
	public OIDCProviderMetadata getProviderMetadata(long companyId) {
		
		OIDCProviderMetadata oidcProviderMetadata = null;
		try {
			String idProvider = _openIdConnectProviderRegistry.getOpenIdConnectProviderNames(companyId).stream().findFirst().get();
			oidcProviderMetadata = _openIdConnectProviderRegistry.findOpenIdConnectProvider(companyId, idProvider).getOIDCProviderMetadata();
			
			if(_log.isDebugEnabled()) {
				String authURL = _openIdConnectProviderRegistry.findOpenIdConnectProvider(companyId,idProvider).getOIDCProviderMetadata().getAuthorizationEndpointURI().toString();
				String tokenURL = _openIdConnectProviderRegistry.findOpenIdConnectProvider(companyId,idProvider).getOIDCProviderMetadata().getTokenEndpointURI().toString();
				
				_log.debug("authURL: "+authURL);
				_log.debug("tokenURL: "+tokenURL);
			}
			
		} catch (Exception e) {
			_log.error("Failed to get OIDC Provider Authorization Endpoint URI");
		}

		return oidcProviderMetadata;
	}


	private static final Log _log = LogFactoryUtil.getLog(OIDCProviderInfoServiceImpl.class);

	
	@Reference
	private OpenIdConnectProviderRegistry<OIDCClientMetadata, OIDCProviderMetadata> _openIdConnectProviderRegistry;

}