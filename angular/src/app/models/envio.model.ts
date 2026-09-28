export interface Envio {
  id: number;
  codigoRastreo: string;
  direccionDestino: string;
  pesoKg: number;
  costo: number;
  estadoEnvio: string;
  placaVehiculo: string;
  nombreConductor: string;
  fechaCreacion: string;
}

export interface CrearEnvioPayload {
  direccionDestino: string;
  pesoKg: number;
  montoFlete: number;
  vehiculoId: number;
  conductorId: number;
}