import React from 'react';
import Login from './Login';
// import './App.css';
import ReactDOM from 'react-dom';
// import properties about de login. Another options here when you are using a hybrid portlet (React + Java) is create configurations and recover System Setting with Java. See more -> https://help.liferay.com/hc/en-us/articles/360018161391-Making-Your-Applications-Configurable
import properties from './properties/properties';

class App extends React.Component {

  makeApiCall() {
    // This block is the way to make calls to an API

    console.log('makeAPI properties ->', properties)

    var xhr = new XMLHttpRequest();
    xhr.open("GET", "https://webserver-lfrspaings-prd.lfr.cloud/o/custom-api/users/123", true);
    xhr.setRequestHeader("Authorization", "Bearer " + this.state.accessToken);
    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          var response = JSON.parse(xhr.responseText);
          console.log(response);
          this.setState({ firstName: response.firstName });
        } else if (xhr.status === 401) {
          console.log("Token has expired!");
          this.requestNewAccessToken();
        } else {
          console.error(xhr.statusText);
        }
      }
    }.bind(this);
    xhr.onerror = function () {
      console.error(xhr.statusText);
    };
    xhr.send();

  }

  requestNewAccessToken() {
    this.setState({ accessToken: null });
    this._login.current.requestNewAccessToken();
  }

  handleAccessToken(accessToken) {
    this.setState({ accessToken: accessToken })
    this.makeApiCall();
  }

  constructor(props) {
    super(props);


    console.log(this.props);
    this.state = {
      accessToken: null,
      firstName: "world"
    }
    this.makeApiCall = this.makeApiCall.bind(this);
    this.handleAccessToken = this.handleAccessToken.bind(this);
    this._login = React.createRef();
  }

  render() {
    return (
      <div className="App">
        <h1>Hello, {this.state.firstName}!</h1>
        <Login ref={this._login} action={this.handleAccessToken} clientId={properties.clientId} idProvider={properties.idProvider} pkce="S256" />
        <button onClick={this.makeApiCall}>API Call</button>
      </div>
    );
  }
}

export default function (elementId) {
  ReactDOM.render(
    <App />, document.getElementById(elementId)
  );
}