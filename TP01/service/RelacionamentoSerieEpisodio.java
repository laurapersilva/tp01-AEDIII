package TP01.service;

import java.io.IOException;
import TP01.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Constructor;

public class RelacionamentoSerieEpisodio {
    private Arquivo<Episodio> arqEpisodios;
    private Arquivo<Serie> arqSerie;
    private ArvoreBMais<ParIDSerieEpisodio> arvoreRelacionamento;
    
    public RelacionamentoSerieEpisodio(Arquivo<Serie> arqSerie, Arquivo<Episodio> arqEpisodios) {
        this.arqSerie = arqSerie;
        this.arqEpisodios = arqEpisodios;
        
        try {
            // Inicializa a árvore B+ para relacionamento
            Constructor<ParIDSerieEpisodio> construtor = ParIDSerieEpisodio.class.getConstructor();
            this.arvoreRelacionamento = new ArvoreBMais<>(
                construtor,
                4, // Ordem da árvore (ajustável conforme necessário)
                "./TP01/Data/serieEpisodio.idx"
            );
        } catch (Exception e) {
            System.err.println("Erro ao inicializar árvore B+: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Adicionar um relacionamento entre série e episódio
    public boolean adicionarRelacionamento(int idSerie, int idEpisodio) throws Exception {
        // Verifica se a série existe
        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            return false;
        }
        
        // Verifica se o episódio existe
        Episodio episodio = arqEpisodios.read(idEpisodio);
        if (episodio == null) {
            return false;
        }
        
        // Atualiza o ID da série no episódio, se necessário
        if (episodio.getSerieId() != idSerie) {
            episodio.setSerieId(idSerie);
            arqEpisodios.update(episodio);
        }
        
        // Adiciona o relacionamento na árvore B+
        ParIDSerieEpisodio par = new ParIDSerieEpisodio(idSerie, idEpisodio);
        return arvoreRelacionamento.create(par);
    }
    
    // Remover um relacionamento entre série e episódio
    public boolean removerRelacionamento(int idSerie, int idEpisodio) throws Exception {
        ParIDSerieEpisodio par = new ParIDSerieEpisodio(idSerie, idEpisodio);
        return arvoreRelacionamento.delete(par);
    }
    
    // Buscar todos os episódios de uma série usando a árvore B+
    public ArrayList<Episodio> getEpisodiosDaSerieArvoreBMais(int idSerie) throws Exception {
        ArrayList<Episodio> episodios = new ArrayList<>();
        
        // Cria um par para busca (com episódio -1 para buscar todos os episódios da série)
        ParIDSerieEpisodio parBusca = new ParIDSerieEpisodio(idSerie, -1);
        
        // Busca todos os pares que começam com o idSerie
        ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(parBusca);
        
        // Para cada par encontrado, recupera o episódio
        for (ParIDSerieEpisodio par : pares) {
            if (par.getIdSerie() == idSerie) { // Verifica se o ID da série corresponde
                Episodio ep = arqEpisodios.read(par.getIdEpisodio());
                if (ep != null) {
                    episodios.add(ep);
                }
            } else {
                // Como a árvore B+ retorna todos os elementos maiores ou iguais,
                // podemos parar quando o idSerie for diferente
                break;
            }
        }
        
        return episodios;
    }
    
    // Verificar se uma série tem episódios
    public boolean serieTemEpisodios(int idSerie) throws Exception {
        // Tenta buscar episódios usando a árvore B+
        try {
            ParIDSerieEpisodio parBusca = new ParIDSerieEpisodio(idSerie, -1);
            ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(parBusca);
            
            // Verifica se algum par com o idSerie foi encontrado
            for (ParIDSerieEpisodio par : pares) {
                if (par.getIdSerie() == idSerie) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            // Fallback: busca linear em caso de erro na árvore B+
            System.err.println("Erro ao buscar na árvore B+, usando método alternativo: " + e.getMessage());
            return serieTemEpisodiosAlternativo(idSerie);
        }
    }
    
    // Método alternativo para verificar se uma série tem episódios (busca linear)
    private boolean serieTemEpisodiosAlternativo(int idSerie) throws Exception {
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
    
    // Obter todos os episódios de uma série (método original, busca linear)
    public ArrayList<Episodio> getEpisodiosDaSerie(int idSerie) throws Exception {
        try {
            return getEpisodiosDaSerieArvoreBMais(idSerie);
        } catch (Exception e) {
            System.err.println("Erro ao buscar na árvore B+, usando método alternativo: " + e.getMessage());
            return getEpisodiosDaSerieAlternativo(idSerie);
        }
    }
    
    // Método alternativo para obter episódios (busca linear)
    private ArrayList<Episodio> getEpisodiosDaSerieAlternativo(int idSerie) throws Exception {
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
        ArrayList<Episodio> todosEpisodios = getEpisodiosDaSerie(idSerie);
        ArrayList<Episodio> episodiosTemporada = new ArrayList<>();
        
        for (Episodio ep : todosEpisodios) {
            if (ep.getNumTemporada() == temporada) {
                episodiosTemporada.add(ep);
            }
        }
        
        return episodiosTemporada;
    }
    
    // Contar o número de episódios por temporada de uma série
    public HashMap<Integer, Integer> contarEpisodiosPorTemporada(int idSerie) throws Exception {
        HashMap<Integer, Integer> contagem = new HashMap<>();
        ArrayList<Episodio> episodios = getEpisodiosDaSerie(idSerie);
        
        for (Episodio ep : episodios) {
            int temporada = ep.getNumTemporada();
            contagem.put(temporada, contagem.getOrDefault(temporada, 0) + 1);
        }
        
        return contagem;
    }
    
    // Atualizar os índices após criação, atualização ou remoção de episódios
    public void atualizarIndicesAposOperacao(Episodio episodio, String operacao) throws Exception {
        int idSerie = episodio.getSerieId();
        int idEpisodio = episodio.getId();
        
        switch (operacao.toLowerCase()) {
            case "create":
            case "update":
                adicionarRelacionamento(idSerie, idEpisodio);
                break;
            case "delete":
                removerRelacionamento(idSerie, idEpisodio);
                break;
        }
    }
    
    // Obter o número total de episódios de uma série
    public int getTotalEpisodios(int idSerie) throws Exception {
        return getEpisodiosDaSerie(idSerie).size();
    }
    
    // Obter o número total de temporadas de uma série
    public int getTotalTemporadas(int idSerie) throws Exception {
        HashMap<Integer, Integer> episodiosPorTemporada = contarEpisodiosPorTemporada(idSerie);
        return episodiosPorTemporada.size();
    }
    
    // Imprimir a árvore de relacionamentos para debugging
    public void imprimirArvore() throws Exception {
        System.out.println("Estrutura da Árvore B+ de Relacionamentos:");
        arvoreRelacionamento.print();
    }
    
    // Buscar série por nome (parcial ou completo)
    public ArrayList<Serie> buscarSeriePorNome(String nomeParcial) throws Exception {
        ArrayList<Serie> seriesEncontradas = new ArrayList<>();
        int ultimoId = arqSerie.ultimoId();
        
        // Converter para minúsculo para busca case-insensitive
        String termoBusca = nomeParcial.toLowerCase();
        
        for (int i = 1; i <= ultimoId; i++) {
            Serie serie = arqSerie.read(i);
            if (serie != null && serie.getTitulo().toLowerCase().contains(termoBusca)) {
                seriesEncontradas.add(serie);
            }
        }
        
        return seriesEncontradas;
    }
    
    // Buscar episódio por nome (parcial ou completo)
    public ArrayList<Episodio> buscarEpisodioPorNome(String nomeParcial) throws Exception {
        ArrayList<Episodio> episodiosEncontrados = new ArrayList<>();
        int ultimoId = arqEpisodios.ultimoId();
        
        // Converter para minúsculo para busca case-insensitive
        String termoBusca = nomeParcial.toLowerCase();
        
        for (int i = 1; i <= ultimoId; i++) {
            Episodio episodio = arqEpisodios.read(i);
            if (episodio != null && episodio.getTitulo().toLowerCase().contains(termoBusca)) {
                episodiosEncontrados.add(episodio);
            }
        }
        
        return episodiosEncontrados;
    }
    
    // Buscar episódios por nome em uma série específica
    public ArrayList<Episodio> buscarEpisodioPorNomeEmSerie(String nomeParcial, int idSerie) throws Exception {
        ArrayList<Episodio> episodiosEncontrados = new ArrayList<>();
        int ultimoId = arqEpisodios.ultimoId();
        
        // Converter para minúsculo para busca case-insensitive
        String termoBusca = nomeParcial.toLowerCase();
        
        for (int i = 1; i <= ultimoId; i++) {
            Episodio episodio = arqEpisodios.read(i);
            if (episodio != null && 
                episodio.getSerieId() == idSerie && 
                episodio.getTitulo().toLowerCase().contains(termoBusca)) {
                episodiosEncontrados.add(episodio);
            }
        }
        
        return episodiosEncontrados;
    }
}