import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FooService} from "./service/foo.service";
import {BarService} from "./service/bar.service";

export class User {
  identifier?: string;
  firstName?: string;
  lastName?: string
  roles?: string[]
}

export class Foo {
  id?: string;
}

export class Bar {
  foo?: Foo
  baz?: string;
}



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  title = 'angular-oidc-demo';
  isAuthenticated = false;
  user: User | null = null;
  foo: Foo | null = null;
  bar: Bar | null = null;

  constructor(private http: HttpClient, private fooService: FooService, private barService: BarService) {
  }

  ngOnInit() {
    this.http.get<any>('/userinfo').subscribe((user: User) => {
      this.isAuthenticated = true;
      this.user = user;
      this.fooService.get().subscribe(foo => this.foo = foo);
      this.barService.get().subscribe(bar => this.bar = bar);
    });
  }

  login() {
    window.location.href = '/authorize';
    return false;
  }

  logout() {
    window.location.href = '/logout';
    return false;
  }
}
