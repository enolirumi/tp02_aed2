import java.io.File;
import java.util.*;

class Hora {
    private int hora, minuto;

    public static void Hora() {

    }

}

class Data {
    private int ano, mes, dia;

    public static void Data() {

    }

}

class Restaurante {

    private int id;
    private String nome;
    private String cidade;
    private int capacidade;
    private double avaliacao;
    private String[] tiposCozinha;
    private int faixaPreco;
    private Data dataAbertura;
    private Hora horarioAbertura;
    private Hora horarioFechamento;
    private boolean aberto;

    public Restaurante(String csvString) {
        int qtdCampos = 0;
        for (int i = 0; i < csvString.length(); i++) {
            if(csvString.charAt(i) == ',') qtdCampos++;
        }

        String[] campos = new String[qtdCampos];
        String buffer = "";
        int campoAtual = 0;

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
            while (scan.hasNextLine()) {
                String linha = scan.nextLine();
                
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}