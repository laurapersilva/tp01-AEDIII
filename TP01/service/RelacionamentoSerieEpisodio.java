package TP01.service;

import java.io.IOException;
import TP01.model.*;
import java.util.ArrayList;

public class RelacionamentoSerieEpisodio {
    private Arquivo<Episodio> arqEpisodios;
    private Arquivo<Serie> arqSerie;
    // Aqui viria a implementação da árvore B+ para armazenar os relacionamentos

    public RelacionamentoSerieEpisodio(Arquivo<Serie> arqSerie, Arquivo<Episodio> arqEpisodios) {
        this.arqSerie = arqSerie;
        this.arqEpisodios = arqEpisodios;
    }

    // Verificar se uma série tem episódios
    public boolean serieTemEpisodios(int idSerie) throws Exception {
        // Verificar episódios até encontrar um com o id da série
        int ultimoId = arqEpisodios.ultimoId();
        for (int i = 1; i <= ultimoId; i++) {
            Episodio ep = arqEpisodios.read(i);
            if (ep != null && ep.getSerieId() == idSerie) {
                return true;
            }
        }
        return false;
    }

    // Obter todos os episódios de uma série
    public ArrayList<Episodio> getEpisodiosDaSerie(int idSerie) throws Exception {
        ArrayList<Episodio> episodios = new ArrayList<>();
        int ultimoId = arqEpisodios.ultimoId();
        for (int i = 1; i <= ultimoId; i++) {
            Episodio ep = arqEpisodios.read(i);
            if (ep != null && ep.getSerieId() == idSerie) {
                episodios.add(ep);
            }
        }
        return episodios;
    }

    // Obter episódios de uma temporada específica
    public ArrayList<Episodio> getEpisodiosPorTemporada(int idSerie, int temporada) throws Exception {
        ArrayList<Episodio> episodios = new ArrayList<>();
        int ultimoId = arqEpisodios.ultimoId();
        for (int i = 1; i <= ultimoId; i++) {
            Episodio ep = arqEpisodios.read(i);
            if (ep != null && ep.getSerieId() == idSerie && ep.getNumTemporada() == temporada) {
                episodios.add(ep);
            }
        }
        return episodios;
    }
}