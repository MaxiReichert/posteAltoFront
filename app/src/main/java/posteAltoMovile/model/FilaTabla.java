package posteAltoMovile.model;

public class FilaTabla {

    private String nombreEquipo;
    private String escudo;
    private Integer partidosJugados;
    private Integer partidosGanados;
    private Integer partidosPerdidos;
    private Integer puntos;
    private Integer puntosAFavor;
    private Integer puntosEnContra;
    private Integer diferenciaDePuntos;

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public String getEscudo() {
        return escudo;
    }

    public Integer getPartidosJugados() {
        return partidosJugados;
    }

    public Integer getPartidosGanados() {
        return partidosGanados;
    }

    public Integer getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public Integer getPuntosAFavor() {
        return puntosAFavor;
    }

    public Integer getPuntosEnContra() {
        return puntosEnContra;
    }

    public Integer getDiferenciaDePuntos() {
        return diferenciaDePuntos;
    }
}
