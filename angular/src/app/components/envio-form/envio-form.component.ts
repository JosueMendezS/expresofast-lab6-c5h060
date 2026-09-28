import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EnvioService } from '../../services/envio.service';
import { CrearEnvioPayload } from '../../models/envio.model';

@Component({
  selector: 'app-envio-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-form.component.html',
  styleUrl: './envio-form.component.css'
})
export class EnvioFormComponent {
  private envioService = inject(EnvioService);
  private router = inject(Router);

  payload: CrearEnvioPayload = {
    direccionDestino: '',
    pesoKg: 0,
    costo: 0,
    vehiculoId: 0,
    conductorId: 0
  };

  mensajeExito = '';
  mensajeError = '';

  registrar(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (!this.payload.direccionDestino || this.payload.pesoKg <= 0 || this.payload.costo <= 0
        || !this.payload.vehiculoId || !this.payload.conductorId) {
      this.mensajeError = 'Complete todos los campos obligatorios.';
      return;
    }

    this.envioService.crearEnvio(this.payload).subscribe({
      next: (envioCreado) => {
        this.mensajeExito = `Envío registrado con código ${envioCreado.codigoRastreo}.`;
        setTimeout(() => this.router.navigate(['/envios']), 1500);
      },
      error: (err) => {
        this.mensajeError = err?.error?.message || 'No se pudo registrar el envío.';
      }
    });
  }
}
