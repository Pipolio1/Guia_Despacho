package com.transporte.guia_despacho.controller;

import com.transporte.guia_despacho.model.Guia;
import com.transporte.guia_despacho.service.EfsService;
import com.transporte.guia_despacho.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    @Autowired
    private EfsService efsService;

    @Autowired
    private S3Service s3Service;

    private final Map<String, Guia> guiasTemporales = new HashMap<>();

    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Guia> crearGuia(
            @RequestParam("transportista") String transportista,
            @RequestParam("fecha") String fecha,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {

        String id = UUID.randomUUID().toString();
        LocalDate localDate = LocalDate.parse(fecha);
        String nombreArchivo = Objects.requireNonNull(archivo.getOriginalFilename());

        Guia guia = new Guia(id, transportista, localDate, nombreArchivo);
        String rutaEfs = efsService.guardarTemporal(archivo.getBytes(), nombreArchivo);
        guia.setRutaEfs(rutaEfs);
        guiasTemporales.put(id, guia);

        return ResponseEntity.status(HttpStatus.CREATED).body(guia);
    }

    @PostMapping("/subir-s3/{id}")
    public ResponseEntity<Guia> subirAS3(@PathVariable String id) throws IOException {
        Guia guia = guiasTemporales.get(id);
        if (guia == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] contenido = efsService.leerTemporal(guia.getRutaEfs());
        String s3Key = s3Service.subirArchivo(guia.getTransportista(), guia.getFecha(), guia.getNombreArchivo(), contenido);
        guia.setRutaS3(s3Key);

        efsService.eliminarTemporal(guia.getRutaEfs());
        guia.setRutaEfs(null);

        return ResponseEntity.ok(guia);
    }

    @GetMapping("/descargar/{transportista}/{fecha}/{nombreArchivo}")
    public ResponseEntity<byte[]> descargarGuia(
            @PathVariable String transportista,
            @PathVariable String fecha,
            @PathVariable String nombreArchivo) {

        LocalDate localDate = LocalDate.parse(fecha);
        if (!s3Service.existeArchivo(transportista, localDate, nombreArchivo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] contenido = s3Service.descargarArchivo(transportista, localDate, nombreArchivo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", nombreArchivo);

        return ResponseEntity.ok().headers(headers).body(contenido);
    }

    @PutMapping(value = "/actualizar/{transportista}/{fecha}/{nombreArchivo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> actualizarGuia(
            @PathVariable String transportista,
            @PathVariable String fecha,
            @PathVariable String nombreArchivo,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {

        LocalDate localDate = LocalDate.parse(fecha);
        String s3Key = s3Service.actualizarArchivo(transportista, localDate, nombreArchivo, archivo.getBytes());

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Archivo actualizado correctamente");
        response.put("rutaS3", s3Key);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{transportista}/{fecha}/{nombreArchivo}")
    public ResponseEntity<Map<String, String>> eliminarGuia(
            @PathVariable String transportista,
            @PathVariable String fecha,
            @PathVariable String nombreArchivo) {

        LocalDate localDate = LocalDate.parse(fecha);
        s3Service.eliminarArchivo(transportista, localDate, nombreArchivo);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Archivo eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/consultar")
    public ResponseEntity<List<String>> consultarGuias(
            @RequestParam("transportista") String transportista,
            @RequestParam("fecha") String fecha) {

        LocalDate localDate = LocalDate.parse(fecha);
        List<String> archivos = s3Service.listarArchivos(transportista, localDate);
        return ResponseEntity.ok(archivos);
    }
}
