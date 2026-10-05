package cr.ac.ucr.paraiso.ie.c5h060.expresofast.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioMapper;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.CrearEnvioDTO;

import org.springframework.security.authentication.AnonymousAuthenticationToken;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioRegistroDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.PaqueteDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Paquete;
import java.math.BigDecimal;

@Service
public class EnvioService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO");
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

        if (ESTADOS_FINALES.contains(estadoAnterior) && ESTADOS_NO_REGRESABLES.contains(estadoNormalizado)) {
            throw new InvalidStateTransitionException(
                    "Transición de estado no permitida para el envío " + envio.getCodigoRastreo());
        }

        envio.setEstadoEnvio(estadoNormalizado);
        envioRepository.save(envio);

        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        boolean hayUsuario = autenticacion != null
                && autenticacion.isAuthenticated()
                && !(autenticacion instanceof AnonymousAuthenticationToken);

        if (hayUsuario) {
            Usuario usuarioActual = obtenerUsuarioAutenticado();

            BitacoraEnvio bitacora = new BitacoraEnvio();
            bitacora.setEnvio(envio);
            bitacora.setEstadoAnterior(estadoAnterior);
            bitacora.setEstadoNuevo(estadoNormalizado);
            bitacora.setFechaCambio(LocalDateTime.now());
            bitacora.setUsuario(usuarioActual);
            bitacora.setObservaciones(observaciones);
            bitacoraEnvioRepository.save(bitacora);
        }
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

    // Lab 9

    @Transactional(readOnly = true)
    public Page<EnvioDTO> listarPaginado(int page, int size, String sortBy, String dir,
            String busqueda, String estado) {

        String campoOrden = (sortBy == null || sortBy.isBlank()) ? "fechaCreacion" : sortBy;
        Sort.Direction direccion = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direccion, campoOrden));

        Page<Envio> pagina;

        if (estado != null && !estado.isBlank()) {
            pagina = envioRepository.findByEstadoEnvio(estado.trim().toUpperCase(), pageable);
        } else if (busqueda != null && !busqueda.isBlank()) {
            pagina = envioRepository.findByDireccionDestinoContainingIgnoreCase(busqueda.trim(), pageable);
        } else {
            pagina = envioRepository.findAll(pageable);
        }

        return pagina.map(EnvioMapper::toEnvioDTO);
    }

    @Transactional(readOnly = true)
    public List<EnvioDTO> listarViaStoredProcedure(String estado) {
        String estadoNormalizado = estado == null ? null : estado.trim().toUpperCase();

        if (estadoNormalizado == null || !ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            throw new InvalidStateTransitionException(
                    "Estado invalido para el procedimiento almacenado: " + estado);
        }

        List<Envio> envios = envioRepository.obtenerEnviosPorEstadoSP(estadoNormalizado);
        return EnvioMapper.toEnvioDTOList(envios);
    }

    // lab 10

    @Transactional(readOnly = true)
    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Envio buscarPorCodigoRastreo(String codigo) {
        return envioRepository.findByCodigoRastreo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el envio con codigo " + codigo));
    }

    @Transactional
    public Envio registrarEnvioDesdeAngular(CrearEnvioDTO dto) {
        String codigoGenerado = generarCodigoRastreoUnico();

        EnvioRequestDTO dtoInterno = new EnvioRequestDTO(
                codigoGenerado,
                dto.direccionDestino(),
                dto.pesoKg(),
                dto.montoFlete(),
                dto.vehiculoId(),
                dto.conductorId());

        return registrarEnvio(dtoInterno);
    }

    private String generarCodigoRastreoUnico() {
        String codigo;
        long secuencia = envioRepository.count() + 1000;
        do {
            secuencia++;
            codigo = "EXP-2026-" + secuencia;
        } while (envioRepository.existsByCodigoRastreo(codigo));
        return codigo;
    }

    // Lab 11

    @Transactional(readOnly = true)
    public boolean existeTracking(String numeroTracking) {
        return numeroTracking != null && envioRepository.existsByCodigoRastreo(numeroTracking.trim());
    }

    @Transactional
    public Envio registrarEnvioConPaquetes(EnvioRegistroDTO dto) {
        String tracking = dto.numeroTracking().trim();

        if (envioRepository.existsByCodigoRastreo(tracking)) {
            throw new InvalidStateTransitionException("El número de rastreo " + tracking + " ya está en uso.");
        }

        if (!dto.fechaEntregaEstimada().isAfter(dto.fechaDespacho())) {
            throw new InvalidStateTransitionException(
                    "La fecha de entrega estimada debe ser posterior a la fecha de despacho.");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(dto.vehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el vehiculo con ID " + dto.vehiculoId()));

        Conductor conductor = conductorRepository.findById(dto.conductorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el conductor con ID " + dto.conductorId()));

        BigDecimal pesoTotal = dto.paquetes().stream()
                .map(PaqueteDTO::pesoKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (pesoTotal.compareTo(vehiculo.getCapacidadKg()) > 0) {
            throw new InvalidStateTransitionException(
                    "El peso total del envio (" + pesoTotal + " kg) supera la capacidad maxima del vehiculo "
                            + vehiculo.getPlaca() + " (" + vehiculo.getCapacidadKg() + " kg).");
        }

        Envio envio = new Envio();
        envio.setCodigoRastreo(tracking);
        envio.setDireccionDestino(dto.direccionDestino());
        envio.setPesoKg(pesoTotal);
        envio.setCosto(dto.costo());
        envio.setEstadoEnvio("PENDIENTE");
        envio.setVehiculo(vehiculo);
        envio.setConductor(conductor);
        envio.setFechaDespacho(dto.fechaDespacho());
        envio.setFechaEntregaEstimada(dto.fechaEntregaEstimada());

        for (PaqueteDTO p : dto.paquetes()) {
            Paquete paquete = new Paquete();
            paquete.setDescripcion(p.descripcion().trim());
            paquete.setPesoKg(p.pesoKg());
            envio.agregarPaquete(paquete);
        }

        return envioRepository.save(envio);
    }

}