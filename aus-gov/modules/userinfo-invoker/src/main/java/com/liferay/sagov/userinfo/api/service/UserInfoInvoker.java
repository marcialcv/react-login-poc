package com.liferay.sagov.userinfo.api.service;

import com.liferay.sagov.userinfo.api.model.response.UserInfoResponse;

public interface UserInfoInvoker {
	
	public UserInfoResponse getUserInfo(String jwt, String idProvider);

}