import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BarService {
  constructor(private http: HttpClient) { }

  get(): Observable<any> {
    return this.http.get<any>('/bars').pipe(catchError((error) => {
      console.error(error);
      window.location.href = 'http://localhost:8083';
      return of([]);
    }));
  }
}
