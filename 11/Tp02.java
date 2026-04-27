import java.io.*;
import java.util.*;

class Globals {
    public static int comparacoes = 0;
    public static int movimentacoes = 0;
}

// Só para facilitar a criação do arquivo de log (Espero que não tenha problema)
class LogWritter {
    private String matricula = "892668";
    private String name;
    private FileWriter fw;
    private PrintWriter pw;

    // Passagem do nome do exercício
    public LogWritter(String name) {
        this.name = name;
        this.newLog(true);
    }

    // Instanciação dos Writers
    private LogWritter newLog(boolean append) {
        try {
            this.pw = new PrintWriter(new FileWriter("./" + this.matricula + "_" + name + ".txt", append));
        } catch (IOException e) {
            System.err.println("Erro ao gerenciar arquivo de log: " + e.getMessage());
        }

        return this;
    }

    // Adição de linha personalizada para o arquivo caso necessário
    public void appendln(String conteudo) {
        try {
            pw.println(conteudo + "\t");
        } catch (Exception e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    // Zera arquivo
    public void clear() {
        close();
        newLog(false);
        close();
        newLog(true);
    }

    // Fecha os Writers
    public void close() {
        if (pw != null) {
            pw.close();
        }
    }

    // Appends padrões
    public void setTempo(long duracao) {
        this.appendln("Tempo de execução: " + duracao + "ns (Ou " + String.format("%.3f", duracao / 1000000.0) + "ms)");
    }

    public void setMovimentacoes(int movimentacoes) {
        this.appendln("Quantidade de movimentações: " + movimentacoes + " movimentações");
    }

    public void setComparacoes(int comparacoes) {
        this.appendln("Quantidade de comparações: " + comparacoes + " comparações");
    }
}

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

    // ------ GETTERS -------

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

    // Retorna positivo caso hora atual seja maior
    // Retorna 0 caso hora atual seja igual
    // Retorna negativo caso hora atual seja menor
    public int comparar(Hora outraHora) {
        if (this.hora != outraHora.hora) {
            return this.hora - outraHora.hora;
        }
        return this.minuto - outraHora.minuto;
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

    // Retorna positivo caso Data atual seja maior
    // Retorna 0 caso Data atual seja igual
    // Retorna negativo caso Data atual seja menor
    public int comparar(Data outraData) {
        if (this.ano != outraData.ano) {
            return this.ano - outraData.ano;
        }
        if (this.mes != outraData.mes) {
            return this.mes - outraData.mes;
        }
        return this.dia - outraData.dia;
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
        int qtdCampos = 1;

        // Conta quantos campos tem
        for (int i = 0; i < csvString.length(); i++) {
            if (csvString.charAt(i) == ',')
                qtdCampos++;
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
            if (campos[5].charAt(i) == ';')
                qtdCozinhas++;
        }

        String[] cozinhas = new String[qtdCozinhas];
        campoAtual = 0;
        buffer = "";

        for (int i = 0; i < campos[5].length(); i++) {
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

        for (int i = 0; i < campos[7].length(); i++) {
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

    // ------ GETTERS -------

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
        String s = String.format("[%d ## %s ## %s ## %d ## %.1f ## [", this.id, this.nome, this.cidade, this.capacidade,
                this.avaliacao);
        for (int i = 0; i < this.tiposCozinha.length; i++) {
            s += this.tiposCozinha[i];
            if (i != this.tiposCozinha.length - 1) {
                s += ",";
            }
        }
        s += "] ## ";
        for (int i = 0; i < this.faixaPreco; i++)
            s += "$";
        s += String.format(" ## %s-%s ## %s ## %b]", this.horarioAbertura.format(), this.horarioFechamento.format(),
                this.dataAbertura.format(), this.aberto);
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
        try (Scanner scan = new Scanner(file);) {
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

    // ------ GETTERS -------

    public int getTamanho() {
        return this.tamanho;
    }

    public Restaurante[] getRestaurantes() {
        return this.restaurantes;
    }

    // Busca sequencial por nome (Questão 05)
    public Restaurante getRestauranteByNomeSequencial(String nome) {
        for (int i = 0; i < this.getTamanho(); i++) {
            Globals.comparacoes++;
            if (this.restaurantes[i].getNome().compareTo(nome) == 0) {
                return this.restaurantes[i];
            }
        }
        // System.out.println("O ID não corresponde a nenhum restaurante");
        return null;
    }

    // Busca sequencial por ID. Linear mas não necessita de ordenação prévia
    public Restaurante getRestauranteByIdSequencial(int id) {
        if (id <= 0) {
            System.out.println("O ID é inválido");
            return null;
        }
        for (int i = 0; i < tamanho; i++) {
            if (this.restaurantes[i].getId() == id) {
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
        while (esq <= dir) {
            int media = (esq + dir) / 2;
            // System.out.println(String.format("Esq: %d; Dir: %d; Media: %d;", esq, dir,
            // media));
            if (this.restaurantes[media].getId() == id) {
                return this.restaurantes[media];
            } else if (this.restaurantes[media].getId() > id) {
                dir = media - 1;
            } else if (this.restaurantes[media].getId() < id) {
                esq = media + 1;
            }
        }
        System.out.println("O ID não corresponde a nenhum restaurante");
        return null;
    }

    // Insertion Sort por cidade (Questão 04)
    public ColecaoRestaurante insertionSortByCidade() {
        for (int i = 1; i < this.getTamanho(); i++) {
            Restaurante tmp = this.restaurantes[i];
            int j = i - 1;

            while ((j >= 0) && (this.restaurantes[j].getCidade().compareTo(tmp.getCidade()) > 0)) {
                Globals.comparacoes++;
                this.restaurantes[j + 1] = this.restaurantes[j];
                Globals.movimentacoes++;
                j--;
            }
            if (j < 0)
                Globals.comparacoes++;

            this.restaurantes[j + 1] = tmp;
            Globals.movimentacoes++;
        }

        return this;
    }

    // Merge Sort por Nome (Questão 07)
    public void mergeSortByNome() {
        this.mergeSort(0, this.getTamanho() - 1);
    }

    private void mergeSort(int esq, int dir) {
        if (esq < dir) {
            int meio = (esq + dir) / 2;
            mergeSort(esq, meio);
            mergeSort(meio + 1, dir);
            intercalar(esq, meio, dir);
        }
    }

    private void intercalar(int esq, int meio, int dir) {
        int n1 = meio - esq + 1;
        int n2 = dir - meio;

        Restaurante[] a1 = new Restaurante[n1];
        Restaurante[] a2 = new Restaurante[n2];

        for (int i = 0; i < n1; i++) {
            a1[i] = this.restaurantes[esq + i];
            Globals.movimentacoes++;
        }
        for (int j = 0; j < n2; j++) {
            a2[j] = this.restaurantes[meio + j + 1];
            Globals.movimentacoes++;
        }

        int i = 0, j = 0, k = esq;

        while (i < n1 && j < n2) {
            Globals.comparacoes++;
            if (a1[i].getCidade().compareTo(a2[j].getCidade()) < 0) {
                this.restaurantes[k] = a1[i];
                i++;
            } else if (a1[i].getCidade().compareTo(a2[j].getCidade()) > 0) {
                this.restaurantes[k] = a2[j];
                j++;
            } else { // Caso sejam a mesma cidade entra para comparar por nome
                Globals.comparacoes++;
                if (a1[i].getNome().compareTo(a2[j].getNome()) <= 0) {
                    this.restaurantes[k] = a1[i];
                    i++;
                } else {
                    this.restaurantes[k] = a2[j];
                    j++;
                }
            }
            Globals.movimentacoes++;
            k++;
        }

        while (i < n1) {
            this.restaurantes[k++] = a1[i++];
            Globals.movimentacoes++;
        }

        while (j < n2) {
            this.restaurantes[k++] = a2[j++];
            Globals.movimentacoes++;
        }
    }

    // Heap Sort (Questão 09)
    public void heapSortByDataAbertura() {
        int n = this.getTamanho();

        for (int tamHeap = 2; tamHeap <= n; tamHeap++) {
            construir(tamHeap);
        }

        int tamHeap = n;
        while (tamHeap > 1) {
            swapHeap(1, tamHeap--);
            reconstruir(tamHeap);
        }
    }

    private void construir(int tamHeap) {
        for (int i = tamHeap; i > 1 && comparar(i, i / 2) > 0; i /= 2) {
            swapHeap(i, i / 2);
        }
    }

    private void reconstruir(int tamHeap) {
        int i = 1;
        while (i <= (tamHeap / 2)) {
            int filho = getMaiorFilho(i, tamHeap);
            if (comparar(i, filho) < 0) {
                swapHeap(i, filho);
                i = filho;
            } else {
                i = tamHeap;
            }
        }
    }

    private int getMaiorFilho(int i, int tamHeap) {
        int filho;
        if (2 * i == tamHeap) {
            filho = 2 * i;
        } else {
            if (comparar(2 * i, 2 * i + 1) > 0) {
                filho = 2 * i;
            } else {
                filho = 2 * i + 1;
            }
        }
        return filho;
    }

    // Lógica de comparação lexográfica e critério de desempate
    private int comparar(int i, int j) {
        Globals.comparacoes++;
        Restaurante r1 = this.restaurantes[i - 1];
        Restaurante r2 = this.restaurantes[j - 1];

        int compData = r1.getDataAbertura().comparar(r2.getDataAbertura());

        if (compData != 0) {
            return compData;
        }

        Globals.comparacoes++;
        return r1.getNome().compareTo(r2.getNome());
    }

    // Swap específico para indexação do heap (i - 1)
    private void swapHeap(int i, int j) {
        Restaurante aux = this.restaurantes[i - 1];
        this.restaurantes[i - 1] = this.restaurantes[j - 1];
        this.restaurantes[j - 1] = aux;
        Globals.movimentacoes += 3;
    }

    // Swap padrão
    private void swap(int i, int j) {
        Restaurante aux = this.restaurantes[i];
        this.restaurantes[i] = this.restaurantes[j];
        this.restaurantes[j] = aux;
        Globals.movimentacoes += 3;
    }

    public Restaurante getRestauranteByIndex(int i) {
        return this.restaurantes[i];
    }

    public void printAll() {
        for (int i = 0; i < this.getTamanho(); i++) {
            System.out.println(this.getRestauranteByIndex(i).formatar());
        }
    }

}

// Estrutura flexível (Exercício 11)
class RestauranteFlex {
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
    private RestauranteFlex prox;

    public RestauranteFlex(String csvString) {
        int qtdCampos = 1;

        // Conta quantos campos tem
        for (int i = 0; i < csvString.length(); i++) {
            if (csvString.charAt(i) == ',')
                qtdCampos++;
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
            if (campos[5].charAt(i) == ';')
                qtdCozinhas++;
        }

        String[] cozinhas = new String[qtdCozinhas];
        campoAtual = 0;
        buffer = "";

        for (int i = 0; i < campos[5].length(); i++) {
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

        for (int i = 0; i < campos[7].length(); i++) {
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
        this.prox = null;
    }

    // Cria uma nova cópia de RestauranteFlex
    public RestauranteFlex(RestauranteFlex prevRestaurante) {
        if (prevRestaurante == null) {
            System.out.println("Não foi possível criar cópia de RestauranteFlex");
            return;
        }
        this.id = prevRestaurante.getId();
        this.nome = prevRestaurante.getNome();
        this.cidade = prevRestaurante.getCidade();
        this.capacidade = prevRestaurante.getCapacidade();
        this.avaliacao = prevRestaurante.getAvaliacao();
        this.tiposCozinha = prevRestaurante.getTiposCozinha();
        this.faixaPreco = prevRestaurante.getFaixaPreco();
        this.horarioAbertura = prevRestaurante.getHorarioAbertura();
        this.horarioFechamento = prevRestaurante.getHorarioFechamento();
        this.dataAbertura = prevRestaurante.getDataAbertura();
        this.aberto = prevRestaurante.isAberto();
        this.prox = null;
    }

    // ------ GETTERS -------

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

    public RestauranteFlex getProx() {
        return this.prox;
    }

    // ---------- SETTERS ------------

    public void setProx(RestauranteFlex restaurante) {
        this.prox = restaurante;
    }

    // Retorno de string padronizado
    public String formatar() {
        String s = String.format("[%d ## %s ## %s ## %d ## %.1f ## [", this.id, this.nome, this.cidade, this.capacidade,
                this.avaliacao);
        for (int i = 0; i < this.tiposCozinha.length; i++) {
            s += this.tiposCozinha[i];
            if (i != this.tiposCozinha.length - 1) {
                s += ",";
            }
        }
        s += "] ## ";
        for (int i = 0; i < this.faixaPreco; i++)
            s += "$";
        s += String.format(" ## %s-%s ## %s ## %b]", this.horarioAbertura.format(), this.horarioFechamento.format(),
                this.dataAbertura.format(), this.aberto);
        return s;
    }

}

class ColecaoRestauranteFlex {
    RestauranteFlex raiz, ultimo;
    int tamanho;

    public ColecaoRestauranteFlex() {
        this.raiz = this.ultimo = null;
        tamanho = 0;
    }

    public static ColecaoRestauranteFlex lerCsv(File file) {
        ColecaoRestauranteFlex restaurantes = new ColecaoRestauranteFlex();
        try (Scanner scan = new Scanner(file);) {
            scan.nextLine();
            while (scan.hasNextLine()) {
                String linha = scan.nextLine();
                RestauranteFlex restaurante = new RestauranteFlex(linha);
                restaurantes.push(restaurante);
            }
            return restaurantes;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public RestauranteFlex getByIdSequencial(int id) {
        RestauranteFlex atual = this.raiz;
        while (atual != null) {
            if (atual.getId() == id) {
                return atual;
            }
            atual = atual.getProx();
        }
        System.out.println("Elemento não encontrado");
        return null;
    }

    // Adiciona item no final da lista
    public void push(RestauranteFlex newRestaurante) {
        if (newRestaurante == null) {
            System.out.println("Não foi possível adicionar restaurante");
            return;
        }
        if (this.raiz == null) {
            this.raiz = this.ultimo = newRestaurante;
            this.tamanho++;
            return;
        }
        this.ultimo.setProx(newRestaurante);
        this.ultimo = newRestaurante;
        this.ultimo.setProx(null);
        this.tamanho++;
    }

    // Adiciona item em local específico da lista, para i == 0 adiciona no início
    public void insert(RestauranteFlex newRestaurante, int i) {
        if (newRestaurante == null) {
            System.out.println("Não foi possível adicionar restaurante");
            return;
        }
        if (i == 0) {
            newRestaurante.setProx(this.raiz);
            this.raiz = newRestaurante;
            this.tamanho++;
            return;
        }
        if (i > this.getTamanho()) {
            System.out.println("índice inválido");
            return;
        }
        RestauranteFlex atual = this.raiz;
        for (int j = 0; j < i - 1; j++) {
            atual = atual.getProx();
        }
        newRestaurante.setProx(atual.getProx());
        atual.setProx(newRestaurante);
        if (newRestaurante.getProx() == null) { // Validação caso seja o último
            this.ultimo = newRestaurante;
        }
        this.tamanho++;
    }

    // Remove elemento da lista, se i == -1, remove ultimo
    public RestauranteFlex shift() {
        if (this.raiz == null)
            return null;

        RestauranteFlex removido = this.raiz;
        this.raiz = this.raiz.getProx();

        if (this.raiz == null) {
            this.ultimo = null;
        }

        this.tamanho--;
        removido.setProx(null);
        return removido;
    }

    // Remove elemento da lista
    public RestauranteFlex pop(int i) {
        if (this.raiz == null)
            return null;
        if (i == 0)
            return shift();

        if (i == -1)
            i = this.tamanho - 1;

        RestauranteFlex anterior = this.raiz;
        for (int j = 0; j < i - 1; j++) {
            anterior = anterior.getProx();
        }

        RestauranteFlex removido = anterior.getProx();
        anterior.setProx(removido.getProx());

        if (anterior.getProx() == null) {
            this.ultimo = anterior;
        }

        this.tamanho--;
        removido.setProx(null);
        return removido;
    }

    public int getTamanho() {
        return this.tamanho;
    }

    // Printa os elementos da lista
    public void mostrar() {
        RestauranteFlex atual = raiz;
        while (atual != null) {
            System.out.println(atual.formatar());
            atual = atual.getProx();
        }
    }

}

public class Tp02 {
    public static boolean isFim(String str) {
        return (str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M');
    }

    public static void main(String args[]) {
        String tmpDir = System.getProperty("java.io.tmpdir"); // caminho para a pasta tmp (Ao invés de usar só "/tmp")
        LogWritter lw = new LogWritter("mergesort"); // Criação do arquivo de log
        lw.clear(); // Limpeza de log existente para facilitar debug local
        long init_tempo, end_tempo; // Inicialização dos tempos

        File file = new File(tmpDir + "/restaurantes.csv");
        ColecaoRestauranteFlex restaurantes = ColecaoRestauranteFlex.lerCsv(file);
        ColecaoRestauranteFlex restaurantesCopy = new ColecaoRestauranteFlex();

        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();

        while (n != -1) {
            RestauranteFlex resultado = restaurantes.getByIdSequencial(n);
            if (resultado != null) {
                restaurantesCopy.push(new RestauranteFlex(resultado));
            }

            n = scan.nextInt();
        }

        n = scan.nextInt();

        for (int i = 0; i < n; i++) {
            String str = scan.next();
            RestauranteFlex newRestauranteFlex = null;
            // System.out.println("Nova Operação: " + str);
            int j = -2;
            switch (str) {
                case "II":
                    newRestauranteFlex = new RestauranteFlex(
                            restaurantes.getByIdSequencial(Integer.parseInt(scan.next())));
                    restaurantesCopy.insert(newRestauranteFlex, 0);
                    break;
                case "I*":
                    j = scan.nextInt();
                    newRestauranteFlex = new RestauranteFlex(
                            restaurantes.getByIdSequencial(Integer.parseInt(scan.next())));
                    restaurantesCopy.insert(newRestauranteFlex, j);
                    break;
                case "IF":
                    newRestauranteFlex = new RestauranteFlex(
                            restaurantes.getByIdSequencial(Integer.parseInt(scan.next())));
                    restaurantesCopy.push(newRestauranteFlex);
                    break;
                case "RI":
                    System.out.println("(R)" + restaurantesCopy.shift().getNome());
                    break;
                case "R*":
                    j = scan.nextInt();
                    System.out.println("(R)" + restaurantesCopy.pop(j).getNome());
                    break;
                case "RF":
                    System.out.println("(R)" + restaurantesCopy.pop(-1).getNome());
                    break;
            }
            
        }
        restaurantesCopy.mostrar();

        init_tempo = System.nanoTime();

        end_tempo = System.nanoTime();

        scan.close();

        long duracao = (end_tempo - init_tempo);
        lw.setTempo(duracao);
        lw.setComparacoes(Globals.comparacoes);
        lw.setMovimentacoes(Globals.movimentacoes);

        lw.close();
    }

}