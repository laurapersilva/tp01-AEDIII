package TP01.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
            System.out.println("1) Incluir nova série");
            System.out.println("2) Buscar série");
            System.out.println("3) Alterar série");
            System.out.println("4) Excluir série");
            System.out.println("5) Ver resumo das temporadas");
            System.out.println("6) Ver episódios por temporada");
            System.out.println("0) Retornar ao menu anterior");
            System.out.print("Escolha uma opção: ");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> incluirSerie();
                case 2 -> buscarSeriePorNome();
                case 3 -> alterarSeriePorNome();
                case 4 -> excluirSeriePorNome();
                case 5 -> verResumoTemporadasPorNome();
                case 6 -> verEpisodiosPorTemporadaPorNome();
                default -> {
                    if (op != 0) {
                        System.out.println("Opção inválida!");
                    }
                }
            }
        } while (op != 0);
    }

    private void incluirSerie() throws Exception {
        Serie serie = visaoSerie.leSerie();
        if (serie == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        int id = arqSerie.create(serie);
        System.out.println("Série criada com sucesso! ID: " + id);
    }

    private void buscarSeriePorId() throws Exception {
        int id = visaoSerie.leIdSerie();
        Serie serie = arqSerie.read(id);
        visaoSerie.mostraSerie(serie);
        
        // Se encontrou a série, perguntar se quer ver detalhes
        if (serie != null) {
            System.out.print("\nDeseja ver o resumo das temporadas? (S/N): ");
            String resposta = sc.nextLine().toUpperCase();
            if (resposta.equals("S")) {
                verResumoTemporadas(id);
            }
        }
    }
    
    private void buscarSeriePorNome() throws Exception {
        String termoBusca = visaoSerie.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarSeriePorNome(termoBusca);
        
        // Exibir resultados
        visaoSerie.mostraResultadoBuscaSeries(resultados);
        
        // Se houver resultados, perguntar se quer selecionar uma série
        if (!resultados.isEmpty()) {
            System.out.print("\nDeseja selecionar uma série para ver mais detalhes? (S/N): ");
            String resposta = sc.nextLine().toUpperCase();
            
            if (resposta.equals("S")) {
                int idSelecionado = visaoSerie.selecionaSerieDoResultado(resultados);
                
                if (idSelecionado > 0) {
                    Serie serieSelecionada = arqSerie.read(idSelecionado);
                    visaoSerie.mostraSerie(serieSelecionada);
                    
                    System.out.print("\nDeseja ver o resumo das temporadas? (S/N): ");
                    resposta = sc.nextLine().toUpperCase();
                    if (resposta.equals("S")) {
                        verResumoTemporadas(idSelecionado);
                    }
                }
            }
        }
    }

    private void alterarSeriePorNome() throws Exception {
        String termoBusca = visaoSerie.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarSeriePorNome(termoBusca);
        
        // Exibir resultados
        visaoSerie.mostraResultadoBuscaSeries(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para alterar
        System.out.print("\nDigite o ID da série que deseja alterar (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        for (Serie s : resultados) {
            if (s.getId() == idSelecionado) {
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Obter série atual
        Serie serieAtual = arqSerie.read(idSelecionado);
        System.out.println("\nDados atuais da série:");
        visaoSerie.mostraSerie(serieAtual);
        
        // Obter novos dados
        Serie serieNova = visaoSerie.leSerie();
        if (serieNova == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        serieNova.setId(idSelecionado);
        boolean resultado = arqSerie.update(serieNova);
        System.out.println(resultado ? "Série atualizada com sucesso!" : "Erro ao atualizar série.");
    }
    
    // Mantido para uso interno
    private void alterarSerie(int id) throws Exception {
        // Verificar se a série existe
        Serie serieAtual = arqSerie.read(id);
        if (serieAtual == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        System.out.println("\nDados atuais da série:");
        visaoSerie.mostraSerie(serieAtual);
        
        Serie serieNova = visaoSerie.leSerie();
        if (serieNova == null) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        serieNova.setId(id);
        boolean resultado = arqSerie.update(serieNova);
        System.out.println(resultado ? "Série atualizada com sucesso!" : "Erro ao atualizar série.");
    }

    private void excluirSeriePorNome() throws Exception {
        String termoBusca = visaoSerie.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarSeriePorNome(termoBusca);
        
        // Exibir resultados
        visaoSerie.mostraResultadoBuscaSeries(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para excluir
        System.out.print("\nDigite o ID da série que deseja excluir (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        Serie serieSelecionada = null;
        for (Serie s : resultados) {
            if (s.getId() == idSelecionado) {
                encontrado = true;
                serieSelecionada = s;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Continuar com a exclusão usando o ID selecionado
        excluirSerie(idSelecionado);
    }
    
    // Mantido para uso interno
    private void excluirSerie(int id) throws Exception {
        // Verificar se a série existe
        Serie serie = arqSerie.read(id);
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }

        // Verificar se existem episódios vinculados à série usando a árvore B+
        if (relacionamento.serieTemEpisodios(id)) {
            System.out.println("Não é possível excluir! Existem episódios vinculados a esta série.");
            System.out.println("Por favor, exclua todos os episódios dessa série antes de excluí-la.");
            
            // Mostrar a quantidade de episódios vinculados
            int totalEpisodios = relacionamento.getTotalEpisodios(id);
            System.out.println("Total de episódios vinculados: " + totalEpisodios);
            return;
        }
        
        // Confirmar exclusão
        System.out.print("\nConfirmar exclusão da série \"" + serie.getTitulo() + "\"? (S/N): ");
        String confirmacao = sc.nextLine().toUpperCase();
        if (!confirmacao.equals("S")) {
            System.out.println("Operação cancelada.");
            return;
        }

        boolean resultado = arqSerie.delete(id);
        System.out.println(resultado ? "Série excluída com sucesso!" : "Erro ao excluir série.");
    }

    private void verResumoTemporadasPorNome() throws Exception {
        String termoBusca = visaoSerie.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarSeriePorNome(termoBusca);
        
        // Exibir resultados
        visaoSerie.mostraResultadoBuscaSeries(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para ver resumo
        System.out.print("\nDigite o ID da série para ver o resumo das temporadas (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        for (Serie s : resultados) {
            if (s.getId() == idSelecionado) {
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Mostrar resumo das temporadas
        verResumoTemporadas(idSelecionado);
    }
    
    // Mantido para uso interno
    private void verResumoTemporadas(int idSerie) throws Exception {
        Serie serie = arqSerie.read(idSerie);
        
        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        // Obter estatísticas
        HashMap<Integer, Integer> episodiosPorTemporada = relacionamento.contarEpisodiosPorTemporada(idSerie);
        int totalEpisodios = relacionamento.getTotalEpisodios(idSerie);
        int totalTemporadas = relacionamento.getTotalTemporadas(idSerie);
        
        // Mostrar resumo - esta é a função que mostra quantos episódios existem por temporada
        visaoSerie.mostraResumoTemporadas(serie, episodiosPorTemporada, totalEpisodios, totalTemporadas);
    }

    private void verEpisodiosPorTemporadaPorNome() throws Exception {
        String termoBusca = visaoSerie.leTermoBusca();
        
        if (termoBusca.trim().isEmpty()) {
            System.out.println("Termo de busca inválido!");
            return;
        }
        
        // Realizar a busca
        ArrayList<Serie> resultados = relacionamento.buscarSeriePorNome(termoBusca);
        
        // Exibir resultados
        visaoSerie.mostraResultadoBuscaSeries(resultados);
        
        if (resultados.isEmpty()) {
            return;
        }
        
        // Selecionar série para ver episódios
        System.out.print("\nDigite o ID da série para ver os episódios (0 para cancelar): ");
        int idSelecionado = sc.nextInt();
        sc.nextLine(); // Limpar buffer
        
        if (idSelecionado <= 0) {
            System.out.println("Operação cancelada.");
            return;
        }
        
        // Verificar se o ID está na lista
        boolean encontrado = false;
        for (Serie s : resultados) {
            if (s.getId() == idSelecionado) {
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("ID não encontrado na lista!");
            return;
        }
        
        // Mostrar episódios por temporada
        verEpisodiosPorTemporada(idSelecionado);
    }
    
    // Mantido para uso interno
    private void verEpisodiosPorTemporada(int idSerie) throws Exception {
        Serie serie = arqSerie.read(idSerie);

        if (serie == null) {
            System.out.println("Série não encontrada.");
            return;
        }
        
        // Primeiro, mostrar um resumo das temporadas disponíveis
        verResumoTemporadas(idSerie);
        
        // Depois, pedir a temporada específica
        int temporada = visaoSerie.leTemporada();
        ArrayList<Episodio> episodios = relacionamento.getEpisodiosPorTemporada(idSerie, temporada);
        visaoSerie.mostraEpisodiosPorTemporada(episodios, temporada);
        
        // Perguntar se deseja ver outra temporada
        System.out.print("\nDeseja ver outra temporada? (S/N): ");
        String resposta = sc.nextLine().toUpperCase();
        
        while (resposta.equals("S")) {
            temporada = visaoSerie.leTemporada();
            episodios = relacionamento.getEpisodiosPorTemporada(idSerie, temporada);
            visaoSerie.mostraEpisodiosPorTemporada(episodios, temporada);
            
            System.out.print("\nDeseja ver outra temporada? (S/N): ");
            resposta = sc.nextLine().toUpperCase();
        }
    }
}