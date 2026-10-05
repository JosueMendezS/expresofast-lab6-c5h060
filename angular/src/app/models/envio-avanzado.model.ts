export interface PaquetePayload {
    descripcion: string;
    pesoKg: number;
}

export interface EnvioAvanzadoPayload {
    numeroTracking: string;
    direccionDestino: string;
    costo: number;
    vehiculoId: number;
    conductorId: number;
    fechaDespacho: string;
    fechaEntregaEstimada: string;
    paquetes: PaquetePayload[];
}

export interface TrackingCheck {
    numeroTracking: string;
    existe: boolean;
}