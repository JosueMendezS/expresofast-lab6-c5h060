import { Component, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import {
    FormArray,
    FormControl,
    FormGroup,
    NonNullableFormBuilder,
    ReactiveFormsModule,
    Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { EnvioService } from '../../services/envio.service';
import { EnvioAvanzadoPayload } from '../../models/envio-avanzado.model';
import { fechasValidator } from '../../validators/fechas.validator';
import { trackingUnicoValidator } from '../../validators/tracking.validator';

type PaqueteForm = FormGroup<{
    descripcion: FormControl<string>;
    pesoKg: FormControl<number>;
}>;

@Component({
    selector: 'app-envio-avanzado-form',
    standalone: true,
    imports: [ReactiveFormsModule, DecimalPipe],
    templateUrl: './envio-avanzado-form.component.html',
    styleUrl: './envio-avanzado-form.component.css'
})
export class EnvioAvanzadoFormComponent {
    private fb = inject(NonNullableFormBuilder);
    private envioService = inject(EnvioService);
    private router = inject(Router);

    mensajeExito = '';
    mensajeError = '';
    enviando = false;

    form = this.fb.group(
        {
            numeroTracking: this.fb.control('', {
                validators: [Validators.required, Validators.maxLength(30)],
                asyncValidators: [trackingUnicoValidator(this.envioService)]
            }),
            direccionDestino: this.fb.control('', [Validators.required, Validators.maxLength(200)]),
            costo: this.fb.control(0, [Validators.required, Validators.min(0.01)]),
            vehiculoId: this.fb.control(0, [Validators.required, Validators.min(1)]),
            conductorId: this.fb.control(0, [Validators.required, Validators.min(1)]),
            fechaDespacho: this.fb.control('', Validators.required),
            fechaEntregaEstimada: this.fb.control('', Validators.required),
            paquetes: this.fb.array<PaqueteForm>([this.crearPaquete()])
        },
        { validators: [fechasValidator] }
    );

    get paquetes(): FormArray<PaqueteForm> {
        return this.form.controls.paquetes;
    }

    private crearPaquete(): PaqueteForm {
        return this.fb.group({
            descripcion: this.fb.control('', [Validators.required, Validators.maxLength(255)]),
            pesoKg: this.fb.control(0, [Validators.required, Validators.min(0.01), Validators.max(999.99)])
        });
    }

    agregarPaquete(): void {
        this.paquetes.push(this.crearPaquete());
    }

    eliminarPaquete(index: number): void {
        if (this.paquetes.length > 1) {
            this.paquetes.removeAt(index);
        }
    }

    get pesoTotal(): number {
        return this.paquetes.controls.reduce((acc, p) => acc + (p.controls.pesoKg.value || 0), 0);
    }

    registrar(): void {
        this.mensajeExito = '';
        this.mensajeError = '';

        if (this.form.invalid || this.form.pending) {
            this.form.markAllAsTouched();
            return;
        }

        const payload: EnvioAvanzadoPayload = this.form.getRawValue();
        this.enviando = true;

        this.envioService.crearEnvioAvanzado(payload).subscribe({
            next: envio => {
                this.enviando = false;
                this.mensajeExito = `Envío ${envio.codigoRastreo} registrado con ${payload.paquetes.length} paquete(s).`;
                setTimeout(() => this.router.navigate(['/envios']), 1500);
            },
            error: err => {
                this.enviando = false;
                this.mensajeError = err?.error?.message || 'No se pudo registrar el envío.';
            }
        });
    }
}