package com.transporte.guia_despacho.model;

import java.time.LocalDate;

public class Guia {
    private String id;
    private String transportista;
    private LocalDate fecha;
    private String nombreArchivo;
    private String rutaEfs;
    private String rutaS3;

    public Guia() {}

    public Guia(String id, String transportista, LocalDate fecha, String nombreArchivo) {
        this.id = id;
        this.transportista = transportista;
        this.fecha = fecha;
        this.nombreArchivo = nombreArchivo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaEfs() { return rutaEfs; }
    public void setRutaEfs(String rutaEfs) { this.rutaEfs = rutaEfs; }

    public String getRutaS3() { return rutaS3; }
    public void setRutaS3(String rutaS3) { this.rutaS3 = rutaS3; }

    public String getS3Key() {
        return String.format("%s/%s/%s", fecha.toString(), transportista, nombreArchivo);
    }
}
