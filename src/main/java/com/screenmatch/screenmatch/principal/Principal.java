package com.screenmatch.screenmatch.principal;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.sound.sampled.SourceDataLine;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;

import com.screenmatch.screenmatch.model.DadosEpisodio;
import com.screenmatch.screenmatch.model.DadosSerie;
import com.screenmatch.screenmatch.model.DadosTemporada;
import com.screenmatch.screenmatch.model.Episodio;
import com.screenmatch.screenmatch.services.ConsumoApi;
import com.screenmatch.screenmatch.services.ConverteDados;

public class Principal {

    //http://www.omdbapi.com/?t =gilmore+girls&Season=1&Episode=2 &apikey=ee527ba6
    private static final String URL = "http://www.omdbapi.com/?t=";
    private static final String API_KEY = "&apikey=ee527ba6";

    private Scanner sc = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Optional<Episodio> first;

    public void menu() throws IOException{

        Boolean continuar = true;

        System.out.println("Bem vindo ao Screenmatch!");
    
        do{
        
            System.out.println("Digite o nome da série que deseja buscar: ");
        
            var nomeSerie = sc.nextLine();
            var json = consumoApi.obterDados(URL + nomeSerie.replace(" ", "+") + API_KEY);
            DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
            //System.out.println(dadosSerie);

            
            //Criando lista de temporadas 
            List<DadosTemporada> temporadas = new ArrayList<>();
            //Iterando sobre as temporadas
            for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            	json = consumoApi.obterDados(URL + nomeSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            	//System.out.println(json);//debbug
            	temporadas.add(dadosTemporada);
            }

            System.out.println("\nA série " + dadosSerie.titulo() + " possui " + dadosSerie.totalTemporadas() + " temporadas.\n");
            //temporadas.forEach(System.out::println);//keep debuging
            
            // System.out.println("\n\n\t\tTitulo dos episódios: \n");
            // temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println("\t" + e.titulo() + " - Avaliação: " + e.avaliacao() + " - Data de lançamento: " + e.dataLancamento())));


            List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
            //dadosEpisodios.forEach(System.out::println);//keep debuging
            
            // // Listando os 5 melhores episódios
            // System.out.println("\nOs 10 melhores episódios são: ");
            // dadosEpisodios.stream()
            //     .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            //     .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
            //     .limit(10)
            //     .map(e -> e.titulo().toUpperCase())
            //     .forEach(System.out::println);


            // Criando Lista de Episódios com dados de Temporada
            List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                    .map(d -> new Episodio(t.numeroTemporada(), d))
                ).collect(Collectors.toList());

            episodios.forEach(System.out::println);


            System.out.println("\nDigite um trecho do título do episódio que deseja buscar: ");
            var trechoTitulo = sc.nextLine();
            Optional<Episodio> epsodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();

            if(epsodioBuscado.isPresent()){
                System.out.println("\nEpisódio encontrado: " + epsodioBuscado.get().getTitulo());
                System.out.println("\nTemporada: " + epsodioBuscado.get().getTemporada());
            }
                
            // // Buscando episódios a partir de uma data
            // System.out.println("\nDigite a data de lançamento para buscar episódios: ");
            // var ano = sc.nextInt();
            // sc.nextLine();
            
            // LocalDate dataBusca = LocalDate.of(ano, 1, 1);
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // episodios.stream()
            //     .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
            //     .forEach(e -> System.out.println("\nTemporada: " + e.getTemporada() + " - Titulo: " + e.getTitulo() + " - Avaliação: " + e.getAvaliacao() + " - Data de lançamento: " + e.getDataLancamento().format(formatter)));
            
            
            // Perguntando ao usuário se deseja continuar
            System.out.println("\nDeseja continuar? (S/N)");
            var resposta = sc.nextLine();
            if(resposta.equalsIgnoreCase("N")){
                continuar = false;
            }
 
        }while(continuar);        
    }
}
