package com.example.demo;

import com.example.demo.modelos.Comentarios;
import com.example.demo.modelos.Usuario;
import com.example.demo.modelos.UsuarioComentarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
		ejemploDelayElements();
	}

	public void ejemploDelayElements() throws InterruptedException {
		Flux<Integer> rango = Flux.range(1,12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i->log.info(i.toString()));
		rango.subscribe();

		Thread.sleep(13000);
	}

	public void ejemploComentarioZipWith2(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()->new Usuario("John","Doe"));
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, ue tal!!!...");
			comentarios.addComentarios("Como estas amigo");
			comentarios.addComentarios("Si te esta gustando el curso de undemy");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioComentarios = usuarioMono.
			zipWith(comentariosUsuarioMono)
					.map(tuple->{
						Usuario u =  tuple.getT1();
						Comentarios c = tuple.getT2();
						return new UsuarioComentarios(u,c);
					});
		usuarioComentarios.subscribe(uc -> log.info(uc.toString()));
	}


	public void ejemploComentarioZipWith(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()->new Usuario("John","Doe"));
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, ue tal!!!...");
			comentarios.addComentarios("Como estas amigo");
			comentarios.addComentarios("Si te esta gustando el curso de undemy");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioComentarios = usuarioMono.zipWith(comentariosUsuarioMono,(usuario,comentariosUsuario)->new UsuarioComentarios(usuario,comentariosUsuario));
		usuarioComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploComentarioFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()->new Usuario("John","Doe"));
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, ue tal!!!...");
			comentarios.addComentarios("Como estas amigo");
			comentarios.addComentarios("Si te esta gustando el curso de undemy");
			return comentarios;
		});

		usuarioMono.flatMap(usuario -> comentariosUsuarioMono.map(comentarios -> new UsuarioComentarios(usuario,comentarios)))
				.subscribe(usuarioComentarios -> log.info(usuarioComentarios.toString()));
	}

	public void ejemploCollectList(String... args) throws Exception {
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("juan","salcedo"));
		usuariosList.add(new Usuario("jose","baldovino"));
		usuariosList.add(new Usuario("javier","salgado"));
		usuariosList.add(new Usuario("maria","vilches"));
		usuariosList.add(new Usuario("leo","perez"));

		Flux.fromIterable(usuariosList)
				.collectList()
				.subscribe(lista->{
					lista.forEach(item->log.info(item.toString()));
				});
	}

	public void ejemploToString(String... args) throws Exception {
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("juan","salcedo"));
		usuariosList.add(new Usuario("jose","baldovino"));
		usuariosList.add(new Usuario("javier","salgado"));
		usuariosList.add(new Usuario("maria","vilches"));
		usuariosList.add(new Usuario("leo","perez"));

		Flux.fromIterable(usuariosList)
				.map(usuario->usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre ->{
					if (nombre.contains("javier")){
						return Mono.just(nombre);
					}
					return Mono.empty();
				})
				.map(nombre -> {
					return nombre.toLowerCase();
				})
				.subscribe(u -> log.info(u.toString()));
	}


	public void ejemploFlatMap(String... args) throws Exception {
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("juan salcedo");
		usuariosList.add("jose baldovino");
		usuariosList.add("javier salgado");
		usuariosList.add("maria vilches");
		usuariosList.add("leo perez");

		Flux.fromIterable(usuariosList)
				.map(nombre->new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario ->{
					if (usuario.getNombre().equalsIgnoreCase("javier")){
						return Mono.just(usuario);
					}
					return Mono.empty();
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				})
				.subscribe(u -> log.info(u.toString()));
	}

	public void ejemploIterable(String... args) throws Exception {
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
