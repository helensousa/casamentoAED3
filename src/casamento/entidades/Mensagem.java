package casamento.entidades;

//Importações
import casamento.interfaces.Registro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Mensagem implements Registro {
    
    //Atributos da classe
    private int idMensagem;
    private int idGrupo;
    private int idUsuario;
    private long momentoEnvio;
    private String titulo;
    private String mensagem;
    
    
    //Construtor Padrã£o
    public Mensagem() {
        this.idMensagem = -1;
        this.idGrupo = -1;
        this.idUsuario = -1;
        this.momentoEnvio = -1;
        this.mensagem = "";
        this.titulo = "";
    }
    
    
    //Construtor inicializando alguns atributos
    public Mensagem(long momentoEnvio, String mensagem, String titulo) {
        this.idMensagem = -1;
        this.idGrupo = -1;
        this.idUsuario = -1;
        this.momentoEnvio = momentoEnvio;
        this.mensagem = mensagem;
        this.titulo = titulo;
    }
    
    
    //Getter e Setter
    @Override
    public int getId() {
        return this.idMensagem;
    }

    @Override
    public void setId(int id) {
        this.idMensagem = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.idMensagem + "|" + this.idGrupo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public long getMomentoEnvio() {
        return momentoEnvio;
    }

    public void setMomentoEnvio(long momentoEnvio) {
        this.momentoEnvio = momentoEnvio;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    //Métodos herdados da interface Registro
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idMensagem);
        saida.writeInt(this.idGrupo);
        saida.writeInt(this.idUsuario);
        saida.writeLong(this.momentoEnvio);
        saida.writeUTF(this.mensagem);
        saida.writeUTF(this.titulo);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idMensagem = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.momentoEnvio = entrada.readLong();
        this.mensagem = entrada.readUTF();
        this.titulo = entrada.readUTF();
    }
    
    //traduz o objeto em uma String 
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

        String stringMensagem = this.titulo;
        stringMensagem += "\nData de envio: " + formatter.print(this.momentoEnvio);
        return stringMensagem;
    }
}
