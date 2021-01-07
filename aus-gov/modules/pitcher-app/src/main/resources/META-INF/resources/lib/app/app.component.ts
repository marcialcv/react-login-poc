import { Component } from '@angular/core';

export class Hero {
	id: number;
	name: string;
}

declare var Liferay: any;

@Component({
	template: `
	<h1>{{title}}</h1>
	<div>
		<button  (click)="throwBall()" >Throw Ball</button>
		
		<button (click)="throwBallAway()">Throw Ball to other page!</button>
	</div>
	`,
})
export class AppComponent {

	hero: Hero = {
		id: 1,
		name: 'Windstorm'
	};
	title = 'Throw the ball';
	
	constructor(){}

    ngOnInit(){}

    throwBall(){
    	console.log("Ball thrown");
    	Liferay.fire('stuff-happened', { data: 'Ball catched!!!'});
  	}
  	
  	throwBallAway(){
  		console.log("Ball thrown to other page!");
  		
  		var myUrl = new URL('/group/guest/privatepage', window.location.origin);
  		
  		myUrl.searchParams.set('message', 'Ball catched!');
  		
  		console.log(myUrl.toString());
		
		document.location.href = myUrl.toString();
  	}
  	
  	
  	
  	throwBallAwayUsingPortlet(){
  	
    	console.log("Ball thrown to other page!");
    	
    	var basePortletURL = "/group/guest/privatepage?p_p_id=p_pitcherapp_INSTANCE_culb";

		var actionURL = Liferay.Util.PortletURL.createRenderURL(
		  basePortletURL,
		  {
		    message: 'Ball Thrown',
		    p_auth: Liferay.authToken
		  }  
		);
		
		console.log(actionURL.toString());
		
		document.location.href = actionURL;
		
  	}
	
}