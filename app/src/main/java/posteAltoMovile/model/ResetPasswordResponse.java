package posteAltoMovile.model;

public class ResetPasswordResponse {

    private Integer codigoRecuperacion;
    private String resetToken;

    public Integer getCodigoRecuperacion() {
        return codigoRecuperacion;
    }

    public void setCodigoRecuperacion(Integer codigoRecuperacion) {
        this.codigoRecuperacion = codigoRecuperacion;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
