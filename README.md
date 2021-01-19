# react-login-poc


This is the **first approach of a PoC** about how to use a Liferay React Application to make calls to a third API with OpenId auth using the client side
The React app is npm-bundled using [liferay-js-generator](https://help.liferay.com/hc/en-us/articles/360029147391-Liferay-JS-Generator) or [Liferay Developer Studio] (https://help.liferay.com/hc/en-us/articles/360018151491-Introduction-to-Liferay-IDE-and-Liferay-Developer-Studio)

This workspace version **is based on [Liferay CE 7.3.5 GA6](https://hub.docker.com/layers/liferay/portal/7.3.5-ga6-d1.3.0-20201215234716/images/sha256-faa876881b7bf300f41aaae0faa59387e9c2417176730afc7b17790fa051bb5b?context=explore) version**: 

* Steps to build the modules:
  1. Execute `./gradlew clean build` command in the parent workspace folder

* Steps to deploy the modules: 

  1. Copy all generated jar files inside `${module}/build/libs/*.jar` to the deploy Liferay folder (usually located on `/opt/liferay/deploy`):
      `modules/callback/build/libs/callback-1.0.0.jar`
      `modules/portal-security-sso-openid-connect-impl-fragment/build/libs/portal.security.sso.openid.connect.impl.fragment-1.0.0.jar `
      `modules/silent-login-react-app/build/libs/silent.login.react.app-1.0.0.jar`

* Considerations in order to test the React App: 

1. Configure Portal level an OpenID Provider following the official documentation: https://help.liferay.com/hc/en-us/articles/360024805271-Authenticating-with-OpenID-Connect
2. Instance it on a widget private page of a site.
3. The Liferay portal is registered as a confidential client towards that IdP with clientID + secret.
4. The react app is another client (a public one) towards that same IdP. That way, if the user authenticates to the IDP to access the private page, he will have an authenticated session on the Authorization Server and the prompt=none token request will work transparently
If few interactions with the Authorization Server happen, the user may lose his session towards the Authorization server while still having an authenticated session on the portal. If that happens, the React widget will send the user to the OpenID Provider to re-authenticate.
