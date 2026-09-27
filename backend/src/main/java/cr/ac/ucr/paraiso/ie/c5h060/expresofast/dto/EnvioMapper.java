package cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto;

import java.util.List;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.BitacoraEnvio;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Envio;

public final class EnvioMapper {

    private EnvioMapper() {
    }

    public static EnvioResponseDTO toResponseDTO(Envio envio) {
        String placaVehiculo = envio.getVehiculo() != null ? envio.getVehiculo().getPlaca() : null;
        String nombreConductor = envio.getConductor() != null
                ? envio.getConductor().getNombre() + " " + envio.getConductor().getApellidos()
                : null;

        return new EnvioResponseDTO(
                envio.getId(),
                envio.getCodigoRastreo(),
                envio.getDireccionDestino(),
                envio.getPesoKg(),
                envio.getCosto(),
                envio.getEstadoEnvio(),
                placaVehiculo,
                nombreConductor);
    }

    public static List<EnvioResponseDTO> toResponseDTOList(List<Envio> envios) {
        return envios.stream().map(EnvioMapper::toResponseDTO).toList();
    }

    public static BitacoraResponseDTO toBitacoraResponseDTO(BitacoraEnvio bitacora) {
        String nombreUsuario = bitacora.getUsuario() != null
                ? bitacora.getUsuario().getUsername()
                : null;

        return new BitacoraResponseDTO(
                bitacora.getId(),
                bitacora.getEstadoAnterior(),
                bitacora.getEstadoNuevo(),
                bitacora.getFechaCambio(),
                nombreUsuario,
                bitacora.getObservaciones());
    }

    public static List<BitacoraResponseDTO> toBitacoraResponseDTOList(List<BitacoraEnvio> bitacoras) {
        return bitacoras.stream().map(EnvioMapper::toBitacoraResponseDTO).toList();
    }

    public static EnvioDTO toEnvioDTO(Envio envio) {
        return new EnvioDTO(
                envio.getId(),
                envio.getCodigoRastreo(),
                envio.getDireccionDestino(),
                envio.getCosto(),
                envio.getEstadoEnvio(),
                envio.getFechaCreacion());
    }

    public static List<EnvioDTO> toEnvioDTOList(List<Envio> envios) {
        return envios.stream().map(EnvioMapper::toEnvioDTO).toList();
    }
}
