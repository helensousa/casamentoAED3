package casamento.hud;

//Importações
import casamento.bancoDeDados.CRUD;
import casamento.entidades.Convite;
import casamento.entidades.Grupo;
import casamento.entidades.Mensagem;
import casamento.entidades.Participacao;
import casamento.entidades.Sugestao;
import casamento.entidades.Usuario;
import casamento.indices.ArvoreBMais_ChaveComposta_String_Int;
import casamento.indices.ArvoreBMais_Int_Int;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Comparator.comparing;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Interface {
    //variável para guardar os dados do usuário logado
    public static Usuario usuario;
    
    //Atributos da classe
    
    //Atributos relativos a aplica��o
    private String nomeAplicacao;
    private String versaoAplicacao;
    
    //Atributos relativos aos CRUDS Gen�ricos
    private CRUD<Usuario> crudUsuario;
    private CRUD<Sugestao> crudSugestao;
    private CRUD<Grupo> crudGrupos;
    private CRUD<Convite> crudConvites;
    private CRUD<Participacao> crudParticipacao;
    private CRUD<Mensagem> crudMensagens;
    
    //entrada de dados padrão
    private final Scanner in;
    
    //variável de verificação de exist�nciaa de login
    private boolean isLogged;
    
    
    //HashMaps utilizados na estratégia de execução dos menus
    private final HashMap<Short, String> opcoesMenuInicial;
    private final HashMap<Short, String> opcoesMenuPrincipal;
    private final HashMap<Short, String> opcoesMenuSugestoes;
    private final HashMap<Short, String> opcoesMenuGrupos;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos_Grupos;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos_Convites;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos_Participantes;
    private final HashMap<Short, String> opcoesMenuGrupos_ParticipacaoNosGrupos;
    private final HashMap<Short, String> opcoesMenuGrupos_EnvioLeituraMensagens;
    
    //guarda a �ltima tela (menu) visitada pelo usu�rio
    Tela ultimaTela;
    
    //Indices utilizados para criar os relacionamentos
    private ArvoreBMais_Int_Int indiceRelacionamentoSugestaoUsuario;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoUsuario;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoConvite;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoParticipacao;
    private ArvoreBMais_Int_Int indiceRelacionamentoUsuarioParticipacao;
    private ArvoreBMais_Int_Int indiceRelacionamentoUsuarioMensagem;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoMensagem;
    private ArvoreBMais_ChaveComposta_String_Int listaInvertidaEmailConvite;
    
    //variável para gerenciar a quantidade de convites que um usuário possui
    private int quantConvites;
    
    
    //Construtor único e padrão
    public Interface(String nome, String versao) {
        //seta os atributos iniciais necessários
        this.nomeAplicacao = nome;
        this.versaoAplicacao = versao;
        this.isLogged = false;
        ultimaTela = Tela.MENU_INICIAL;
        usuario = null;
        
        //inicializa tudo que for responsável por trabalhar com escrita e leitura nos arquivos
        try {
            crudUsuario = new CRUD("BDUsuario", Usuario.class.getConstructor());
            crudSugestao = new CRUD("BDSugestao", Sugestao.class.getConstructor());
            crudGrupos = new CRUD("BDGrupos", Grupo.class.getConstructor());
            crudConvites = new CRUD("BDConvites", Convite.class.getConstructor());
            crudParticipacao = new CRUD("BDParticipacao", Participacao.class.getConstructor());
            crudMensagens = new CRUD("BDMensagens", Mensagem.class.getConstructor());

            indiceRelacionamentoSugestaoUsuario = new ArvoreBMais_Int_Int(10,
                    this.crudSugestao.getDiretorio() + "/indiceRelacionamento." + "Sugestao" + ".idx");
            indiceRelacionamentoGrupoUsuario = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "Grupo" + ".idx");
            indiceRelacionamentoGrupoConvite = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "Convite" + ".idx");
            listaInvertidaEmailConvite = new ArvoreBMais_ChaveComposta_String_Int(10, this.crudConvites.getDiretorio() + "/listaInvertida_EmailConvite."
                    + "Convite" + ".idx");
            indiceRelacionamentoGrupoParticipacao = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "GrupoParticipacao" + ".idx");
            indiceRelacionamentoUsuarioParticipacao = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "UsuarioParticipacao" + ".idx");
            indiceRelacionamentoGrupoMensagem = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "GrupoMensagem" + ".idx");
            indiceRelacionamentoUsuarioMensagem = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "UsuarioMensagem" + ".idx");

        } catch (Exception ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //inicializa a entrada padrão
        in = new Scanner(System.in);
        
        //seta o conteúdo de cada menu através da criação de entradas no modelo (opção, nome da opção)
        opcoesMenuInicial = new HashMap<>();
        opcoesMenuInicial.put((short) 1, "Entrar");
        opcoesMenuInicial.put((short) 2, "Criar novo usuário (primeiro acesso)");
        opcoesMenuInicial.put((short) 3, "Recuperar senha");
        opcoesMenuInicial.put((short) 4, "Sair");

        opcoesMenuPrincipal = new HashMap<>();
        opcoesMenuPrincipal.put((short) 1, "Sugestão de presentes");
        opcoesMenuPrincipal.put((short) 2, "Grupos");
        opcoesMenuPrincipal.put((short) 3, "Convites: " + quantConvites);
        opcoesMenuPrincipal.put((short) 4, "Deslogar");

        opcoesMenuSugestoes = new HashMap<>();
        opcoesMenuSugestoes.put((short) 1, "Listar");
        opcoesMenuSugestoes.put((short) 2, "Incluir");
        opcoesMenuSugestoes.put((short) 3, "Alterar");
        opcoesMenuSugestoes.put((short) 4, "Excluir");
        opcoesMenuSugestoes.put((short) 5, "Retornar ao menu anterior");

        opcoesMenuGrupos = new HashMap<>();
        opcoesMenuGrupos.put((short) 1, "Criação e gerenciamento de grupos");
        opcoesMenuGrupos.put((short) 2, "Participação nos grupos");
        opcoesMenuGrupos.put((short) 3, "Enviar/Ler mensagens");
        opcoesMenuGrupos.put((short) 4, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos.put((short) 1, "Grupos");
        opcoesMenuGerencimentoDeGrupos.put((short) 2, "Convites");
        opcoesMenuGerencimentoDeGrupos.put((short) 3, "Participantes");
        opcoesMenuGerencimentoDeGrupos.put((short) 4, "Sorteio");
        opcoesMenuGerencimentoDeGrupos.put((short) 5, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos_Grupos = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 1, "Listar");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 2, "Incluir");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 3, "Alterar");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 4, "Desativar");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 5, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos_Convites = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 1, "Listagem dos convites");
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 2, "Emissão de convites");
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 3, "Cancelamento de convites");
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 4, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos_Participantes = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos_Participantes.put((short) 1, "Listagem");
        opcoesMenuGerencimentoDeGrupos_Participantes.put((short) 2, "Remoção");
        opcoesMenuGerencimentoDeGrupos_Participantes.put((short) 3, "Retornar ao menu anterior");

        opcoesMenuGrupos_ParticipacaoNosGrupos = new HashMap<>();
        opcoesMenuGrupos_ParticipacaoNosGrupos.put((short) 1, "Visualizar participantes");
        opcoesMenuGrupos_ParticipacaoNosGrupos.put((short) 2, "Visualizar amigo sorteado");
        opcoesMenuGrupos_ParticipacaoNosGrupos.put((short) 3, "Ler/enviar mensagens ao grupo");
        opcoesMenuGrupos_ParticipacaoNosGrupos.put((short) 4, "Retornar ao menu anterior");

        opcoesMenuGrupos_EnvioLeituraMensagens = new HashMap<>();
        opcoesMenuGrupos_EnvioLeituraMensagens.put((short) 1, "Enviar mensagem");
        opcoesMenuGrupos_EnvioLeituraMensagens.put((short) 2, "Ler mensagens");
        opcoesMenuGrupos_EnvioLeituraMensagens.put((short) 3, "Retornar ao menu anterior");
    }
    
    //loop principal de execução do programa
    public void loopExec() throws Exception {
        boolean exec; //flag de teste para saber se o programa ainda está em execução
        
        do {
            //se o usuário está logado
            if (isLogged) {
                
                //recupera os convites que ele possui
                int[] convites = listaInvertidaEmailConvite.read(usuario.chaveSecundaria());
                //atualiza a quantidade a ser mostrada no menu principal
                setQuantConvites(convites.length);
                opcoesMenuPrincipal.remove(3);
                opcoesMenuPrincipal.put((short) 3, "Convites: " + getQuantConvites());
            }
            //está a linha principal que mantém o programa em execução até que exec seja false
            exec = showMenu(ultimaTela);
            clearScreen();
        } while (exec);
    }
    
    //mostra o cabe�alho da aplica��o
    private void showHeader() {
        System.out.println("--------------------------");
        System.out.println(nomeAplicacao + " " + versaoAplicacao);
        System.out.println("Desenvolvido por Helen Machado de Sousa");
        System.out.println("--------------------------");
        if (isLogged) {
            System.out.println("Voc� est� logado como " + usuario.getNome());
            System.out.println("--------------------------");

        }
        System.out.println();
    }
    
    //responsável por determinar qual menu será mostrado em cada tela
    //e manter a variável exec atualizada com o status de execução do programa
    public boolean showMenu(Tela menu) throws Exception {
        boolean exec;

        showHeader();

        showOpcoes(menu);

        exec = procOption(getOption(menu), menu);

        return exec;
    }
    
    
    /**
     * Basicamente, realiza um switch tomando como valor de teste o conteúdo da variável
     * menu que dirá quais opções deverão ser mostradas em função da tela que deve ser mostrada
     * É um código padrão para todos os menus, em que a única diferença está na variável que dirá
     * qual é o keySet
     * @param menu 
     */
    private void showOpcoes(Tela menu) {
        switch (menu) {
            case MENU_INICIAL:
                System.out.println("ACESSO\n");
                for (Short s : opcoesMenuInicial.keySet()) {
                    System.out.println(s + ")" + opcoesMenuInicial.get(s));
                }
                break;
            case MENU_PRINCIPAL:
                System.out.println("IN�CIO\n");
                for (Short s : opcoesMenuPrincipal.keySet()) {
                    System.out.println(s + ")" + opcoesMenuPrincipal.get(s));
                }
                break;
            case MENU_SUGESTOES:
                System.out.println("IN�CIO > SUGESTÕES\n");
                for (Short s : opcoesMenuSugestoes.keySet()) {
                    System.out.println(s + ")" + opcoesMenuSugestoes.get(s));
                }
                break;
            case MENU_GRUPOS:
                System.out.println("IN�CIO > GRUPOS\n");
                for (Short s : opcoesMenuGrupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGrupos.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS:
                System.out.println("IN�CIO > GRUPOS > GERENCIAMENTO DE GRUPOS\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_GRUPOS:
                System.out.println("IN�CIO > GRUPOS > GERENCIAMENTO DE GRUPOS > GRUPOS\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos_Grupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos_Grupos.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_CONVITES:
                System.out.println("IN�CIO > GRUPOS > GERENCIAMENTO DE GRUPOS > CONVITES\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos_Convites.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos_Convites.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES:
                System.out.println("IN�CIO > GRUPOS > GERENCIAMENTO DE GRUPOS > PARTICIPANTES\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos_Participantes.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos_Participantes.get(s));
                }
                break;
            case MENU_GRUPOS_PARTICIPACAO_NOS_GRUPOS:
                System.out.println("IN�CIO > GRUPOS > PARTICIPAÇÃO EM GRUPO\n");
                for (Short s : opcoesMenuGrupos_ParticipacaoNosGrupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGrupos_ParticipacaoNosGrupos.get(s));
                }
                break;
            case MENU_GRUPOS_ENVIO_LEITURA_MENSAGENS:
                System.out.println("IN�CIO > GRUPOS > MENSAGENS\n");
                for (Short s : opcoesMenuGrupos_EnvioLeituraMensagens.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGrupos_EnvioLeituraMensagens.get(s));
                }
                break;
            default:
                System.out.println("ERRO: Não foi possível exibir o menu desejado.");
                break;
        }
    }

    /**
     * Também baseada na variável menu e serve para validar se a entrada de opção do
     * usuário está condizente com as opções existentes em cada menu
     * @param menu
     * @return opcao escolhida
     */
    private short getOption(Tela menu) {
        short option;
        System.out.print("Op��es: ");
        option = in.nextShort();

        switch (menu) {
            case MENU_INICIAL:
                while (!opcoesMenuInicial.containsKey(option)) {
                    System.out.print("\nOp��o inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_PRINCIPAL:
                while (!opcoesMenuPrincipal.containsKey(option)) {
                    System.out.print("\nOp��o inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_SUGESTOES:
                while (!opcoesMenuSugestoes.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GRUPOS:
                while (!opcoesMenuGrupos.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS:
                while (!opcoesMenuGerencimentoDeGrupos.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_GRUPOS:
                while (!opcoesMenuGerencimentoDeGrupos_Grupos.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_CONVITES:
                while (!opcoesMenuGerencimentoDeGrupos_Convites.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES:
                while (!opcoesMenuGerencimentoDeGrupos_Participantes.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;

            case MENU_GRUPOS_PARTICIPACAO_NOS_GRUPOS:
                while (!opcoesMenuGrupos_ParticipacaoNosGrupos.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GRUPOS_ENVIO_LEITURA_MENSAGENS:
                while (!opcoesMenuGrupos_EnvioLeituraMensagens.containsKey(option)) {
                    System.out.print("\nOp��o Inv�lida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;

            default:
                System.out.println("ERRO: Opção inexistente.");
                break;
        }

        return option;
    }

    /**
     * Método chave responsável por dizer o que deve ser executado e qual tela deve ser exibida a cada opção
     * escolhida pelo usuário
     * @param option
     * @param menu
     * @return
     * @throws Exception
     */
    private boolean procOption(short option, Tela menu) throws Exception {

        if (menu == Tela.MENU_INICIAL) {
            switch (option) {
                case 1:
                    usuario = procLogin();
                    if (!(usuario == null)) {
                        ultimaTela = Tela.MENU_PRINCIPAL;
                    }

                    return true;
                case 2:
                    procCadastro();
                    return true;
                case 3:
                    procRecSenha();
                    return true;
                case 4:
                    showMensagemSaida();
                    return false;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    return false;
            }
        } else if (menu == Tela.MENU_PRINCIPAL) {
            switch (option) {
                case 1:
                    ultimaTela = Tela.MENU_SUGESTOES;
                    return true;
                case 2:
                    ultimaTela = Tela.MENU_GRUPOS;
                    return true;
                case 3:
                    procConvitesPendentes();
                    ultimaTela = Tela.MENU_PRINCIPAL;
                    return true;
                case 4:
                    ultimaTela = Tela.MENU_INICIAL;
                    setIsLogged(false);
                    return true;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_SUGESTOES) {
            switch (option) {
                case 1:
                    procListagemSugestoes();
                    break;
                case 2:
                    procCadastroSugestao();
                    break;
                case 3:
                    procAlteracaoSugestao();
                    break;
                case 4:
                    procExclusaoSugestao();
                    break;
                case 5:
                    ultimaTela = Tela.MENU_PRINCIPAL;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GRUPOS) {
            switch (option) {
                case 1:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                case 2:
                    ultimaTela = Tela.MENU_GRUPOS_PARTICIPACAO_NOS_GRUPOS;
                    break;
                case 3:
                    ultimaTela = Tela.MENU_GRUPOS_ENVIO_LEITURA_MENSAGENS;
                    break;
                case 4:
                    ultimaTela = Tela.MENU_PRINCIPAL;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS) {
            switch (option) {
                case 1:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS;
                    break;
                case 2:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES;
                    break;
                case 3:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES;
                    break;
                case 4:
                    procSorteio();
                    break;
                case 5:
                    ultimaTela = Tela.MENU_GRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS) {
            switch (option) {
                case 1:
                    procListagemGrupos();
                    break;
                case 2:
                    procCadastroGrupo();
                    break;
                case 3:
                    procAlteracaoGrupo();
                    break;
                case 4:
                    procDesativarGrupo();
                    break;
                case 5:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES) {
            switch (option) {
                case 1:
                    procListagemConvites();
                    break;
                case 2:
                    procEmissaoConvites();
                    break;
                case 3:
                    procCancelamentoConvites();
                    break;
                case 4:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES) {
            switch (option) {
                case 1:
                    procListagemParticipantes();
                    break;
                case 2:
                    procRemocaoParticipantes();
                    break;
                case 3:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GRUPOS_PARTICIPACAO_NOS_GRUPOS) {
            switch (option) {
                case 1:
                    procVisualizarParticipantes();
                    break;
                case 2:
                    procVisualizarAmigoSorteado();
                    break;
                case 3:
                    break;
                case 4:
                    ultimaTela = Tela.MENU_GRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GRUPOS_ENVIO_LEITURA_MENSAGENS) {
            switch (option) {
                case 1:
                    procEnvioMensagem();
                    break;
                case 2:
                    procLeituraMensagens();
                    break;
                case 3:
                    ultimaTela = Tela.MENU_GRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        }
        return true;
    }
    
    //mensagem padrão de saída
    private void showMensagemSaida() {
        System.out.println("\nObrigado por utilizar esta aplicação! Volte sempre.");
    }
    
    //Daqui em diante todas as funções proc tem relação com alguma funcionalidade
    //em algum menu específico dentro da aplicação
    
    /**
     * Processa o login do usuário na tela inicial retornando um objeto
     * com os dados do usuário que logou no sistema
     * @return
     * @throws Exception 
     */
    private Usuario procLogin() throws Exception {
        String email;
        String senha;
        Usuario us;
        
        //lê o email
        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();
        
        //valida a existência do usuário com base em sua chave secundária
        //em inválido retorna null
        if ((us = crudUsuario.read(email)) == null) {
            System.out.println("\nERRO: Não existe usuário cadastrado para o email inserido.");
            pressToContinue();
            return null;
        }
        
        //lê a senha
        System.out.print("\nDigite sua senha: ");
        senha = in.nextLine();
        
        //valida a senha e em caso inválido retorna null
        if (!us.getSenha().equals(senha)) {
            System.out.println("\nERRO: A senha inserida é inválida.");
            pressToContinue();
            return null;
        }

        System.out.println("\nLogin realizado com sucesso!\n\n\n");
        
        //diz que o usuário está logado
        setIsLogged(true);
        
        //retorna o objeto do usuário logado
        return us;
    }
    
    //Daqui em diante é seguido o passo a passo do projeto especificado no Canvas
    //com pequenas modificações feitas a medida que eu fui achando necessário
    
    //Processa o cadastro de um usuário
    private void procCadastro() throws Exception {
        Usuario us;
        String email;
        String nome;
        String apelido;
        String senha;
        String codRec;
        String confirma;

        in.nextLine();
        
        //lê email
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();
        
        //valida email
        if (crudUsuario.read(email) != null || !isValidEmailAddressRegex(email)) {
            System.out.println("\nERRO: O email inserido já pertence a outro usuário ou é inválido.");
            pressToContinue();
            return;
        }
        
        //lê nome
        System.out.print("\nDigite seu nome: ");
        nome = in.nextLine();
        
        //valida o nome
        while (nome.equals("") || nome.length() < 1) {
            System.out.println("ERRO: O nome inserido é inválido. Insira novamente.");
            System.out.print("\nDigite seu nome: ");
            nome = in.nextLine();
        }
        
        //lê apelido
        System.out.print("\nDigite seu apelido: ");
        apelido = in.nextLine();
        
        //valida o apelido
        while (apelido == null || apelido.length() < 1) {
            System.out.println("ERRO: O apelido inserido é inválido. Insira novamente.");
            System.out.print("\nDigite seu apelido: ");
            apelido = in.nextLine();
        }
        
        //lê a senha
        System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
        senha = in.nextLine();
        
        //valida a senha
        while (senha.equals("") || senha.length() < 5) {
            System.out.println("ERRO: A senha inserida é inválida. Insira novamente.");
            System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
            senha = in.nextLine();
        }
        
        //lê o código de recuperação
        System.out.print("\nDigite seu código de recuperação de senha (no mínimo 5 caracteres): ");
        codRec = in.nextLine();

        //valida o código de recuperação
        while (codRec.equals("") || codRec.length() < 5) {
            System.out.println("ERRO: O código de recuperação é inválido. Insira novamente.");
            System.out.print("\nDigite seu código de recuperação de senha (no mínimo 5 caracteres): ");
            codRec = in.nextLine();
        }
        
        //instância o novo objeto do tipo Usuario
        us = new Usuario(nome, apelido, email, senha, codRec);
        
        //solicita a confirmação da criação
        System.out.println("\nVocê confirma a criação de um usuário com estes dados? (Digite sim ou nao)\n");
        System.out.println(us);

        System.out.print("\nConfirma: ");
        confirma = in.nextLine();

        while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
            System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
            System.out.print("Confirma: ");
            confirma = in.nextLine();
        }

        if (confirma.equals("sim")) {
            crudUsuario.create(us);
            System.out.println("\nUsuário cadastrado com sucesso.");
        }
        if (confirma.equals("nao")) {
            System.out.println("\nCadastro de usuário abortado.");
        }

        pressToContinue();
    }
    
    /**
     * O processo de recuperação de senha segue a mesma ideia da criação do usuário, porém ao invés
     * de utilizar o método create utilizamos o método update para atualizar a senha para um novo valor
     * @throws Exception 
     */
    private void procRecSenha() throws Exception {
        String email;
        Usuario us;
        String novaSenha;
        String codRec;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if ((us = crudUsuario.read(email)) == null) {
            System.out.println("\nERRO: Não existe usuário cadastrado com este email.");
        } else {
            System.out.print("\nDigite seu código de recuperação: ");
            codRec = in.nextLine();

            if (codRec.equals(us.getCodigoDeRecuperacao())) {
                System.out.print("\nDigite a nova senha (no mínimo 5 caracteres): ");
                novaSenha = in.nextLine();

                while (novaSenha.equals("") || novaSenha.length() < 5) {
                    System.out.println("ERRO: A senha inserida é inválida. Insira novamente.");
                    System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
                    novaSenha = in.nextLine();
                }

                us.setSenha(novaSenha);
                crudUsuario.update(us);

                System.out.println("\nSenha atualizada com sucesso!");
            } else {
                System.out.println("\nERRO: Código de recuperação inválido.");
            }
        }

        pressToContinue();
    }
    
    /**
     * Lista as sugestões do usuário com base na obtenção das sugestões através
     * do índice de relacionamento que conecta a sugestão ao usuário
     * @throws Exception 
     */
    private void procListagemSugestoes() throws Exception {
        int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]));
                System.out.println();
            }
        } else {
            System.out.println("Você não possui sugestões cadastradas.");
        }

        pressToContinue();
    }
    
    /**
     * Cadastra uma sugestão seguindo o mesmo processo dos outros métodos de cadastro
     * com alguma diferença no uso dos índices
     * @throws Exception 
     */
    private void procCadastroSugestao() throws Exception {
        Sugestao sg;
        String produto, loja, observacoes;
        float valor;
        String confirma;

        in.nextLine();
        System.out.print("\nDigite o nome do produto: ");
        produto = in.nextLine();

        if (!produto.equals("")) {
            System.out.print("\nDigite o nome da loja: ");
            loja = in.nextLine();

            while (loja.equals("")) {
                System.out.println("ERRO: O nome fornecido para loja é inválido. Insira novamente.");
                System.out.print("\nDigite o nome da loja: ");
                loja = in.nextLine();
            }

            System.out.print("\nDigite as observações: ");
            observacoes = in.nextLine();

            while (observacoes.equals("")) {
                System.out.println("ERRO: Texto inválido. Insira novamente.");
                System.out.print("\nDigite as observações: ");
                observacoes = in.nextLine();
            }

            System.out.print("\nDigite o valor: R$ ");
            valor = in.nextFloat();

            while (valor <= 0) {
                System.out.println("ERRO: O valor inserido é inválido. Insira novamente.");
                System.out.print("\nDigite o valor: R$ ");
                valor = in.nextFloat();
            }

            in.nextLine();
            sg = new Sugestao(produto, loja, valor, observacoes);

            System.out.println("\nVocê confirma a criação de uma sugestão com estes dados? (Digite sim ou nao)\n");
            System.out.println(sg);

            System.out.print("\nConfirma: ");
            confirma = in.nextLine();

            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                System.out.print("Confirma: ");
                confirma = in.nextLine();
            }

            if (confirma.equals("sim")) {
                sg.setIdUsuario(usuario.getId());
                int id = crudSugestao.create(sg);
                indiceRelacionamentoSugestaoUsuario.create(usuario.getId(), id);
                System.out.println("\nSugestão cadastrada com sucesso.");
            }

            if (confirma.equals("nao")) {
                System.out.println("\nCadastro de sugestão abortado.");
            }

            pressToContinue();
        }
    }
    
    /**
     * Segue o modelo padrão apresentado para alteração de registros e no final realiza
     * um update através do CRUD
     * @throws Exception 
     */
    private void procAlteracaoSugestao() throws Exception {
        int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());
        String sgEscolhida;
        Sugestao escolhida;
        int alterado = 0;
        String produto, loja, observacoes;
        String valor;
        String confirma;

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]));
                System.out.println();
            }

            System.out.print("Qual sugestão deseja alterar: ");
            sgEscolhida = in.nextLine();

            if (sgEscolhida.equals("0")) {

            } else {
                while (sgEscolhida.equals("") || (Integer.parseInt(sgEscolhida) < 0 || Integer.parseInt(sgEscolhida) > listaIdSugestoes.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual sugestão deseja alterar: ");
                    sgEscolhida = in.nextLine();
                }

                if (sgEscolhida.equals("0")) {
                    return;
                }

                escolhida = crudSugestao.read(listaIdSugestoes[Integer.parseInt(sgEscolhida) - 1]);

                System.out.println("\n\nOs dados da sugestão que deseja alterar são: ");
                System.out.println(escolhida);

                System.out.print("\nDigite o novo nome do produto: ");
                produto = in.nextLine();

                if (!produto.equals("")) {
                    escolhida.setProduto(produto);
                    alterado++;
                }

                System.out.print("\nDigite o novo nome da loja: ");
                loja = in.nextLine();

                if (!loja.equals("")) {
                    escolhida.setLoja(loja);
                    alterado++;
                }

                System.out.print("\nDigite o novo valor: R$ ");
                valor = in.nextLine();

                if (!valor.equals("")) {
                    escolhida.setValor(Float.parseFloat(valor));
                    alterado++;
                }

                System.out.print("\nDigite as novas observações: ");
                observacoes = in.nextLine();

                if (!observacoes.equals("")) {
                    escolhida.setObservacoes(observacoes);
                    alterado++;
                }

                if (alterado != 0) {
                    System.out.println("\nVocê confirma a alteração da sugestão com estes dados? (Digite sim ou nao)\n");
                    System.out.println(escolhida);

                    System.out.print("\nConfirma: ");
                    confirma = in.nextLine();

                    while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                        System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        confirma = in.nextLine();
                    }

                    if (confirma.equals("sim")) {
                        crudSugestao.update(escolhida);
                        System.out.println("\nSugestão atualizada com sucesso.");
                    }

                    if (confirma.equals("nao")) {
                        System.out.println("\nAtualização de sugestão abortada.");
                    }
                } else {
                    System.out.println("Nenhum dado foi alterado.");
                }
            }
        } else {
            System.out.println("Você não possui sugestões cadastradas.");
        }
        pressToContinue();
    }
    
    private void procExclusaoSugestao() throws Exception {
        int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());
        String sgEscolhida;
        Sugestao escolhida;
        String confirma;

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]));
                System.out.println();
            }

            System.out.print("Qual sugestão deseja excluir: ");
            sgEscolhida = in.nextLine();
            if (sgEscolhida.equals("0")) {

            } else {
                while (sgEscolhida.equals("") || (Integer.parseInt(sgEscolhida) < 0 || Integer.parseInt(sgEscolhida) > listaIdSugestoes.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual sugestão deseja excluir: ");
                    sgEscolhida = in.nextLine();
                }

                if (sgEscolhida.equals("0")) {
                    return;
                }

                escolhida = crudSugestao.read(listaIdSugestoes[Integer.parseInt(sgEscolhida) - 1]);

                System.out.println("\n\nOs dados da sugestão que deseja excluir são: ");
                System.out.println(escolhida);

                System.out.println("\nVocê confirma a exclusão da sugestão acima? (Digite sim ou nao)\n");

                System.out.print("\nConfirma: ");
                confirma = in.nextLine();

                while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                    System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirma = in.nextLine();
                }

                if (confirma.equals("sim")) {
                    crudSugestao.delete(escolhida.getId());
                    indiceRelacionamentoSugestaoUsuario.delete(usuario.getId(), escolhida.getId());
                    System.out.println("\nSugestão excluída com sucesso.");
                }

                if (confirma.equals("nao")) {
                    System.out.println("\nExclusão de sugestão abortada.");
                }
            }
        } else {
            System.out.println("Você não possui sugestões cadastradas.");
        }

        pressToContinue();
    }

    private void procListagemGrupos() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] participacoes = indiceRelacionamentoUsuarioParticipacao.read(usuario.getId());

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos (criados por mim): \n");
            int contador = 1;
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    contador++;
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados que foram criados por você.\n\n");
        }

        if (participacoes.length != 0) {
            System.out.println("Meus grupos (dos quais eu participo): \n");
            int contador = 1;
            for (int i = 0; i < participacoes.length; i++) {
                Grupo grupo = crudGrupos.read(crudParticipacao.read(participacoes[i]).getIdGrupo());
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    contador++;
                }
            }
        } else {
            System.out.println("Você não possui grupos dos quais participa.");
        }

        pressToContinue();
    }

    private void procCadastroGrupo() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTimeZone timeZone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime dateTimeSorteio, dateTimeEncontro;
        Grupo grp;
        String nome, localEncontro, observacoes, dataSorteio, dataEncontro;
        String dia, mes, ano, hora, minuto;
        long momentoSorteio;
        float valor;
        long momentoEncontro;
        String confirma;
        String confirmaPart;
        int anoAtual = DateTime.now().year().get();
        int maiorMesPossivel = DateTime.now().monthOfYear().getMaximumValue();

        in.nextLine();
        System.out.print("\nDigite o nome do grupo: ");
        nome = in.nextLine();

        if (!nome.equals("")) {
            System.out.println("\nDigite a data do sorteio: ");
            System.out.print("Dia: ");
            dia = in.nextLine();
            while (!dia.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o dia: ");
                dia = in.nextLine();
            }

            dia = (dia.length() == 1) ? "0" + dia : dia;

            System.out.print("Mês: ");
            mes = in.nextLine();
            while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o mês: ");
                mes = in.nextLine();
            }

            mes = (mes.length() == 1) ? "0" + mes : mes;

            System.out.print("Ano: ");
            ano = in.nextLine();
            while (!ano.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(ano) < anoAtual) {
                System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o ano: ");
                ano = in.nextLine();
            }

            System.out.print("Hora: ");
            hora = in.nextLine();
            while (!hora.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                System.out.print("\nDigite a hora: ");
                hora = in.nextLine();
            }

            hora = (hora.length() == 1) ? "0" + hora : hora;

            System.out.print("Minuto: ");
            minuto = in.nextLine();
            while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o minuto: ");
                minuto = in.nextLine();
            }

            minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

            dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
            dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
            momentoSorteio = dateTimeSorteio.getMillis();

            while (dateTimeSorteio.isBeforeNow()) {
                System.out.println("Data do sorteio inferior a data atual. Insira a data do sorteio novamente.\n");
                System.out.println("\nDigite a data do sorteio: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(ano) < anoAtual) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
                momentoSorteio = dateTimeSorteio.getMillis();
            }

            System.out.print("\nDigite o valor: R$ ");
            valor = in.nextFloat();

            while (valor <= 0) {
                System.out.println("ERRO: O valor inserido é inválido. Insira novamente.");
                System.out.print("\nDigite o valor: R$ ");
                valor = in.nextFloat();
            }

            in.nextLine();
            System.out.println("\nDigite a data do encontro: ");
            System.out.print("Dia: ");
            dia = in.nextLine();
            while (!dia.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o dia: ");
                dia = in.nextLine();
            }

            dia = (dia.length() == 1) ? "0" + dia : dia;

            System.out.print("Mês: ");
            mes = in.nextLine();
            while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o mês: ");
                mes = in.nextLine();
            }

            mes = (mes.length() == 1) ? "0" + mes : mes;

            System.out.print("Ano: ");
            ano = in.nextLine();
            while (!ano.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(ano) < anoAtual) {
                System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o ano: ");
                ano = in.nextLine();
            }

            System.out.print("Hora: ");
            hora = in.nextLine();
            while (!hora.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                System.out.print("\nDigite a hora: ");
                hora = in.nextLine();
            }

            hora = (hora.length() == 1) ? "0" + hora : hora;

            System.out.print("Minuto: ");
            minuto = in.nextLine();
            while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o minuto: ");
                minuto = in.nextLine();
            }

            minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

            dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
            dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
            momentoEncontro = dateTimeEncontro.getMillis();

            while (momentoEncontro < momentoSorteio) {
                System.out.println("Data do encontro inferior a data do sorteio. Insira a data do encontro novamente.\n");
                System.out.println("\nDigite a data do encontro: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
                momentoEncontro = dateTimeEncontro.getMillis();
            }

            System.out.print("\nDigite o local do encontro: ");
            localEncontro = in.nextLine();

            while (localEncontro.equals("")) {
                System.out.println("ERRO: Texto inválido. Insira novamente.");
                System.out.print("\nDigite o local do encontro: ");
                localEncontro = in.nextLine();
            }

            System.out.print("\nDigite as observações: ");
            observacoes = in.nextLine();

            while (observacoes.equals("")) {
                System.out.println("ERRO: Texto inválido. Insira novamente.");
                System.out.print("\nDigite as observações: ");
                observacoes = in.nextLine();
            }

            grp = new Grupo(nome, momentoSorteio, valor, momentoEncontro, localEncontro, observacoes);

            System.out.println("\nVocê confirma a criação de um grupo com estes dados? (Digite sim ou nao)\n");
            System.out.println(grp);

            System.out.print("\nConfirma: ");
            confirma = in.nextLine();

            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                System.out.print("Confirma: ");
                confirma = in.nextLine();
            }

            if (confirma.equals("sim")) {
                grp.setIdUsuario(usuario.getId());
                int id = crudGrupos.create(grp);
                indiceRelacionamentoGrupoUsuario.create(usuario.getId(), id);

                System.out.println("\nVocê deseja participar do grupo? (Digite sim ou nao)\n");

                System.out.print("\nConfirma: ");
                confirmaPart = in.nextLine();

                while ((!confirmaPart.equals("sim")) && (!confirmaPart.equals("nao"))) {
                    System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirmaPart = in.nextLine();
                }

                if (confirmaPart.equals("sim")) {
                    Participacao participacao = new Participacao();
                    participacao.setIdGrupo(grp.getId());
                    participacao.setIdUsuario(usuario.getId());
                    int idParticipacao = crudParticipacao.create(participacao);
                    indiceRelacionamentoGrupoParticipacao.create(id, idParticipacao);
                    indiceRelacionamentoUsuarioParticipacao.create(usuario.getId(), idParticipacao);
                }
            }

            if (confirma.equals("nao")) {
                System.out.println("\nCadastro de grupo abortado.");
            }

            pressToContinue();
        }
    }

    private void procAlteracaoGrupo() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTimeZone timeZone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime dateTimeSorteio, dateTimeEncontro;
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        String grpEscolhido;
        Grupo escolhido;
        String nome, localEncontro, observacoes, dataSorteio, dataEncontro;
        String dia, mes, ano, hora, minuto;
        long momentoSorteio;
        String valor;
        long momentoEncontro;
        String confirma;
        int alterado = 0;
        int anoAtual = DateTime.now().year().get();
        int maiorMesPossivel = DateTime.now().monthOfYear().getMaximumValue();
        int quantidadeDeGruposValidos = 0;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposValidos++;
                }
            }

            System.out.print("Qual grupo deseja alterar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposValidos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja alterar: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("\n\nOs dados do grupo que deseja alterar são: ");
                System.out.println(escolhido);

                System.out.print("\nDigite o novo nome do grupo: ");
                nome = in.nextLine();

                if (!nome.equals("")) {
                    escolhido.setNome(nome);
                    alterado++;
                }

                System.out.println("\nDigite o novo momento do sorteio: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
                momentoSorteio = dateTimeSorteio.getMillis();

                while (momentoSorteio < DateTime.now().getMillis()) {
                    System.out.println("Data do sorteio inferior a data atual. Insira a data do sorteio novamente.\n");

                    System.out.println("\nDigite o novo momento do sorteio: ");
                    System.out.print("Dia: ");
                    dia = in.nextLine();
                    while (!dia.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o dia: ");
                        dia = in.nextLine();
                    }

                    dia = (dia.length() == 1) ? "0" + dia : dia;

                    System.out.print("Mês: ");
                    mes = in.nextLine();
                    while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                        System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o mês: ");
                        mes = in.nextLine();
                    }

                    mes = (mes.length() == 1) ? "0" + mes : mes;

                    System.out.print("Ano: ");
                    ano = in.nextLine();
                    while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                        System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o ano: ");
                        ano = in.nextLine();
                    }

                    System.out.print("Hora: ");
                    hora = in.nextLine();
                    while (!hora.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                        System.out.print("\nDigite a hora: ");
                        hora = in.nextLine();
                    }

                    hora = (hora.length() == 1) ? "0" + hora : hora;

                    System.out.print("Minuto: ");
                    minuto = in.nextLine();
                    while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o minuto: ");
                        minuto = in.nextLine();
                    }

                    minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                    dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                    dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
                    momentoSorteio = dateTimeSorteio.getMillis();
                }

                if (!(momentoSorteio == escolhido.getMomentoSorteio())) {
                    escolhido.setMomentoSorteio(momentoSorteio);
                    alterado++;
                }

                System.out.print("\nDigite o novo valor: R$ ");
                valor = in.nextLine();

                if (!valor.equals("")) {
                    escolhido.setValor(Float.parseFloat(valor));
                    alterado++;
                }

                System.out.println("\nDigite o novo momento do encontro: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
                momentoEncontro = dateTimeEncontro.getMillis();

                while (momentoEncontro < momentoSorteio) {
                    System.out.println("Data do encontro inferior a data do sorteio. Insira a data do encontro novamente.\n");
                    System.out.println("\nDigite o novo momento do encontro: ");
                    System.out.print("Dia: ");
                    dia = in.nextLine();
                    while (!dia.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o dia: ");
                        dia = in.nextLine();
                    }

                    dia = (dia.length() == 1) ? "0" + dia : dia;

                    System.out.print("Mês: ");
                    mes = in.nextLine();
                    while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                        System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o mês: ");
                        mes = in.nextLine();
                    }

                    mes = (mes.length() == 1) ? "0" + mes : mes;

                    System.out.print("Ano: ");
                    ano = in.nextLine();
                    while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                        System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o ano: ");
                        ano = in.nextLine();
                    }

                    System.out.print("Hora: ");
                    hora = in.nextLine();
                    while (!hora.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                        System.out.print("\nDigite a hora: ");
                        hora = in.nextLine();
                    }

                    hora = (hora.length() == 1) ? "0" + hora : hora;

                    System.out.print("Minuto: ");
                    minuto = in.nextLine();
                    while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o minuto: ");
                        minuto = in.nextLine();
                    }

                    minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                    dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                    dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
                    momentoEncontro = dateTimeEncontro.getMillis();
                }

                if (!(momentoEncontro == escolhido.getMomentoEncontro())) {
                    escolhido.setMomentoEncontro(momentoEncontro);
                    alterado++;
                }

                System.out.print("\nDigite o novo local do encontro: ");
                localEncontro = in.nextLine();

                if (!localEncontro.equals("")) {
                    escolhido.setLocalEncontro(localEncontro);
                    alterado++;
                }

                System.out.print("\nDigite as novas observações: ");
                observacoes = in.nextLine();

                if (!observacoes.equals("")) {
                    escolhido.setObservacoes(observacoes);
                    alterado++;
                }

                if (alterado != 0) {
                    System.out.println("\nVocê confirma a alteração de um grupo com estes dados? (Digite sim ou nao)\n");
                    System.out.println(escolhido);

                    System.out.print("\nConfirma: ");
                    confirma = in.nextLine();

                    while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                        System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        confirma = in.nextLine();
                    }

                    if (confirma.equals("sim")) {
                        crudGrupos.update(escolhido);
                        System.out.println("\nGrupo atualizado com sucesso.");
                    }

                    if (confirma.equals("nao")) {
                        System.out.println("\nAtualização de grupo abortada.");
                    }
                } else {
                    System.out.println("Nenhum dado foi alterado.");
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }
        pressToContinue();
    }

    private void procDesativarGrupo() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        String grpEscolhido;
        Grupo escolhido;
        String confirma;
        int quantidadeDeGruposAtivos = 0;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja desativar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja alterar: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("\n\nOs dados do grupo que deseja alterar são: ");
                System.out.println(escolhido);

                System.out.println("\nVocê confirma a desativação de um grupo com estes dados? (Digite sim ou nao)\n");
                System.out.println(escolhido);

                System.out.print("\nConfirma: ");
                confirma = in.nextLine();

                while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                    System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirma = in.nextLine();
                }

                if (confirma.equals("sim")) {
                    
                    escolhido.setAtivo(false);
                    crudGrupos.update(escolhido);
                    System.out.println("\nGrupo desativado com sucesso.");
                }

                if (confirma.equals("nao")) {
                    System.out.println("\nDesativação de grupo abortado.");
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procListagemConvites() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdConvites;
        String grpEscolhido;
        Grupo escolhido;
        int quantidadeDeGruposAtivos = 0;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja visualizar os convites: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("\n\nCONVITES DO GRUPO " + '"' + escolhido.getNome() + '"' + "\n");

                listaIdConvites = indiceRelacionamentoGrupoConvite.read(escolhido.getId());

                if (listaIdConvites.length == 0) {
                    System.out.println("Não há convites emitidos neste grupo.");
                } else {
                    for (int i = 0; i < listaIdConvites.length; i++) {
                        Convite convite = crudConvites.read(listaIdConvites[i]);
                        System.out.print((i + 1) + ". ");
                        System.out.println(convite);
                    }
                }
            }
        } else {
            System.out.println("\n\nVocê não possui grupos cadastrados.");
        }
        System.out.println();
        pressToContinue();
    }

    private void procEmissaoConvites() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());

        String grpEscolhido, email, confirma;
        Grupo escolhido;
        int quantidadeDeGruposAtivos = 0;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo() && !grupo.isSorteado()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja visualizar os convites: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("GRUPO " + '"' + escolhido.getNome() + '"' + "\n");

                System.out.print("Digite um email para emitir o convite: ");
                email = in.nextLine();

                while (!email.isEmpty()) {
                    Convite cv;
                    if (!((cv = crudConvites.read(escolhido.getId() + "|" + email)) == null)) {
                        if (cv.getEstado() == 0 || cv.getEstado() == 1) {
                            clearScreen();
                            System.out.println(cv);

                            System.out.println("\n\nConvite já emitido para este usuário.\n");
                        } else if (cv.getEstado() == 2 || cv.getEstado() == 3) {
                            clearScreen();
                            System.out.println(cv);
                            System.out.println("\n\nConvite já recusado ou cancelado. Deseja reenviar? (Digite sim ou nao)\n");

                            System.out.print("\nConfirma: ");
                            confirma = in.nextLine();

                            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                                System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                                System.out.print("Confirma: ");
                                confirma = in.nextLine();
                            }

                            if (confirma.equals("sim")) {
                                cv.setEstado((byte) 0);
                                crudConvites.update(cv);
                                System.out.println("\nConvite reenviado com sucesso.");
                            }

                            if (confirma.equals("nao")) {
                                System.out.println("\nReenvio de convite abortado.");
                            }
                        }
                    } else {
                        cv = new Convite(email, DateTime.now().getMillis(), (byte) 0);
                        cv.setIdGrupo(escolhido.getId());
                        int id = crudConvites.create(cv);
                        listaInvertidaEmailConvite.create(email, id);
                        indiceRelacionamentoGrupoConvite.create(escolhido.getId(), id);
                        System.out.println("\n\nConvite enviado para o usuário de email: " + email);
                    }

                    System.out.print("\n\nDigite um email para emitir o convite: ");
                    email = in.nextLine();
                }
            }
        } else {
            System.out.println("\n\nVocê não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procCancelamentoConvites() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdConvites;
        String grpEscolhido, email, confirma, cvEscolhido;
        Grupo escolhido;
        Convite conviteEscolhido;
        int quantidadeDeGruposAtivos = 0;
        int quantidadeDeConvitesPendetes = 0;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            System.out.println("Meus grupos: \n");
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo() && !grupo.isSorteado()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja visualizar os convites: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("GRUPO " + '"' + escolhido.getNome() + '"' + "\n");

                listaIdConvites = indiceRelacionamentoGrupoConvite.read(escolhido.getId());
                contador = 1;
                vetorId = new int[listaIdConvites.length + 1];
                for (int i = 0; i < listaIdConvites.length; i++) {
                    Convite convite = crudConvites.read(listaIdConvites[i]);

                    if (convite.getEstado() == 0) {
                        System.out.print((contador) + ". ");
                        System.out.println(convite);
                        vetorId[contador] = convite.getId();
                        contador++;
                        quantidadeDeConvitesPendetes++;
                    }
                }

                System.out.print("Qual convite deseja cancelar: ");
                cvEscolhido = in.nextLine();

                if (cvEscolhido.equals("0")) {

                } else {
                    while (cvEscolhido.equals("") || (Integer.parseInt(cvEscolhido) < 0 || Integer.parseInt(cvEscolhido) > quantidadeDeConvitesPendetes)) {
                        System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                        System.out.print("\nQual convite deseja cancelar: ");
                        cvEscolhido = in.nextLine();
                    }

                    if (cvEscolhido.equals("0")) {
                        return;
                    }

                    conviteEscolhido = crudConvites.read(vetorId[Integer.parseInt(cvEscolhido)]);

                    System.out.println("\nVocê confirma o cancelamento do convite com estes dados? (Digite sim ou nao)\n");
                    System.out.println(conviteEscolhido);

                    System.out.print("\nConfirma: ");
                    confirma = in.nextLine();

                    while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                        System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        confirma = in.nextLine();
                    }

                    if (confirma.equals("sim")) {
                        conviteEscolhido.setEstado((byte) 3);
                        crudConvites.update(conviteEscolhido);
                        listaInvertidaEmailConvite.delete(conviteEscolhido.getEmail(), conviteEscolhido.getId());
                        System.out.println("\nConvite cancelado com sucesso.");
                    }

                    if (confirma.equals("nao")) {
                        System.out.println("\nCancelamento de convite abortado.");
                    }
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procConvitesPendentes() throws Exception {
        int[] listaIdConvites;
        Convite conviteEscolhido;
        String cvEscolhido, confirma;
        int quantidadeDeConvitesPendentes = 0;

        listaIdConvites = listaInvertidaEmailConvite.read(usuario.chaveSecundaria());

        in.nextLine();
        clearScreen();

        if (listaIdConvites.length != 0) {
            int contador = 1;
            int[] vetorId = new int[listaIdConvites.length + 1];
            System.out.println("Meus convites: \n");
            for (int i = 0; i < listaIdConvites.length; i++) {
                Convite cv = crudConvites.read(listaIdConvites[i]);
                Grupo grupo = crudGrupos.read(cv.getIdGrupo());
                if (cv.getEstado() == 0) {
                    System.out.print((contador) + ". ");
                    DateTime dateTime = new DateTime(cv.getMomentoConvite());

                    String mensagem = "Convidado em " + dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/"
                            + dateTime.getYear() + ", às " + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay()) + ":"
                            + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour()) + " por " + crudUsuario.read(grupo.getIdUsuario()).getNome();
                    System.out.println(grupo.getNome() + "\n" + mensagem);
                    System.out.println();
                    vetorId[contador] = cv.getId();
                    contador++;
                    quantidadeDeConvitesPendentes++;
                } else {
                    listaInvertidaEmailConvite.delete(cv.chaveSecundaria(), cv.getId());
                }
            }

            System.out.print("Qual convite deseja aceitar ou recusar: ");
            cvEscolhido = in.nextLine();

            while (!cvEscolhido.equals("0")) {

                while (cvEscolhido.equals("") || (Integer.parseInt(cvEscolhido) < 0 || Integer.parseInt(cvEscolhido) > quantidadeDeConvitesPendentes)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual convite deseja cancelar: ");
                    cvEscolhido = in.nextLine();
                }

                if (cvEscolhido.equals("0")) {
                    break;
                }

                conviteEscolhido = crudConvites.read(vetorId[Integer.parseInt(cvEscolhido)]);

                System.out.println("\nVocê aceita ou recusa o convite selecionado? (Digite A(para aceitar), R(para recusar) ou V(para voltar))\n");
                DateTime dateTime = new DateTime(conviteEscolhido.getMomentoConvite());
                Grupo grupo = crudGrupos.read(conviteEscolhido.getIdGrupo());

                String mensagem = "Convidado em " + dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/"
                        + dateTime.getYear() + ", às " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour()
                        + " por " + crudUsuario.read(grupo.getIdUsuario()).getNome();

                System.out.println(grupo.getNome() + "\n" + mensagem);

                System.out.print("\nConfirma: ");
                confirma = in.nextLine();

                while ((!confirma.equals("A")) && (!confirma.equals("R")) && (!confirma.equals("V"))) {
                    System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirma = in.nextLine();
                }

                if (confirma.equals("A")) {
                    conviteEscolhido.setEstado((byte) 1);
                    crudConvites.update(conviteEscolhido);
                    listaInvertidaEmailConvite.delete(conviteEscolhido.getEmail(), conviteEscolhido.getId());
                    Participacao participacao = new Participacao();
                    participacao.setIdGrupo(conviteEscolhido.getIdGrupo());
                    participacao.setIdUsuario(usuario.getId());
                    int idParticipacao = crudParticipacao.create(participacao);
                    indiceRelacionamentoGrupoParticipacao.create(conviteEscolhido.getIdGrupo(), idParticipacao);
                    indiceRelacionamentoUsuarioParticipacao.create(usuario.getId(), idParticipacao);
                    System.out.println("\nConvite aceito com sucesso.");
                }

                if (confirma.equals("R")) {
                    conviteEscolhido.setEstado((byte) 2);
                    crudConvites.update(conviteEscolhido);
                    listaInvertidaEmailConvite.delete(conviteEscolhido.getEmail(), conviteEscolhido.getId());
                    System.out.println("\nConvite recusado com sucesso.");
                }

                contador = 1;
                quantidadeDeConvitesPendentes = 0;
                vetorId = new int[listaIdConvites.length + 1];
                System.out.println("\n\nMeus convites: \n");
                for (int i = 0; i < listaIdConvites.length; i++) {
                    Convite cv = crudConvites.read(listaIdConvites[i]);
                    grupo = crudGrupos.read(cv.getIdGrupo());
                    if (cv.getEstado() == 0) {
                        System.out.print((contador) + ". ");
                        dateTime = new DateTime(cv.getMomentoConvite());

                        mensagem = "Convidado em " + dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/"
                                + dateTime.getYear() + ", às " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour()
                                + " por " + crudUsuario.read(grupo.getIdUsuario()).getNome();
                        System.out.println(grupo.getNome() + "\n" + mensagem);
                        System.out.println();
                        vetorId[contador] = cv.getId();
                        contador++;
                        quantidadeDeConvitesPendentes++;
                    } else {
                        listaInvertidaEmailConvite.delete(cv.chaveSecundaria(), cv.getId());
                    }
                }

                listaIdConvites = listaInvertidaEmailConvite.read(usuario.chaveSecundaria());
                if (listaIdConvites.length == 0) {
                    System.out.println("\n\nVocê não possui mais convites pendentes.\n\n");
                    break;
                }
                System.out.print("Qual convite deseja aceitar ou recusar: ");
                cvEscolhido = in.nextLine();
            }
        } else {
            System.out.println("\n\nVocê não possui convites pendentes.\n\n");
        }
        pressToContinue();
    }

    private void procListagemParticipantes() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdParticipacoes;
        String grpEscolhido;
        Grupo escolhido;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        int quantidadeDeGruposAtivos = 0;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos (dos quais eu sou dono): \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja visualizar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);
                listaIdParticipacoes = indiceRelacionamentoGrupoParticipacao.read(escolhido.getId());

                System.out.println("\n\nParticipantes do grupo " + '"' + escolhido.getNome() + '"' + ":\n");

                contador = 1;
                for (int i = 0; i < listaIdParticipacoes.length; i++) {
                    Participacao participacao = crudParticipacao.read(listaIdParticipacoes[i]);
                    System.out.print((contador) + ". ");
                    System.out.println(crudUsuario.read(participacao.getIdUsuario()).getNome());
                    System.out.println();
                    contador++;
                }
            }
        } else {
            System.out.println("Você não possui grupos dos quais você é dono.");
        }

        pressToContinue();
    }

    private void procRemocaoParticipantes() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdParticipacoes;
        String grpEscolhido;
        Grupo escolhido;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        HashMap<Integer, Integer> presenteadosPor = new HashMap<>();
        int quantidadeDeGruposAtivos = 0;
        String partEscolhida;
        Participacao escolhida;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja gerenciar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);
                listaIdParticipacoes = indiceRelacionamentoGrupoParticipacao.read(escolhido.getId());

                System.out.println("\n\nParticipantes do grupo " + '"' + escolhido.getNome() + '"' + ":\n");

                contador = 1;
                vetorId = new int[listaIdParticipacoes.length + 1];

                for (int i = 0; i < listaIdParticipacoes.length; i++) {
                    Participacao participacao = crudParticipacao.read(listaIdParticipacoes[i]);
                    System.out.print((contador) + ". ");
                    System.out.println(crudUsuario.read(participacao.getIdUsuario()).getNome());
                    System.out.println();
                    vetorId[contador] = participacao.getId();

                    if (escolhido.isSorteado()) {
                        presenteadosPor.put(participacao.getIdAmigo(), participacao.getId());
                    }
                    contador++;
                }

                System.out.print("Quem você deseja remover: ");
                partEscolhida = in.nextLine();

                if (partEscolhida.equals("0")) {

                } else {
                    while (partEscolhida.equals("") || (Integer.parseInt(partEscolhida) < 0 || Integer.parseInt(partEscolhida) > listaIdParticipacoes.length)) {
                        System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                        System.out.print("\nQuem você deseja remover: ");
                        partEscolhida = in.nextLine();
                    }

                    if (partEscolhida.equals("0")) {
                        return;
                    }

                    escolhida = crudParticipacao.read(vetorId[Integer.parseInt(partEscolhida)]);

                    if (escolhido.isSorteado()) {
                        int idUsuarioASerPresenteado = escolhida.getIdAmigo();
                        int idPartPresenteadorDoRemovido = presenteadosPor.get(escolhida.getIdUsuario());
                        Participacao aSerAtualizada = crudParticipacao.read(idPartPresenteadorDoRemovido);
                        aSerAtualizada.setIdAmigo(idUsuarioASerPresenteado);
                        crudParticipacao.update(aSerAtualizada);
                        System.out.println("\n\nFoi realizado um ajuste no amigo oculto para que ninguem ficasse sem presente.");
                    }

                    crudParticipacao.delete(escolhida.getId());
                    indiceRelacionamentoGrupoParticipacao.delete(escolhido.getId(), escolhida.getId());
                    indiceRelacionamentoUsuarioParticipacao.delete(escolhida.getIdUsuario(), escolhida.getId());

                    System.out.println("\n\nParticipante removido com sucesso!\n\n");
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }
        pressToContinue();
    }

    private Grupo escolherGrupo(boolean clearBuffer) throws Exception {
        int[] participacoes = indiceRelacionamentoUsuarioParticipacao.read(usuario.getId());
        String grpEscolhido;
        Grupo escolhido = null;
        int quantidadeDeGruposAtivos = 0;

        if (clearBuffer) {
            in.nextLine();
            clearScreen();
        }

        if (participacoes.length != 0) {
            System.out.println("\n\nMeus grupos (dos quais eu participo): \n");
            int contador = 1;
            int[] vetorId = new int[participacoes.length + 1];
            for (int i = 0; i < participacoes.length; i++) {
                Grupo grupo = crudGrupos.read(crudParticipacao.read(participacoes[i]).getIdGrupo());
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposAtivos++;
                }
            }

            System.out.print("Qual grupo deseja escolher: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposAtivos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja escolher: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return null;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);
            }
        } else {
            System.out.println("Você não possui grupos dos quais participa.");
        }

        return escolhido;
    }

    private void procVisualizarParticipantes() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        Grupo escolhido = escolherGrupo(true);
        int contador = 1;
        int[] listaIdParticipacoes;

        if (escolhido != null) {
            System.out.println("\n\n" + escolhido);

            listaIdParticipacoes = indiceRelacionamentoGrupoParticipacao.read(escolhido.getId());

            System.out.println("\n\nParticipantes do grupo " + '"' + escolhido.getNome() + '"' + ":\n");

            for (int i = 0; i < listaIdParticipacoes.length; i++) {
                Participacao participacao = crudParticipacao.read(listaIdParticipacoes[i]);
                System.out.print((contador) + ". ");
                System.out.println(crudUsuario.read(participacao.getIdUsuario()).getNome());
                System.out.println();
                contador++;
            }
        }

        pressToContinue();
    }

    private void procVisualizarAmigoSorteado() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        Grupo escolhido = escolherGrupo(true);

        if (escolhido != null) {
            System.out.println("\n\n" + escolhido);

            if (escolhido.isSorteado()) {
                String chaveSecundaria = usuario.getId() + "|" + escolhido.getId();
                Participacao participacao = crudParticipacao.read(chaveSecundaria);
                Usuario amigoSorteado = crudUsuario.read(participacao.getIdAmigo());
                Sugestao sugestoes = crudSugestao.read(amigoSorteado.getId());

                System.out.println("\n\nO amigo que você deve presentear é: \n");
                System.out.println("Nome: " + amigoSorteado.getNome());
                int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());

                if (listaIdSugestoes.length != 0) {
                    System.out.println("Sugestões: \n");
                    for (int i = 0; i < listaIdSugestoes.length; i++) {
                        System.out.print((i + 1) + ". ");
                        System.out.println(crudSugestao.read(listaIdSugestoes[i]));
                        System.out.println();
                    }
                } else {
                    System.out.println("O seu amigo não possui sugestões cadastradas.");
                }
            } else {
                System.out.println("\n\nSorteio ainda não realizado neste grupo.\n\n");
            }
        }

        pressToContinue();
    }

    private void procSorteio() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdParticipacoes;
        String grpEscolhido;
        Grupo escolhido;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        int quantidadeDeGruposValidos = 0;
        long dataAtual = DateTime.now().getMillis();
        Participacao us1;
        Participacao us2;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos (dos quais eu sou dono): \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo() && !grupo.isSorteado() && (grupo.getMomentoSorteio() < dataAtual)) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);

                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                    quantidadeDeGruposValidos++;
                }
            }

            System.out.print("Qual grupo deseja realizar o sorteio: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposValidos)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja realizar o sorteio: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("\n\nO grupo escolhido para realizar o sorteio é:\n");
                System.out.println(escolhido);

                System.out.println("Sorteio: " + (escolhido.isSorteado() ? "Já sorteado em " + formatter.print(escolhido.getMomentoSorteio())
                        : "Terá sorteio em " + formatter.print(escolhido.getMomentoSorteio())));

                listaIdParticipacoes = indiceRelacionamentoGrupoParticipacao.read(escolhido.getId());

                shuffleArray(listaIdParticipacoes);

                System.out.println("\n\nParticipantes do grupo " + '"' + escolhido.getNome() + '"' + ":\n");

                for (int i = 0; i < listaIdParticipacoes.length; i++) {
                    if (i == listaIdParticipacoes.length - 1) {
                        us1 = crudParticipacao.read(listaIdParticipacoes[i]);
                        us2 = crudParticipacao.read(listaIdParticipacoes[0]);

                        us1.setIdAmigo(us2.getIdUsuario());
                    } else {
                        us1 = crudParticipacao.read(listaIdParticipacoes[i]);
                        us2 = crudParticipacao.read(listaIdParticipacoes[i + 1]);

                        us1.setIdAmigo(us2.getIdUsuario());
                    }

                    crudParticipacao.update(us1);
                }

                escolhido.setSorteado(true);
                crudGrupos.update(escolhido);

                System.out.println("\n\nSorteio realizado com sucesso!");
            }
        } else {
            System.out.println("Você não possui grupos dos quais você é dono.");
        }

        pressToContinue();
    }

    private void procEnvioMensagem() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        String grpEscolhido;
        Grupo escolhido;
        int quantidadeDeGruposValidos = 0;
        String mensagem, titulo;
        String donoOuPart;

        in.nextLine();
        clearScreen();

        System.out.println("Digite 1 para visualizar os grupos que você criou e 2 para visualizar os grupos que você participa:");
        donoOuPart = in.nextLine();

        while ((!donoOuPart.equals("1")) && (!donoOuPart.equals("2"))) {
            System.out.println("\nERRO: Op��o Inv�lida. Digite 1 ou 2.");
            System.out.print("Confirma: ");
            donoOuPart = in.nextLine();
        }

        if (donoOuPart.equals("1")) {
            if (listaIdGrupos.length != 0) {
                System.out.println("Meus grupos (dos quais eu criei): \n");
                int contador = 1;
                int[] vetorId = new int[listaIdGrupos.length + 1];
                for (int i = 0; i < listaIdGrupos.length; i++) {
                    Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                    if (grupo.isAtivo()) {
                        System.out.print((contador) + ". ");
                        System.out.println(grupo);

                        System.out.println();
                        vetorId[contador] = grupo.getId();
                        contador++;
                        quantidadeDeGruposValidos++;
                    }
                }

                System.out.print("Qual grupo deseja enviar a mensagem: ");
                grpEscolhido = in.nextLine();

                if (grpEscolhido.equals("0")) {

                } else {
                    while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposValidos)) {
                        System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                        System.out.print("\nQual grupo deseja visualizar os convites: ");
                        grpEscolhido = in.nextLine();
                    }

                    if (grpEscolhido.equals("0")) {
                        return;
                    }

                    escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                    System.out.println("\n\nO grupo escolhido para enviar a mensagem é:\n");
                    System.out.println(escolhido);

                    System.out.println("\n\nEscreva o título da mensagem:");
                    titulo = in.nextLine();

                    while (titulo.isEmpty()) {
                        System.out.println("\nERRO: Você escreveu um título inválido. Insira novamente.");
                        titulo = in.nextLine();
                    }

                    System.out.println("\n\nEscreva a sua mensagem:");
                    mensagem = in.nextLine();

                    while (mensagem.isEmpty()) {
                        System.out.println("\nERRO: Você escreveu uma mensagem inválida. Insira novamente.");
                        mensagem = in.nextLine();
                    }

                    Mensagem mensagemEnviada = new Mensagem(DateTime.now().getMillis(), mensagem, titulo);

                    mensagemEnviada.setIdGrupo(escolhido.getId());
                    mensagemEnviada.setIdUsuario(usuario.getId());

                    int idMensagemEnviada = crudMensagens.create(mensagemEnviada);

                    indiceRelacionamentoGrupoMensagem.create(escolhido.getId(), idMensagemEnviada);
                    indiceRelacionamentoUsuarioMensagem.create(usuario.getId(), idMensagemEnviada);

                    System.out.println("\n\nMensagem enviada com sucesso!");
                }
            } else {
                System.out.println("Você não possui grupos dos quais você é dono.");
            }
        }

        if (donoOuPart.equals("2")) {
            Grupo grupo = escolherGrupo(false);

            if (grupo != null) {
                System.out.println("\n\nO grupo escolhido para enviar a mensagem é:\n");
                System.out.println(grupo);

                System.out.println("\n\nEscreva o título da mensagem:");
                titulo = in.nextLine();

                while (titulo.isEmpty()) {
                    System.out.println("\nERRO: Você escreveu um título inválido. Insira novamente.");
                    titulo = in.nextLine();
                }

                System.out.println("\n\nEscreva a sua mensagem:");
                mensagem = in.nextLine();

                while (mensagem.isEmpty()) {
                    System.out.println("\nERRO: Você escreveu uma mensagem inválida. Insira novamente.");
                    mensagem = in.nextLine();
                }

                Mensagem mensagemEnviada = new Mensagem(DateTime.now().getMillis(), mensagem, titulo);

                mensagemEnviada.setIdGrupo(grupo.getId());
                mensagemEnviada.setIdUsuario(usuario.getId());

                int idMensagemEnviada = crudMensagens.create(mensagemEnviada);

                indiceRelacionamentoGrupoMensagem.create(grupo.getId(), idMensagemEnviada);
                indiceRelacionamentoUsuarioMensagem.create(usuario.getId(), idMensagemEnviada);

                System.out.println("\n\nMensagem enviada com sucesso!");
            }
        }

        pressToContinue();
    }

    private void procLeituraMensagens() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        String grpEscolhido;
        String msgEscolhida;
        Grupo escolhido;
        int quantidadeDeGruposValidos = 0; //variável de controle para seleção do grupo desejado
        String donoOuPart; //diz se o usuário quer ver os grupos que foram criados por ele ou só os que ele participa
        ArrayList<Mensagem> mensagens = new ArrayList<>();
        String mudarPagina; //variável de controle da mudança de p�gina
        int posicaoArray = 0; //variável auxiliar do processo de visualização das mensagens selecionadas
        
        //clear buffer
        in.nextLine();
        clearScreen();
        
        //aqui utilizamos a estratégia de dar ao usuário a opção de visualizar tanto as mensagens dos grupos que ele criou
        //quanto os grupos que ele participa, podendo haver coincidência em alguns casos
        System.out.println("Digite 1 para visualizar os grupos que você criou e 2 para visualizar os grupos que você participa:");
        donoOuPart = in.nextLine();

        while ((!donoOuPart.equals("1")) && (!donoOuPart.equals("2"))) {
            System.out.println("\nERRO: Op��o Inv�lida. Digite 1 ou 2.");
            System.out.print("Confirma: ");
            donoOuPart = in.nextLine();
        }

        if (donoOuPart.equals("1")) {
            if (listaIdGrupos.length != 0) {
                System.out.println("\n\nMeus grupos (dos quais eu criei): \n");
                int contador = 1;
                int[] vetorId = new int[listaIdGrupos.length + 1];
                for (int i = 0; i < listaIdGrupos.length; i++) {
                    Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                    if (grupo.isAtivo()) {
                        System.out.print((contador) + ". ");
                        System.out.println(grupo);

                        System.out.println();
                        vetorId[contador] = grupo.getId();
                        contador++;
                        quantidadeDeGruposValidos++;
                    }
                }

                System.out.print("\n\nQual grupo deseja visualizar as mensagens: ");
                grpEscolhido = in.nextLine();

                if (grpEscolhido.equals("0")) {

                } else {
                    while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > quantidadeDeGruposValidos)) {
                        System.out.println("ERRO: Voc� fez uma escolha inválida. Insira novamente.");
                        System.out.print("\nQual grupo deseja visualizar os convites: ");
                        grpEscolhido = in.nextLine();
                    }

                    if (grpEscolhido.equals("0")) {
                        return;
                    }

                    escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                    System.out.println("\n\nO grupo escolhido para visualizar as mensagens é:\n");
                    System.out.println(escolhido);

                    int[] listaIdMensagens = indiceRelacionamentoGrupoMensagem.read(escolhido.getId());

                    if (listaIdMensagens.length == 0) {
                        System.out.println("\n\nN�o h� mensagens neste grupo!\n\n");
                        pressToContinue();
                        return;
                    }

                    for (int i = 0; i < listaIdMensagens.length; i++) {
                        mensagens.add(crudMensagens.read(listaIdMensagens[i]));
                    }

                    Collections.sort(mensagens, comparing(Mensagem::getMomentoEnvio));
                    Collections.reverse(mensagens);

                    clearScreen();
                    System.out.println("Mensagens:\n");
                    if (mensagens.size() >= 5) {
                        for (int i = posicaoArray; i < 5; i++) {
                            System.out.print((i + 1) + ". ");
                            System.out.print(mensagens.get(i));
                            System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                            System.out.println();
                        }

                        System.out.print("\n\nQual mensagem deseja visualizar: ");
                        msgEscolhida = in.nextLine();

                        if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > 5)) {
                            System.out.println("\n\nMensagem:");
                            System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                        } else {
                            System.out.println("\n\nNenhuma op��o v�lida foi escolhida.");
                        }
                    } else {
                        for (int i = posicaoArray; i < mensagens.size(); i++) {
                            System.out.print((i + 1) + ". ");
                            System.out.print(mensagens.get(i));
                            System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                            System.out.println();
                        }

                        System.out.print("\n\nQual mensagem deseja visualizar: ");
                        msgEscolhida = in.nextLine();

                        if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                            System.out.println("\n\nMensagem:");
                            System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                        } else {
                            System.out.println("\n\nNenhuma op��o v�lida foi escolhida.");
                        }
                    }

                    if (mensagens.size() > 5 && !msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                        System.out.println("\n\nDeseja mudar de p�gina? (Digite sim ou nao)\n");
                        System.out.print("Confirma: ");
                        mudarPagina = in.nextLine();

                        while ((!mudarPagina.equals("sim")) && (!mudarPagina.equals("nao"))) {
                            System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                            System.out.print("Confirma: ");
                            mudarPagina = in.nextLine();
                        }

                        if (mudarPagina.equals("sim")) {
                            mensagens.remove(0);
                            mensagens.remove(0);
                            mensagens.remove(0);
                            mensagens.remove(0);
                            mensagens.remove(0);
                        }
                    }

                    while (!msgEscolhida.equals("0")) {
                        System.out.println("\n\nMensagens:\n");
                        if (mensagens.size() >= 5) {
                            for (int i = posicaoArray; i < 5; i++) {
                                System.out.print((i + 1) + ". ");
                                System.out.print(mensagens.get(i));
                                System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                                System.out.println();
                            }

                            System.out.print("\n\nQual mensagem deseja visualizar: ");
                            msgEscolhida = in.nextLine();

                            if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > 5)) {
                                System.out.println("\n\nMensagem:");
                                System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                            }
                        } else {
                            for (int i = posicaoArray; i < mensagens.size(); i++) {
                                System.out.print((i + 1) + ". ");
                                System.out.print(mensagens.get(i));
                                System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                                System.out.println();
                            }

                            System.out.print("\n\nQual mensagem deseja visualizar: ");
                            msgEscolhida = in.nextLine();

                            if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                                System.out.println("\n\nMensagem:");
                                System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                            } else {
                                System.out.println("\n\nNenhuma op��o v�lida foi escolhida.");
                            }
                        }

                        if (mensagens.size() > 5 && !msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                            System.out.println("\n\nDeseja mudar de p�gina? (Digite sim ou nao)\n");
                            System.out.print("Confirma: ");
                            mudarPagina = in.nextLine();

                            while ((!mudarPagina.equals("sim")) && (!mudarPagina.equals("nao"))) {
                                System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                                System.out.print("Confirma: ");
                                mudarPagina = in.nextLine();
                            }

                            if (mudarPagina.equals("sim")) {
                                mensagens.remove(0);
                                mensagens.remove(0);
                                mensagens.remove(0);
                                mensagens.remove(0);
                                mensagens.remove(0);
                            }
                        }
                    }
                }
            } else {
                System.out.println("Você não possui grupos dos quais você é dono.");
            }
        }

        if (donoOuPart.equals("2")) {
            Grupo grupo = escolherGrupo(false);

            if (grupo != null) {
                System.out.println("\n\nO grupo escolhido para visualizar as mensagens é:\n");
                System.out.println(grupo);

                int[] listaIdMensagens = indiceRelacionamentoGrupoMensagem.read(grupo.getId());

                if (listaIdMensagens.length == 0) {
                    System.out.println("\n\nNão há mensagens neste grupo!\n\n");
                    pressToContinue();
                    return;
                }

                for (int i = 0; i < listaIdMensagens.length; i++) {
                    mensagens.add(crudMensagens.read(listaIdMensagens[i]));
                }

                Collections.sort(mensagens, comparing(Mensagem::getMomentoEnvio));
                Collections.reverse(mensagens);

                clearScreen();
                System.out.println("Mensagens:\n");
                if (mensagens.size() >= 5) {
                    for (int i = posicaoArray; i < 5; i++) {
                        System.out.print((i + 1) + ". ");
                        System.out.print(mensagens.get(i));
                        System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                        System.out.println();
                    }

                    System.out.print("\n\nQual mensagem deseja visualizar: ");
                    msgEscolhida = in.nextLine();

                    if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > 5)) {
                        System.out.println("\n\nMensagem:");
                        System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                    } else {
                        System.out.println("\n\nERRO: Você escolheu uma mensagem inválida.");
                    }
                } else {
                    for (int i = posicaoArray; i < mensagens.size(); i++) {
                        System.out.print((i + 1) + ". ");
                        System.out.print(mensagens.get(i));
                        System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                        System.out.println();
                    }

                    System.out.print("\n\nQual mensagem deseja visualizar: ");
                    msgEscolhida = in.nextLine();

                    if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                        System.out.println("\n\nMensagem:");
                        System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                    } else {
                        System.out.println("\n\nNenhuma op��o v�lida foi escolhida.");
                    }
                }

                if (mensagens.size() > 5 && !msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                    System.out.println("\n\nDeseja mudar de p�gina? (Digite sim ou nao)\n");
                    System.out.print("Confirma: ");
                    mudarPagina = in.nextLine();

                    while ((!mudarPagina.equals("sim")) && (!mudarPagina.equals("nao"))) {
                        System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        mudarPagina = in.nextLine();
                    }

                    if (mudarPagina.equals("sim")) {
                        mensagens.remove(0);
                        mensagens.remove(0);
                        mensagens.remove(0);
                        mensagens.remove(0);
                        mensagens.remove(0);

                    }
                }

                while (!msgEscolhida.equals("0")) {
                    System.out.println("\n\nMensagens:\n");
                    if (mensagens.size() >= 5) {
                        for (int i = posicaoArray; i <= posicaoArray + 4; i++) {
                            System.out.print((i + 1) + ". ");
                            System.out.print(mensagens.get(i));
                            System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                            System.out.println();
                        }

                        System.out.print("\n\nQual mensagem deseja visualizar: ");
                        msgEscolhida = in.nextLine();

                        if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > 5)) {
                            System.out.println("\n\nMensagem:");
                            System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                        } else {
                            System.out.println("\n\nNenhuma op��o v�lida foi escolhida.");
                        }
                    } else {
                        for (int i = posicaoArray; i < mensagens.size(); i++) {
                            System.out.print((i + 1) + ". ");
                            System.out.print(mensagens.get(i));
                            System.out.println("\nEnviado por: " + crudUsuario.read(mensagens.get(i).getIdUsuario()).getNome());
                            System.out.println();
                        }

                        System.out.print("\n\nQual mensagem deseja visualizar: ");
                        msgEscolhida = in.nextLine();

                        if (!msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                            System.out.println("\n\nMensagem:");
                            System.out.println(mensagens.get(Integer.parseInt(msgEscolhida) - 1).getMensagem());
                        } else {
                            System.out.println("\n\nNenhuma op��o v�lida foi escolhida.");
                        }
                    }

                    if (mensagens.size() > 5 && !msgEscolhida.equals("0") && !(Integer.parseInt(msgEscolhida) < 0) && !(Integer.parseInt(msgEscolhida) > mensagens.size())) {
                        System.out.println("\n\nDeseja mudar de p�gina? (Digite sim ou nao)\n");
                        System.out.print("Confirma: ");
                        mudarPagina = in.nextLine();

                        while ((!mudarPagina.equals("sim")) && (!mudarPagina.equals("nao"))) {
                            System.out.println("\nERRO: Confirma��o inv�lida. Digite sim ou nao.");
                            System.out.print("Confirma: ");
                            mudarPagina = in.nextLine();
                        }

                        if (mudarPagina.equals("sim")) {
                            mensagens.remove(0);
                            mensagens.remove(0);
                            mensagens.remove(0);
                            mensagens.remove(0);
                            mensagens.remove(0);

                        }
                    }
                }
            }
        }

        pressToContinue();
    }
    
    //Métodos auxiliares

    /**
     * Valida��o de email atrav�s de Regex
     * @param email
     * @return 
     */
    private boolean isValidEmailAddressRegex(String email) {
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }
    
    /**
     * Embaralha vetor de inteiros
     * @param array 
     */
    private void shuffleArray(int[] array) {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }
   
    //Métodos get e set e alguns auxiliares para simplificar a escrita de coisas
    //na HUD
    public String getNomeAplicacao() {
        return nomeAplicacao;
    }

    public void setNomeAplicacao(String nomeAplicacao) {
        this.nomeAplicacao = nomeAplicacao;
    }

    public String getVersaoAplicacao() {
        return versaoAplicacao;
    }

    public void setVersaoAplicacao(String versaoAplicacao) {
        this.versaoAplicacao = versaoAplicacao;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public int getQuantConvites() {
        return quantConvites;
    }

    public void setQuantConvites(int quantConvites) {
        this.quantConvites = quantConvites;
    }

    private void clearScreen() {
        System.out.print("\n\n\n");
    }

    private void pressToContinue() {
        System.out.println("Pressione qualquer tecla para continuar...");
        in.nextLine();
    }
    //Conjunto de ENUMS que representam cada MENU que pode aparecer no programa
    public enum Tela {
        MENU_INICIAL, MENU_PRINCIPAL, MENU_SUGESTOES, MENU_GRUPOS, MENU_GERENCIAMENTODEGRUPOS,
        MENU_GERENCIAMENTODEGRUPOS_GRUPOS, MENU_GERENCIAMENTODEGRUPOS_CONVITES, MENU_CONVITES,
        MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES, MENU_GRUPOS_PARTICIPACAO_NOS_GRUPOS,
        MENU_GRUPOS_ENVIO_LEITURA_MENSAGENS;
    }
}
