package com.transporte.guia_despacho.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class EfsService {

    @Value("${efs.mount.path:/tmp/efs}")
    private String efsMountPath;

    public String guardarTemporal(byte[] contenido, String nombreOriginal) throws IOException {
        Path directorio = Paths.get(efsMountPath);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
        String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
        Path rutaArchivo = directorio.resolve(nombreUnico);
        Files.write(rutaArchivo, contenido);
        return rutaArchivo.toString();
    }

    public byte[] leerTemporal(String ruta) throws IOException {
        return Files.readAllBytes(Paths.get(ruta));
    }

    public void eliminarTemporal(String ruta) throws IOException {
        if (ruta != null) {
            Files.deleteIfExists(Paths.get(ruta));
        }
    }

    public boolean existeTemporal(String ruta) {
        return ruta != null && Files.exists(Paths.get(ruta));
    }
}
