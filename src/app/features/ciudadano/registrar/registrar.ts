import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { required } from '@angular/forms/signals';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-registrar',
  styleUrl: './registrar.css',
  templateUrl: './registrar.html',
})
export class Registrar {
  formularioReclamo: FormGroup;

  constructor(private fb: FormBuilder){
    this.formularioReclamo = this.fb.group({
      apellidos: ['', Validators.required],
      nombre: ['', Validators.required],
      // exactamente 8 dígitos
      dni: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      // solo números, exactamente 9 dígitos
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      sexo: ['', Validators.required],
      // debe tener formato de email (@)
      correo: ['', [Validators.required, Validators.email]],
      tipoReclamo: ['', Validators.required],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      evidencia: [''] // Es opcional, no lleva validadores
    })
  }

  enviarDatos(){
    if(this.formularioReclamo.invalid){
      alert("Por favor, completa correctamente todos los campos obligatorios.");
      this.formularioReclamo.markAllAsTouched();
      return;
    }
    console.log("Datos recogidos perfectamente: ", this.formularioReclamo.value);
  }
}
