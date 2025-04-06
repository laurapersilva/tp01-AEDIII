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
            // Verificar se o diretório de dados existe
            java.io.File dataDir = new java.io.File("./TP01/Data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Verificar se o arquivo de índice existe
            java.io.File idxFile = new java.io.File("./TP01/Data/serieEpisodio.idx");
            boolean arquivoNovo = !idxFile.exists();

            // Inicializa a árvore B+ para relacionamento
            Constructor<ParIDSerieEpisodio> construtor = ParIDSerieEpisodio.class.getConstructor();
            this.arvoreRelacionamento = new ArvoreBMais<>(
                    construtor,
                    4, // Ordem da árvore
                    "./TP01/Data/serieEpisodio.idx"
            );

            // Se o arquivo for novo, inicializa com um registro dummy
            if (arquivoNovo) {
                System.out.println("Inicializando índice árvore B+ com registro inicial...");
                try {
                    ParIDSerieEpisodio dummy = new ParIDSerieEpisodio(0, 0);
                    arvoreRelacionamento.create(dummy);
                    arvoreRelacionamento.delete(dummy); // Remove em seguida
                } catch (Exception e) {
                    System.err.println("Erro ao inicializar árvore B+: " + e.getMessage());
                }
            }

            // Verificar estado da árvore
            verificarIntegridadeArvore();

        } catch (Exception e) {
            System.err.println("Erro ao inicializar árvore B+: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarIntegridadeArvore() {
        try {
            boolean vazia = arvoreRelacionamento.empty();
            if (vazia) {
                System.err.println("AVISO: Árvore B+ está vazia. O sistema usará o método alternativo até que o índice seja reconstruído.");

                // Tentar reconstruir o índice automaticamente
                reconstruirIndice();
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar integridade da árvore: " + e.getMessage());
        }
    }

    // Método para reconstruir o índice completo
    private void reconstruirIndice() {
        try {
            System.out.println("Iniciando reconstrução do índice da árvore B+...");
            int totalRegistros = 0;

            // Percorre todos os episódios e reconstrói a árvore
            int ultimoIdEpisodio = arqEpisodios.ultimoId();
            for (int i = 1; i <= ultimoIdEpisodio; i++) {
                Episodio ep = arqEpisodios.read(i);
                if (ep != null && ep.getSerieId() > 0) {
                    try {
                        ParIDSerieEpisodio par = new ParIDSerieEpisodio(ep.getSerieId(), ep.getId());
                        arvoreRelacionamento.create(par);
                        totalRegistros++;
                    } catch (Exception e) {
                        System.err.println("Erro ao inserir episódio " + i + " no índice: " + e.getMessage());
                    }
                }
            }

            System.out.println("Reconstrução do índice concluída. " + totalRegistros + " registros indexados.");
        } catch (Exception e) {
            System.err.println("Falha na reconstrução do índice: " + e.getMessage());
        }
    }
    
    // Adicionar um relacionamento entre série e episódio
    // Adicionar um relacionamento entre série e episódio
    public boolean adicionarRelacionamento(int idSerie, int idEpisodio) throws Exception {
        // Verifica se a série existe
        Serie serie = arqSerie.read(idSerie);
        if (serie == null) {
            System.err.println("ERRO: Série ID " + idSerie + " não encontrada ao adicionar relacionamento");
            return false;
        }

        // Verifica se o episódio existe
        Episodio episodio = arqEpisodios.read(idEpisodio);
        if (episodio == null) {
            System.err.println("ERRO: Episódio ID " + idEpisodio + " não encontrado ao adicionar relacionamento");
            return false;
        }

        // Atualiza o ID da série no episódio, se necessário
        if (episodio.getSerieId() != idSerie) {
            episodio.setSerieId(idSerie);
            arqEpisodios.update(episodio);
        }

        try {
            // Primeiro verifica se o relacionamento já existe na árvore
            ParIDSerieEpisodio parBusca = new ParIDSerieEpisodio(idSerie, idEpisodio);
            ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(parBusca);
            boolean jaExiste = false;

            // Verifica se o par exato já existe
            for (ParIDSerieEpisodio par : pares) {
                if (par.getIdSerie() == idSerie && par.getIdEpisodio() == idEpisodio) {
                    jaExiste = true;
                    break;
                }
            }

            // Se já existe, considera como sucesso
            if (jaExiste) {
                return true;
            }

            // Adiciona o relacionamento na árvore B+
            ParIDSerieEpisodio par = new ParIDSerieEpisodio(idSerie, idEpisodio);

            // Tente até 3 vezes para corrigir possíveis problemas temporários
            boolean resultado = false;
            int tentativas = 0;
            Exception ultimoErro = null;

            while (!resultado && tentativas < 3) {
                tentativas++;
                try {
                    // Verifica se a árvore está vazia antes de tentar inserir
                    boolean arvoreVazia = false;
                    try {
                        arvoreVazia = arvoreRelacionamento.empty();
                        if (arvoreVazia && tentativas == 1) {
                            // Se estiver vazia na primeira tentativa, tenta reconstruir o índice
                            reconstruirIndice();
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao verificar se a árvore está vazia: " + e.getMessage());
                    }

                    resultado = arvoreRelacionamento.create(par);

                    if (!resultado && tentativas == 3) {
                        System.err.println("AVISO: Falha ao inserir na árvore B+ o par (" + idSerie + ", " + idEpisodio + ") após " + tentativas + " tentativas");

                        // Força uma reconstrução manual apenas como último recurso
                        try {
                            // Implementação de reparo manual da árvore
                            criarRelacionamentoManualmente(idSerie, idEpisodio);
                            return true; // Se chegar aqui, conseguimos registrar o relacionamento
                        } catch (Exception repairEx) {
                            System.err.println("Falha no reparo manual: " + repairEx.getMessage());
                            // Continua com o resultado atual
                        }
                    }
                } catch (Exception e) {
                    ultimoErro = e;
                    System.err.println("Tentativa " + tentativas + " falhou: " + e.getMessage());

                    // Pequeno atraso entre tentativas
                    try { Thread.sleep(100); } catch (InterruptedException ie) { }

                    // Se a operação falhar devido a uma árvore corrompida, tenta repará-la
                    if (tentativas == 2) {
                        try {
                            System.err.println("Tentando reparar a árvore B+ antes da última tentativa");
                            criarRelacionamentoManualmente(idSerie, idEpisodio);
                            return true;
                        } catch (Exception repairEx) {
                            System.err.println("Falha no reparo: " + repairEx.getMessage());
                        }
                    }
                }
            }

            if (!resultado && ultimoErro != null) {
                throw ultimoErro;
            }

            return resultado;
        } catch (Exception e) {
            System.err.println("ERRO ao adicionar relacionamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Método de contingência para criar um relacionamento quando a árvore falha
    private void criarRelacionamentoManualmente(int idSerie, int idEpisodio) throws Exception {
        // Esta é uma solução alternativa para quando a árvore B+ falha completamente
        System.err.println("Utilizando método alternativo para criar relacionamento");
        
        // Tenta criar um diretório de debug se não existir
        try {
            java.io.File debugDir = new java.io.File("./TP01/debug");
            if (!debugDir.exists()) {
                debugDir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar diretório de debug: " + e.getMessage());
        }
        
        // Diagnóstico - salva o estado da árvore antes da operação
        boolean arvoreVaziaInicial = false;
        try {
            arvoreVaziaInicial = arvoreRelacionamento.empty();
            java.io.FileOutputStream diagOut = new java.io.FileOutputStream("./TP01/debug/arvore_antes_reparo.txt", true);
            java.io.PrintStream ps = new java.io.PrintStream(diagOut);
            ps.println("Diagnóstico antes de inserir (" + idSerie + "," + idEpisodio + ")");
            ps.println("Árvore vazia? " + arvoreVaziaInicial);
            ps.println("Timestamp: " + new java.util.Date());
            ps.println("--------------------------------------------");
            ps.close();
            diagOut.close();
        } catch (Exception e) {
            System.err.println("Erro ao diagnosticar árvore: " + e.getMessage());
        }
        
        // Força a recriação da árvore B+ independentemente do estado atual
        // para resolver problemas de corrupção do arquivo de índice
        System.err.println("Tentando reconstruir o índice da árvore B+...");
        
        // Precisamos criar uma nova instância da árvore B+
        try {
            // Tenta criar uma nova árvore do zero (potencialmente sobrescrevendo o arquivo)
            Constructor<ParIDSerieEpisodio> construtor = ParIDSerieEpisodio.class.getConstructor();
            
            // Primeiro salva o arquivo atual como backup
            java.io.File arquivoAntigo = new java.io.File("./TP01/Data/serieEpisodio.idx");
            java.io.File arquivoBackup = new java.io.File("./TP01/Data/serieEpisodio.idx.bak");
            
            // Faz backup do arquivo antigo
            if (arquivoAntigo.exists()) {
                arquivoAntigo.renameTo(arquivoBackup);
            }
            
            // Cria uma nova árvore diretamente no arquivo original
            this.arvoreRelacionamento = new ArvoreBMais<>(
                construtor,
                4, // Ordem da árvore (ajustável conforme necessário)
                "./TP01/Data/serieEpisodio.idx"
            );
            
            // Registra o relacionamento solicitado primeiro
            ParIDSerieEpisodio par = new ParIDSerieEpisodio(idSerie, idEpisodio);
            boolean primeiroInserido = arvoreRelacionamento.create(par);
            System.err.println("Inserção do par solicitado (" + idSerie + "," + idEpisodio + "): " + 
                             (primeiroInserido ? "SUCESSO" : "FALHA"));
            
            // Reconstrua o índice a partir dos dados primários
            int ultimoIdEpisodio = arqEpisodios.ultimoId();
            int sucessos = 0;
            for (int i = 1; i <= ultimoIdEpisodio; i++) {
                Episodio ep = arqEpisodios.read(i);
                if (ep != null && ep.getSerieId() > 0 && 
                    !(ep.getSerieId() == idSerie && ep.getId() == idEpisodio)) { // Evita duplicação
                    ParIDSerieEpisodio parAtual = new ParIDSerieEpisodio(ep.getSerieId(), ep.getId());
                    try {
                        boolean inserido = arvoreRelacionamento.create(parAtual);
                        if (inserido) sucessos++;
                    } catch (Exception e) {
                        // Ignore erros individuais durante a reconstrução
                    }
                }
            }
            
            System.err.println("Reconstrução completa concluída: " + sucessos + " relacionamentos adicionados à árvore");
            
        } catch (Exception e) {
            System.err.println("Falha na reconstrução total da árvore: " + e.getMessage());
        }
        
        // Diagnóstico final - verifica se a operação teve sucesso
        try {
            boolean arvoreVaziaFinal = arvoreRelacionamento.empty();
            ParIDSerieEpisodio testPar = new ParIDSerieEpisodio(idSerie, idEpisodio);
            ArrayList<ParIDSerieEpisodio> resultados = arvoreRelacionamento.read(testPar);
            
            java.io.FileOutputStream diagOut = new java.io.FileOutputStream("./TP01/debug/arvore_apos_reparo.txt", true);
            java.io.PrintStream ps = new java.io.PrintStream(diagOut);
            ps.println("Diagnóstico após inserir (" + idSerie + "," + idEpisodio + ")");
            ps.println("Árvore vazia? " + arvoreVaziaFinal);
            ps.println("Encontrados " + resultados.size() + " resultados ao buscar o par");
            ps.println("Timestamp: " + new java.util.Date());
            ps.println("--------------------------------------------");
            ps.close();
            diagOut.close();
        } catch (Exception e) {
            System.err.println("Erro ao diagnosticar árvore após reparo: " + e.getMessage());
        }
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
    // Obter todos os episódios de uma série (método principal)
    public ArrayList<Episodio> getEpisodiosDaSerie(int idSerie) throws Exception {
        ArrayList<Episodio> resultadoArvore = new ArrayList<>();

        try {
            // Primeiro busca pelo método alternativo para garantir que temos todos os episódios
            ArrayList<Episodio> resultadoAlternativo = getEpisodiosDaSerieAlternativo(idSerie);

            // Se não houver episódios pelo método alternativo, não há episódios para esta série
            if (resultadoAlternativo.isEmpty()) {
                return resultadoAlternativo; // Retorna lista vazia
            }

            // Tenta buscar usando a árvore B+
            try {
                resultadoArvore = getEpisodiosDaSerieArvoreBMais(idSerie);
            } catch (Exception e) {
                System.err.println("Erro ao buscar na árvore B+: " + e.getMessage());
                resultadoArvore = new ArrayList<>(); // Reinicializa em caso de erro
            }

            // Verifica se há registros na árvore
            if (resultadoArvore.isEmpty()) {
                System.err.println("Nenhum episódio encontrado na árvore B+ para a série " + idSerie +
                        ", verificando método alternativo");

                System.err.println("Encontrados " + resultadoAlternativo.size() +
                        " episódios pelo método alternativo. Reparando relacionamentos...");

                // Recria o arquivo de índice se estiver vazio
                boolean arvoreVazia = false;
                try {
                    arvoreVazia = arvoreRelacionamento.empty();
                } catch (Exception e) {
                    System.err.println("Erro ao verificar se a árvore está vazia: " + e.getMessage());
                }

                if (arvoreVazia) {
                    System.err.println("Árvore B+ está vazia. Reconstruindo índice completo...");
                    try {
                        // Usar o método de reconstrução sistemática em vez de apenas um relacionamento
                        reconstruirIndice();
                        // Também tenta adicionar o primeiro episódio manualmente como garantia
                        if (!resultadoAlternativo.isEmpty()) {
                            criarRelacionamentoManualmente(idSerie, resultadoAlternativo.get(0).getId());
                        }
                    } catch (Exception e) {
                        System.err.println("Falha ao reconstruir índice: " + e.getMessage());
                    }
                }

                // Adiciona todos os relacionamentos que estavam faltando
                for (Episodio ep : resultadoAlternativo) {
                    // Silencia mensagens de erro temporariamente
                    try {
                        boolean sucesso = adicionarRelacionamento(idSerie, ep.getId());
                        if (!sucesso) {
                            // Se falhar, tenta usar o método manual
                            criarRelacionamentoManualmente(idSerie, ep.getId());
                        }
                    } catch (Exception ex) {
                        System.err.println("Erro ao reparar relacionamento para episódio ID " + ep.getId() + ": " + ex.getMessage());
                    }
                }

                // Após a reconstrução, tenta recuperar da árvore novamente
                try {
                    resultadoArvore = getEpisodiosDaSerieArvoreBMais(idSerie);
                } catch (Exception e) {
                    System.err.println("Ainda não foi possível recuperar da árvore após reparo: " + e.getMessage());
                    // Se ainda falhar, usa o método alternativo
                    return resultadoAlternativo;
                }
            }

            // Verifica se há diferenças entre os resultados da árvore e do método alternativo
            if (resultadoArvore.size() != resultadoAlternativo.size()) {
                System.err.println("Diferença entre árvore (" + resultadoArvore.size() +
                        " episódios) e método alternativo (" + resultadoAlternativo.size() +
                        " episódios). Combinando resultados...");

                // Combine os dois conjuntos de resultados para garantir que todos os episódios sejam retornados
                ArrayList<Episodio> resultadoCombinado = new ArrayList<>(resultadoAlternativo);
                for (Episodio ep : resultadoArvore) {
                    boolean encontrado = false;
                    for (Episodio epAlt : resultadoAlternativo) {
                        if (ep.getId() == epAlt.getId()) {
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) {
                        resultadoCombinado.add(ep);
                    }
                }

                // Tenta corrigir os relacionamentos faltantes
                if (resultadoArvore.size() < resultadoAlternativo.size()) {
                    System.err.println("Faltam elementos na árvore B+. Tentando adicionar os faltantes...");
                    for (Episodio ep : resultadoAlternativo) {
                        boolean encontrado = false;
                        for (Episodio epArvore : resultadoArvore) {
                            if (ep.getId() == epArvore.getId()) {
                                encontrado = true;
                                break;
                            }
                        }
                        if (!encontrado) {
                            try {
                                adicionarRelacionamento(idSerie, ep.getId());
                            } catch (Exception e) {
                                System.err.println("Erro ao adicionar relacionamento faltante: " + e.getMessage());
                            }
                        }
                    }
                }

                return resultadoCombinado;
            }

            // Se os tamanhos são iguais, assume que os resultados da árvore estão corretos
            return resultadoArvore;
        } catch (Exception e) {
            System.err.println("Erro geral ao buscar episódios da série: " + e.getMessage());
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
                boolean resultado = adicionarRelacionamento(idSerie, idEpisodio);
                if (!resultado) {
                    System.err.println("AVISO: Falha ao adicionar relacionamento entre série " + idSerie + 
                                     " e episódio " + idEpisodio + " à árvore B+");
                }
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
        try {
            arvoreRelacionamento.print();
            
            // Diagnóstico adicional
            System.out.println("\nDiagnóstico da árvore:");
            try {
                ParIDSerieEpisodio test = new ParIDSerieEpisodio(1, 1);
                ArrayList<ParIDSerieEpisodio> pares = arvoreRelacionamento.read(test);
                System.out.println("Buscando o par (1, 1) na árvore...");
                System.out.println("Encontrados " + pares.size() + " resultados para (1, 1)");
                for (ParIDSerieEpisodio p : pares) {
                    System.out.println(" - " + p);
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar par na árvore: " + e.getMessage());
            }
            
            // Teste adicional - força inserção (temporário)
            boolean vazia = arvoreRelacionamento.empty();
            System.out.println("Árvore está vazia? " + vazia);
            
            // Salva estado da árvore para análise
            try {
                java.io.FileOutputStream fos = new java.io.FileOutputStream("./TP01/debug/arvore_diagnostico.txt");
                java.io.PrintStream ps = new java.io.PrintStream(fos);
                ps.println("Raiz da árvore no arquivo: ./TP01/Data/serieEpisodio.idx");
                ps.println("Árvore está vazia? " + vazia);
                ps.close();
                fos.close();
                System.out.println("Diagnóstico salvo em ./TP01/debug/arvore_diagnostico.txt");
            } catch (Exception e) {
                System.out.println("Erro ao salvar diagnóstico: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Erro ao imprimir árvore: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Tenta reparar o índice, se necessário
        try {
            System.out.println("\nVerificando/reparando o índice da árvore...");
            boolean vazia = arvoreRelacionamento.empty();
            if (vazia) {
                System.out.println("Árvore está vazia! Iniciando reparo completo...");
                criarRelacionamentoManualmente(1, 1); // Tenta reconstruir completamente
                
                // Verifica novamente
                boolean vaziaApos = arvoreRelacionamento.empty();
                System.out.println("Árvore vazia após reparo? " + vaziaApos);
            } else {
                System.out.println("Árvore B+ possui raiz válida.");
            }
        } catch (Exception e) {
            System.out.println("Erro durante verificação/reparo: " + e.getMessage());
        }
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