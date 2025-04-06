import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class VisaoSeries {
    
    ControleSeries arqSerie;
    Scanner sc = new Scanner(System.in);

    public VisaoSeries() throws Exception {
        arqSerie = new ControleSeries();
    }

    public void menu() {

        int opcao;
        do {

            System.out.println("PUCFlix 1.0");
            System.out.println("-------");
            System.out.println("> Início > Series");
            System.out.println("\n1 - Incluir");
            System.out.println("2 - Buscar");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("0 - Retornar ao menu anterior");

            try {
                opcao = sc.nextInt();
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    incluirSerie();
                    break;
                case 2:
                    buscarSerie();
                    break;
                case 3:
                    alterarSerie();
                    break;
                case 4:
                    excluirSerie();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }


    public void buscarSerie() {
        System.out.println("\nBusca de série");
        int id;
        boolean idValido = false;

        do {
            System.out.print("\nId da série:");
            id = sc.nextInt();  

            // Validação do CPF (11 dígitos e composto apenas por números)
            if (cpf.length() == 11 && cpf.matches("\\d{11}")) {
                cpfValido = true;  // CPF válido
            } else {
                System.out.println("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos, sem pontos e traços.");
            }
        } while (!cpfValido);

        try {
            Serie serie = arqSerie.read(id);  // Chama o método de leitura da classe Arquivo
            if (serie != null) {
                mostraSerie(serie);  // Exibe os detalhes da serie encontrada
            } else {
                System.out.println("Série não encontrada.");
            }
        } catch(Exception e) {
            System.out.println("Erro do sistema. Não foi possível buscar a série!");
            e.printStackTrace();
        }
    }   


    public void incluirSerie() {
        System.out.println("\nInclusão de série");
        int id;
        String nome;
        String diretor;
        LocalDate lancamento;
        String sinopse;
        String streaming;
        boolean dadosCorretos = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        do {
            System.out.print("\nNome (min. de 4 letras ou vazio para cancelar): ");
            nome = sc.nextLine();
            if(nome.length()==0)
                return;
            if(nome.length()<4)
                System.err.println("O nome da serie deve ter no mínimo 4 caracteres.");
        } while(nome.length()<4);

    
        System.out.print("id: ");
        id = sc.nextInt();

        System.out.print("diretor: ");
        diretor = sc.nextLine();

        System.out.print("sinopse: ");
        sinopse = sc.nextLine();

        System.out.print("streaming: ");
        streaming = sc.nextLine();
        
        do {
            System.out.print("Data de lançamento (DD/MM/AAAA): ");
            String dataStr = sc.nextLine();
            dadosCorretos = false;
            try {
                lancamento = LocalDate.parse(dataStr, formatter);
                dadosCorretos = true;
            } catch (Exception e) {
                System.err.println("Data inválida! Use o formato DD/MM/AAAA.");
            }
        } while(!dadosCorretos);

        System.out.print("\nConfirma a inclusão da serie? (S/N) ");
        char resp = sc.nextLine().charAt(0);
        if(resp=='S' || resp=='s') {
            try {
                Serie s = new Serie(id, nome, diretor, lancamento, sinopse, streaming);
                arqSerie.create(s);
                System.out.println("Série incluída com sucesso.");
            } catch(Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir a série!");
            }
        }
    }

    public void alterarSerie() {
        System.out.println("\nAlteração de série");
        int id;
        
        try {
            // Tenta ler a serie com o ID fornecido
            Serie serie = arqSerie.read(id);
            if (serie != null) {
                System.out.println("Serie encontrada:");
                mostraSerie(serie);  // Exibe os dados do serie para confirmação

                // Alteração de nome
                System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                String novoNome = sc.nextLine();
                if (!novoNome.isEmpty()) {
                    serie.nome = novoNome;  // Atualiza o nome se fornecido
                }

                // Alteração de id
                System.out.print("Novo id (deixe em branco para manter o anterior): ");
                int novoId = sc.nextInt();
                serie.id = novoId;

                // Alteração de diretor
                System.out.print("Novo diretor (deixe em branco para manter o anterior): ");
                String novoDiretor = sc.nextLine();
                if (!novoDiretor.isEmpty()) {
                    try {
                        serie.diretor = novoDiretor;  // Atualiza o diretor se fornecido
                    } catch (NumberFormatException e) {
                        System.err.println("Diretor inválido. Valor mantido.");
                    }
                }

                // Alteração de streaming
                System.out.print("Novo streaming (deixe em branco para manter o anterior): ");
                String novoStreaming = sc.nextLine();
                if (!novoStreaming.isEmpty()) {
                    try {
                        serie.streaming = novoStreaming;  // Atualiza o streaming se fornecido
                    } catch (NumberFormatException e) {
                        System.err.println("Streaming inválido. Valor mantido.");
                    }
                }

                // Alteração de sinopse
                System.out.print("Novo sinopse (deixe em branco para manter o anterior): ");
                String novoSinopse = sc.nextLine();
                if (!novoSinopse.isEmpty()) {
                    try {
                        serie.sinopse = novoSinopse;  // Atualiza a sinopse se fornecida
                    } catch (NumberFormatException e) {
                        System.err.println("Sinopse inválida. Valor mantido.");
                    }
                }

                // Alteração de data de lancamento
                System.out.print("Nova data de lancamento (DD/MM/AAAA) (deixe em branco para manter a anterior): ");
                String novaDataStr = sc.nextLine();
                if (!novaDataStr.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        serie.lancamento = LocalDate.parse(novaDataStr, formatter);  // Atualiza a data de lancamento se fornecida
                    } catch (Exception e) {
                        System.err.println("Data inválida. Valor mantido.");
                    }
                }

                // Confirmação da alteração
                System.out.print("\nConfirma as alterações? (S/N) ");
                char resp = sc.next().charAt(0);
                if (resp == 'S' || resp == 's') {
                    // Salva as alterações no arquivo
                    boolean alterado = arqSerie.update(serie);
                    if (alterado) {
                        System.out.println("Série alterada com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar o cliente.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }
            } else {
                System.out.println("Série não encontrada.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível alterar a série!");
            e.printStackTrace();
        }
        
    }


    public void excluirSerie() {
        System.out.println("\nExclusão de série");
        
        try {
            // Tenta ler a serie com o ID fornecido
            Serie serie = arqSerie.read(id);
            ControleEps arqEpisodios = new ControleEps();
            Episodio episodio = arqEpisodios.read(id);

            if (serie != null) {
                System.out.println("Serie encontrada:");
                mostraSerie(serie);  // Exibe os dados da serie para confirmação

                //se a serie tem episódios nao podemos exclui-la
                if(serie.id == episodio.id) {
                    System.out.println("Não é possível excluir a série pois ela tem episódios vinculados a ela");
                }

                System.out.print("\nConfirma a exclusão da serie? (S/N) ");
                char resp = sc.next().charAt(0);  // Lê a resposta do usuário

                if (resp == 'S' || resp == 's') {
                    boolean excluido = arqSerie.delete(id);  // Chama o método de exclusão no arquivo
                    if (excluido) {
                        System.out.println("Série excluida com sucesso.");
                    } else {
                        System.out.println("Erro ao excluir a série.");
                    }
                } else {
                    System.out.println("Exclusão cancelada.");
                }
            } else {
                System.out.println("Série não encontrada.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível excluir a série!");
            e.printStackTrace();
        }
    }


    public void mostraSerie(Serie serie) {
    if (serie != null) {
        System.out.println("\nDetalhes da série:");
        System.out.println("----------------------");
        System.out.printf("Nome......: %s%n", serie.nome);
        System.out.printf("ID.......: %s%n", serie.id);
        System.out.printf("Diretor...: %s%n", serie.diretor);
        System.out.printf("Lançamento: %s%n", serie.lancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.printf("Sinopse...: %s%n", serie.sinopse);
        System.out.printf("Streaming...: %s%n", serie.streaming);
        System.out.println("----------------------");
    }
}
}
