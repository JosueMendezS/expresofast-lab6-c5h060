import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';


export const fechasValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
    const despacho = group.get('fechaDespacho')?.value as string | null;
    const entrega = group.get('fechaEntregaEstimada')?.value as string | null;

    if (!despacho || !entrega) {
        return null;
    }

    const tDespacho = new Date(despacho).getTime();
    const tEntrega = new Date(entrega).getTime();

    return tEntrega > tDespacho ? null : { fechasInvalidas: true };
};