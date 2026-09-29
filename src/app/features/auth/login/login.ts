import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  imports: [],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  
  constructor(private router: Router) {}

  ingresar() {
    this.router.navigate(['/admin/panel-control']); 
  }
}