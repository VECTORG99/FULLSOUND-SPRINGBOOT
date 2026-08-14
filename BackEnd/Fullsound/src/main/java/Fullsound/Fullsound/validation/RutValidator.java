package Fullsound.Fullsound.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidator implements ConstraintValidator<ValidRut, String> {

    @Override
    public void initialize(ValidRut constraintAnnotation) {
    }

    @Override
    public boolean isValid(String rut, ConstraintValidatorContext context) {
        // Si el RUT es null o vacío, dejamos que @NotBlank lo maneje
        if (rut == null || rut.trim().isEmpty()) {
            return true;
        }

        // Remover puntos, guiones y espacios
        String rutLimpio = rut.replaceAll("[.\\-\\s]", "");

        // Validar formato básico
        if (rutLimpio.length() < 8) {
            return false;
        }

        // Validar que tenga formato correcto (números y último puede ser K)
        if (!rutLimpio.matches("^[0-9]+[0-9kK]$")) {
            return false;
        }

        // Separar número y dígito verificador
        String numero = rutLimpio.substring(0, rutLimpio.length() - 1);
        char dvIngresado = Character.toUpperCase(rutLimpio.charAt(rutLimpio.length() - 1));

        // Calcular dígito verificador
        int suma = 0;
        int multiplicador = 2;

        for (int i = numero.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(numero.charAt(i)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }

        int resto = suma % 11;
        char dvCalculado = resto == 0 ? '0' : (resto == 1 ? 'K' : (char) ('0' + (11 - resto)));

        return dvIngresado == dvCalculado;
    }
}
