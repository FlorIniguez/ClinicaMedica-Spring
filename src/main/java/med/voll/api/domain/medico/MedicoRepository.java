package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    //no requiere test,. ya lo hace spring framework. Si no esta 0k me da error
    Page<Medico> findByActivoTrue(Pageable paginacion);

    //Busca medico activo true y especialidad, sino encuentra por id, busca por que NO tenga consulta en
    // esa fecha/hora
    // --- TEST ---
    @Query("""
            select m from Medico m
            where m.activo= true
            and
            m.especialidad=:especialidad
            and
            m.id not in(  
                select c.medico.id from Consulta c
                where
                c.fecha=:fecha
            )
            order by rand()
            limit 1
            """)
    Medico seleccionarMedicoConEspecialidadEnFecha(Especialidad especialidad, LocalDateTime fecha);

    //hacerlo por mi cuenta
    @Query("""
            select m.activo
            from Medico m
            where m.id=:idMedico
            """)
    Boolean findActivoById(Long idMedico);
}
