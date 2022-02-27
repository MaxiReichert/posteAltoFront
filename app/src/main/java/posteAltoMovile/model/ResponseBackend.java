package posteAltoMovile.model;

public class ResponseBackend {

    String mensaje;

    public ResponseBackend(String mensaje){
        this.mensaje= mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return mensaje;
    }
}
