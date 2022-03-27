package com.springboot.webflux.app;
// //
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.springboot.webflux.app.models.dao.IngredienteDao;
import com.springboot.webflux.app.models.dao.RacionDao;
import com.springboot.webflux.app.models.dao.RecetaDao;
import com.springboot.webflux.app.models.documents.Ingrediente;
import com.springboot.webflux.app.models.documents.Racion;
import com.springboot.webflux.app.models.documents.Receta;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootWebfluxRecetasApplication implements CommandLineRunner{

	private Logger log = LoggerFactory.getLogger(SpringBootWebfluxRecetasApplication.class);
	
	@Autowired
	private RecetaDao recetaDao;
	
	@Autowired
	private RacionDao racionDao;
	
	@Autowired
	private IngredienteDao ingredienteDao;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxRecetasApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//limpiarRecetas()
		//.thenMany(limpiarRaciones())
		//.thenMany(limpiarIngredientes())
		//.thenMany(cargarDatos())
		//.thenMany(actualizarDatos())
		//.subscribe(item-> log.info(item.getId()));
	}

	public Flux<Object> cargarDatos() {
		
		Ingrediente lechuga = new Ingrediente("Lechuga");
		Ingrediente jitomate = new Ingrediente("Jitomate");
		Ingrediente queso = new Ingrediente("Queso");
		
		
				Racion rac1 = new Racion(5, lechuga);
				Racion rac2 = new Racion(8, jitomate);
				Racion rac3 = new Racion(3, queso);
				
				ArrayList<Racion> lista = new ArrayList<Racion>();
				lista.add(rac1);
				lista.add(rac2);
				lista.add(rac3);
		
		return			
				
		Flux.just(lechuga, jitomate, queso)
		.flatMap(ingrediente -> {
		    return ingredienteDao.save(ingrediente);
		    
		})
		.thenMany(
		
		Flux.just(new Racion(6,lechuga),
				  new Racion(10, jitomate),
				  new Racion(4, queso))
		.flatMap(racion -> 
			racionDao.save(racion)
		))
		.thenMany(

				Flux.just(
						new Receta("Enchiladas",5,"20min", lista,"Receta pa ser feliz"),
						new Receta("Coso",5,"50min", lista,"Receta pa morir"))
				.flatMap(rec -> 
				
					recetaDao.save(rec)
				));
	}
	
	public Mono<Void> limpiarRecetas() {
		return mongoTemplate.dropCollection("receta");
	}
	
	public Mono<Void> limpiarRaciones() {
		return mongoTemplate.dropCollection("racion");
	}
	
	public Mono<Void> limpiarIngredientes() {
		return mongoTemplate.dropCollection("ingrediente");
	}
	
	public Flux<Racion> actualizarDatos() {
		
		return racionDao
				.findAll()
				.flatMap(racion -> {
			return racionDao.save(racion);
		
		}).doOnNext(item -> log.info("Updated:"+item.getIngrediente()));
	}
}
