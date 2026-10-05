import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, of, timer } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { EnvioService } from '../services/envio.service';

export function trackingUnicoValidator(envioService: EnvioService): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
        const valor = String(control.value ?? '').trim();

        if (!valor) {
            return of(null);
        }

        return timer(400).pipe(
            switchMap(() => envioService.verificarTracking(valor)),
            map(resp => (resp.existe ? { trackingTomado: true } : null)),
            catchError(() => of(null))
        );
    };
}