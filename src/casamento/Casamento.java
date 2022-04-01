package casamento;

//Importações
import casamento.hud.Interface;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Casamento {

    public static void main(String[] args) {
        //Instância um objeto do tipo Interface que ira gerenciar o loop principal de execução
        Interface i = new Interface("CASAMENTO", "1.0");
        try {
            //inicia o loop de execução
            i.loopExec();
        } catch (Exception ex) {
            Logger.getLogger(Casamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
