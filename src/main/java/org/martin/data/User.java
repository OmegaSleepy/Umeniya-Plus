package org.martin.data;

import org.martin.util.enums.PermittingLevel;
import org.martin.util.enums.ProfileIcons;

/**
 * Represents just the user table (without iskri)
 * @see org.martin.dao.UserDao
 * **/
public record User(String username, String passwordHash, PermittingLevel permittingLevel, String registered_at, String last_login, ProfileIcons ProfileIcon) {
}
