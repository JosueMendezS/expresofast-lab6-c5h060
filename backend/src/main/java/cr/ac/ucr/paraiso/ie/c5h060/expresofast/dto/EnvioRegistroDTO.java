package cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EnvioRegistroDTO(
        @NotBlank(message = "El número de tracking es obligatorio") @Size(max = 30, message = "El tracking no puede superar 30 caracteres") String numeroTracking,

        @NotBlank(message = "La dirección de destino es obligatoria") @Size(max = 200) String direccionDestino,

        @NotNull(message = "El costo es obligatorio") @Positive(message = "El costo debe ser mayor a cero") BigDecimal costo,

        @NotNull(message = "Debe indicar el ID del vehículo") Integer vehiculoId,

        @NotNull(message = "Debe indicar el ID del conductor") Integer conductorId,

        @NotNull(message = "La fecha de despacho es obligatoria") LocalDate fechaDespacho,

        @NotNull(message = "La fecha de entrega estimada es obligatoria") LocalDate fechaEntregaEstimada,

        @NotEmpty(message = "Debe incluir al menos un paquete") @Valid List<PaqueteDTO> paquetes) {
}