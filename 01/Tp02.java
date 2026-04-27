import java.io.File;
import java.util.*;

class Hora {
    private int hora, minuto;

    public Hora(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
    }

    public static Hora parseHora(String strHora) {
        String[] campos = new String[2];
        String buffer = "";
        int campoAtual = 0;
        for (int i = 0; i < strHora.length(); i++) {
            char cAtual = strHora.charAt(i);
            if (cAtual == ':') {
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
            campos[campoAtual] = buffer;
        }
        return new Hora(Integer.parseInt(campos[0]), Integer.parseInt(campos[1]));
    }

    public String format() {
        return String.format("%02d:%02d", this.hora, this.minuto);
    }


    // ------  GETTERS  -------


    public String getHora() {
        return String.format("%02d", this.hora);
    }

    public String getMinuto() {
        return String.format("%02d", this.minuto);
    }

    public int getHoraNum() {
        return this.hora;
    }

    public int getMinutoNum() {
        return this.minuto;
    }
}

class Data {
    private int ano, mes, dia;

    public Data(int dia, int mes, int ano) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    public static Data parseData(String strData) {
        String[] campos = new String[3];
        String buffer = "";
        int campoAtual = 0;
        for (int i = 0; i < strData.length(); i++) {
            char cAtual = strData.charAt(i);
            if (cAtual == '-') {
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
            campos[campoAtual] = buffer;
        }
        return new Data(Integer.parseInt(campos[2]), Integer.parseInt(campos[1]), Integer.parseInt(campos[0]));
    }

    public String format() {
        return String.format("%02d/%02d/%04d", this.dia, this.mes, this.ano);
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
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
            campos[campoAtual] = buffer;
        }

        // Repete mesma ideia para separar os tipos de cozinha
        int qtdCozinhas = 1;
        for (int i = 0; i < campos[5].length(); i++) {
            if(campos[5].charAt(i) == ';') qtdCozinhas++;
        }
        
        String[] cozinhas = new String[qtdCozinhas];
        campoAtual = 0;
        buffer = "";

        for(int i = 0; i < campos[5].length(); i++) {
            char cAtual = campos[5].charAt(i);
            if (cAtual == ';') {
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
            cozinhas[campoAtual] = buffer;
        }

        // Repete mesma ideia para horários
        String[] horarios = new String[2];
        campoAtual = 0;
        buffer = "";

        for(int i = 0; i < campos[7].length(); i++) {
            char cAtual = campos[7].charAt(i);
            if (cAtual == '-') {
                buffer = "";
                campoAtual++;
            } else {
                buffer += cAtual;
            }
            horarios[campoAtual] = buffer;
        }

        this.id = Integer.parseInt(campos[0]);
        this.nome = campos[1];
        this.cidade = campos[2];
        this.capacidade = Integer.parseInt(campos[3]);
        this.avaliacao = Double.parseDouble(campos[4]);
        this.tiposCozinha = cozinhas;
        this.faixaPreco = campos[6].length();
        this.horarioAbertura = Hora.parseHora(horarios[0]);
        this.horarioFechamento = Hora.parseHora(horarios[1]);
        this.dataAbertura = Data.parseData(campos[8]);
        this.aberto = Boolean.parseBoolean(campos[9]);
    }
    
    // ------  GETTERS  -------
    
    public int getId() {
        return this.id;
    }
    public String getNome() {
        return this.nome;
    }
    public String getCidade() {
        return this.cidade;
    }
    public int getCapacidade() {
        return this.capacidade;
    }
    public double getAvaliacao() {
        return this.avaliacao;
    }
    public String[] getTiposCozinha() {
        return this.tiposCozinha;
    }
    public int getFaixaPreco() {
        return this.faixaPreco;
    }
    public Hora getHorarioAbertura() {
        return this.horarioAbertura;
    }
    public Hora getHorarioFechamento() {
        return this.horarioFechamento;
    }
    public Data getDataAbertura() {
        return this.dataAbertura;
    }
    public boolean isAberto() {
        return this.aberto;
    }

    // Retorno de string padronizado
    public String formatar() {
        String s = String.format("[%d ## %s ## %s ## %d ## %.1f ## [", this.id, this.nome, this.cidade, this.capacidade, this.avaliacao);
        for(int i = 0; i < this.tiposCozinha.length; i++) {
            s += this.tiposCozinha[i];
            if (i != this.tiposCozinha.length - 1) {
                s += ",";
            }
        }
        s += "] ## ";
        for (int i = 0; i < this.faixaPreco; i++) s += "$";
        s += String.format(" ## %s-%s ## %s ## %b]", this.horarioAbertura.format(), this.horarioFechamento.format(), this.dataAbertura.format(), this.aberto);
        return s;
    }

}

class ColecaoRestaurante {
    private int tamanho;
    private Restaurante[] restaurantes;

    public ColecaoRestaurante() {
        this.tamanho = 0;
        this.restaurantes = new Restaurante[1000];
    }
    
    public ColecaoRestaurante(Restaurante restaurante) {
        this.tamanho = 1;
        this.restaurantes = new Restaurante[1000];
        this.restaurantes[tamanho - 1] = restaurante;
    }

    // Leitura de Arquivo CSV
    public static ColecaoRestaurante lerCsv(File file) {
        ColecaoRestaurante restaurantes = new ColecaoRestaurante();
        try(Scanner scan = new Scanner(file);) {
            scan.nextLine();
            while (scan.hasNextLine()) {
                String linha = scan.nextLine();
                Restaurante restaurante = new Restaurante(linha);
                restaurantes.push(restaurante);
            }
            return restaurantes;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // Adiciona no fim
    public void push(Restaurante restaurante) {
        this.restaurantes[tamanho] = restaurante;
        this.tamanho++;
    }

    // ------  GETTERS  -------

    public int getTamanho() {
        return this.tamanho;
    }

    // Busca sequencial por ID. Linear mas não necessita de ordenação prévia
    public Restaurante getRestauranteByIdSequencial(int id) {
        if (id <= 0) {
            System.out.println("O ID é inválido");
            return null;
        }
        for (int i = 0; i < tamanho; i++) {
            if(this.restaurantes[i].id == id) {
                return this.restaurantes[i];
            }
        }
        System.out.println("O ID não corresponde a nenhum restaurante");
        return null;
    }

    // Busca binária por ID. Logarítmico mas demanda ordenação por ID
    public Restaurante getRestauranteByIdBinario(int id) {
        int esq = 0, dir = this.getTamanho() - 1;
        if (id <= 0) {
            System.out.println("O ID é inválido");
            return null;
        }
        while(esq <= dir) { 
            int media = (esq + dir) / 2;
            // System.out.println(String.format("Esq: %d; Dir: %d; Media: %d;", esq, dir, media));
            if (this.restaurantes[media].id == id) {
                return this.restaurantes[media];
            } else if(this.restaurantes[media].id > id) {
                dir = media - 1;
            } else if(this.restaurantes[media].id < id) {
                esq = media + 1;
            }
        }
        System.out.println("O ID não corresponde a nenhum restaurante");
        return null;
    }

    public ColecaoRestaurante insertionSortByCidade() {
        for (int i = 1; i < this.restaurantes.length; i++) {
            String key = this.restaurantes[i].cidade;
            int j = i - 1;

            while (j >= 0 && this.restaurantes[j].cidade.compareTo(key) == 1) {
                this.restaurantes[j + 1] = this.restaurantes[j];
                j--;
            }
            
            this.restaurantes[j + 1].cidade = key;
        }

        return this;
    }
    
    public Restaurante getRestauranteByIndex(int i) {
        return this.restaurantes[i];
    }

}

public class Tp02 {
    public static void main(String args[]){

        try {
            String tmpDir = System.getProperty("java.io.tmpdir");
            File file = new File(tmpDir + "/restaurantes.csv");
            ColecaoRestaurante restaurantes = ColecaoRestaurante.lerCsv(file);
        
            Scanner scan = new Scanner(System.in);
            int n;
            n = scan.nextInt();
            
            while (n != -1) {
                Restaurante resultado = restaurantes.getRestauranteByIdBinario(n);
                if (resultado != null) {
                    System.out.println(resultado.formatar());
                }
                
                n = scan.nextInt();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}