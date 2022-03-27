package com.springboot.webflux.app.models.documents;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

public class Receta {
	@Id
	private String id;
	@NotNull
	@NotEmpty(message ="El nombre no debe estar vacío")
	private String nombre;
	@NotNull
	@Min(value=1, message = "Dificultad no debe ser menor a 1")
	@Max(value=10, message = "Dificultad no debe ser mayor a 10")
	private int dificultad;
	private String tiempoPreparacion;
	private List <Racion> raciones;
	private String descripcion;

	public Receta() {
		
	}
	
	public Receta(String nombre, int dificultad, String tiempoPreparacion, List<Racion>raciones, String descripcion) {
		this.nombre = nombre;
		this.dificultad = dificultad;
		this.tiempoPreparacion=tiempoPreparacion;
		this.raciones = raciones;
		this.descripcion = descripcion;
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
	public int getDificultad() {
		return dificultad;
	}
	public void setDificultad(int dificultad) {
		this.dificultad = dificultad;
	}
	public String getTiempoPreparacion() {
		return tiempoPreparacion;
	}
	public void setTiempoPreparacion(String tiempoPreparacion) {
		this.tiempoPreparacion = tiempoPreparacion;
	}
	public List<Racion> getRaciones() {
		return raciones;
	}
	public void setRaciones(List<Racion> raciones) {
		this.raciones = raciones;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
