package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.paciente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {
    @Autowired
    PacienteRepository pacienteRepository;

    @PostMapping
    public ResponseEntity<DatosRespuestaPaciente> registrarPaciente(@RequestBody @Valid DatosRegistroPaciente datosRegistroPaciente,
                                                                    UriComponentsBuilder uriComponentsBuilder) {

        Paciente paciente = pacienteRepository.save(new Paciente(datosRegistroPaciente));

        //URL donde encontrar al paciente un get con parametro el id
        DatosRespuestaPaciente datosRespuestaPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(), paciente.getTelefono(), paciente.getDocumento(), new DatosDireccion(
                paciente.getDireccion().getCalle(), paciente.getDireccion().getDistrito(), paciente.getDireccion().getCiudad(), paciente.getDireccion().getNumero(),
                paciente.getDireccion().getComplemento()));

        URI url = uriComponentsBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaPaciente);


    }

    //    listado de pacientes por nombre ascendente por pag
    @GetMapping
    public ResponseEntity<Page<DatosListadoPaciente>> listadoPacientes(@PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(pacienteRepository.findByActivoTrue(pageable).map(DatosListadoPaciente::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaPaciente> retornarPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);

        //URL donde encontrar al paciente un get con parametro el id
        DatosRespuestaPaciente datosRespuestaPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(), paciente.getTelefono(), paciente.getDocumento(), new DatosDireccion(
                paciente.getDireccion().getCalle(), paciente.getDireccion().getDistrito(), paciente.getDireccion().getCiudad(), paciente.getDireccion().getNumero(),
                paciente.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosRespuestaPaciente);
    }

    @PutMapping
    @Transactional
    public ResponseEntity actualizarPaciente(@RequestBody @Valid DatosActualizarPaciente datosActualizarPaciente) {
        Paciente paciente = pacienteRepository.getReferenceById(datosActualizarPaciente.id());
        paciente.actualizarPaciente(datosActualizarPaciente);
        return ResponseEntity.ok(new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(), paciente.getTelefono(), paciente.getDocumento(), new DatosDireccion(
                paciente.getDireccion().getCalle(), paciente.getDireccion().getDistrito(), paciente.getDireccion().getCiudad(), paciente.getDireccion().getNumero(),
                paciente.getDireccion().getComplemento())));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity inactivarPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        paciente.desactivarPaciente();
        return ResponseEntity.noContent().build();
    }

}
