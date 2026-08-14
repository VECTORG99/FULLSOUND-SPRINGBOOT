# GitHub Actions Workflows - FullSound Backend

## Deploy Backend to AWS App Runner

Este workflow automatiza el deployment del backend a AWS App Runner cuando se hace push a la rama `main`.

### Archivo
`.github/workflows/deploy-backend-aws.yml`

### Triggers

El workflow se ejecuta en los siguientes casos:

1. **Push a main**: Cuando se hace push a la rama `main` y hay cambios en:
   - `BackEnd/Fullsound/**`
   - `.github/workflows/deploy-backend-aws.yml`

2. **Manual**: Se puede ejecutar manualmente desde la pestaña Actions en GitHub usando `workflow_dispatch`

### Pasos del Workflow

1. **Checkout code**: Clona el repositorio
2. **Configure AWS credentials**: Configura las credenciales de AWS
3. **Deploy to App Runner**: Despliega la aplicación a AWS App Runner
4. **App Runner URL**: Muestra la URL del servicio desplegado

---

## Configuración de Secretos en GitHub

Para que el workflow funcione correctamente, debes configurar los siguientes secretos en GitHub:

**Ubicación**: `Settings > Secrets and variables > Actions > New repository secret`

### Secretos Requeridos

#### 1. AWS_ACCESS_KEY_ID
- **Descripción**: Access Key ID de tu usuario IAM de AWS
- **Cómo obtenerlo**:
  1. Ir a AWS IAM Console
  2. Seleccionar "Users" > tu usuario
  3. Pestaña "Security credentials"
  4. Click en "Create access key"
  5. Copiar el "Access key ID"

#### 2. AWS_SECRET_ACCESS_KEY
- **Descripción**: Secret Access Key de tu usuario IAM de AWS
- **Cómo obtenerlo**:
  1. Se genera junto con el Access Key ID
  2. **IMPORTANTE**: Solo se muestra una vez, guárdala de forma segura
  3. Si la pierdes, tendrás que crear una nueva access key

#### 3. APPRUNNER_SOURCE_CONNECTION_ARN
- **Descripción**: ARN de la conexión de App Runner con GitHub
- **Cómo obtenerlo**:
  1. Ir a [AWS App Runner Console](https://console.aws.amazon.com/apprunner)
  2. En el menú lateral, ir a "Source code connections"
  3. Click en "Create connection"
  4. Seleccionar "GitHub"
  5. Darle un nombre (ej: "fullsound-github-connection")
  6. Click en "Next"
  7. Autorizar la conexión con GitHub
  8. Una vez creada, copiar el ARN (formato: `arn:aws:apprunner:region:account-id:connection/name/id`)
  9. Agregar este ARN como secreto en GitHub

#### 4. DB_PASSWORD (Opcional)
- **Descripción**: Password de la base de datos Supabase
- **Cómo obtenerlo**:
  1. Ir a tu proyecto en Supabase
  2. Settings > Database
  3. Copiar el password de la base de datos

#### 5. JWT_SECRET (Opcional)
- **Descripción**: Secret para generar tokens JWT
- **Sugerencia**: Generar un string aleatorio largo y seguro
- **Ejemplo de generación**:
  ```bash
  openssl rand -base64 32
  ```

### Permisos IAM Requeridos

El usuario IAM de AWS debe tener los siguientes permisos:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "apprunner:*",
        "iam:PassRole",
        "iam:CreateServiceLinkedRole",
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "*"
    }
  ]
}
```

---

## Uso del Workflow

### Deployment Automático

1. Hacer cambios en el código del backend (`BackEnd/Fullsound/`)
2. Commit y push a la rama `main`
3. El workflow se ejecutará automáticamente
4. Puedes ver el progreso en la pestaña "Actions" de GitHub
5. Una vez completado, se mostrará la URL del servicio desplegado

### Deployment Manual

1. Ir a la pestaña "Actions" en GitHub
2. Seleccionar "Deploy Backend to AWS App Runner"
3. Click en "Run workflow"
4. Seleccionar la rama `main`
5. Click en "Run workflow"

---

## Verificación del Deployment

Después de que el workflow se complete exitosamente:

1. Revisa los logs en la pestaña "Actions"
2. Busca la línea con "App Runner URL" para obtener la URL del servicio
3. Accede a la URL para verificar que el backend esté funcionando
4. Verifica los logs en AWS CloudWatch si es necesario

---

## Troubleshooting

### Error: "Invalid AWS credentials"
- Verifica que los secretos `AWS_ACCESS_KEY_ID` y `AWS_SECRET_ACCESS_KEY` estén configurados correctamente
- Asegúrate de que las credenciales no hayan expirado

### Error: "Source connection not found"
- Verifica que el `APPRUNNER_SOURCE_CONNECTION_ARN` sea correcto
- Asegúrate de que la conexión con GitHub esté activa en AWS App Runner

### Error: "Service already exists"
- El servicio `fullsound-backend` ya existe en App Runner
- Puedes modificar el nombre del servicio en el workflow si es necesario

### El workflow no se ejecuta
- Verifica que los cambios estén en las rutas especificadas (`BackEnd/Fullsound/**`)
- Asegúrate de estar pusheando a la rama `main`

---

## Referencias

- [GitHub Actions for AWS](https://github.com/aws-actions)
- [App Runner Deploy Action](https://github.com/awslabs/amazon-app-runner-deploy)
- [AWS App Runner Documentation](https://docs.aws.amazon.com/apprunner/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

---

## Notas Importantes

- El workflow usa Docker para construir y desplegar la aplicación
- Asegúrate de que el `Dockerfile` esté en la raíz del proyecto backend
- El puerto configurado es `8080` (ajusta según tu configuración)
- Los recursos asignados son: 1 vCPU y 2 GB de memoria (ajusta según necesidad)
- La región de AWS configurada es `us-east-1` (ajusta según tu configuración)
