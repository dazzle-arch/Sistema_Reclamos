import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  loginForm: FormGroup;
  cargando = false;
  errorMensaje = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      nombreUsuario: ['', Validators.required],
      contrasena: ['', Validators.required]
    });
  }

  ingresar() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.cargando = true;
    this.errorMensaje = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: (respuesta) => {
        this.cargando = false;
        
        // Redirección condicional según el rol de PostgreSQL
        if (respuesta.idRol === 1) {
          this.router.navigate(['/admin/panel-control']);
        } else if (respuesta.idRol === 2) {
          this.router.navigate(['/funcionario/bandeja-casos']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.cargando = false;
        this.errorMensaje = 'Credenciales incorrectas. Intente nuevamente.';
        console.error('Error de login', err);
      }
    });
  }
}