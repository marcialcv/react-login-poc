import  React  from  'react';
var  Hashes = require('jshashes');
//const  cryptoRandomString = require('crypto-random-string');

class Login extends React.Component {

  

    handleMessageEvent(event) {
	    console.log("handleMessageEvent");
		if(event.data.client_id === this.props.clientId) {
		    if(event.data.interaction_required) {
			    this.triggerRedirect();
		    } else  if (event.data.data !== null) {
			    console.log("Authorization code received: " + event.data.code);
			    let  xhr = new  XMLHttpRequest();
			    console.log("state tokenURL value --> "+this.state.tokenURL);
			    xhr.open("POST", this.state.tokenURL);
			    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			    xhr.onreadystatechange = function () {
				    if (xhr.readyState === 4) {
				    	console.log("Status --> " +xhr.status);
					    if (xhr.status === 200) {
						    var  json_obj = JSON.parse(xhr.responseText);
						    this.setState({idToken: json_obj.id_token});
						    this.setState({accessToken:  json_obj.access_token});
						    this.props.action(json_obj.access_token);
					    } else {
						    console.error(xhr.statusText);
					    }
				    }
			    }.bind(this);
			    xhr.onerror = function () {
					    console.error(xhr.statusText);
			    };
			    let  callbackRedirectUri = window.origin + "/o/oidc/callback/" + this.props.idProvider + "/" + this.props.clientId;
			    let  tokenRequestData = "grant_type=authorization_code&code=" + event.data.code + "&redirect_uri=" + callbackRedirectUri + "&client_id=" + this.props.clientId;
			    if (this.props.pkce !== null) {
				    tokenRequestData += "&code_verifier=" + this.state.codeVerifier;
			    }
			    console.log("tokenRequestData value --> "+ tokenRequestData);
			    xhr.send(tokenRequestData);
		    }
	    }
    }

    getOIDCUrlAndDo(action) {
	    console.log("getAuthUrlAndDo");
	    console.log("action value --> "+ action);
	    console.log("this.state.authURL --> "+ this.state.authURL)
	    
	    if(this.state.authURL === null) {
	    	console.log("enter");
		    let  xhr = new  XMLHttpRequest();
		    xhr.open("GET", "/o/oidc/providerinfo/" + this.props.idProvider + "/" + this.props.clientId);
		    xhr.onreadystatechange = function () {
		    	console.log("xhr.readyState value --> "+ xhr.readyState);
			    if (xhr.readyState === 4) {
			    	console.log("xhr.status value --> "+ xhr.status);
				    if (xhr.status === 200) {
				    	
					    var  json_obj = JSON.parse(xhr.responseText);
					    this.state.authURL = json_obj.auth_url;
					    this.state.tokenURL = json_obj.token_url;
					    console.log("token_url value --> "+ json_obj.token_url);
					    action(json_obj.auth_url);
				    } else {
					    console.error(xhr.statusText);
				    }
			    }
		    }.bind(this);
		    xhr.onerror = function () {
			    console.error(xhr.statusText);
		    };
		    xhr.send();
	    } else {
	    	console.log("NO enter");
		    action(this.state.authURL);
	    }
    }

    triggerRedirect() {
	    console.log("triggerRedirect");
	    this.getOIDCUrlAndDo(this.doTriggerRedirect);
    }

    loadSilentLoginIframe() {
	    console.log("loadSilentLoginIframe");
	    this.getOIDCUrlAndDo(this.doLoadSilentLoginIframe);
    }

    requestNewAccessToken() {
	    console.log("requestNewAccessToken");
	    this.state.accessToken = null;
	    this.state.idToken = null;
	    this.doLoadSilentLoginIframe(this.state.authURL);
    }

	doLoadSilentLoginIframe(authURL) {
	    console.log("doLoadSilentLoginIframe: " + authURL);
	    let  callbackRedirectUri = window.origin + "/o/oidc/callback/" + this.props.idProvider + "/" + this.props.clientId;
	    let  callbackEncodedRedirectUri = encodeURIComponent(callbackRedirectUri);
	    let  location = authURL + "?response_type=code&state=silent&client_id=" + this.props.clientId + "&scope=openid&redirect_uri=" + callbackEncodedRedirectUri + "&prompt=none";
	    if (this.props.pkce === "S256") {
		    let  codeVerifier = this.generateRandomCodeVerifier();
		    this.setState({codeVerifier:  codeVerifier});
		    let  challenge = this.base64URIEncode(new  Hashes.SHA256().b64(codeVerifier));
		    console.log("Code challenge: " + challenge);
		    location = location + '&code_challenge_method=S256&code_challenge=' + challenge;
	    } else  if (this.props.pkce === "plain") {
		    let  codeVerifier = this.generateRandomCodeVerifier();
		    this.setState({codeVerifier:  codeVerifier});
		    location = location + '&code_challenge_method=S256&code_challenge=' + codeVerifier;
	    }
	    this.loginIframeRef.current.src = location;
	}

    doTriggerRedirect(authURL) {
	    console.log("doTriggerRedirect: " + authURL);
	    let  redirectUri = document.location;
	    let  encodedRedirectUri = encodeURIComponent(redirectUri);
	    document.location = authURL + "?response_type=code&state=prompt&client_id=" + this.props.clientId + "&scope=openid&redirect_uri=" + encodedRedirectUri;
    }

	    // [A-Z] / [a-z] / [0-9] / "-" / "." / "_" / "~" (43 to 128 chars long)
	generateRandomCodeVerifier() {
//	    let  result = cryptoRandomString({length:  43, characters:  'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~'});
	    let result = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ";
		console.log("Generated code verifier: " + result);
		return  result;
    }

    base64URIEncode(message) {
	    let  result = message.split('+').join('-').split('/').join('_').split("=").join('');

	    return  result;
    }

    constructor(props) {
        super(props);
        
        this.state = {
        	    accessToken:  null,
        	    authURL:  null,
                tokenURL:  null,
        	    codeVerifier:  null,
        	    idToken: null
            }
       
	    // This line allows me to call this.setState from inside of the handleMessageEvent method
	    this.handleMessageEvent = this.handleMessageEvent.bind(this);
	    this.doTriggerRedirect = this.doTriggerRedirect.bind(this);
	    this.triggerRedirect = this.triggerRedirect.bind(this);
	    this.doLoadSilentLoginIframe = this.doLoadSilentLoginIframe.bind(this);
	    this.loadSilentLoginIframe = this.loadSilentLoginIframe.bind(this);
	    this.getOIDCUrlAndDo = this.getOIDCUrlAndDo.bind(this);
	    this.requestNewAccessToken = this.requestNewAccessToken.bind(this);
	    
    }

    componentDidMount() {
	    if(this.state.accessToken === null) {
		    this.loadSilentLoginIframe();
		    window.addEventListener('message', this.handleMessageEvent);
	    }
    }
    render() {
	    this.loginIframeRef = React.createRef();
	    let  loginIframe = <iframe className='hidden' title='Silent login iframe'  ref={this.loginIframeRef} ></iframe>;
	    return (
		    <div  className="Login">
			    <p>Authorization URL: {this.state.authURL}</p>
			    <p>Access token: {this.state.accessToken}</p>
			    <p>ID token: {this.state.idToken}</p>
			    {loginIframe}
			</div>
	    );
    }
}
export  default  Login;