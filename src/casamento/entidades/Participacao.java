package casamento.entidades;

//Importações
import casamento.interfaces.Registro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Participacao implements Registro {
    
    //Atributos da classe
    private int idParticipacao;
    private int idUsuario;
    private int idGrupo;
    private int idAmigo;
    
    //Construtor padrão
    public Participacao() {
        this.idParticipacao = -1;
        this.idUsuario = -1;
        this.idGrupo = -1;
        this.idAmigo = -1;
    }
    
    //Getter e Setter
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdAmigo() {
        return idAmigo;
    }

    public void setIdAmigo(int idAmigo) {
        this.idAmigo = idAmigo;
    }

    @Override
    public int getId() {
        return this.idParticipacao;
    }

    @Override
    public void setId(int id) {
        this.idParticipacao = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.idGrupo;
    }
    
    //Métodos herdados da interface Registro
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idParticipacao);
        saida.writeInt(this.idUsuario);
        saida.writeInt(this.idGrupo);
        saida.writeInt(this.idAmigo);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idParticipacao = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.idAmigo = entrada.readInt();
    }
}
