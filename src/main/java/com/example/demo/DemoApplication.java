package com.example.demo;

import com.example.demo.modelos.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("juan salcedo");
		usuariosList.add("jose baldovino");
		usuariosList.add("javier salgado");
		usuariosList.add("maria vilches");
		usuariosList.add("leo perez");

		Flux<String> nombres = Flux.fromIterable(usuariosList);

		Flux<Usuario> usuarios = nombres.map(nombre->new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("jose"))
				.doOnNext(usuario->{
					if (usuario.equals(null)){
						throw new RuntimeException("El nombre no puede ser vacio");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});
		nombres.subscribe(e -> log.info(e.toString())
				, error -> log.error(error.getMessage())
				, new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del observable con exito");
					}
				});
	}
}
