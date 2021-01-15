import { Component } from '@angular/core';

import LiferayParams from '../types/LiferayParams'

declare const Liferay: any;

@Component({
	templateUrl: 
		Liferay.ThemeDisplay.getPathContext() + 
		'/o/angular-pitcher/app/app.component.html'
})
export class AppComponent {
	params: LiferayParams;
	labels: any;

	constructor() {
		this.labels = {        
			
			configuration: Liferay.Language.get('configuration'),
			pageConfigured: Liferay.Language.get('page-configured'),
			portletNamespace: Liferay.Language.get('portlet-namespace'),
        	contextPath: Liferay.Language.get('context-path'),
			portletElementId: Liferay.Language.get('portlet-element-id'),
		}
	}

	get configurationJSON() {
		return JSON.stringify(this.params.configuration, null, 2);
	}

	get configurationPage() {
		return JSON.stringify(this.params.configuration.portletInstance.page);
	}

	throwBall(){
    	console.log("Ball thrown");
    	Liferay.fire('my-message-event', { data: 'Ball catched!!!'});
  	}
  	
  	throwBallAway(){
  		console.log("Ball thrown to other page!");
  		
  		var myUrl = new URL(this.configurationPage.replace(/"/g,""), window.location.origin);
  		
  		myUrl.searchParams.set('message', 'Ball catched!');
  		
  		console.log(myUrl.toString());
		
		document.location.href = myUrl.toString();
  	}
  	
  	throwBySession(){
  		console.log("Ball thrown to other page!");
  		Liferay.Util.Session.set("my-message-session","Ball thrown. Catched from session!");
	    document.location.href = this.configurationPage.replace(/"/g,"");
  	}
}
