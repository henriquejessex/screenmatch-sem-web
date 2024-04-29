package com.screenmatch.screenmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.screenmatch.screenmatch.model.DadosSerie;
import com.screenmatch.screenmatch.services.ConsumoApi;
import com.screenmatch.screenmatch.services.ConverteDados;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoApi consumoApi = new ConsumoApi();
		String json = consumoApi.obterDados("http://www.omdbapi.com/?t=gilmore+girls&apikey=ee527ba6");
		//System.out.println(json);//debbug

		// Convertendo o JSON para um objeto
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);

		//json = consumoApi.obterDados("https://coffee.alexflipnote.dev/random.json");
		//System.out.println(json);
	}

}
