package org.martin.data;

import org.martin.util.enums.PermittingLevel;
import org.martin.util.enums.ProfileIcons;

/**
 * Record class holding all user information from all tables username is a primary/foreign key
 * @see org.martin.dao.UserDao
 * **/
public record UserWithExtras(String username, String passwordHash, PermittingLevel permittingLevel, String registered_at, String last_login, ProfileIcons ProfileIcon, int flames, String style) {
}
