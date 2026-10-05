package cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaqueteDTO(
        @NotBlank(message = "La descripción del paquete es obligatoria") @Size(max = 255, message = "La descripción no puede superar 255 caracteres") String descripcion,

        @NotNull(message = "El peso del paquete es obligatorio") @DecimalMin(value = "0.01", message = "El peso debe ser mayor a cero") @DecimalMax(value = "999.99", message = "El peso máximo por paquete es 999.99 kg") BigDecimal pesoKg) {
}