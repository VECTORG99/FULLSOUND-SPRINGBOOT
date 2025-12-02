#!/bin/bash
# Script de configuración inicial de instancia EC2
# Ejecutar UNA SOLA VEZ al crear la instancia

set -e

echo "========================================="
echo "Configurando instancia EC2"
echo "========================================="

# Actualizar sistema
echo "Actualizando paquetes del sistema..."
sudo yum update -y

# Instalar Java 17
echo "Instalando Java 17..."
sudo yum install java-17-amazon-corretto-devel -y

# Verificar instalación
echo "Verificando Java..."
java -version

# Instalar AWS CLI (si no está)
echo "Verificando AWS CLI..."
aws --version || {
    echo "Instalando AWS CLI..."
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
    unzip awscliv2.zip
    sudo ./aws/install
    rm -rf aws awscliv2.zip
}

# Crear directorio de aplicación
echo "Creando estructura de directorios..."
mkdir -p /home/ec2-user/app
mkdir -p /home/ec2-user/scripts

# Descargar script de deployment
echo "Configurando scripts de deployment..."
cd /home/ec2-user/scripts

# Crear servicio systemd para auto-inicio
echo "Creando servicio systemd..."
sudo tee /etc/systemd/system/fullsound-backend.service > /dev/null << 'EOF'
[Unit]
Description=Fullsound Backend Application
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/app
Environment="DB_PASSWORD=CHANGE_ME"
Environment="JWT_SECRET=CHANGE_ME"
ExecStart=/usr/bin/java -jar /home/ec2-user/app/fullsound-backend-latest.jar --spring.config.location=/home/ec2-user/app/application-prod.properties
Restart=on-failure
RestartSec=10
StandardOutput=append:/home/ec2-user/app/app.log
StandardError=append:/home/ec2-user/app/app.log

[Install]
WantedBy=multi-user.target
EOF

echo "========================================="
echo "✓ Configuración completada"
echo "========================================="
echo ""
echo "PRÓXIMOS PASOS:"
echo "1. Editar variables de entorno en el servicio:"
echo "   sudo nano /etc/systemd/system/fullsound-backend.service"
echo ""
echo "2. Descargar y ejecutar la aplicación:"
echo "   cd /home/ec2-user/scripts"
echo "   curl -O https://raw.githubusercontent.com/VECTORG99/FULLSOUND-SPRINGBOOT/main/scripts/deploy-ec2.sh"
echo "   chmod +x deploy-ec2.sh"
echo "   export DB_PASSWORD='tu_password'"
echo "   export JWT_SECRET='tu_secret'"
echo "   ./deploy-ec2.sh"
echo ""
echo "3. (Opcional) Habilitar auto-inicio con systemd:"
echo "   sudo systemctl daemon-reload"
echo "   sudo systemctl enable fullsound-backend"
echo "   sudo systemctl start fullsound-backend"
echo "========================================="
