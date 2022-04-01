package casamento.entidades;

import casamento.interfaces.Registro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Sugestao implements Registro {
    
    //Atributos da classe
    private int idSugestao;
    private int idUsuario;
    String produto;
    String loja;
    float valor;
    String observacoes;
    
    //Construtor padrão
    public Sugestao() {
        this.idSugestao = -1;
        this.idUsuario = -1;
        this.produto = "";
        this.loja = "";
        this.valor = 0;
        this.observacoes = "";
    }
    
    //Construtor inicializando alguns atributos
    public Sugestao(String produto, String loja, float valor, String observacoes) {
        this.produto = produto;
        this.loja = loja;
        this.valor = valor;
        this.observacoes = observacoes;
    }
    
    //Getter e Setter
    
    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int getId() {
        return this.idSugestao;
    }

    @Override
    public void setId(int id) {
        this.idSugestao = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.produto;
    }
    
    
    //Métodos herdados da interface Registro
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idSugestao);
        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.produto);
        saida.writeUTF(this.loja);
        saida.writeFloat(this.valor);
        saida.writeUTF(this.observacoes);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idSugestao = entrada.readInt();
        this.idUsuario = entrada.readInt();
        this.produto = entrada.readUTF();
        this.loja = entrada.readUTF();
        this.valor = entrada.readFloat();
        this.observacoes = entrada.readUTF();
    }
    
    
    //traduz o objeto em uma String
    @Override
    public String toString() {
        String sugestaoString = "Produto: " + this.produto;
        sugestaoString += "\nLoja: " + this.loja;
        sugestaoString += "\nValor: R$ " + this.valor;
        sugestaoString += "\nObservações: " + this.observacoes;

        return sugestaoString;
    }
}
