#!/bin/bash
# Script de deployment automático para EC2
# Se ejecuta en la instancia EC2 para descargar y ejecutar el JAR desde S3

set -e

# Configuración
APP_NAME="fullsound-backend"
S3_BUCKET="fullsound-deployments-471143100372"
JAR_NAME="fullsound-backend-latest.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="$APP_DIR/app.log"

echo "========================================="
echo "Iniciando deployment de $APP_NAME"
echo "========================================="

# Crear directorio de aplicación
mkdir -p $APP_DIR
cd $APP_DIR

# Detener aplicación si está corriendo
echo "Deteniendo aplicación anterior..."
pkill -f "$JAR_NAME" || echo "No hay proceso previo ejecutándose"
sleep 5

# Descargar JAR desde S3
echo "Descargando JAR desde S3..."
aws s3 cp s3://$S3_BUCKET/$JAR_NAME $APP_DIR/$JAR_NAME
chmod +x $APP_DIR/$JAR_NAME

# Verificar variables de entorno
if [ -z "$DB_PASSWORD" ]; then
    echo "ERROR: Variable DB_PASSWORD no definida"
    exit 1
fi

if [ -z "$JWT_SECRET" ]; then
    echo "ERROR: Variable JWT_SECRET no definida"
    exit 1
fi

# Crear archivo de configuración
cat > $APP_DIR/application-prod.properties << EOF
server.port=8080
server.forward-headers-strategy=framework
spring.datasource.url=jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
spring.datasource.username=postgres.kivpcepyhfpqjfoycwel
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
EOF

# Ejecutar aplicación en background
echo "Iniciando aplicación..."
nohup java -jar $APP_DIR/$JAR_NAME \
    --spring.config.location=$APP_DIR/application-prod.properties \
    > $LOG_FILE 2>&1 &

PID=$!
echo "Aplicación iniciada con PID: $PID"

# Esperar y verificar que la aplicación arrancó
sleep 10

if ps -p $PID > /dev/null; then
    echo "========================================="
    echo "✓ Deployment exitoso"
    echo "✓ PID: $PID"
    echo "✓ Log: $LOG_FILE"
    echo "========================================="
    echo "Últimas líneas del log:"
    tail -n 20 $LOG_FILE
else
    echo "========================================="
    echo "✗ ERROR: La aplicación falló al iniciar"
    echo "========================================="
    echo "Log de error:"
    cat $LOG_FILE
    exit 1
fi
