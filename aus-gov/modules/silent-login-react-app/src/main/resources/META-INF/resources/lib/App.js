import  React  from  'react';
import  Login  from  './Login';
// import './App.css';
import ReactDOM from 'react-dom';

class App extends React.Component {
  
  makeApiCall() {
// This block is the way to make calls to an API
	  
    var xhr = new  XMLHttpRequest();
    xhr.open("GET", "http://intranet.myliferay.com:8082/o/custom-api/users/123", true);
    xhr.setRequestHeader("Authorization", "Bearer " + this.state.accessToken);
    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          var response = JSON.parse(xhr.responseText);
          console.log(response);
          this.setState({firstName:  response.firstName});
        } else  if (xhr.status === 401) {
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
    this.setState({accessToken:  null});
    this._login.current.requestNewAccessToken();
  }

  handleAccessToken(accessToken) {
    this.setState({accessToken:  accessToken})
    this.makeApiCall();
  }

  constructor(props) {
    super(props);
    
    console.log(this.props);
    this.state = {
    	    accessToken: null,
    	    firstName: "world",
    	    message: "Waiting for a ball..."
    	  }
    this.makeApiCall = this.makeApiCall.bind(this);
    this.handleAccessToken = this.handleAccessToken.bind(this);
    this._login = React.createRef();
    this.checkParam = this.checkParam.bind(this);
    Liferay.on('stuff-happened', this.printMessage.bind(this));
    
   
    
  }
  
  checkParam(){
	  console.log("Reading param from URL");
	  var urlParams = new URLSearchParams(window['location'].search);
	  var message = urlParams.get("message");
	  if(message){
		  this.setState({message: message});
	  }
  }
  
	  
  printMessage(event) {
	console.log("Event received!!!");
	this.setState({message: event.data});
  }
  
  componentDidMount(){
	     this.checkParam();
  }

  render() {
    return (
      <div  className="App">
        <h1>Hello, {this.state.firstName}!</h1>
        <Login  ref={this._login}  action={this.handleAccessToken}  clientId="medfile-react-client-app"  idProvider="oi"  pkce="S256"  />
        <button  onClick={this.makeApiCall}>API Call</button>
        
        
        <h1>Catcher: {this.state.message}</h1>
        
        
      </div>
    );
  }
}

export default function(elementId) {
	ReactDOM.render(
			<App />, document.getElementById(elementId)
	);
}