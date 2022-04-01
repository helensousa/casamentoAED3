package casamento.entidades;

//Importações
import casamento.interfaces.Registro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Usuario implements Registro {

    //Atributos da classe
    private int idUsuario;
    private String nome;
    private String apelido;
    private String email;
    private String senha;
    private String codigoDeRecuperacao;
    
    //Construtor padrão
    public Usuario() {
        this.idUsuario = -1;
        this.nome = "";
        this.apelido = "";
        this.email = "";
        this.senha = "";
        this.codigoDeRecuperacao = "";
    }
    
    //Construtor inicializando alguns atributos
    public Usuario(String nome, String apelido, String email, String senha, String codigoDeResgate) {
        this.idUsuario = -1;
        this.nome = nome;
        this.apelido = apelido;
        this.email = email;
        this.senha = senha;
        this.codigoDeRecuperacao = codigoDeResgate;
    }

    //Getter e Setter
    @Override
    public int getId() {
        return idUsuario;
    }

    @Override
    public void setId(int id) {
        this.idUsuario = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCodigoDeRecuperacao() {
        return codigoDeRecuperacao;
    }

    public void setCodigoDeRecuperacao(String codigoDeRecuperacao) {
        this.codigoDeRecuperacao = codigoDeRecuperacao;
    }

    @Override
    public String chaveSecundaria() {
        return this.email;
    }
    
    //Métodos herdados da interface Registro
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.nome);
        saida.writeUTF(this.apelido);
        saida.writeUTF(this.email);
        saida.writeUTF(this.senha);
        saida.writeUTF(this.codigoDeRecuperacao);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idUsuario = entrada.readInt();
        this.nome = entrada.readUTF();
        this.apelido = entrada.readUTF();
        this.email = entrada.readUTF();
        this.senha = entrada.readUTF();
        this.codigoDeRecuperacao = entrada.readUTF();
    }
    
    
    //traduz o objeto em uma String
    @Override
    public String toString() {
        String userString = "Nome: " + this.nome;
        userString += "\nApelido: " + this.apelido;
        userString += "\nEmail: " + this.email;
        userString += "\nSenha: " + this.senha;
        userString += "\nCódigo: " + this.codigoDeRecuperacao;

        return userString;
    }
}
