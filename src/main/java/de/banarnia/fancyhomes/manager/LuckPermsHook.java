package de.banarnia.fancyhomes.manager;

import de.banarnia.api.UtilMath;
import de.banarnia.api.permissions.PermissionManager;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.util.Tristate;

import java.util.UUID;

/**
 * Temporary fix for #4.
 * Needs to be fixed in API.
 */
public class LuckPermsHook {

    /**
     * Get a meta permission value of type int. Will check user and users groups.
     * @param playerId Players UUID.
     * @param metaPermission Meta permission String.
     * @return Highest meta value, either 0, user meta value or group meta value.
     */
    public static int getMetaPermission(UUID playerId, String metaPermission) {
        int maxHomes = 0;

        // Check limit for every group.
        for (Group group : PermissionManager.getGroupManager().getLoadedGroups()) {
            Tristate hasGroup = PermissionManager.getCachedPermissionData(playerId).checkPermission("group." + group.getName());
            if (hasGroup.asBoolean()) {
                String groupVal = PermissionManager.getMetaValue(group, metaPermission);
                if (groupVal == null || !UtilMath.isInt(groupVal))
                    continue;

                int groupLimit = Integer.parseInt(groupVal);
                if (groupLimit > maxHomes)
                    maxHomes = groupLimit;
            }
        }

        // Check user limit.
        String userVal = PermissionManager.getMetaValue(playerId, metaPermission);
        if (userVal == null || !UtilMath.isInt(userVal))
            return maxHomes;

        int userLimit = Integer.parseInt(userVal);
        if (userLimit > maxHomes)
            maxHomes = userLimit;

        return maxHomes;
    }

}
