import { Component } from '@angular/core';


import LiferayParams from '../types/LiferayParams'

declare const Liferay: any;

export class Communication {
	message: string;
}

@Component({
	templateUrl: 
		Liferay.ThemeDisplay.getPathContext() + 
		'/o/angular-catcher/app/app.component.html'
})


export class AppComponent {
	params: LiferayParams;
	labels: any;
	imageLogo: string;

	communication: Communication = {
		message: 'Waiting for a ball...'

	};
	
	constructor() {
		this.labels = {        
			
			configuration: Liferay.Language.get('configuration'),
			portletNamespace: Liferay.Language.get('portlet-namespace'),
        	contextPath: Liferay.Language.get('context-path'),
			portletElementId: Liferay.Language.get('portlet-element-id'),
			message: Liferay.Language.get('message')
			
		}
		this.imageLogo= "/images/logo_liferay.jpg"
	}
	
	get configurationJSON() {
		return JSON.stringify(this.params.configuration, null, 2);
	}

	get relativeImagePath() {
		return this.params.contextPath + this.imageLogo;
	}

	ngOnInit() {
		//check IPC with event
		Liferay.on('my-message-event', this.printMessageFromEvent.bind(this));
		
		//check param from URL
		this.printMessageFromParam();
		
		//check IPC by session
		Liferay.Util.Session.get('my-message-session').then((value:string) =>{ 
			if(value != null && value != "null"){
				console.log(`value into promise -> ${value}`)
				this.printMessageFromSession(value);
				Liferay.Util.Session.set("my-message-session",null);
			} 
		});
	}
	
	//print message from URL query string
	printMessageFromParam(){
	  console.log("Reading param from URL");
	  var urlParams = new URLSearchParams(window['location'].search);
	  var messageVar = urlParams.get("message");
	  if(messageVar){
	  	  console.log("Message --> "+messageVar);
		  this.modifyMessage(messageVar);
	  }
	  
  	}
  	
	//print message from IPC with event
	printMessageFromEvent(event:any) {
		console.log("Reading message from event");
		console.log("Message --> "+event.data);
	  	
		this.modifyMessage(event.data);
	}
	
	//print message from IPC with session
	printMessageFromSession(message:any) {
		console.log("Reading message from session");
		console.log("Message value --> "+message);
	  	if(message !== "get"){
			this.modifyMessage(message);	
		}
	}
	
	//change message
	modifyMessage(newMessage:any){
		console.log("Setting new message into model --> "+newMessage);
		this.communication = {message: newMessage};
		(<HTMLInputElement>document.querySelector("#"+this.params.portletElementId+" .communicationMessage"))!.innerText = newMessage;
	}
}
