package cr.ac.ucr.paraiso.ie.c5h060.expresofast.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.BitacoraEnvioRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.EnvioRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.UsuarioRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.BitacoraEnvio;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Usuario;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Vehiculo;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioRequestDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.exception.InvalidStateTransitionException;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.exception.ResourceNotFoundException;

@Service
public class EnvioService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO");
    // Reto autonomo: un envio en estado final ya no puede "retroceder".
    private static final Set<String> ESTADOS_FINALES = Set.of("ENTREGADO", "CANCELADO");
    private static final Set<String> ESTADOS_NO_REGRESABLES = Set.of("PENDIENTE", "EN_TRANSITO");

    private final EnvioRepository envioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;
    private final BitacoraEnvioRepository bitacoraEnvioRepository;
    private final UsuarioRepository usuarioRepository;

    public EnvioService(EnvioRepository envioRepository,
            VehiculoRepository vehiculoRepository,
            ConductorRepository conductorRepository,
            BitacoraEnvioRepository bitacoraEnvioRepository,
            UsuarioRepository usuarioRepository) {
        this.envioRepository = envioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
        this.bitacoraEnvioRepository = bitacoraEnvioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Envio> obtenerEnviosOptimizados() {
        return envioRepository.findAllOptimizado();
    }

    @Transactional
    public Envio registrarEnvio(EnvioRequestDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(dto.vehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el vehiculo con ID " + dto.vehiculoId()));

        Conductor conductor = conductorRepository.findById(dto.conductorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el conductor con ID " + dto.conductorId()));

        if (dto.pesoKg().compareTo(vehiculo.getCapacidadKg()) > 0) {
            throw new InvalidStateTransitionException(
                    "El peso del envio (" + dto.pesoKg() + " kg) supera la capacidad maxima del vehiculo "
                            + vehiculo.getPlaca() + " (" + vehiculo.getCapacidadKg() + " kg).");
        }

        Envio envio = new Envio();
        envio.setCodigoRastreo(dto.codigoRastreo());
        envio.setDireccionDestino(dto.direccionDestino());
        envio.setPesoKg(dto.pesoKg());
        envio.setCosto(dto.costo());
        envio.setVehiculo(vehiculo);
        envio.setConductor(conductor);
        envio.setEstadoEnvio("PENDIENTE");

        return envioRepository.save(envio);
    }

    @Transactional
    public Envio actualizarEstado(Integer envioId, String nuevoEstado, String observaciones) {
        String estadoNormalizado = nuevoEstado == null ? null : nuevoEstado.trim().toUpperCase();

        if (estadoNormalizado == null || !ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new InvalidStateTransitionException(
                    "Estado invalido: " + nuevoEstado + ". Valores permitidos: " + ESTADOS_VALIDOS);
        }

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el envio con ID " + envioId));

        String estadoAnterior = envio.getEstadoEnvio();

        // Reto autonomo: bloquear transicion invalida (ENTREGADO/CANCELADO -> PENDIENTE/EN_TRANSITO)
        if (ESTADOS_FINALES.contains(estadoAnterior) && ESTADOS_NO_REGRESABLES.contains(estadoNormalizado)) {
            throw new InvalidStateTransitionException(
                    "Transición de estado no permitida para el envío " + envio.getCodigoRastreo());
        }

        envio.setEstadoEnvio(estadoNormalizado);
        envioRepository.save(envio);

        Usuario usuarioActual = obtenerUsuarioAutenticado();

        BitacoraEnvio bitacora = new BitacoraEnvio();
        bitacora.setEnvio(envio);
        bitacora.setEstadoAnterior(estadoAnterior);
        bitacora.setEstadoNuevo(estadoNormalizado);
        bitacora.setFechaCambio(LocalDateTime.now());
        bitacora.setUsuario(usuarioActual);
        bitacora.setObservaciones(observaciones);
        bitacoraEnvioRepository.save(bitacora);

        return envio;
    }

    @Transactional(readOnly = true)
    public List<BitacoraEnvio> obtenerBitacora(Integer envioId) {
        if (!envioRepository.existsById(envioId)) {
            throw new ResourceNotFoundException("No existe el envio con ID " + envioId);
        }
        return bitacoraEnvioRepository.findByEnvioId(envioId);
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        String username = autenticacion.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }
}