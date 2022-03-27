package com.springboot.webflux.app.models.documents;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

public class Racion {
	@Id
	private String id;
	private int cantidad;
	@NotNull
	private Ingrediente ingrediente;
	
	public Racion() {
		
	}
	
	public Racion(int cantidad, Ingrediente ingrediente) {
		this.cantidad = cantidad;
		this.ingrediente = ingrediente;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public Ingrediente getIngrediente() {
		return ingrediente;
	}
	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}
	

}
