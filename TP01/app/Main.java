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
        System.out.println("\n==================");
        System.out.println("  PUCFlix 1.0");
        System.out.println("==================");
        System.out.println("> Início");
        System.out.println("1) Séries");
        System.out.println("2) Episódios");
        System.out.println("0) Sair");
        System.out.print("Escolha uma opção: ");
    }

    // Este método foi substituído pelo ControleSerie
    public static void menuSerie() throws Exception {
        // Método mantido para compatibilidade, mas redirecionado para o controller
        ControleSerie controleSerie = new ControleSerie(sc, arqSerie, arqEpisodios);
        controleSerie.menuSerie();
    }

    // Este método foi substituído pelo ControleEpisodio
    public static void menuEpisodio() throws Exception {
        // Método mantido para compatibilidade, mas redirecionado para o controller
        ControleEpisodio controleEpisodio = new ControleEpisodio(sc, arqEpisodios, arqSerie);
        controleEpisodio.menuEpisodio();
    }

    

    public static void main(String[] args) throws Exception {
        try {
            // Cria a pasta de dados se não existir
            File dataDir = new File("./TP01/Data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            // Inicializa os arquivos
            arqEpisodios = new Arquivo<>("catalogo_episodios.bin", Episodio.class);
            arqSerie = new Arquivo<>("catalogo_series.bin", Serie.class);
    
            // Inicializa os controladores
            ControleSerie controleSerie = new ControleSerie(sc, arqSerie, arqEpisodios);
            ControleEpisodio controleEpisodio = new ControleEpisodio(sc, arqEpisodios, arqSerie);
    
            int opcao;
            do {
                menu();
                opcao = sc.nextInt();
                sc.nextLine(); // Limpar buffer
    
                switch (opcao) {
                    case 1 -> controleSerie.menuSerie();
                    case 2 -> controleEpisodio.menuEpisodio();
                    case 0 -> System.out.println("\nEncerrando o sistema...");
                    default -> System.out.println("\nOpção inválida! Por favor, tente novamente.");
                }
            } while (opcao != 0);
    
            System.out.println("\nObrigado por usar o PUCFlix!");
        } catch (Exception e) {
            System.err.println("\nErro no sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}
