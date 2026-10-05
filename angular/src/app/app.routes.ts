import { Routes } from '@angular/router';
import { EnvioListComponent } from './components/envio-list/envio-list.component';
import { EnvioFormComponent } from './components/envio-form/envio-form.component';
import { EnvioTrackingComponent } from './components/envio-tracking/envio-tracking.component';
import { EnvioAvanzadoFormComponent } from './components/envio-avanzado-form/envio-avanzado-form.component';

export const routes: Routes = [
  { path: '', redirectTo: 'envios', pathMatch: 'full' },
  { path: 'envios', component: EnvioListComponent },
  { path: 'nuevo-envio', component: EnvioFormComponent },
  { path: 'envio-avanzado', component: EnvioAvanzadoFormComponent },
  { path: 'rastreo', component: EnvioTrackingComponent },
  { path: '**', redirectTo: 'envios' }
];
