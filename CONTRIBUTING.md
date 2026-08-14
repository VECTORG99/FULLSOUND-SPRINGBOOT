# Contribuir a FullSound

¡Gracias por tu interés en contribuir a este proyecto! Este es un proyecto
educativo, pero las contribuciones son bienvenidas.

## 📚 Sobre este proyecto

Este repositorio forma parte de mi formación temprana en **DUOC UC** (Instituto
Profesional). Las prácticas aquí reflejadas corresponden al momento de
desarrollo y pueden no representar estándares actuales. Ten esto en cuenta al
revisar el código.

## 🚀 Cómo contribuir

1. **Haz un fork** del repositorio.
2. **Crea una rama** descriptiva desde `main`:
   ```bash
   git checkout -b feature/mi-nueva-funcionalidad
   ```
3. **Realiza tus cambios** manteniendo el código limpio y documentado.
4. **Ejecuta los tests** antes de enviar tu cambio:
   ```bash
   cd BackEnd/Fullsound
   mvn clean test -Dspring.profiles.active=test
   ```
5. **Haz commit** usando [Conventional Commits](https://www.conventionalcommits.org/):
   ```bash
   git commit -m "feat: descripción breve del cambio"
   ```
6. **Abre un Pull Request** hacia `main` describiendo el cambio y la motivación.

## 📋 Convenciones

- **Mensajes de commit:** usa [Conventional Commits](https://www.conventionalcommits.org/)
  (`feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:`).
- **Estilo de código:** sigue las convenciones de Java y Spring Boot.
- **Tests:** incluye tests para nuevas funcionalidades o correcciones de bugs.
- **Documentación:** actualiza el README u otra documentación relevante si tu
  cambio lo requiere.

## 🐛 Reportar bugs

Abre un [issue](https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT/issues/new)
usando la plantilla de bug report, incluyendo:

- Pasos para reproducir
- Comportamiento esperado vs. actual
- Versión de Java / Spring Boot / entorno

## 🔒 Seguridad

**Nunca subas credenciales, contraseñas o secretos al repositorio.** Si
encuentras un secreto expuesto, repórtalo de forma privada en lugar de abrir un
issue público.

## 📄 Licencia

Al contribuir, aceptas que tus cambios se publiquen bajo la licencia [MIT](LICENSE).
