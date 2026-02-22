package omega.sleepy.data;

import omega.sleepy.util.enums.PermittingLevel;
import omega.sleepy.util.enums.ProfileIcons;

public record UserWithExtras(String username, String passwordHash, PermittingLevel permittingLevel, String registered_at, String last_login, ProfileIcons ProfileIcon, int flames, String style) {
}
