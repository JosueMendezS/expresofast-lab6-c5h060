package cr.ac.ucr.paraiso.ie.c5h060.expresofast.data;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {

        @Query("SELECT e FROM Envio e " +
                        "JOIN FETCH e.vehiculo v " +
                        "JOIN FETCH v.empresa " +
                        "JOIN FETCH e.conductor " +
                        "ORDER BY e.id DESC")
        List<Envio> findAllOptimizado();

        @Modifying(clearAutomatically = true)
        @Query("UPDATE Envio e SET e.estadoEnvio = :estado WHERE e.vehiculo.id = :vehiculoId")
        int actualizarEstadoPorVehiculo(@Param("vehiculoId") Integer vehiculoId,
                        @Param("estado") String estado);

        // Lab9

        @Procedure(name = "Envio.obtenerPorEstado")
        List<Envio> obtenerEnviosPorEstadoSP(@Param("pEstado") String pEstado);

        Page<Envio> findByEstadoEnvio(String estadoEnvio, Pageable pageable);

        Page<Envio> findByDireccionDestinoContainingIgnoreCase(String busqueda, Pageable pageable);

        // Lab 10

        java.util.Optional<Envio> findByCodigoRastreo(String codigoRastreo);

        boolean existsByCodigoRastreo(String codigoRastreo);
        
}
