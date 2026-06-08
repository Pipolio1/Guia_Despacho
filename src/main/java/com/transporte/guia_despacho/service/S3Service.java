package com.transporte.guia_despacho.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String subirArchivo(String transportista, LocalDate fecha, String nombreArchivo, byte[] contenido) {
        String key = String.format("%s/%s/%s", fecha.toString(), transportista, nombreArchivo);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(contenido));
        return key;
    }

    public byte[] descargarArchivo(String transportista, LocalDate fecha, String nombreArchivo) {
        String key = String.format("%s/%s/%s", fecha.toString(), transportista, nombreArchivo);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            return s3Client.getObject(request).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error al descargar archivo de S3: " + e.getMessage(), e);
        }
    }

    public String actualizarArchivo(String transportista, LocalDate fecha, String nombreArchivo, byte[] nuevoContenido) {
        String key = String.format("%s/%s/%s", fecha.toString(), transportista, nombreArchivo);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(nuevoContenido));
        return key;
    }

    public void eliminarArchivo(String transportista, LocalDate fecha, String nombreArchivo) {
        String key = String.format("%s/%s/%s", fecha.toString(), transportista, nombreArchivo);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    public List<String> listarArchivos(String transportista, LocalDate fecha) {
        String prefix = String.format("%s/%s/", fecha.toString(), transportista);

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        List<String> archivos = new ArrayList<>();
        for (S3Object obj : response.contents()) {
            archivos.add(obj.key());
        }
        return archivos;
    }

    public boolean existeArchivo(String transportista, LocalDate fecha, String nombreArchivo) {
        String key = String.format("%s/%s/%s", fecha.toString(), transportista, nombreArchivo);
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
