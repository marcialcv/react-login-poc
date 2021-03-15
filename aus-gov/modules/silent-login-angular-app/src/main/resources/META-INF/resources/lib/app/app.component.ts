import { Component, Inject, Input } from '@angular/core';
import { Person } from '../model/person.model';
import HttpService from '../service/http.service';

@Component({
	template: `
	<h2>Name of the User from api: {{person.firstName}}!</h2>
	<div><label>id: </label>{{person.person_id}}</div>
	<div>
		<label>name: </label>
		<input [(ngModel)]="person.firstName" placeholder="name">
		<button id="callAPI" (click)="makeApiCall()">Call API</button>
	</div>
	`,
})
export class AppComponent {

	constructor(@Inject(HttpService) private httpService: HttpService) {}

	person: Person = {	
		person_id: "Press the button to retrieve the Id",
		firstName: "Press the button to retrieve the Name"
	};

	@Input('config') config;

	makeApiCall():void {
		    console.warn('Calling the API with the Access Token ->', this.config.accessToken)
			this.httpService.getUser(this.config.accessToken).subscribe(responseBody => {
			this.person = responseBody;
			console.log(responseBody);
	  });
	}
}