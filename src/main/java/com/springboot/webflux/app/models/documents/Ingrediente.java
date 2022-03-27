package com.springboot.webflux.app.models.documents;

import org.springframework.data.annotation.Id;

public class Ingrediente {
	@Id
	private String id;
	private String nombre;
	
	public Ingrediente() {
		
	}
	public Ingrediente(String nombre) {
		this.nombre = nombre;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

}
