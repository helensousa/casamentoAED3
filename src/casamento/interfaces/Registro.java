
package casamento.interfaces;

//Importações
import java.io.IOException;


public interface Registro {

    public int getId();

    public void setId(int id);

    public String chaveSecundaria();

    public byte[] toByteArray() throws IOException;

    public void fromByteArray(byte[] bytes) throws IOException;
}
