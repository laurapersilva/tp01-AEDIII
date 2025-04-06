package TP01.app;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import TP01.model.*;
import TP01.service.*;
import TP01.controller.*;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static Arquivo<Episodio> arqEpisodios;
    static Arquivo<Serie> arqSerie;

    public static void menu() {
        System.out.println("PUCFlix 1.0 \n ----------- \n> Início \n 1) Séries \n 2) Episódios \n 3) Atores \n 0) Sair");
    }

    public static void menuSerie() throws Exception {
        int op;
        do {
            System.out.println("PUCFlix \n 1.0----------->  \n Início > Séries \n 1) Incluir \n 2) Buscar \n 3) Alterar \n 4) Excluir \n 0) Retornar ao menu anterior");
            op = sc.nextInt();
            sc.nextLine();
            switch (op) {
                case 1 -> {
                    System.out.println("Crie sua série: ");
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Ano de Lançamento: ");
                    long AnoLancamento = sc.nextLong();
                    System.out.print("Tamanho da Sinopse: ");
                    int SinopseSize = sc.nextInt();
                    sc.nextLine(); // Limpar buffer
                    System.out.print("Sinopse: ");
                    String Sinopse = sc.nextLine();
                    System.out.println("Escolha seu streaming: \n 1) Netflix \n 2) Amazon Prime Video \n 3) Max \n 4) Disney Plus \n 5) Globo Play \n 6) Star Plus");
                    int streaming = sc.nextInt();
                    String Streaming = switch (streaming) {
                        case 1 -> "Netflix";
                        case 2 -> "Amazon Prime Video";
                        case 3 -> "Max";
                        case 4 -> "Disney Plus";
                        case 5 -> "Globo Play";
                        case 6 -> "Star Plus";
                        default -> "Desconhecido";
                    };
                    Serie S = new Serie(nome, AnoLancamento, SinopseSize, Sinopse, Streaming);
                    arqSerie.create(S);
                }
                case 2 -> {
                    System.out.println("Digite o id da série que deseja encontrar: ");
                    int idS = sc.nextInt();
                    Serie S = arqSerie.read(idS);
                    if (S != null) {
                        System.out.println("Série encontrada: ");
                        System.out.println(S.getTitulo());
                    } else {
                        System.out.println("Série não encontrada.");
                    }
                }
                case 3 -> {
                    System.out.print("Digite o ID da série que deseja alterar: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Ano de Lançamento: ");
                    long AnoLancamento = sc.nextLong();
                    System.out.print("Tamanho da Sinopse: ");
                    int SinopseSize = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Sinopse: ");
                    String Sinopse = sc.nextLine();
                    System.out.println("Escolha seu streaming: \n 1) Netflix \n 2) Amazon Prime Video \n 3) Max \n 4) Disney Plus \n 5) Globo Play \n 6) Star Plus");
                    int streaming = sc.nextInt();
                    String Streaming = switch (streaming) {
                        case 1 -> "Netflix";
                        case 2 -> "Amazon Prime Video";
                        case 3 -> "Max";
                        case 4 -> "Disney Plus";
                        case 5 -> "Globo Play";
                        case 6 -> "Star Plus";
                        default -> "Desconhecido";
                    };
                    Serie S = new Serie(nome, AnoLancamento, SinopseSize, Sinopse, Streaming);
                    S.setId(id);
                    boolean result = arqSerie.update(S);
                    System.out.println(result ? "Série atualizada com sucesso!" : "Erro ao atualizar série.");
                }
                case 4 -> {
                    System.out.print("Digite o id da série que deseja remover: ");
                    int idS = sc.nextInt();
                    boolean result = arqSerie.delete(idS);
                    System.out.println(result ? "Série deletada com sucesso!" : "Erro ao deletar série.");
                }
            }
        } while (op != 0);
    }

    public static void menuEpisodio() throws Exception {
        int op;
        do {
            System.out.println("PUCFlix \n 1.0----------->  \n Início > Episódios \n 1) Incluir \n 2) Buscar \n 3) Alterar \n 4) Excluir \n 0) Retornar ao menu anterior");
            op = sc.nextInt();
            sc.nextLine();
            switch (op) {
                case 1 -> {
                    System.out.println("Crie seu episódio: ");
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Temporada: ");
                    int temporada = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Escreva a data de lançamento (DD/MM/AAAA): ");
                    String data = sc.nextLine();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate DataL = LocalDate.parse(data, dtf);
                    System.out.print("Duração: ");
                    long duracao = sc.nextLong();

                    System.out.println("Séries disponíveis:");
                    int ultimoId = arqSerie.ultimoId();
                    for (int i = 0; i < ultimoId; i++) {
                        Serie S = arqSerie.read(i);
                        if (S != null) {
                            System.out.println("ID: " + S.getId() + " | Nome: " + S.getTitulo());
                        }
                    }

                    System.out.print("Escolha o id da série desejada: ");
                    int idSerie = sc.nextInt();
                    Episodio E = new Episodio(nome, temporada, DataL, duracao, idSerie);
                    arqEpisodios.create(E);
                }
                case 2 -> {
                    System.out.print("Digite o id do Episódio: ");
                    int idE = sc.nextInt();
                    Episodio E = arqEpisodios.read(idE);
                    if (E != null) {
                        System.out.println("Episódio encontrado: " + E.getTitulo());
                    } else {
                        System.out.println("Episódio não encontrado.");
                    }
                }
                case 3 -> {
                    System.out.print("Digite o ID do episódio: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Temporada: ");
                    int temporada = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Escreva a data de lançamento (DD/MM/AAAA): ");
                    String data = sc.nextLine();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate DataL = LocalDate.parse(data, dtf);
                    System.out.print("Duração: ");
                    long duracao = sc.nextLong();

                    System.out.println("Séries disponíveis:");
                    int ultimoId = arqSerie.ultimoId();
                    for (int i = 0; i < ultimoId; i++) {
                        Serie S = arqSerie.read(i);
                        if (S != null) {
                            System.out.println("ID: " + S.getId() + " | Nome: " + S.getTitulo());
                        }
                    }

                    System.out.print("Escolha o id da série desejada: ");
                    int idSerie = sc.nextInt();
                    Episodio E = new Episodio(nome, temporada, DataL, duracao, idSerie);
                    E.setId(id);
                    arqEpisodios.update(E);
                }
                case 4 -> {
                    System.out.print("Digite o id do episódio: ");
                    int idE = sc.nextInt();
                    boolean result = arqEpisodios.delete(idE);
                    System.out.println(result ? "Episódio deletado com sucesso!" : "Erro ao deletar episódio.");
                }
            }
        } while (op != 0);
    }

    public static void menuAtores() {
        System.out.println("PUCFlix \n 1.0----------->  \n Início > Atores \n 1) Incluir \n 2) Buscar \n 3) Alterar \n 4) Excluir \n 0) Retornar ao menu anterior");
        int op = sc.nextInt();
        while (op != 0) {
            switch (op) {
                case 1 -> {
                    // Implementar criação de ator
                }
                case 2 -> {
                    // Implementar busca de ator
                }
                case 3 -> {
                    // Implementar atualização de ator
                }
                case 4 -> {
                    // Implementar exclusão de ator
                }
            }
            System.out.println("PUCFlix \n Início > Atores \n 1) Incluir \n 2) Buscar \n 3) Alterar \n 4) Excluir \n 0) Retornar ao menu anterior");
            op = sc.nextInt();
        }
    }

    public static void main(String[] args) throws Exception {
        arqEpisodios = new Arquivo<>("episodios.db", Episodio.class);
        arqSerie = new Arquivo<>("series.db", Serie.class);

        // Inicializa os controladores
        ControleSerie controleSerie = new ControleSerie(sc, arqSerie, arqEpisodios);
        ControleEpisodio controleEpisodio = new ControleEpisodio(sc, arqEpisodios, arqSerie);
        ControleAtor controleAtor = new ControleAtor(sc);

        int opcao;
        do {
            menu();
            opcao = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1 -> controleSerie.menuSerie();
                case 2 -> controleEpisodio.menuEpisodio();
                case 3 -> controleAtor.menuAtores();
            }
        } while (opcao != 0);

        System.out.println("Obrigado por usar o PUCFlix!");
        sc.close();
    }
}
