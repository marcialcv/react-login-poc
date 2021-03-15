package com.liferay.sagov.oidc.providerinfo.service.api;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

public interface OIDCProviderInfoService {

	OIDCProviderMetadata getProviderMetadata(long companyId);
	
}
