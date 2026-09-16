package cr.ac.ucr.paraiso.ie.c5h060.expresofast.business;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.EnvioRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Vehiculo;

@Service
public class EnvioService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO");

    private final EnvioRepository envioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;

    public EnvioService(EnvioRepository envioRepository,
            VehiculoRepository vehiculoRepository,
            ConductorRepository conductorRepository) {
        this.envioRepository = envioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
    }

    @Transactional(readOnly = true)
    public List<Envio> obtenerEnviosOptimizados() {
        return envioRepository.findAllOptimizado();
    }

    @Transactional
    public Envio registrarEnvio(Envio envioEntrante) {
        if (envioEntrante.getVehiculo() == null || envioEntrante.getVehiculo().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe indicar el ID del vehiculo asignado.");
        }
        if (envioEntrante.getConductor() == null || envioEntrante.getConductor().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe indicar el ID del conductor asignado.");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(envioEntrante.getVehiculo().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el vehiculo con ID " + envioEntrante.getVehiculo().getId()));

        Conductor conductor = conductorRepository.findById(envioEntrante.getConductor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el conductor con ID " + envioEntrante.getConductor().getId()));

        if (envioEntrante.getPesoKg() != null
                && envioEntrante.getPesoKg().compareTo(vehiculo.getCapacidadKg()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El peso del envio (" + envioEntrante.getPesoKg() + " kg) supera la capacidad "
                            + "maxima del vehiculo " + vehiculo.getPlaca() + " (" + vehiculo.getCapacidadKg()
                            + " kg).");
        }

        envioEntrante.setVehiculo(vehiculo);
        envioEntrante.setConductor(conductor);
        if (envioEntrante.getEstadoEnvio() == null || envioEntrante.getEstadoEnvio().isBlank()) {
            envioEntrante.setEstadoEnvio("PENDIENTE");
        }

        return envioRepository.save(envioEntrante);
    }

    @Transactional
    public Envio actualizarEstado(Integer envioId, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado invalido: " + nuevoEstado + ". Valores permitidos: " + ESTADOS_VALIDOS);
        }

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el envio con ID " + envioId));

        envio.setEstadoEnvio(nuevoEstado.toUpperCase());
        return envio;
    }
}