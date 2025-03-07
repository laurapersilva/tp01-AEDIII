import java.util.*;

public class Principal {
    public static void main(String[] args) {
        Scanner sc = new Scanner(sc);
        int opcao;
        
        do {
            System.out.println("PUCFlix 1.0");
            System.out.println("----------");
            System.out.println("> Início");
            System.out.println("1) Séries\r\n" + "2) Episódios\r\n" + "3) Atores\r\n" + "0) Sair");
        
            try {
                opcao = sc.nextInt();
            } catch(NumberFormatException e) {
                opcao = -1;
            }
            switch(opcao) {
                case 1:
                    (new VisaoSeries()).menu();
                    break;
                case 2:
                    (new VisaoEps()).menu();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while(opcao!=0);
    }
}
