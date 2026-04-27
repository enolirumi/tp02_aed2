// Tradução em C de Base.java (implementação principal)
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

// Contadores semelhantes à classe Globals usada no Java
long long comparacoes = 0;
long long movimentacoes = 0;

typedef struct {
    int hora;
    int minuto;
} Hora;

Hora parseHora(const char *str) {
    Hora h = {0,0};
    int hh = 0, mm = 0;
    sscanf(str, "%d:%d", &hh, &mm);
    h.hora = hh; h.minuto = mm;
    return h;
}

void formatHora(Hora h, char *out) {
    sprintf(out, "%02d:%02d", h.hora, h.minuto);
}

int compararHora(Hora a, Hora b) {
    if (a.hora != b.hora) return a.hora - b.hora;
    return a.minuto - b.minuto;
}

typedef struct {
    int dia;
    int mes;
    int ano;
} Data;

Data parseData(const char *str) {
    Data d = {0,0,0};
    int y=0, m=0, day=0;
    sscanf(str, "%d-%d-%d", &y, &m, &day); // espera YYYY-MM-DD
    d.dia = day; d.mes = m; d.ano = y;
    return d;
}

void formatData(Data d, char *out) {
    sprintf(out, "%02d/%02d/%04d", d.dia, d.mes, d.ano);
}

int compararData(Data a, Data b) {
    if (a.ano != b.ano) return a.ano - b.ano;
    if (a.mes != b.mes) return a.mes - b.mes;
    return a.dia - b.dia;
}

typedef struct {
    int id;
    char *nome;
    char *cidade;
    int capacidade;
    double avaliacao;
    char **tiposCozinha;
    int nCozinhas;
    int faixaPreco;
    Hora horarioAbertura;
    Hora horarioFechamento;
    Data dataAbertura;
    bool aberto;
} Restaurante;

Restaurante *restauranteFromCsv(const char *csvString) {
    int qtdCampos = 1;

    // Conta quantos campos tem
    for (int i = 0; csvString[i] != '\0'; i++) {
        if (csvString[i] == ',')
            qtdCampos++;
    }

    // Aloca array de campos
    char **campos = malloc(sizeof(char*) * qtdCampos);
    for (int i = 0; i < qtdCampos; i++) campos[i] = NULL;

    char buffer[4096];
    int campoAtual = 0;
    int bufPos = 0;

    // Mapeia caractere por caractere e atribui em uma lista temporaria
    for (int i = 0; csvString[i] != '\0'; i++) {
        char cAtual = csvString[i];
        if (cAtual == ',') {
            // finaliza campo atual
            buffer[bufPos] = '\0';
            // aloca e copia
            int len = bufPos;
            campos[campoAtual] = malloc(len + 1);
            for (int k = 0; k <= len; k++) campos[campoAtual][k] = buffer[k];
            // reset buffer
            bufPos = 0;
            campoAtual++;
        } else {
            buffer[bufPos++] = cAtual;
        }
        // atualiza campo em construção (como em Java)
        buffer[bufPos] = '\0';
    }
    // finaliza último campo
    buffer[bufPos] = '\0';
    int lenLast = bufPos;
    campos[campoAtual] = malloc(lenLast + 1);
    for (int k = 0; k <= lenLast; k++) campos[campoAtual][k] = buffer[k];

    Restaurante *rest = malloc(sizeof(Restaurante));
    // Converte campos para os atributos (seguindo a mesma ordem do Java)
    rest->id = campos[0] ? atoi(campos[0]) : 0;
    rest->nome = campos[1] ? campos[1] : malloc(1);
    if (!campos[1]) rest->nome[0] = '\0';
    rest->cidade = campos[2] ? campos[2] : malloc(1);
    if (!campos[2]) rest->cidade[0] = '\0';
    rest->capacidade = campos[3] ? atoi(campos[3]) : 0;
    rest->avaliacao = campos[4] ? atof(campos[4]) : 0.0;

    // Repete mesma ideia para separar os tipos de cozinha
    int qtdCozinhas = 1;
    if (campos[5]) {
        for (int i = 0; campos[5][i] != '\0'; i++) {
            if (campos[5][i] == ';') qtdCozinhas++;
        }
    }

    rest->tiposCozinha = NULL;
    rest->nCozinhas = 0;
    if (campos[5]) {
        rest->tiposCozinha = malloc(sizeof(char*) * qtdCozinhas);
        int cAtual = 0;
        int bPos = 0;
        char bufC[4096];
        for (int i = 0; ; i++) {
            char ch = campos[5][i];
            if (ch == ';' || ch == '\0') {
                bufC[bPos] = '\0';
                int l = bPos;
                rest->tiposCozinha[cAtual] = malloc(l + 1);
                for (int k = 0; k <= l; k++) rest->tiposCozinha[cAtual][k] = bufC[k];
                cAtual++;
                bPos = 0;
                if (ch == '\0') break;
            } else {
                bufC[bPos++] = ch;
            }
        }
        rest->nCozinhas = cAtual;
    }

    // Repete mesma ideia para horários
    char horarios0[32] = "";
    char horarios1[32] = "";
    if (campos[7]) {
        int hCampoAtual = 0;
        int hPos = 0;
        for (int i = 0; ; i++) {
            char ch = campos[7][i];
            if (ch == '-' || ch == '\0') {
                if (hCampoAtual == 0) {
                    horarios0[hPos] = '\0';
                } else {
                    horarios1[hPos] = '\0';
                }
                hCampoAtual++;
                hPos = 0;
                if (ch == '\0') break;
            } else {
                if (hCampoAtual == 0) horarios0[hPos++] = ch;
                else horarios1[hPos++] = ch;
            }
        }
    }

    rest->faixaPreco = 0;
    if (campos[6]) {
        int p = 0; while (campos[6][p] != '\0') { rest->faixaPreco++; p++; }
    }

    if (campos[7]) rest->horarioAbertura = parseHora(horarios0);
    else rest->horarioAbertura = (Hora){0,0};
    if (campos[7]) rest->horarioFechamento = parseHora(horarios1);
    else rest->horarioFechamento = (Hora){0,0};

    if (campos[8]) rest->dataAbertura = parseData(campos[8]); else rest->dataAbertura = (Data){0,0,0};

    if (campos[9]) {
        if (strcmp(campos[9], "true") == 0 || strcmp(campos[9], "True") == 0 || strcmp(campos[9], "1") == 0)
            rest->aberto = true;
        else
            rest->aberto = false;
    } else rest->aberto = false;

    // libera memória dos campos que já foram copiados para os membros que precisam (nome e cidade already point to campos entries)
    // Observação: campos[1] e campos[2] já foram atribuadas diretamente a rest->nome/rest->cidade
    // liberamos o array de ponteiros, mas não as strings apontadas por campos[1], campos[2], campos[5], etc., pois elas são usadas
    free(campos);
    return rest;
}

void imprimirRestaurante(Restaurante *r) {
    char h1[6], h2[6], d[16];
    formatHora(r->horarioAbertura, h1);
    formatHora(r->horarioFechamento, h2);
    formatData(r->dataAbertura, d);
    printf("[%d ## %s ## %s ## %d ## %.1f ## [", r->id, r->nome, r->cidade, r->capacidade, r->avaliacao);
    for (int i=0;i<r->nCozinhas;i++){
        printf("%s", r->tiposCozinha[i]);
        if (i != r->nCozinhas-1) printf(",");
    }
    printf("] ## ");
    for (int i=0;i<r->faixaPreco;i++) putchar('$');
    printf(" ## %s-%s ## %s ## %s]\n", h1, h2, d, r->aberto?"true":"false");
}

typedef struct {
    int tamanho;
    Restaurante *restaurantes[1000];
} ColecaoRestaurante;

ColecaoRestaurante *colecaoNova() {
    ColecaoRestaurante *c = malloc(sizeof(ColecaoRestaurante));
    c->tamanho = 0;
    for (int i=0;i<1000;i++) c->restaurantes[i] = NULL;
    return c;
}

void colecaoPush(ColecaoRestaurante *c, Restaurante *r) {
    if (c->tamanho < 1000) {
        c->restaurantes[c->tamanho++] = r;
    }
}

ColecaoRestaurante *lerCsv(const char *path) {
    FILE *f = fopen(path, "r");
    if (!f) return NULL;
    ColecaoRestaurante *c = colecaoNova();
    char buffer[4096];
    // pular header
    if (!fgets(buffer, sizeof(buffer), f)) { fclose(f); return c; }
    while (fgets(buffer, sizeof(buffer), f)) {
        Restaurante *r = restauranteFromCsv(buffer);
        if (r) colecaoPush(c, r);
    }
    fclose(f);
    return c;
}

Restaurante *getRestauranteByNomeSequencial(ColecaoRestaurante *c, const char *nome) {
    for (int i=0;i<c->tamanho;i++) {
        comparacoes++;
        if (strcmp(c->restaurantes[i]->nome, nome) == 0) return c->restaurantes[i];
    }
    return NULL;
}

Restaurante *getRestauranteByIdSequencial(ColecaoRestaurante *c, int id) {
    if (id <= 0) {
        printf("O ID é inválido\n");
        return NULL;
    }
    for (int i=0;i<c->tamanho;i++) {
        if (c->restaurantes[i]->id == id) return c->restaurantes[i];
    }
    printf("O ID não corresponde a nenhum restaurante\n");
    return NULL;
}

Restaurante *getRestauranteByIdBinario(ColecaoRestaurante *c, int id) {
    int esq = 0, dir = c->tamanho - 1;
    if (id <= 0) { printf("O ID é inválido\n"); return NULL; }
    while (esq <= dir) {
        int media = (esq + dir) / 2;
        if (c->restaurantes[media]->id == id) return c->restaurantes[media];
        else if (c->restaurantes[media]->id > id) dir = media - 1;
        else esq = media + 1;
    }
    printf("O ID não corresponde a nenhum restaurante\n");
    return NULL;
}

void swap(ColecaoRestaurante *c, int i, int j) {
    Restaurante *aux = c->restaurantes[i];
    c->restaurantes[i] = c->restaurantes[j];
    c->restaurantes[j] = aux;
    movimentacoes += 3;
}

Restaurante *getRestauranteByIndex(ColecaoRestaurante *c, int i) {
    if (i < 0 || i >= c->tamanho) return NULL;
    return c->restaurantes[i];
}

void printAll(ColecaoRestaurante *c) {
    for (int i=0;i<c->tamanho;i++) {
        imprimirRestaurante(c->restaurantes[i]);
    }
}

bool isFim(const char *str) {
    return (str[0]=='F' && str[1]=='I' && str[2]=='M' && str[3]=='\0');
}

int main(int argc, char **argv) {
    // Ler o arquivo de restaurantes (espera-se o arquivo em 02/restaurantes.csv)
    ColecaoRestaurante *colecao = lerCsv("02/restaurantes.csv");
    if (!colecao) {
        fprintf(stderr, "Erro ao abrir arquivo de restaurantes\n");
        return 1;
    }

    // Ler ids de stdin (pub.in) até -1 e realizar busca binária por id
    int id;
    while (scanf("%d", &id) == 1) {
        if (id == -1) break;
        Restaurante *r = getRestauranteByIdBinario(colecao, id);
        if (r != NULL) {
            imprimirRestaurante(r);
        }
        // se r == NULL, a própria função já imprime mensagem de erro
    }

    return 0;
}
