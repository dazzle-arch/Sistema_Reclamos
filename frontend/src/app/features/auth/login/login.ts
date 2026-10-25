import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormGroup, } from '@angular/forms';

@Component({
  imports: [RouterLink],
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