package TP01.controller;

import java.util.ArrayList;
import java.util.Scanner;
import TP01.model.*;
import TP01.service.*;
import TP01.view.*;

public class ControleSerie {
    private Scanner sc;
    private Arquivo<Serie> arqSerie;
    private VisaoSerie visaoSerie;
    private RelacionamentoSerieEpisodio relacionamento;

    public ControleSerie(Scanner sc, Arquivo<Serie> arqSerie, Arquivo<Episodio> arqEpisodios) {
        this.sc = sc;
        this.arqSerie = arqSerie;
        this.visaoSerie = new VisaoSerie(sc);
        this.relacionamento = new RelacionamentoSerieEpisodio(arqSerie, arqEpisodios);
    }

    public void menuSerie() throws Exception {
        int op;
        do {
            System.out.println("\nPUCFlix 1.0\n-----------\n> Início > Séries");
            System.out.println("1) Incluir");
            System.out.println("2) Buscar");
            System.out.println("3) Alterar");
            System.out.println("4) Excluir");
            System.out.println("5) Ver episódios por temporada");
            System.out.println("0) Retornar ao menu anterior");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> incluirSerie();
                case 2 -> buscarSerie();
                case 3 -> alterarSerie();
                case 4 -> excluirSerie();
                case 5 -> verEpisodiosPorTemporada();
            }
        } while (op != 0);
    }

    private void incluirSerie() throws Exception {
        Serie serie = visaoSerie.leSerie();
        int id = arqSerie.create(serie);
        System.out.println("Série criada com sucesso! ID: " + id);
    }

    private void buscarSerie() throws Exception {
        int id = visaoSerie.leIdSerie();
        Serie serie = arqSerie.read(id);
        visaoSerie.mostraSerie(serie);
    }

    private void alterarSerie() throws Exception {
        int id = visaoSerie.leIdSerie();
        Serie serie = visaoSerie.leSerie();
        serie.setId(id);
        boolean resultado = arqSerie.update(serie);
        System.out.println(resultado ? "Série atualizada com sucesso!" : "Erro ao atualizar série.");
    }

    private void excluirSerie() throws Exception {
        int id = visaoSerie.leIdSerie();

        // Verificar se existem episódios vinculados à série
        if (relacionamento.serieTemEpisodios(id)) {
            System.out.println("Não é possível excluir! Existem episódios vinculados a esta série.");
            return;
        }

        boolean resultado = arqSerie.delete(id);
        System.out.println(resultado ? "Série excluída com sucesso!" : "Erro ao excluir série.");
    }

    private void verEpisodiosPorTemporada() throws Exception {
        int idSerie = visaoSerie.leIdSerie();
        Serie serie = arqSerie.read(idSerie);

        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        int temporada = visaoSerie.leTemporada();
        ArrayList<Episodio> episodios = relacionamento.getEpisodiosPorTemporada(idSerie, temporada);
        visaoSerie.mostraEpisodiosPorTemporada(episodios, temporada);
    }
}