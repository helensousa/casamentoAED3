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


public class Grupo implements Registro {
    
    //Atributos da classe
    private int idGrupo;
    private int idUsuario;
    private String nome;
    private long momentoSorteio;
    private float valor;
    private long momentoEncontro;
    private String localEncontro;
    private String observacoes;
    private boolean sorteado;
    private boolean ativo;
    
    //Construtor padrão
    public Grupo() {
        this.idGrupo = -1;
        this.idUsuario = -1;
        this.nome = "";
        this.momentoSorteio = -1;
        this.valor = -1;
        this.momentoEncontro = -1;
        this.localEncontro = "";
        this.observacoes = "";
        this.sorteado = false;
        this.ativo = true;
    }
    
    //Construtor inicializando alguns atributos
    public Grupo(String nome, long momentoSorteio, float valor, long momentoEncontro, String localEncontro, String observacoes) {
        this.nome = nome;
        this.momentoSorteio = momentoSorteio;
        this.valor = valor;
        this.momentoEncontro = momentoEncontro;
        this.localEncontro = localEncontro;
        this.observacoes = observacoes;
        this.sorteado = false;
        this.ativo = true;
    }
    
    //Getter e Setter

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getMomentoSorteio() {
        return momentoSorteio;
    }

    public void setMomentoSorteio(long momentoSorteio) {
        this.momentoSorteio = momentoSorteio;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public long getMomentoEncontro() {
        return momentoEncontro;
    }

    public void setMomentoEncontro(long momentoEncontro) {
        this.momentoEncontro = momentoEncontro;
    }

    public String getLocalEncontro() {
        return localEncontro;
    }

    public void setLocalEncontro(String localEncontro) {
        this.localEncontro = localEncontro;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isSorteado() {
        return sorteado;
    }

    public void setSorteado(boolean sorteado) {
        this.sorteado = sorteado;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public int getId() {
        return this.idGrupo;
    }

    @Override
    public void setId(int id) {
        this.idGrupo = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.nome;
    }
    
    //Métodos herdados da interface Registro para auxiliar na escrita e leitura
    // do registro
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idGrupo);
        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.nome);
        saida.writeLong(this.momentoSorteio);
        saida.writeFloat(this.valor);
        saida.writeLong(this.momentoEncontro);
        saida.writeUTF(this.localEncontro);
        saida.writeUTF(this.observacoes);
        saida.writeBoolean(this.sorteado);
        saida.writeBoolean(this.ativo);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idGrupo = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.nome = entrada.readUTF();
        this.momentoSorteio = entrada.readLong();
        this.valor = entrada.readFloat();
        this.momentoEncontro = entrada.readLong();
        this.localEncontro = entrada.readUTF();
        this.observacoes = entrada.readUTF();
        this.sorteado = entrada.readBoolean();
        this.ativo = entrada.readBoolean();
    }
    
    //traduz o objeto em uma string que contÃ©m seus atributos
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

        String grupoString = this.nome;
        grupoString += "\nData do encontro: " + formatter.print(this.momentoEncontro);
        grupoString += "\nData do sorteio: " + formatter.print(this.momentoSorteio);
        grupoString += "\nValor aproximado dos presentes: R$ " + this.valor;
        grupoString += "\nLocal do encontro: " + this.localEncontro;
        grupoString += (isSorteado()) ? "\nO sorteio jÃ¡ aconteceu!" : "\nO sorteio ainda não aconteceu!";
        grupoString += (!this.observacoes.isEmpty()) ? "\n\nObservações:\n" + this.observacoes : "";
        return grupoString;
    }
}
