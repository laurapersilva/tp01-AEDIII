

import java.time.LocalDate;

public class Serie { 
    public int id;
    public String nome;
    public String diretor;
    public LocalDate lancamento;
    public String sinopse;
    public String streaming;

    public Serie() {
        this(-1, "", "", LocalDate.now(), "", "");
    }
    public Serie(String n, String d, LocalDate l, String si, String st) {
        this(-1, n, d, l, si, st);
    }

    public Serie(int i, String n, String d, LocalDate l, String si, String st) {
        this.id = i;
        this.nome = n;
        this.diretor = d;
        this.lancamento = l;
        this.sinopse = si;
        this.streaming = st;
    }

    public int getId() {return id;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getDiretor() {return diretor;}
    public void setDiretor(String diretor) {this.diretor = diretor;}

    public LocalDate getLancamento() {return lancamento;}
    public void setLancamento(LocalDate lancamento) {this.lancamento = lancamento;}

    public String getSinopse() {return sinopse;}
    public void setSinopse(String sinopse) {this.sinopse = sinopse;}
    
    public String getStreaming() {return streaming;}
    public void setStreaming(String streaming) {this.streaming = streaming;}

    @Override
    public String toString() {
        return "Serie [id=" + id + ", nome=" + nome + ", diretor=" + diretor + ", lancamento=" + lancamento
                + ", sinopse=" + sinopse + ", streaming=" + streaming + "]";
    }

}

class Episodio extends Serie {
    public int id;
    public Serie serie;
    public String nome;
    public int temporada;
    public LocalDate lancamento;
    public int duracao;

    
    public Episodio(Serie serie, String nome, int temporada, LocalDate lancamento, int duracao) {
        this.id = serie.getId(); // ID do episódio vai ser o mesmo da série
        this.serie = serie;
        this.nome = nome;
        this.temporada = temporada;
        this.lancamento = lancamento;
        this.duracao = duracao;
    }
    
    public int getId() {return id;}
    public Serie getSerie() {return serie;}
    
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    
    public int getTemporada() {return temporada;}
    public void setTemporada(int temporada) {this.temporada = temporada;}
    
    public LocalDate getLancamento() {return lancamento;}
    public void setLancamento(LocalDate lancamento) {this.lancamento = lancamento;}
    
    public int getDuracao() {return duracao;}
    public void setDuracao(int duracao) {this.duracao = duracao;}
    
    @Override
    public String toString() {
        return "Episodio [id=" + id + ", serie=" + serie + ", nome=" + nome + ", temporada=" + temporada
                + ", lancamento=" + lancamento + ", duracao=" + duracao + "]";
    }
}