import java.io.File;
import java.util.*;

class Hora {
    private int hora, minuto;

    public Hora(int minuto, int hora) {
        this.hora = hora;
        this.minuto = minuto;
    }

    public Hora(String strHora) {
        String[] campos = new String[3];
        String buffer = "";
        int campoAtual = 0;
        for (int i = 0; i < strHora.length(); i++) {
            char cAtual = strHora.charAt(i);
            if (cAtual == ':') {
                campos[campoAtual] = buffer;
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
        }
        this.hora = Integer.parseInt(campos[0]);
        this.minuto = Integer.parseInt(campos[1]);
    }

}

class Data {
    private int ano, mes, dia;

    public Data(int dia, int mes, int ano) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    public Data(String strData) {
        String[] campos = new String[3];
        String buffer = "";
        int campoAtual = 0;
        for (int i = 0; i < strData.length(); i++) {
            char cAtual = strData.charAt(i);
            if (cAtual == '-') {
                campos[campoAtual] = buffer;
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
        }
        this.ano = Integer.parseInt(campos[0]);
        this.mes = Integer.parseInt(campos[1]);
        this.dia = Integer.parseInt(campos[2]);
    }

}

class Restaurante {

    public int id;
    public String nome;
    public String cidade;
    public int capacidade;
    public double avaliacao;
    public String[] tiposCozinha;
    public int faixaPreco;
    public Data dataAbertura;
    public Hora horarioAbertura;
    public Hora horarioFechamento;
    public boolean aberto;

    public Restaurante(String csvString) {
        int qtdCampos = 1;

        //Conta quantos campos tem
        for (int i = 0; i < csvString.length(); i++) {
            if(csvString.charAt(i) == ',') qtdCampos++;
        }

        String[] campos = new String[qtdCampos];
        String buffer = "";
        int campoAtual = 0;

        // Mapeia caractere por caractere e atribui em uma lista temporaria
        for (int i = 0; i < csvString.length(); i++) {
            char cAtual = csvString.charAt(i);
            if (cAtual == ',') {
                campos[campoAtual] = buffer;
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
        }

        // Repete mesma ideia para separar os tipos de cozinha
        int qtdCozinhas = 1;
        for (int i = 0; i < campos[5].length(); i++) {
            if(campos[5].charAt(i) == ',') qtdCozinhas++;
        }
        
        String[] cozinhas = new String[qtdCozinhas];
        campoAtual = 0;
        buffer = "";
        
        for(int i = 0; i < campos[5].length(); i++) {
            char cAtual = campos[5].charAt(i);
            if (cAtual == ';') {
                cozinhas[campoAtual] = buffer;
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
        }

        // Repete mesma ideia para horários
        String[] horarios = new String[2];
        campoAtual = 0;
        buffer = "";

        for(int i = 0; i < campos[7].length(); i++) {
            char cAtual = campos[7].charAt(i);
            if (cAtual == '-') {
                cozinhas[campoAtual] = buffer;
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
        }

        this.id = Integer.parseInt(campos[0]);
        this.nome = campos[1];
        this.cidade = campos[2];
        this.capacidade = Integer.parseInt(campos[3]);
        this.avaliacao = Double.parseDouble(campos[4]);
        this.tiposCozinha = cozinhas;
        this.faixaPreco = campos[6].length();
        this.horarioAbertura = new Hora(horarios[0]);
        this.horarioFechamento = new Hora(horarios[1]);
        this.dataAbertura = new Data(campos[8]);
        this.aberto = Boolean.parseBoolean(campos[9]);
    }

}

class ColecaoRestaurante {
    private int tamanho;
    private Restaurante[] restaurantes;

    public static void ColecaoRestaurante() {

    }

}

public class Tp02 {
    public static void main(String args[]){

        File file = new File("./tmp/restaurantes.csv");
        
        try(Scanner scan = new Scanner(file);) {
            scan.nextLine();
            while (scan.hasNextLine()) {
                String linha = scan.nextLine();
                Restaurante restaurante = new Restaurante(linha);
                System.out.println(restaurante.id);
                System.out.println(restaurante.nome);
                System.out.println(restaurante.cidade);
                System.out.println(restaurante.capacidade);
                System.out.println(restaurante.avaliacao);
                System.out.println(restaurante.tiposCozinha);
                System.out.println(restaurante.faixaPreco);
                System.out.println(restaurante.horarioAbertura);
                System.out.println(restaurante.horarioFechamento);
                System.out.println(restaurante.dataAbertura);
                System.out.println(restaurante.aberto);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}