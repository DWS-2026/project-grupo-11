package es.footleague.app.dto;

public record PasswordChangeDTO(
    String oldPassword,
    String newPassword
) {}
