package TP01.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import TP01.model.*;

public class VisaoEpisodio {
    private Scanner sc;

    public VisaoEpisodio(Scanner sc) {
        this.sc = sc;
    }

    public Episodio leEpisodio(int idSerie) {
        System.out.println("Crie seu episódio: ");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Temporada: ");
        int temporada = sc.nextInt();
        sc.nextLine(); // Limpar buffer

        LocalDate dataLancamento = null;
        boolean dataValida = false;

        while (!dataValida) {
            try {
                System.out.print("Data de lançamento (DD/MM/AAAA): ");
                String data = sc.nextLine();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataLancamento = LocalDate.parse(data, dtf);
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Tente novamente.");
            }
        }

        System.out.print("Duração (em minutos): ");
        long duracao = sc.nextLong();
        sc.nextLine(); // Limpar buffer

        return new Episodio(nome, temporada, dataLancamento, duracao, idSerie);
    }

    public void mostraEpisodio(Episodio ep) {
        if (ep == null) {
            System.out.println("Episódio não encontrado.");
            return;
        }

        System.out.println(ep.toString());
    }

    public void mostraListaEpisodios(ArrayList<Episodio> episodios) {
        if (episodios.isEmpty()) {
            System.out.println("Nenhum episódio encontrado.");
            return;
        }

        for (Episodio ep : episodios) {
            System.out.println(ep.toString());
        }
    }

    public int leIdEpisodio() {
        System.out.print("Digite o ID do episódio: ");
        int id = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        return id;
    }
}

