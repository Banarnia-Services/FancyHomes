package de.banarnia.fancyhomes.manager;

import de.banarnia.api.UtilMath;
import de.banarnia.api.permissions.PermissionManager;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;

import java.beans.PersistenceDelegate;
import java.util.UUID;

/**
 * Temporary fix for #4.
 * Needs to be fixed in API.
 */
public class LuckPermsHook {

    /**
     * Check a users Home-Limit in LuckPerms.
     * @param playerId Players UUID.
     * @return Max home limit, minimum 0.
     */
    public static int getLuckPermsHomeLimit(UUID playerId) {
        int maxHomes = 0;

        // Check limit for every group.
        for (Group group : PermissionManager.getGroupManager().getLoadedGroups()) {
            Tristate hasGroup = PermissionManager.getCachedPermissionData(playerId).checkPermission("group." + group.getName());
            if (hasGroup.asBoolean()) {
                String groupVal = PermissionManager.getMetaValue(group, "fancyhomes.limit");
                if (groupVal == null || !UtilMath.isInt(groupVal))
                    continue;

                int groupLimit = Integer.parseInt(groupVal);
                if (groupLimit > maxHomes)
                    maxHomes = groupLimit;
            }
        }

        // Check user limit.
        String userVal = PermissionManager.getMetaValue(playerId, "fancyhomes.limit");
        if (userVal == null || !UtilMath.isInt(userVal))
            return maxHomes;

        int userLimit = Integer.parseInt(userVal);
        if (userLimit > maxHomes)
            maxHomes = userLimit;

        return maxHomes;
    }

}
