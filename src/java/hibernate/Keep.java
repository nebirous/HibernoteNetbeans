package hibernate;

public class Keep  implements java.io.Serializable {

     private int id;
     private Usuario usuario;
     private int idAndroid;
     private String contenido;
     private String ruta;
     private String estado;

    public Keep() {
    }

    public Keep(int id, int idAndroid, String contenido, String estado) {
        this.id = id;
        this.idAndroid = idAndroid;
        this.contenido = contenido;
        this.estado = estado;
    }
	
    public Keep(Usuario usuario, String estado) {
        this.usuario = usuario;
        this.estado = estado;
    }
    
    public Keep(Usuario usuario, int idAndroid, String contenido, String ruta, String estado) {
       this.usuario = usuario;
       this.idAndroid = idAndroid;
       this.contenido = contenido;
       this.ruta = ruta;
       this.estado = estado;
    }
    
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return this.usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Integer getIdAndroid() {
        return this.idAndroid;
    }
    
    public void setIdAndroid(Integer idAndroid) {
        this.idAndroid = idAndroid;
    }
    public String getContenido() {
        return this.contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public String getRuta() {
        return this.ruta;
    }
    
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }


    public String getUserName(){
        return usuario.getLogin();
    }


}


