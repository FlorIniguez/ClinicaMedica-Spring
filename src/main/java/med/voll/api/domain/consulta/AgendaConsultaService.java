package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validaciones.ValidadorCancelarConsulta;
import med.voll.api.domain.consulta.validaciones.ValidadorDeConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaConsultaService {

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private ConsultaRepository consultaRepository;
    //me inyecta la interfaz y todos sus metodos
    @Autowired
    List<ValidadorDeConsultas> validadores;
    @Autowired
    List<ValidadorCancelarConsulta> validadorCancelarConsultas;

    public DatosDetalleConsulta agendar(DatosAgendarConsulta datos) {

        if (!pacienteRepository.findById(datos.idPaciente()).isPresent()) {
            throw new ValidacionDeIntegridad("este id para el paciente no fue encontrado");
        }

        if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())) {
            throw new ValidacionDeIntegridad("este id para el medico no fue encontrado");
        }
        //con el forEach recorro todos los elementos de la lista, cada validador,
        //uso el metodo validar con los datos que estoy recibiendo por paramatro
        validadores.forEach(v -> v.validar(datos));

        Paciente paciente = pacienteRepository.findById(datos.idPaciente()).get();

        Medico medico = seleccionarMedico(datos);
        if (medico == null) {
            throw new ValidacionDeIntegridad("No existen medicos dispobibles para este horario y especialidad");
        }

        Consulta consulta = new Consulta(medico, paciente, datos.fecha());

        consultaRepository.save(consulta);
        return new DatosDetalleConsulta(consulta);

    }

    public Consulta cancelarConsulta(DatosCancelamientoConsulta datos) {
        if (!consultaRepository.existsById(datos.idConsulta())) {
            throw new ValidacionDeIntegridad("Id de la consulta no existe");
        }
        validadorCancelarConsultas.forEach(v -> v.validar(datos));
        Consulta consulta = consultaRepository.getReferenceById(datos.idConsulta());
        consulta.cancelar(MotivoCancelacion.valueOf(datos.motivo()));
        return consulta;
    }

    private Medico seleccionarMedico(DatosAgendarConsulta datos) {
        if (datos.idMedico() != null) {
            return medicoRepository.getReferenceById(datos.idMedico());
        }
        if (datos.especialidad() == null) {
            throw new ValidacionDeIntegridad("debe seleccionarse una especialidad para el medico");
        }

        return medicoRepository.seleccionarMedicoConEspecialidadEnFecha(datos.especialidad(), datos.fecha());
    }
}