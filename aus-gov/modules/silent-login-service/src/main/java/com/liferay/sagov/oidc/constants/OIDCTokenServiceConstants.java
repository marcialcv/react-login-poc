package com.liferay.sagov.oidc.constants;

public class OIDCTokenServiceConstants {

	public static final String ACCESS_TOKEN = "access_token";
	
	public static final String REFRESH_TOKEN = "refresh_token";
	
	public static final String ID_TOKEN = "id_token";
	
	public static final String AUTHORIZATION_CODE = "authorization_code";
	
	public static final String ENCODE_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	
	public static final String GRANT_TYPE_ATTR = "grant_type";
	
	public static final String CODE_ATTR = "code";
	
	public static final String REDIRECT_URI_ATTR = "redirect_uri";
	
	public static final String CLIENT_ID_ATTR = "client_id";
	
	public static final String CODE_VERIFIER_ATTR= "code_verifier";
	
	public static final String S256_ALGORITHM = "SHA-256";
	
	public static final String SESSION_ATTR_JOINER_CHAR = "-";
	
	public static final String ORIGIN_ATTR= "origin";
	
	public static final String CHARS_CODE_VERIFIER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ"; 
	
	public static final String CLIENT_SECRET_ATTR = "client_secret";
	
	public static final String PORTAL_LOGIN_URL_WITH_REDIRECT_PARAM = "/c/portal/login?redirect=";
}