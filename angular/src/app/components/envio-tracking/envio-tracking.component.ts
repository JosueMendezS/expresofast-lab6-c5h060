import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnvioService } from '../../services/envio.service';
import { Envio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-tracking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-tracking.component.html',
  styleUrl: './envio-tracking.component.css'
})
export class EnvioTrackingComponent {
  private envioService = inject(EnvioService);

  codigo = '';
  envio: Envio | null = null;
  error = '';

  private pasos = ['PENDIENTE', 'EN_TRANSITO', 'ENTREGADO'];

  buscar(): void {
    this.error = '';
    this.envio = null;

    if (!this.codigo.trim()) {
      this.error = 'Ingrese un código de rastreo.';
      return;
    }

    this.envioService.obtenerPorRastreo(this.codigo.trim()).subscribe({
      next: (data) => (this.envio = data),
      error: () => (this.error = 'No se encontró ningún envío con ese código.')
    });
  }

  progreso(): number {
    if (!this.envio) return 0;
    if (this.envio.estadoEnvio === 'CANCELADO') return 0;
    const indice = this.pasos.indexOf(this.envio.estadoEnvio);
    return indice === -1 ? 0 : ((indice + 1) / this.pasos.length) * 100;
  }
}
