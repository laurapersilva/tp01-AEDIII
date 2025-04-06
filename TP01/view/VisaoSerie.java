package TP01.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import TP01.model.*;

public class VisaoSerie {
    private Scanner sc;

    public VisaoSerie(Scanner sc) {
        this.sc = sc;
    }

    public Serie leSerie() {
        System.out.println("Crie sua série: ");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Ano de Lançamento: ");
        long anoLancamento = sc.nextLong();
        System.out.print("Tamanho da Sinopse: ");
        int sinopseSize = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        System.out.print("Sinopse: ");
        String sinopse = sc.nextLine();

        System.out.println("Escolha seu streaming: \n 1) Netflix \n 2) Amazon Prime Video \n 3) Max \n 4) Disney Plus \n 5) Globo Play \n 6) Star Plus");
        int streamingOpcao = sc.nextInt();
        sc.nextLine(); // Limpar buffer

        String streaming = switch (streamingOpcao) {
            case 1 -> "Netflix";
            case 2 -> "Amazon Prime Video";
            case 3 -> "Max";
            case 4 -> "Disney Plus";
            case 5 -> "Globo Play";
            case 6 -> "Star Plus";
            default -> "Desconhecido";
        };

        return new Serie(nome, anoLancamento, sinopseSize, sinopse, streaming);
    }

    public void mostraSerie(Serie serie) {
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        System.out.println(serie.toString());
    }

    public void mostraEpisodiosPorTemporada(ArrayList<Episodio> episodios, int temporada) {
        System.out.println("\n==== Episódios da Temporada " + temporada + " ====");
        if (episodios.isEmpty()) {
            System.out.println("Nenhum episódio encontrado para esta temporada.");
            return;
        }

        for (Episodio ep : episodios) {
            System.out.println(ep.toString());
        }
    }

    public int leIdSerie() {
        System.out.print("Digite o ID da série: ");
        int id = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        return id;
    }

    public int leTemporada() {
        System.out.print("Digite a temporada desejada: ");
        int temporada = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        return temporada;
    }
}
