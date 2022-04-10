package posteAltoMovile.model;

public class Partido {

    private Integer id;
    private Equipo local;
    private Equipo visitante;
    private Integer puntosLocal;
    private Integer puntosVisitante;
    private String fechaJuego;
    private Integer fechaCompetencia;
    private Estado estado;
    private Integer idCompetencia;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Equipo getLocal() {
        return local;
    }

    public void setLocal(Equipo local) {
        this.local = local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public void setVisitante(Equipo visitante) {
        this.visitante = visitante;
    }

    public Integer getPuntosLocal() {
        return puntosLocal;
    }

    public void setPuntosLocal(Integer puntosLocal) {
        this.puntosLocal = puntosLocal;
    }

    public Integer getPuntosVisitante() {
        return puntosVisitante;
    }

    public void setPuntosVisitante(Integer puntosVisitante) {
        this.puntosVisitante = puntosVisitante;
    }

    public String getFechaJuego() {
        return fechaJuego;
    }

    public void setFechaJuego(String fechaJuego) {
        this.fechaJuego = fechaJuego;
    }

    public Integer getFechaCompetencia() {
        return fechaCompetencia;
    }

    public void setFechaCompetencia(Integer fechaCompetencia) {
        this.fechaCompetencia = fechaCompetencia;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Integer getIdCompetencia() {
        return idCompetencia;
    }

    public void setIdCompetencia(Integer idCompetencia) {
        this.idCompetencia = idCompetencia;
    }
}
