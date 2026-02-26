package org.martin.dto;

/**
 * Used when the client requests user information. Makes it easier for packaging information
 * **/
public record UserRequestDTO(String username, String icon, String permittingLevel, int flames) {

}
