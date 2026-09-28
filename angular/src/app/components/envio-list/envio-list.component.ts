import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnvioService } from '../../services/envio.service';
import { Envio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-list.component.html',
  styleUrl: './envio-list.component.css'
})
export class EnvioListComponent implements OnInit {
  private envioService = inject(EnvioService);

  envios: Envio[] = [];
  cargando = false;
  error = '';

  estadosDisponibles = ['PENDIENTE', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO'];

  ngOnInit(): void {
    this.cargarEnvios();
  }

  cargarEnvios(): void {
    this.cargando = true;
    this.envioService.obtenerEnvios().subscribe({
      next: (data) => {
        this.envios = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los envíos.';
        this.cargando = false;
      }
    });
  }

  cambiarEstado(envio: Envio, nuevoEstado: string): void {
    this.envioService.actualizarEstado(envio.id, nuevoEstado).subscribe({
      next: (actualizado) => {
        envio.estadoEnvio = actualizado.estadoEnvio;
      },
      error: () => {
        this.error = `No se pudo actualizar el estado del envío ${envio.codigoRastreo}.`;
      }
    });
  }

  claseEstado(estado: string): string {
    return 'badge badge--' + estado.toLowerCase();
  }
}
