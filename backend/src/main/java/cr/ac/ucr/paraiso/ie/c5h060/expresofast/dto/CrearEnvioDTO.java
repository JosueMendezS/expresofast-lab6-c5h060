package cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CrearEnvioDTO(
        @NotBlank(message = "La dirección de destino es obligatoria") String direccionDestino,

        @NotNull(message = "El peso es obligatorio") @Positive(message = "El peso debe ser mayor a cero") BigDecimal pesoKg,

        @NotNull(message = "El monto de flete es obligatorio") @Positive(message = "El monto de flete debe ser mayor a cero") BigDecimal montoFlete,

        @NotNull(message = "Debe indicar el ID del vehículo") Integer vehiculoId,

        @NotNull(message = "Debe indicar el ID del conductor") Integer conductorId) {
}