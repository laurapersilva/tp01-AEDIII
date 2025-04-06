package TP01.controller;

import java.util.ArrayList;
import java.util.Scanner;
import TP01.model.*;
import TP01.service.*;
import TP01.view.*;


public class ControleEpisodio {
    private Scanner sc;
    private Arquivo<Episodio> arqEpisodios;
    private Arquivo<Serie> arqSerie;
    private VisaoEpisodio visaoEpisodio;
    private RelacionamentoSerieEpisodio relacionamento;

    public ControleEpisodio(Scanner sc, Arquivo<Episodio> arqEpisodios, Arquivo<Serie> arqSerie) {
        this.sc = sc;
        this.arqEpisodios = arqEpisodios;
        this.arqSerie = arqSerie;
        this.visaoEpisodio = new VisaoEpisodio(sc);
        this.relacionamento = new RelacionamentoSerieEpisodio(arqSerie, arqEpisodios);
    }

    public void menuEpisodio() throws Exception {
        // Mostrar séries disponíveis
        System.out.println("\nSéries disponíveis:");
        int ultimoId = arqSerie.ultimoId();
        boolean temSeries = false;

        for (int i = 1; i <= ultimoId; i++) {
            Serie serie = arqSerie.read(i);
            if (serie != null) {
                System.out.println("ID: " + serie.getId() + " | Nome: " + serie.getTitulo());
                temSeries = true;
            }
        }

        if (!temSeries) {
            System.out.println("Não há séries cadastradas. Cadastre uma série primeiro.");
            return;
        }

        System.out.print("\nEscolha o ID da série para gerenciar seus episódios: ");
        int idSerie = sc.nextInt();
        sc.nextLine(); // Limpar buffer

        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        System.out.println("Gerenciando episódios da série: " + serie.getTitulo());

        int op;
        do {
            System.out.println("\nPUCFlix 1.0\n-----------\n> Início > Episódios > " + serie.getTitulo());
            System.out.println("1) Incluir");
            System.out.println("2) Buscar");
            System.out.println("3) Alterar");
            System.out.println("4) Excluir");
            System.out.println("5) Listar todos os episódios");
            System.out.println("0) Retornar ao menu anterior");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> incluirEpisodio(idSerie);
                case 2 -> buscarEpisodio();
                case 3 -> alterarEpisodio(idSerie);
                case 4 -> excluirEpisodio();
                case 5 -> listarEpisodiosDaSerie(idSerie);
            }
        } while (op != 0);
    }

    private void incluirEpisodio(int idSerie) throws Exception {
        Episodio episodio = visaoEpisodio.leEpisodio(idSerie);
        int id = arqEpisodios.create(episodio);
        System.out.println("Episódio criado com sucesso! ID: " + id);
    }

    private void buscarEpisodio() throws Exception {
        int id = visaoEpisodio.leIdEpisodio();
        Episodio episodio = arqEpisodios.read(id);
        visaoEpisodio.mostraEpisodio(episodio);
    }

    private void alterarEpisodio(int idSerie) throws Exception {
        int id = visaoEpisodio.leIdEpisodio();
        Episodio episodio = visaoEpisodio.leEpisodio(idSerie);
        episodio.setId(id);
        boolean resultado = arqEpisodios.update(episodio);
        System.out.println(resultado ? "Episódio atualizado com sucesso!" : "Erro ao atualizar episódio.");
    }

    private void excluirEpisodio() throws Exception {
        int id = visaoEpisodio.leIdEpisodio();
        boolean resultado = arqEpisodios.delete(id);
        System.out.println(resultado ? "Episódio excluído com sucesso!" : "Erro ao excluir episódio.");
    }

    private void listarEpisodiosDaSerie(int idSerie) throws Exception {
        ArrayList<Episodio> episodios = relacionamento.getEpisodiosDaSerie(idSerie);
        visaoEpisodio.mostraListaEpisodios(episodios);
    }
}
