# Deployment Automático EC2

Scripts para automatizar el deployment de Fullsound Backend en EC2.

## Configuración Inicial (Una sola vez)

Cuando crees una nueva instancia EC2 en AWS Learner Lab:

```bash
# Conectarse a EC2
ssh -i vockey.pem ec2-user@<IP-PUBLICA>

# Ejecutar script de setup
curl -fsSL https://raw.githubusercontent.com/VECTORG99/FULLSOUND-SPRINGBOOT/main/scripts/setup-ec2.sh | bash
```

Esto instalará:
- Java 17
- AWS CLI
- Estructura de directorios
- Servicio systemd (opcional)

## Deployment Manual

Para desplegar manualmente la aplicación:

```bash
# Conectarse a EC2
ssh -i vockey.pem ec2-user@<IP-PUBLICA>

# Configurar variables de entorno
export DB_PASSWORD='tu_password_supabase'
export JWT_SECRET='tu_secret_jwt'

# Ejecutar deployment
curl -fsSL https://raw.githubusercontent.com/VECTORG99/FULLSOUND-SPRINGBOOT/main/scripts/deploy-ec2.sh | bash
```

## Deployment Automático con GitHub Actions

Si tu instancia EC2 tiene:
- Tag `Name=fullsound-backend`
- SSM Agent habilitado
- IAM role con permisos SSM

El workflow ejecutará automáticamente el deployment cuando hagas push a `main`.

## Verificar Aplicación

```bash
# Ver logs en tiempo real
tail -f /home/ec2-user/app/app.log

# Verificar proceso
ps aux | grep java

# Health check
curl http://localhost:8080/actuator/health
```

## Detener Aplicación

```bash
pkill -f fullsound-backend-latest.jar
```

## Troubleshooting

**Aplicación no inicia:**
```bash
cd /home/ec2-user/app
cat app.log
```

**Variables de entorno no definidas:**
```bash
export DB_PASSWORD='valor'
export JWT_SECRET='valor'
./deploy-ec2.sh
```

**JAR no descarga desde S3:**
```bash
aws s3 ls s3://fullsound-deployments-471143100372/
aws configure list
```
