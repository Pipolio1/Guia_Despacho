# 📦 Guía de Despacho - API REST

Servicio RESTful desarrollado con **Spring Boot** y **Java 21** para la gestión de guías de despacho, integrando **Amazon S3** para almacenamiento persistente y **Amazon EFS** para almacenamiento temporal.

---

## 🏗️ Arquitectura

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│   Cliente   │────▶│   API REST  │────▶│   Amazon S3     │
│  (Postman)  │     │  (EC2:8080) │     │ (Persistencia)  │
└─────────────┘     └──────┬──────┘     └─────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  Amazon EFS │
                    │  (/mnt/efs) │
                    │  (Temporal) │
                    └─────────────┘
```

### Flujo de datos
1. **Crear guía** → Archivo se guarda temporalmente en **EFS**
2. **Subir a S3** → Archivo se traslada de EFS a **S3** con estructura `fecha/transportista/archivo`
3. **Consultar / Descargar / Actualizar / Eliminar** → Operaciones directas sobre **S3**

---

## 🚀 Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Spring Boot | 4.0.6 | Framework backend |
| Java | 21 | Lenguaje de programación |
| AWS SDK S3 | 2.25.0 | Interacción con Amazon S3 |
| Docker | 25.0.14 | Contenedorización |
| GitHub Actions | - | CI/CD automatizado |
| Amazon EC2 | - | Servidor de producción |
| Amazon S3 | - | Almacenamiento de archivos |
| Amazon EFS | - | Almacenamiento temporal |

---

## 🔌 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/guias/crear` | Crea una guía y guarda el archivo en EFS (temporal) |
| `POST` | `/api/guias/subir-s3/{id}` | Sube la guía desde EFS a S3 |
| `GET` | `/api/guias/descargar/{transportista}/{fecha}/{archivo}` | Descarga un archivo desde S3 |
| `PUT` | `/api/guias/actualizar/{transportista}/{fecha}/{archivo}` | Actualiza un archivo en S3 |
| `DELETE` | `/api/guias/eliminar/{transportista}/{fecha}/{archivo}` | Elimina un archivo de S3 |
| `GET` | `/api/guias/consultar?transportista=X&fecha=YYYY-MM-DD` | Lista guías filtradas |

### Ejemplo de uso

**Crear guía:**
```bash
curl -X POST http://54.157.246.101:8080/api/guias/crear \
  -F "transportista=TransportistaX" \
  -F "fecha=2025-06-04" \
  -F "archivo=@guia.pdf"
```

**Subir a S3:**
```bash
curl -X POST http://54.157.246.101:8080/api/guias/subir-s3/1
```

**Consultar:**
```bash
curl "http://54.157.246.101:8080/api/guias/consultar?transportista=TransportistaX&fecha=2025-06-04"
```

---

## ☁️ Infraestructura AWS

| Recurso | Identificador / URL |
|---------|---------------------|
| **EC2** | `54.157.246.101` (Amazon Linux 2023) |
| **S3 Bucket** | `s3://guias-transporte-fg-001` |
| **EFS** | `fs-0a351ea2d51499999` montado en `/mnt/efs` |
| **Security Group EC2** | `guia-despacho-sg` (SSH 22, HTTP 8080) |
| **Security Group EFS** | `sg-065cf202cf9e5fa87` (NFS 2049) |

---

## 🔄 Pipeline CI/CD

El despliegue está completamente automatizado mediante **GitHub Actions**:

```
Push a main
    │
    ▼
┌─────────────────┐
│  Maven Build    │
│  ./mvnw package │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Docker Build   │
│  docker build   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Docker Push    │
│  pipolio/guia-  │
│  despacho:latest│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  SSH Deploy     │
│  EC2: docker    │
│  pull & run     │
└─────────────────┘
```

### Variables de entorno requeridas

| Variable | Descripción |
|----------|-------------|
| `AWS_ACCESS_KEY_ID` | Credencial de AWS Academy |
| `AWS_SECRET_ACCESS_KEY` | Credencial de AWS Academy |
| `AWS_SESSION_TOKEN` | Token de sesión (AWS Academy) |
| `AWS_REGION` | Región (us-east-1) |
| `AWS_S3_BUCKET` | Nombre del bucket S3 |
| `EFS_MOUNT_PATH` | Ruta de montaje EFS (`/mnt/efs`) |

---

## 🐳 Docker

### Construcción local
```bash
docker build -t guia-despacho .
```

### Ejecución local
```bash
docker run -p 8080:8080 \
  -e AWS_ACCESS_KEY_ID=xxx \
  -e AWS_SECRET_ACCESS_KEY=xxx \
  -e AWS_SESSION_TOKEN=xxx \
  -e AWS_REGION=us-east-1 \
  -e AWS_S3_BUCKET=guias-transporte-fg-001 \
  -e EFS_MOUNT_PATH=/tmp/efs \
  guia-despacho
```

### Imagen publicada
```bash
docker pull pipolio/guia-despacho:latest
```

---

## 📁 Estructura del proyecto

```
guia-despacho/
├── .github/
│   └── workflows/
│       └── deploy.yml          # Pipeline CI/CD
├── src/
│   └── main/
│       ├── java/com/transporte/guia_despacho/
│       │   ├── config/
│       │   │   └── S3Config.java      # Configuración AWS S3
│       │   ├── controller/
│       │   │   └── GuiaController.java # Endpoints REST
│       │   ├── service/
│       │   │   ├── S3Service.java     # Lógica S3
│       │   │   └── EfsService.java    # Lógica EFS
│       │   └── GuiaDespachoApplication.java
│       └── resources/
│           └── application.properties  # Configuración Spring
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 📝 Autores

- **Felipe Garrido** - Desarrollo e implementación
- **Equipo CDY2204** - Proyecto semana 3

---

## 📄 Licencia

Proyecto académico - CDY2204 Experiencia 1, Semana 3.
