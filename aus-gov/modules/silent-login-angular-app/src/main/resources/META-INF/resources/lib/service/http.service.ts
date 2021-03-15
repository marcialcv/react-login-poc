
import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { EMPTY, Observable } from 'rxjs';
import { Person } from '../model/person.model';
import { catchError, map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export default class HttpService {

    apiUrl = "https://webserver-lfrspaings-prd.lfr.cloud/o/custom-api/users/123";
    refreshTokenUrl = window.origin + "/o/oidc/refresh_token/";

    constructor(@Inject(HttpClient) private httpClient: HttpClient) { }

    getUser(access_token:string): Observable<Person> {
      console.log("Calling getUser service...with access token: " + access_token)
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${access_token}`
        })
        return this.httpClient.get<Person>(this.apiUrl, { headers: headers }).pipe(
          map(obj => obj),catchError(e=> this.handleError(e))
        );
      }

      refreshToken(): Observable<any> {
        console.log("Calling refreshToken service...")
        return this.httpClient.get<any>(this.refreshTokenUrl).pipe(
          map(obj => obj),catchError(e=> this.handleError(e))
        );
      }

      private handleError(error: HttpErrorResponse) {
        if (error.error instanceof ErrorEvent) {
          console.error('A client-side or network error occurred:', error.error.message);
        }else if(error.status === 401){
          console.warn("Calling Refresh Token");
          this.refreshToken().subscribe( response => {
            if (response.access_token != null && response.access_token!=""){
              console.log("Access token from refresh token " + response.access_token);
              this.getUser(response.access_token).subscribe(responseBody => {
                console.log(responseBody);
              });
            }
          });
        }else {
          console.error(
            `The backend returned an unsuccessful response code ${error.status}, ` +
            `The response body was: ${error.error}`);
        }
        return EMPTY;
      }
}
