package org.martin.data;

import org.martin.util.enums.PermittingLevel;
import org.martin.util.enums.ProfileIcons;

public record UserWithExtras(String username, String passwordHash, PermittingLevel permittingLevel, String registered_at, String last_login, ProfileIcons ProfileIcon, int flames, String style) {
}
