
package TP01.controller;

import java.util.ArrayList;
import java.util.Scanner;
import TP01.model.*;
import TP01.service.*;
import TP01.view.*;

public class ControleAtor {
    private Scanner sc;

    public ControleAtor(Scanner sc) {
        this.sc = sc;
    }

    public void menuAtores() {
        int op;
        do {
            System.out.println("\nPUCFlix 1.0\n-----------\n> Início > Atores");
            System.out.println("1) Incluir");
            System.out.println("2) Buscar");
            System.out.println("3) Alterar");
            System.out.println("4) Excluir");
            System.out.println("0) Retornar ao menu anterior");

            op = sc.nextInt();
            sc.nextLine(); // Limpar buffer

            switch (op) {
                case 1 -> System.out.println("Funcionalidade em desenvolvimento.");
                case 2 -> System.out.println("Funcionalidade em desenvolvimento.");
                case 3 -> System.out.println("Funcionalidade em desenvolvimento.");
                case 4 -> System.out.println("Funcionalidade em desenvolvimento.");
            }
        } while (op != 0);
    }
}