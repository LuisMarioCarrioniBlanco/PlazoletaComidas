package com.plazoleta.plazoleta.infrastructure.output.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "platos")
public class PlatoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(nullable = false)
    private Integer precio;
    @Column(nullable = false, length = 1000)
    private String descripcion;
    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;
    @Column(nullable = false, length = 80)
    private String categoria;
    @Column(nullable = false)
    private boolean activo;
    @Column(name = "restaurante_id", nullable = false)
    private Long restauranteId;

    public PlatoEntity() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
}
