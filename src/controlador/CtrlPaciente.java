package controlador;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import modelo.Conexion;
import modelo.Paciente;
import modelo.PacienteDAO;


public class CtrlPaciente {

    private static final String PATRON_CORREO = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final String PATRON_TELEFONO = "^[0-9+()\\-\\s]{7,30}$";

    private final PacienteDAO dao = new PacienteDAO();

    //  CONSULTAS 
    public List<Paciente> listarTodos() {

        List<Paciente> lista = dao.listarTodos();
        verificarError();
        return lista;
    }

    /** Busca por nombre, apellido, cédula, teléfono o correo. Vacío = todos. */
    public List<Paciente> buscarPorTexto(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        List<Paciente> lista = dao.buscarPorTexto(texto.trim());
        verificarError();
        return lista;
    }

    public Paciente buscarPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID del paciente no es válido.");
        }

        Paciente paciente = dao.buscarPorId(id);
        verificarError();
        return paciente;
    }

    /** Total de pacientes registrados, o -1 si no se pudo consultar. */
    public int contarPacientes() {
        return dao.contar();
    }

    public boolean baseDeDatosDisponible() {
        return Conexion.estaDisponible();
    }

    //  OPERACIONES 
    public void registrar(Paciente paciente) {

        validar(paciente);

        if (!dao.insertar(paciente)) {
            throw new IllegalStateException(
                    traducirError(dao.getUltimoError()));
        }
    }

    public void actualizar(Paciente paciente) {

        if (paciente == null || paciente.getId() <= 0) {
            throw new IllegalArgumentException(
                    "No se puede actualizar: primero selecciona un paciente.");
        }

        validar(paciente);

        if (!dao.actualizar(paciente)) {
            String motivo = dao.getUltimoError();

            throw new IllegalStateException(motivo == null
                    ? "No se encontró el paciente. Es posible que ya haya sido eliminado."
                    : traducirError(motivo));
        }
    }

    public void eliminar(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar: primero selecciona un paciente.");
        }

        if (!dao.eliminar(id)) {
            String motivo = dao.getUltimoError();

            throw new IllegalStateException(motivo == null
                    ? "No se encontró el paciente. Es posible que ya haya sido eliminado."
                    : traducirError(motivo));
        }
    }

    // REGLAS
    /** Calcula la edad en años cumplidos a partir de la fecha de nacimiento. */
    public static int calcularEdad(Date fechaNacimiento) {
        return Period.between(fechaNacimiento.toLocalDate(),
                LocalDate.now()).getYears();
    }

    private void validar(Paciente p) {

        if (p == null) {
            throw new IllegalArgumentException("No hay datos de paciente.");
        }

        p.setNombre1(limpiar(p.getNombre1()));
        p.setNombre2(limpiar(p.getNombre2()));
        p.setApellido1(limpiar(p.getApellido1()));
        p.setApellido2(limpiar(p.getApellido2()));
        p.setCedula(limpiar(p.getCedula()));
        p.setSexo(limpiar(p.getSexo()));
        p.setTelefono(limpiar(p.getTelefono()));
        p.setCorreo(limpiar(p.getCorreo()));

        if (p.getNombre1() == null || p.getApellido1() == null
                || p.getCedula() == null || p.getSexo() == null) {
            throw new IllegalArgumentException(
                    "Completa los campos obligatorios: nombre, primer apellido, cédula y sexo.");
        }

        limite(p.getNombre1(), 100, "El primer nombre");
        limite(p.getNombre2(), 100, "El segundo nombre");
        limite(p.getApellido1(), 100, "El primer apellido");
        limite(p.getApellido2(), 100, "El segundo apellido");
        limite(p.getCedula(), 30, "La cédula");
        limite(p.getCorreo(), 150, "El correo");

        if (p.getTelefono() != null
                && !p.getTelefono().matches(PATRON_TELEFONO)) {
            throw new IllegalArgumentException(
                    "El teléfono solo puede tener números, espacios, +, - y paréntesis (mínimo 7 dígitos).");
        }

        if (p.getCorreo() != null && !p.getCorreo().matches(PATRON_CORREO)) {
            throw new IllegalArgumentException(
                    "El correo electrónico no tiene un formato válido.");
        }

        if (p.getFechaNacimiento() != null) {

            if (p.getFechaNacimiento().toLocalDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "La fecha de nacimiento no puede ser futura.");
            }

            // La edad siempre se deriva de la fecha para evitar incoherencias.
            p.setEdad(calcularEdad(p.getFechaNacimiento()));
        }

        if (p.getEdad() != null && (p.getEdad() < 0 || p.getEdad() > 120)) {
            throw new IllegalArgumentException(
                    "La edad debe estar entre 0 y 120 años.");
        }
    }

    private void limite(String valor, int maximo, String campo) {

        if (valor != null && valor.length() > maximo) {
            throw new IllegalArgumentException(campo
                    + " no puede superar " + maximo + " caracteres.");
        }
    }

    private String limpiar(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }

    private void verificarError() {

        String motivo = dao.getUltimoError();

        if (motivo != null) {
            throw new IllegalStateException(traducirError(motivo));
        }
    }

    /** Convierte errores técnicos de MySQL en mensajes para el usuario. */
    private String traducirError(String motivo) {

        if (motivo == null) {
            return "No fue posible completar la operación.";
        }

        String m = motivo.toLowerCase();

        if (m.contains("duplicate entry") && m.contains("cedula")) {
            return "Ya existe un paciente registrado con esa cédula.";
        }

        if (m.contains("duplicate entry") && m.contains("correo")) {
            return "Ya existe un paciente registrado con ese correo.";
        }

        if (m.contains("foreign key")) {
            return "No se puede eliminar: el paciente tiene citas, exámenes o historia clínica asociados.";
        }

        if (m.contains("communications link failure")
                || m.contains("connection refused")
                || m.contains("access denied")
                || m.contains("unknown database")
                || m.contains("no suitable driver")) {
            return "No hay conexión con la base de datos. Verifica que MySQL esté activo.";
        }

        return "Error de base de datos: " + motivo;
    }
}
