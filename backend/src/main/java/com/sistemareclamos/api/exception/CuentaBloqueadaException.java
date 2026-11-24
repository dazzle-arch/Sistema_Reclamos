package com.sistemareclamos.api.exception;

public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException(String mensaje) { super(mensaje); }
}