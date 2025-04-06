package TP01.service;

import TP01.interfaces.RegistroArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParIDSerieEpisodio implements RegistroArvoreBMais<ParIDSerieEpisodio> {
    
    private int idSerie;
    private int idEpisodio;
    
    public ParIDSerieEpisodio() {
        this.idSerie = -1;
        this.idEpisodio = -1;
    }
    
    public ParIDSerieEpisodio(int idSerie, int idEpisodio) {
        this.idSerie = idSerie;
        this.idEpisodio = idEpisodio;
    }
    
    public int getIdSerie() {
        return this.idSerie;
    }
    
    public void setIdSerie(int idSerie) {
        this.idSerie = idSerie;
    }
    
    public int getIdEpisodio() {
        return this.idEpisodio;
    }
    
    public void setIdEpisodio(int idEpisodio) {
        this.idEpisodio = idEpisodio;
    }
    
    @Override
    public short size() {
        return 8; // 4 bytes para cada int (idSerie e idEpisodio)
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(idSerie);
        dos.writeInt(idEpisodio);
        
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        this.idSerie = dis.readInt();
        this.idEpisodio = dis.readInt();
    }

    @Override
    public int compareTo(ParIDSerieEpisodio outro) {
        // Garante que não ocorrerá NullPointerException
        if (outro == null) return 1;

        // Compara primeiro pelo idSerie
        if (this.idSerie < outro.idSerie) return -1;
        if (this.idSerie > outro.idSerie) return 1;

        // Se idSerie for igual, compara pelo idEpisodio
        // Importante: comparação com -1 deve tratar como caso especial para busca
        if (outro.idEpisodio == -1) return 0; // Considera match para buscas com chave parcial

        if (this.idEpisodio < outro.idEpisodio) return -1;
        if (this.idEpisodio > outro.idEpisodio) return 1;

        return 0; // Ambos iguais
    }


    @Override
    public ParIDSerieEpisodio clone() {
        return new ParIDSerieEpisodio(this.idSerie, this.idEpisodio);
    }
    
    @Override
    public String toString() {
        return "Serie: " + idSerie + ", Episodio: " + idEpisodio;
    }
}