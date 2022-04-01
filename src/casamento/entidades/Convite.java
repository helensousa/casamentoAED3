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


public class Convite implements Registro {
    
    //Atributos da classe
    private int idConvite;
    private int idGrupo;
    private String email;
    private long momentoConvite;
    private byte estado;
    
    //Construtor Padrão
    public Convite() {
        this.idConvite = -1;
        this.idGrupo = -1;
        this.email = "";
        this.momentoConvite = -1;
        this.estado = 0;
    }
    
    //Construtor inicializando alguns atributos
    public Convite(String email, long momentoConvite, byte estado) {
        this.idConvite = -1;
        this.idGrupo = -1;
        this.email = email;
        this.momentoConvite = momentoConvite;
        this.estado = estado;
    }
    
    //Getter e Setter
    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getMomentoConvite() {
        return momentoConvite;
    }

    public void setMomentoConvite(long momentoConvite) {
        this.momentoConvite = momentoConvite;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    @Override
    public int getId() {
        return this.idConvite;
    }

    @Override
    public void setId(int id) {
        this.idConvite = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idGrupo + "|" + this.email;
    }
    
    //Métodos herdados da interface Registro para auxiliar na escrita e leitura
    //do registro
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idConvite);
        saida.writeInt(this.idGrupo);
        saida.writeUTF(this.email);
        saida.writeLong(this.momentoConvite);
        saida.writeByte(this.estado);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idConvite = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.email = entrada.readUTF();
        this.momentoConvite = entrada.readLong();
        this.estado = entrada.readByte();
    }
    
    //Retorna uma string com os dados do objeto
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm"); //formatação da data a ser apresentada

        String conviteString = this.email;
        conviteString += " (" + formatter.print(momentoConvite) + " - " + decodificarEstado() + ")";

        return conviteString;
    }
    
    //transforma o estado(valor inteiro) em uma String significante
    private String decodificarEstado() {
        String estadoConvite;
        switch (this.estado) {
            case 0:
                estadoConvite = "pendente";
                break;
            case 1:
                estadoConvite = "aceito";
                break;
            case 2:
                estadoConvite = "recusado";
                break;
            case 3:
                estadoConvite = "cancelado";
                break;
            default:
                estadoConvite = "Não identificado";
                break;
        }
        return estadoConvite;
    }
}
