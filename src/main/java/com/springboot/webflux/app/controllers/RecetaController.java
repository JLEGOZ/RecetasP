package com.springboot.webflux.app.controllers;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

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
	@GetMapping("/ingredientes")
	public Flux<Ingrediente> listarIngredientes() {
		return ingredienteDao.findAll();
	}
	@GetMapping("/raciones")
	public Flux<Racion> listarRaciones() {
		return racionDao.findAll();
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

}
