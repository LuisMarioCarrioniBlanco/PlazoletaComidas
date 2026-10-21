package com.plazoleta.plazoleta.infrastructure.output.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurantes", uniqueConstraints = @UniqueConstraint(name = "uk_restaurante_nit", columnNames = "nit"))
public class RestauranteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(nullable = false, length = 30)
    private String nit;
    @Column(nullable = false, length = 200)
    private String direccion;
    @Column(nullable = false, length = 13)
    private String telefono;
    @Column(name = "url_logo", nullable = false, length = 500)
    private String urlLogo;
    @Column(name = "propietario_id", nullable = false)
    private Long propietarioId;

    public RestauranteEntity() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getUrlLogo() { return urlLogo; }
    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }
    public Long getPropietarioId() { return propietarioId; }
    public void setPropietarioId(Long propietarioId) { this.propietarioId = propietarioId; }
}
