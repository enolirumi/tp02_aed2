/**
 * Trabalho Prático 02 - Estruturas de Dados e Algoritmos II
 * Aluno: Enzo de Oliveira Russo Miranda
 * Matrícula: 892668
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Contadores semelhantes à classe Globals usada no Java
long long comparacoes = 0;
long long movimentacoes = 0;

void concatena(char *destino, const char *origem)
{
    while (*destino != '\0')
    {
        destino++;
    }

    while (*origem != '\0')
    {
        *destino = *origem;
        destino++;
        origem++;
    }

    *destino = '\0';
}

int meu_strlen(const char *s)
{
    int i = 0;
    while (s[i] != '\0')
        i++;
    return i;
}

void meu_strncpy(char *dest, const char *src, int n)
{
    int i;
    for (i = 0; i < n && src[i] != '\0'; i++)
    {
        dest[i] = src[i];
    }
    dest[i] = '\0';
}

typedef struct
{
    int hora;
    int minuto;
} Hora;

Hora parseHora(const char *str)
{
    Hora h = {0, 0};
    int hh = 0, mm = 0;
    sscanf(str, "%d:%d", &hh, &mm);
    h.hora = hh;
    h.minuto = mm;
    return h;
}

void formatHora(Hora h, char *out)
{
    sprintf(out, "%02d:%02d", h.hora, h.minuto);
}

int compararHora(Hora a, Hora b)
{
    if (a.hora != b.hora)
        return a.hora - b.hora;
    return a.minuto - b.minuto;
}

typedef struct
{
    int dia;
    int mes;
    int ano;
} Data;

Data parseData(const char *str)
{
    Data d = {0, 0, 0};
    int y = 0, m = 0, day = 0;
    sscanf(str, "%d-%d-%d", &y, &m, &day);
    d.dia = day;
    d.mes = m;
    d.ano = y;
    return d;
}

void formatData(Data d, char *out)
{
    sprintf(out, "%02d/%02d/%04d", d.dia, d.mes, d.ano);
}

int compararData(Data a, Data b)
{
    if (a.ano != b.ano)
        return a.ano - b.ano;
    if (a.mes != b.mes)
        return a.mes - b.mes;
    return a.dia - b.dia;
}

typedef struct
{
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

Restaurante *restauranteFromCsv(const char *csvString)
{
    char linha[4096];
    int len = meu_strlen(csvString);
    meu_strncpy(linha, csvString, len);

    if (len > 0 && (linha[len - 1] == '\n' || linha[len - 1] == '\r'))
        linha[len - 1] = '\0';
    if (len > 1 && (linha[len - 2] == '\n' || linha[len - 2] == '\r'))
        linha[len - 2] = '\0';

    char *campos[10];
    int inicio = 0, campoIdx = 0;
    int tamLinha = meu_strlen(linha);

    for (int i = 0; i <= tamLinha && campoIdx < 10; i++)
    {
        if (linha[i] == ',' || linha[i] == '\0')
        {
            int tamCampo = i - inicio;
            campos[campoIdx] = (char *)malloc(tamCampo + 1);
            meu_strncpy(campos[campoIdx], &linha[inicio], tamCampo);
            campos[campoIdx][tamCampo] = '\0';
            inicio = i + 1;
            campoIdx++;
        }
    }

    Restaurante *rest = (Restaurante *)malloc(sizeof(Restaurante));

    rest->id = atoi(campos[0]);
    rest->nome = campos[1];
    rest->cidade = campos[2];
    rest->capacidade = atoi(campos[3]);
    rest->avaliacao = atof(campos[4]);

    int nC = 1;
    for (int i = 0; campos[5][i] != '\0'; i++)
        if (campos[5][i] == ';')
            nC++;

    rest->nCozinhas = nC;
    rest->tiposCozinha = (char **)malloc(sizeof(char *) * nC);

    int cInicio = 0, cIdx = 0;
    int tamCozinhaTotal = meu_strlen(campos[5]);
    for (int i = 0; i <= tamCozinhaTotal; i++)
    {
        if (campos[5][i] == ';' || campos[5][i] == '\0')
        {
            int t = i - cInicio;
            rest->tiposCozinha[cIdx] = (char *)malloc(t + 1);
            meu_strncpy(rest->tiposCozinha[cIdx], &campos[5][cInicio], t);
            cInicio = i + 1;
            cIdx++;
        }
    }

    rest->faixaPreco = meu_strlen(campos[6]);

    char hA[10], hF[10];
    if (sscanf(campos[7], "%[^-]-%s", hA, hF) == 2)
    {
        rest->horarioAbertura = parseHora(hA);
        rest->horarioFechamento = parseHora(hF);
    }
    else
    {
        rest->horarioAbertura = (Hora){0, 0};
        rest->horarioFechamento = (Hora){0, 0};
    }

    rest->dataAbertura = parseData(campos[8]);
    rest->aberto = (strcmp(campos[9], "true") == 0 || strcmp(campos[9], "1") == 0);

    for (int i = 0; i < 10; i++)
    {
        if (i != 1 && i != 2 && i != 5)
            free(campos[i]);
    }
    free(campos[5]);

    return rest;
}

void imprimirRestaurante(Restaurante *r)
{
    char h1[6], h2[6], d[16];
    formatHora(r->horarioAbertura, h1);
    formatHora(r->horarioFechamento, h2);
    formatData(r->dataAbertura, d);
    printf("[%d ## %s ## %s ## %d ## %.1f ## [", r->id, r->nome, r->cidade, r->capacidade, r->avaliacao);
    for (int i = 0; i < r->nCozinhas; i++)
    {
        printf("%s", r->tiposCozinha[i]);
        if (i != r->nCozinhas - 1)
            printf(",");
    }
    printf("] ## ");
    for (int i = 0; i < r->faixaPreco; i++)
        putchar('$');
    printf(" ## %s-%s ## %s ## %s]\n", h1, h2, d, r->aberto ? "true" : "false");
}

typedef struct
{
    int tamanho;
    Restaurante *restaurantes[1000];
} ColecaoRestaurante;

ColecaoRestaurante *colecaoNova()
{
    ColecaoRestaurante *c = malloc(sizeof(ColecaoRestaurante));
    c->tamanho = 0;
    for (int i = 0; i < 1000; i++)
        c->restaurantes[i] = NULL;
    return c;
}

void colecaoPush(ColecaoRestaurante *c, Restaurante *r)
{
    if (c->tamanho < 1000)
    {
        c->restaurantes[c->tamanho++] = r;
    }
}

ColecaoRestaurante *lerCsv(const char *path)
{
    FILE *f = fopen(path, "r");
    if (!f)
        return NULL;
    ColecaoRestaurante *c = colecaoNova();
    char buffer[4096];
    // pular header
    if (!fgets(buffer, sizeof(buffer), f))
    {
        fclose(f);
        return c;
    }
    while (fgets(buffer, sizeof(buffer), f))
    {
        Restaurante *r = restauranteFromCsv(buffer);
        if (r)
            colecaoPush(c, r);
    }
    fclose(f);
    return c;
}

Restaurante *getRestauranteByNomeSequencial(ColecaoRestaurante *c, const char *nome)
{
    for (int i = 0; i < c->tamanho; i++)
    {
        comparacoes++;
        if (strcmp(c->restaurantes[i]->nome, nome) == 0)
            return c->restaurantes[i];
    }
    return NULL;
}

Restaurante *getRestauranteByIdSequencial(ColecaoRestaurante *c, int id)
{
    if (id <= 0)
    {
        printf("O ID é inválido\n");
        return NULL;
    }
    for (int i = 0; i < c->tamanho; i++)
    {
        if (c->restaurantes[i]->id == id)
            return c->restaurantes[i];
    }
    printf("O ID não corresponde a nenhum restaurante\n");
    return NULL;
}

Restaurante *getRestauranteByIdBinario(ColecaoRestaurante *c, int id)
{
    int esq = 0, dir = c->tamanho - 1;
    if (id <= 0)
    {
        printf("O ID é inválido\n");
        return NULL;
    }
    while (esq <= dir)
    {
        int media = (esq + dir) / 2;
        if (c->restaurantes[media]->id == id)
            return c->restaurantes[media];
        else if (c->restaurantes[media]->id > id)
            dir = media - 1;
        else
            esq = media + 1;
    }
    printf("O ID não corresponde a nenhum restaurante\n");
    return NULL;
}

Restaurante *getRestauranteByNomeBinario(ColecaoRestaurante *c, const char *nome)
{
    int esq = 0, dir = c->tamanho - 1;
    while (esq <= dir)
    {
        int media = (esq + dir) / 2;
        comparacoes++;
        int cmp = strcmp(c->restaurantes[media]->nome, nome);
        if (cmp == 0)
            return c->restaurantes[media];
        else if (cmp > 0)
            dir = media - 1;
        else
            esq = media + 1;
    }
    return NULL;
}

void swap(ColecaoRestaurante *c, int i, int j)
{
    Restaurante *aux = c->restaurantes[i];
    c->restaurantes[i] = c->restaurantes[j];
    c->restaurantes[j] = aux;
    movimentacoes += 3;
}

Restaurante *getRestauranteByIndex(ColecaoRestaurante *c, int i)
{
    if (i < 0 || i >= c->tamanho)
        return NULL;
    return c->restaurantes[i];
}

void printAll(ColecaoRestaurante *c)
{
    for (int i = 0; i < c->tamanho; i++)
    {
        imprimirRestaurante(c->restaurantes[i]);
    }
}

void selectionSort(ColecaoRestaurante *c) {
    for (int i = 0; i < c->tamanho - 1; i++) {
        int indiceMenor = i;
        
        for (int j = i + 1; j < c->tamanho; j++) {
            comparacoes++;
            if (strcmp(c->restaurantes[j]->nome, c->restaurantes[indiceMenor]->nome) < 0) {
                indiceMenor = j;
            }
        }
        
        if (indiceMenor != i) {
            swap(c, i, indiceMenor); // swap já incrementa movimentacoes += 3
        }
    }
}

void quicksort(ColecaoRestaurante *c, int esq, int dir)
{
    if (esq < dir)
    {
        int i = esq, j = dir;
        Restaurante *pivo = c->restaurantes[(esq + dir) / 2];

        while (i <= j)
        {
            comparacoes++;
            while (c->restaurantes[i]->avaliacao < pivo->avaliacao ||
                  (c->restaurantes[i]->avaliacao == pivo->avaliacao &&
                   strcmp(c->restaurantes[i]->nome, pivo->nome) < 0))
            {
                i++;
                comparacoes++;
            }

            comparacoes++;
            while (c->restaurantes[j]->avaliacao > pivo->avaliacao ||
                  (c->restaurantes[j]->avaliacao == pivo->avaliacao &&
                   strcmp(c->restaurantes[j]->nome, pivo->nome) > 0))
            {
                j--;
                comparacoes++;
            }

            if (i <= j)
            {
                swap(c, i, j);
                i++;
                j--;
            }
        }

        quicksort(c, esq, j);
        quicksort(c, i, dir);
    }
}

// Só para facilitar a criação do arquivo de log (Espero que não tenha problema)
void logWriter() {
    FILE *log = fopen("892668_quicksort.txt", "w");
    fprintf(log, "Quantidade de comparações: %lld comparações\n", comparacoes);
    fprintf(log, "Quantidade de movimentações: %lld movimentações\n", movimentacoes);
    fclose(log);
}

int isFim(const char *str)
{
    return (str[0] == 'F' && str[1] == 'I' && str[2] == 'M' && str[3] == '\0');
}

int main(int argc, char **argv)
{
    char tmp_path[512] = {0};

// Pegar o caminho para tmp (peguei da internet)
    #ifdef _WIN32
        meu_strncpy(tmp_path, getenv("TEMP"));
    #else
        char *tmpdir = getenv("TMPDIR");
        if (tmpdir == NULL)
            meu_strncpy(tmp_path, "/tmp", 4);
        else
            meu_strncpy(tmp_path, tmpdir, meu_strlen(tmpdir));
    #endif

    // Ler o arquivo de restaurantes
    concatena(tmp_path, "/restaurantes.csv");
    //printf("Lendo arquivo de restaurantes em: %s\n", tmp_path);
    ColecaoRestaurante *colecao = lerCsv(tmp_path);
    if (!colecao)
    {
        fprintf(stderr, "Erro ao abrir arquivo de restaurantes\n");
        return 1;
    }

    ColecaoRestaurante *selecionados = colecaoNova();

    int id;
    while (scanf("%d", &id) == 1) {
        if (id == -1) break;
        Restaurante *r = getRestauranteByIdSequencial(colecao, id);
        if (r != NULL)
            colecaoPush(selecionados, r);
    }

    quicksort(selecionados, 0, selecionados->tamanho - 1);
    printAll(selecionados);

    logWriter();
    return 0;
}
