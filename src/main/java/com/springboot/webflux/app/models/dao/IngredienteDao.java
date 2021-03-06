package com.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springboot.webflux.app.models.documents.Ingrediente;

public interface IngredienteDao extends ReactiveMongoRepository<Ingrediente, String> {

}
