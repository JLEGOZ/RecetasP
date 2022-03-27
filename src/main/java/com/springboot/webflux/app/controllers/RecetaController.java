package com.springboot.webflux.app.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.springboot.webflux.app.SpringBootWebfluxRecetasApplication;
import com.springboot.webflux.app.models.dao.IngredienteDao;
import com.springboot.webflux.app.models.dao.RacionDao;
import com.springboot.webflux.app.models.dao.RecetaDao;
import com.springboot.webflux.app.models.documents.Ingrediente;
import com.springboot.webflux.app.models.documents.Racion;
import com.springboot.webflux.app.models.documents.Receta;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/receta-controller/recetas")
public class RecetaController {

	private Logger log = LoggerFactory.getLogger(SpringBootWebfluxRecetasApplication.class);
	@Autowired
	private RecetaDao recetaDao;
	
	@Autowired
	private RacionDao racionDao;
	
	@Autowired
	private IngredienteDao ingredienteDao;
	
	@GetMapping
	public Flux<Receta> listar() {
		return recetaDao.findAll();
	}
	
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Receta>> consultar(@PathVariable String id) {
	  return recetaDao.findById(id)
	          .map(receta -> {
	           return ResponseEntity
	              .ok()
	              .contentType(MediaType.APPLICATION_JSON)
	              .body(receta);
	          })
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> crear(@Valid @RequestBody Mono<Receta> monoReceta) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		return monoReceta
				.flatMap(receta -> {
					return recetaDao
							.save(receta)
							.map(prod -> {

								respuesta.put("receta", receta);
								respuesta.put("mensaje", "Alta exitosa");
								respuesta.put("timestamp", new Date());

								return ResponseEntity
										.created(URI.create("/receta-controller/recetas/" + prod.getId()))
										.contentType(MediaType.APPLICATION_JSON)
										.body(respuesta);
			});
		}).onErrorResume(ex -> {
			return generarError(ex);

		});
	}
	
	@GetMapping("/ingredientes")
	public Flux<Ingrediente> listarIngredientes() {
		return ingredienteDao.findAll();
		
	}
	
	@PostMapping("/ingredientes")
	public Mono<ResponseEntity<Map<String, Object>>> crearIngredientes(@Valid @RequestBody Mono<Ingrediente> monoIngrediete) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return monoIngrediete
				.flatMap(ingrediente -> {
					return ingredienteDao
							.save(ingrediente)
							.map(prod -> {

								respuesta.put("ingrediente", ingrediente);
								respuesta.put("mensaje", "Alta exitosa");
								respuesta.put("timestamp", new Date());

								return ResponseEntity
										.created(URI.create("/receta-controller/recetas/" + prod.getId()))
										.contentType(MediaType.APPLICATION_JSON)
										.body(respuesta);
			});
		}).onErrorResume(ex -> {
			return generarError(ex);

		});
	}
	@GetMapping("/raciones")
	public Flux<Racion> listarRaciones() {
		return racionDao.findAll();
	}
	
	@PostMapping("/raciones")
	public Mono<ResponseEntity<Map<String, Object>>> crearRacion(@Valid @RequestBody Mono<Racion> monoRacion) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return monoRacion
				.flatMap(racion -> {
					return racionDao
							.save(racion)
							.map(prod -> {

								respuesta.put("racion", racion);
								respuesta.put("mensaje", "Alta exitosa");
								respuesta.put("timestamp", new Date());

								return ResponseEntity
										.created(URI.create("/receta-controller/recetas/" + prod.getId()))
										.contentType(MediaType.APPLICATION_JSON)
										.body(respuesta);
			});
		}).onErrorResume(ex -> {
			return generarError(ex);

		});
	}
	
	private Mono<ResponseEntity<Map<String, Object>>> generarError(Throwable ex) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		return Mono.just(ex)
				.cast(WebExchangeBindException.class).flatMapMany(e -> Flux.fromIterable(e.getFieldErrors()))
				.map(e -> {
					return "Campo:" + e.getField() + " Error:" + e.getDefaultMessage();
				}).collectList().flatMap(list -> {
					respuesta.put("errors", list);
					respuesta.put("mensaje", "Ocurrio un error");
					respuesta.put("timestamp", new Date());

					return Mono.just(ResponseEntity.badRequest().body(respuesta));
				});
	}
	

	@GetMapping("/recRaciones/{id}")
	public Mono<ResponseEntity<List<Racion>>> listarRacionesReceta(@PathVariable String id) {
		return  recetaDao.findById(id)
		.map(receta -> {
      	  return ResponseEntity
      			  .ok()
      			  .contentType(MediaType.APPLICATION_JSON)
      			  .body(receta.getRaciones());
		})
		.defaultIfEmpty(ResponseEntity.notFound().build());
		/*
		return racionDao.findAll()
				.filter(racion -> { racion.
					
				});*/
		
	}
	//Eliminar receta
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id, Model model) {
	return recetaDao.findById(id)
			.defaultIfEmpty(new Receta())
	          .flatMap(receta -> {
	        	  if(null == receta.getId())
	        		   return Mono.error(new InterruptedException("No se localizo la receta:" + id));
	           log.info("Producto a eliminar: Id["+receta.getId()+" Nombre:"+receta.getNombre());
	           return recetaDao.delete(receta);
	          })
              .thenReturn("redirect:/listar?success=Baja_Exitosa")
              .onErrorResume(exception -> {
                  String message = exception.getMessage();
                  
                  try {
					message = URLEncoder.encode(message, "UTF8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					message = "Error";
				}
                  
                  return Mono.just("redirect:/listar?error="+message);  
                 });
	}

}
