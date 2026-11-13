package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class PacienteDAO {

   
    private final ThreadLocal<String> ultimoError = new ThreadLocal<>();

    public String getUltimoError() {
        return ultimoError.get();
    }

    private void registrarError(String operacion, SQLException e) {
        ultimoError.set(e.getMessage());
        System.err.println("Error al " + operacion + ": " + e.getMessage());
    }

    // INSERTAR PACIENTE
    public boolean insertar(Paciente p) {

        ultimoError.remove();

        String sql = "INSERT INTO Paciente "
                + "(Nombre1, Nombre2, Apellido1, Apellido2, "
                + "Cedula, Sexo, Fecha_Nacimiento, Edad, "
                + "Telefono, Correo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre1());
            ps.setString(2, p.getNombre2());
            ps.setString(3, p.getApellido1());
            ps.setString(4, p.getApellido2());
            ps.setString(5, p.getCedula());
            ps.setString(6, p.getSexo());

            if (p.getFechaNacimiento() != null) {
                ps.setDate(7, p.getFechaNacimiento());
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }

            if (p.getEdad() != null) {
                ps.setInt(8, p.getEdad());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            ps.setString(9, p.getTelefono());
            ps.setString(10, p.getCorreo());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            registrarError("insertar paciente", e);
        }

        return false;
    }

    // BUSCAR PACIENTE POR ID
    public Paciente buscarPorId(int id) {

        ultimoError.remove();

        String sql = "SELECT * FROM Paciente WHERE ID_Paciente = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPaciente(rs);
                }
            }

        } catch (SQLException e) {
            registrarError("buscar paciente por ID", e);
        }

        return null;
    }

    // BUSCAR PACIENTES POR NOMBRE, APELLIDO, CEDULA, TELEFONO O CORREO
    public List<Paciente> buscarPorTexto(String texto) {

        ultimoError.remove();

        List<Paciente> lista = new ArrayList<>();

        String sql = "SELECT * FROM Paciente WHERE "
                + "CONCAT_WS(' ', Nombre1, Nombre2, Apellido1, Apellido2) LIKE ? "
                + "OR Nombre1 LIKE ? "
                + "OR Nombre2 LIKE ? "
                + "OR Apellido1 LIKE ? "
                + "OR Apellido2 LIKE ? "
                + "OR Cedula LIKE ? "
                + "OR Telefono LIKE ? "
                + "OR Correo LIKE ? "
                + "ORDER BY Apellido1, Nombre1";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + texto.trim() + "%";

            for (int i = 1; i <= 8; i++) {
                ps.setString(i, busqueda);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapRowToPaciente(rs));
                }
            }

        } catch (SQLException e) {
            registrarError("buscar pacientes", e);
        }

        return lista;
    }

    // ACTUALIZAR PACIENTE
    public boolean actualizar(Paciente p) {

        ultimoError.remove();

        String sql = "UPDATE Paciente SET "
                + "Nombre1 = ?, Nombre2 = ?, "
                + "Apellido1 = ?, Apellido2 = ?, "
                + "Cedula = ?, Sexo = ?, "
                + "Fecha_Nacimiento = ?, Edad = ?, "
                + "Telefono = ?, Correo = ? "
                + "WHERE ID_Paciente = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre1());
            ps.setString(2, p.getNombre2());
            ps.setString(3, p.getApellido1());
            ps.setString(4, p.getApellido2());
            ps.setString(5, p.getCedula());
            ps.setString(6, p.getSexo());

            if (p.getFechaNacimiento() != null) {
                ps.setDate(7, p.getFechaNacimiento());
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }

            if (p.getEdad() != null) {
                ps.setInt(8, p.getEdad());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            ps.setString(9, p.getTelefono());
            ps.setString(10, p.getCorreo());
            ps.setInt(11, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarError("actualizar paciente", e);
        }

        return false;
    }

    // ELIMINAR PACIENTE
    public boolean eliminar(int id) {

        ultimoError.remove();

        String sql = "DELETE FROM Paciente WHERE ID_Paciente = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            registrarError("eliminar paciente", e);
        }

        return false;
    }

    // LISTAR TODOS LOS PACIENTES (ordenados por apellido y nombre)
    public List<Paciente> listarTodos() {

        ultimoError.remove();

        List<Paciente> lista = new ArrayList<>();

        String sql = "SELECT * FROM Paciente ORDER BY Apellido1, Nombre1";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRowToPaciente(rs));
            }

        } catch (SQLException e) {
            registrarError("listar pacientes", e);
        }

        return lista;
    }

    // CONTAR PACIENTES REGISTRADOS (-1 si no fue posible consultarlos)
    public int contar() {

        ultimoError.remove();

        String sql = "SELECT COUNT(*) FROM Paciente";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            registrarError("contar pacientes", e);
        }

        return -1;
    }

    // CONVERTIR RESULTADO SQL A OBJETO PACIENTE
    private Paciente mapRowToPaciente(ResultSet rs)
            throws SQLException {

        Paciente p = new Paciente();

        p.setId(rs.getInt("ID_Paciente"));
        p.setNombre1(rs.getString("Nombre1"));
        p.setNombre2(rs.getString("Nombre2"));
        p.setApellido1(rs.getString("Apellido1"));
        p.setApellido2(rs.getString("Apellido2"));
        p.setCedula(rs.getString("Cedula"));
        p.setSexo(rs.getString("Sexo"));
        p.setFechaNacimiento(rs.getDate("Fecha_Nacimiento"));

        int edad = rs.getInt("Edad");

        if (rs.wasNull()) {
            p.setEdad(null);
        } else {
            p.setEdad(edad);
        }

        p.setTelefono(rs.getString("Telefono"));
        p.setCorreo(rs.getString("Correo"));

        return p;
    }
}
