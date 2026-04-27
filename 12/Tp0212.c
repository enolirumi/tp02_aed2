/**
 * Trabalho Prático 02 - Estruturas de Dados e Algoritmos II
 * Aluno: Enzo de Oliveira Russo Miranda
 * Matrícula: 892668
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// --- Estruturas e Globais ---
long long comparacoes = 0;
long long movimentacoes = 0;

typedef struct { int hora; int minuto; } Hora;
typedef struct { int dia; int mes; int ano; } Data;

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
    int aberto;
} Restaurante;

// --- Célula e Pilha Flexível ---
typedef struct Celula {
    Restaurante *elemento;
    struct Celula *prox;
} Celula;

typedef struct {
    Celula *topo;
    int tamanho;
} Pilha;

Pilha* novaPilha() {
    Pilha *p = (Pilha*)malloc(sizeof(Pilha));
    p->topo = NULL;
    p->tamanho = 0;
    return p;
}

void empilhar(Pilha *p, Restaurante *r) {
    Celula *tmp = (Celula*)malloc(sizeof(Celula));
    tmp->elemento = r;
    tmp->prox = p->topo;
    p->topo = tmp;
    p->tamanho++;
}

Restaurante* desempilhar(Pilha *p) {
    if (p->topo == NULL) return NULL;
    Celula *tmp = p->topo;
    Restaurante *r = tmp->elemento;
    p->topo = p->topo->prox;
    free(tmp);
    p->tamanho--;
    return r;
}

// --- Funções de Apoio (Strings e Formatação) ---
void meu_strncpy(char *dest, const char *src, int n) {
    int i;
    for (i = 0; i < n && src[i] != '\0'; i++) dest[i] = src[i];
    dest[i] = '\0';
}

int meu_strlen(const char *s) {
    int i = 0;
    while (s[i] != '\0') i++;
    return i;
}

void formatHora(Hora h, char *out) { sprintf(out, "%02d:%02d", h.hora, h.minuto); }
void formatData(Data d, char *out) { sprintf(out, "%02d/%02d/%04d", d.dia, d.mes, d.ano); }

void imprimirRestaurante(Restaurante *r) {
    char h1[6], h2[6], d[16];
    formatHora(r->horarioAbertura, h1);
    formatHora(r->horarioFechamento, h2);
    formatData(r->dataAbertura, d);
    printf("[%d ## %s ## %s ## %d ## %.1f ## [", r->id, r->nome, r->cidade, r->capacidade, r->avaliacao);
    for (int i = 0; i < r->nCozinhas; i++) {
        printf("%s%s", r->tiposCozinha[i], (i != r->nCozinhas - 1) ? "," : "");
    }
    printf("] ## ");
    for (int i = 0; i < r->faixaPreco; i++) putchar('$');
    printf(" ## %s-%s ## %s ## %s]\n", h1, h2, d, r->aberto ? "true" : "false");
}

// Impressão recursiva do topo para a base (conforme pub.out)
void mostrarPilha(Celula *c) {
    if (c == NULL) return;
    imprimirRestaurante(c->elemento);
    mostrarPilha(c->prox);
}

static void rtrim(char *s) {
    int len = meu_strlen(s);
    while (len > 0 && (s[len - 1] == '\n' || s[len - 1] == '\r')) {
        s[--len] = '\0';
    }
}

// --- Parsing do CSV ---
Restaurante* restauranteFromCsv(char *linha) {
    Restaurante *r = (Restaurante*)malloc(sizeof(Restaurante));
    rtrim(linha);
    
    // Como o CSV tem campos vazios ou complexos, o tratamento deve ser cuidadoso
    // Para simplificar e manter a compatibilidade com seu código original:
    char *campos[10];
    int c = 0;
    int start = 0;
    int len = meu_strlen(linha);

    for(int i = 0; i <= len && c < 10; i++) {
        if(linha[i] == ',' || linha[i] == '\0') {
            int clen = i - start;
            campos[c] = (char*)malloc(clen + 1);
            meu_strncpy(campos[c], &linha[start], clen);
            campos[c][clen] = '\0';
            rtrim(campos[c]);
            start = i + 1;
            c++;
        }
    }

    r->id = atoi(campos[0]);
    r->nome = strdup(campos[1]);
    r->cidade = strdup(campos[2]);
    r->capacidade = atoi(campos[3]);
    r->avaliacao = atof(campos[4]);

    // Cozinhas
    int n = 1;
    for(int i=0; campos[5][i]; i++) if(campos[5][i] == ';') n++;
    r->nCozinhas = n;
    r->tiposCozinha = (char**)malloc(sizeof(char*) * n);
    char *cj = strdup(campos[5]);
    char *tok = strtok(cj, ";");
    int k = 0;
    while(tok) { r->tiposCozinha[k++] = strdup(tok); tok = strtok(NULL, ";"); }
    free(cj);

    r->faixaPreco = meu_strlen(campos[6]);
    sscanf(campos[7], "%d:%d-%d:%d", &r->horarioAbertura.hora, &r->horarioAbertura.minuto, &r->horarioFechamento.hora, &r->horarioFechamento.minuto);
    sscanf(campos[8], "%d-%d-%d", &r->dataAbertura.ano, &r->dataAbertura.mes, &r->dataAbertura.dia);
    r->aberto = (strcmp(campos[9], "true") == 0);

    for(int i=0; i<c; i++) free(campos[i]);
    return r;
}

// --- Main ---
int main() {
    Restaurante *db[1000];
    int dbSize = 0;
    FILE *f = NULL;
    const char *paths[] = {
        "/tmp/restaurantes.csv",
        "restaurantes.csv",
        "tmp/restaurantes.csv",
        "../tmp/restaurantes.csv",
        "../../tmp/restaurantes.csv",
        "../../../tmp/restaurantes.csv",
        NULL
    };
    for (int pi = 0; paths[pi] != NULL; pi++) {
        f = fopen(paths[pi], "r");
        if (f) break;
    }
    if (!f) {
        fprintf(stderr, "Erro: nao foi possivel abrir restaurantes.csv\n");
        return 1;
    }

    char buffer[1024];
    fgets(buffer, 1024, f); // Header
    while(fgets(buffer, 1024, f)) db[dbSize++] = restauranteFromCsv(buffer);
    fclose(f);

    Pilha *pilha = novaPilha();
    char entrada[50];

    // Entrada Inicial
    while(scanf("%s", entrada) && strcmp(entrada, "-1") != 0) {
        int id = atoi(entrada);
        for(int i=0; i<dbSize; i++) {
            if(db[i]->id == id) { empilhar(pilha, db[i]); break; }
        }
    }

    // Comandos
    int n;
    scanf("%d", &n);
    while(n--) {
        scanf("%s", entrada);
        if(entrada[0] == 'I') {
            int id;
            scanf("%d", &id);
            for(int i=0; i<dbSize; i++) {
                if(db[i]->id == id) { empilhar(pilha, db[i]); break; }
            }
        } else if(entrada[0] == 'R') {
            Restaurante *rem = desempilhar(pilha);
            if(rem) printf("(R)%s\n", rem->nome);
        }
    }

    mostrarPilha(pilha->topo);

    return 0;
}