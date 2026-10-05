import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Envio, CrearEnvioPayload } from '../models/envio.model';
import { EnvioAvanzadoPayload, TrackingCheck } from '../models/envio-avanzado.model';

@Injectable({ providedIn: 'root' })
export class EnvioService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}envios`;

  obtenerEnvios(): Observable<Envio[]> {
    return this.http.get<Envio[]>(this.baseUrl);
  }

  obtenerPorRastreo(codigo: string): Observable<Envio> {
    return this.http.get<Envio>(`${this.baseUrl}/rastreo/${codigo}`);
  }

  crearEnvio(payload: CrearEnvioPayload): Observable<Envio> {
    return this.http.post<Envio>(this.baseUrl, payload);
  }

  actualizarEstado(id: number, nuevoEstado: string): Observable<Envio> {
    return this.http.patch<Envio>(`${this.baseUrl}/${id}/estado`, { nuevoEstado });
  }
  
  // Lab 11 (endpoints bajo /api/envios)
  verificarTracking(tracking: string): Observable<TrackingCheck> {
    return this.http.get<TrackingCheck>(
      `${environment.apiRootUrl}envios/check-tracking/${encodeURIComponent(tracking)}`
    );
  }

  crearEnvioAvanzado(payload: EnvioAvanzadoPayload): Observable<Envio> {
    return this.http.post<Envio>(`${environment.apiRootUrl}envios/avanzado`, payload);
  }
}
