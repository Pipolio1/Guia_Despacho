# Resumen de la Actividad - Semana 3 Cloud Native

## Contexto de la conversacion con Kimi
Este archivo resume todo lo que se ha construido hasta el momento para poder retomar el proyecto sin perder el hilo.

---

## 1. Documentos leidos y analizados

### Instrucciones Especificas (CDY2204_EXP1_S3_Instrucciones_especificas)
- Actividad sumativa grupal: "Construyendo y desplegando una solucion Cloud Native"
- Caso: Empresa transportista necesita Sistema de Gestion de Pedidos y Generacion de Guias de Despacho

### Requisitos tecnicos:
1. **EFS temporal**: Las guias generadas se guardan temporalmente en EFS asociado al microservicio
2. **S3 automatico**: Se almacenan en S3 organizadas por fecha y transportista (`/fecha/transportista/archivo`)
3. **6 Endpoints REST**:
   - Crear guias de despacho
   - Subir guias generadas a S3
   - Descargar guias con validacion de permisos
   - Modificar o actualizar guias
   - Eliminar guias especificas
   - Consultar guias por transportista y fecha
4. **Despliegue automatizado**:
   - Imagen Docker generada y publicada en Docker Hub al hacer push a main
   - Despliegue automatico en EC2 usando GitHub Actions

### Pauta de Evaluacion (Completamente Logrado 100%)
| # | Criterio | Pts |
|---|----------|-----|
| 1 | Configura EFS correctamente, funcional y organizado | 15 |
| 2 | Sube archivos automaticamente a S3, organizados por fecha/entidad | 10 |
| 3 | Modifica y actualiza archivos en S3 correctamente | 15 |
| 4 | Descarga archivos desde S3 con integridad y funcionalidad completa | 10 |
| 5 | Consulta historial preciso y eficiente con filtros requeridos | 10 |
| 6 | Pipeline funcional: Docker image + GitHub Actions + EC2 deploy | 20 |
| 7 | Video explicativo completo demostrando todas las funciones | 20 |

---

## 2. Texto teorico estudiado
- **Amazon EFS**: Sistema de archivos compartido, elastico, gestionado por AWS, compatible NFS, acceso concurrente desde multiples instancias EC2. Ideal para almacenamiento temporal compartido.
- **Amazon S3**: Almacenamiento de objetos, acceso via API REST/SDK/CLI, organizado en buckets con prefijos (carpetas virtuales).
- **Despliegue Azul/Verde**: Estrategia de dos entornos identicos para zero-downtime.
- **CI/CD con GitHub Actions**: Pipeline automatizado para build, test, Docker push y deploy EC2.
- **Seguridad EFS**: Requiere Security Groups para comunicacion NFS (puerto 2049).

---

## 3. Proyecto generado con Spring Initializr
- **Group**: `com.transporte`
- **Artifact**: `guia-despacho`
- **Package**: `com.transporte.guia_despacho`
- **Java**: 21
- **Spring Boot**: 4.0.6
- **Dependencies**: Spring Web, Spring Boot DevTools

---

## 4. Archivos creados en el proyecto

### Configuracion
- `pom.xml` - Agregada dependencia `software.amazon.awssdk:s3:2.25.0`
- `application.properties` - Configuracion AWS, S3 bucket, EFS mount path, multipart

### Codigo Java
- `config/S3Config.java` - Cliente AWS S3 con credenciales estaticas
- `model/Guia.java` - Modelo con id, transportista, fecha, nombreArchivo, rutaEfs, rutaS3
- `service/EfsService.java` - Guarda/lee/elimina archivos temporalmente en filesystem (simula EFS)
- `service/S3Service.java` - Subir, descargar, modificar, eliminar y listar archivos en S3 organizados por `fecha/transportista/archivo`
- `controller/GuiaController.java` - 6 endpoints REST:
  - `POST /api/guias/crear` - Crear guia (guarda en EFS temporal)
  - `POST /api/guias/subir-s3/{id}` - Subir guia desde EFS a S3 (automatico)
  - `GET /api/guias/descargar/{transportista}/{fecha}/{nombreArchivo}` - Descargar con validacion
  - `PUT /api/guias/actualizar/{transportista}/{fecha}/{nombreArchivo}` - Modificar/actualizar
  - `DELETE /api/guias/eliminar/{transportista}/{fecha}/{nombreArchivo}` - Eliminar
  - `GET /api/guias/consultar?transportista=X&fecha=YYYY-MM-DD` - Consultar historial

### Docker y CI/CD
- `Dockerfile` - Build multi-stage (eclipse-temurin:21-jdk-alpine -> jre-alpine)
- `.github/workflows/deploy.yml` - Pipeline CI/CD completo:
  1. Build con Maven
  2. Login + Build + Push a Docker Hub
  3. Deploy en EC2 via SSH (docker pull + docker run con volumen EFS)

---

## 5. Estado actual
- ✅ Proyecto creado y estructurado
- ✅ Codigo implementado (modelo, servicios, controller, config)
- ✅ Dockerfile creado
- ✅ GitHub Actions workflow creado
- ✅ Compilacion exitosa (`mvnw clean compile` -> BUILD SUCCESS)
- ⏳ Pendiente: Configuracion AWS (S3, EFS, EC2)
- ⏳ Pendiente: Subir a GitHub
- ⏳ Pendiente: Configurar Secrets en GitHub
- ⏳ Pendiente: Probar pipeline y endpoints
- ⏳ Pendiente: Grabar video explicativo

---

## 6. Proximos pasos para continuar (cuando se retome)
1. Crear bucket S3 en AWS Academy
2. Crear filesystem EFS en AWS Academy
3. Lanzar instancia EC2 y montar EFS
4. Instalar Docker en EC2
5. Crear cuenta Docker Hub
6. Subir proyecto a GitHub (rama main)
7. Configurar Secrets en GitHub (Docker, AWS, EC2 SSH)
8. Hacer push para probar pipeline CI/CD
9. Probar endpoints REST con Postman/curl
10. Grabar video explicativo

---

*Resumen generado el 2026-06-04 para retomar la actividad sin perder contexto.*
