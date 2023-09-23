package de.banarnia.fancyhomes.api.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.track.Track;
import net.luckperms.api.track.TrackManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class PermissionManager {

    private static LuckPerms api = LuckPermsProvider.get();

    // Managers.

    /**
     * Get the LuckPerms user manager.
     * @return UserManager instance.
     */
    public static UserManager getUserManager() {
        return api.getUserManager();
    }

    /**
     * Get the LuckPerms group manager.
     * @return GroupManager instance.
     */
    public static GroupManager getGroupManager() {
        return api.getGroupManager();
    }

    /**
     * Get the LuckPerms track manager.
     * @return TrackManager instance.
     */
    public static TrackManager getTrackManager() {
        return api.getTrackManager();
    }

    /**
     * Get the LuckPerms context manager.
     * @return ContextManager instance.
     */
    public static ContextManager getContextManager() {
        return api.getContextManager();
    }

    // Users, groups and tracks.

    /**
     * Check if a user is loaded.
     * @param playerId Players id.
     * @return True if user is loaded, else false.
     */
    public static boolean isUserLoaded(UUID playerId) {
        return getUserManager().isLoaded(playerId);
    }

    /**
     * Load a user.
     * @param playerId Player id.
     * @return Future user.
     */
    public static CompletableFuture<User> loadUser(UUID playerId) {
        return getUserManager().loadUser(playerId);
    }

    /**
     * Get a user immediately.
     * @param playerId Player id.
     * @return User instance.
     */
    public static User getUser(UUID playerId) {
        return getUserManager().getUser(playerId);
    }

    /**
     * Get a group by its name.
     * @param name Group name.
     * @return Group instance if exists, else null.
     */
    public static Group getGroup(String name) {
        return getGroupManager().getGroup(name);
    }

    /**
     * Save a group and push an update.
     * @param group Group instance.
     * @return Future completion.
     */
    public static CompletableFuture<Void> saveAndPushUpdate(Group group) {
        return getGroupManager().saveGroup(group).thenRun(() -> {
            api.getMessagingService().ifPresent(service -> {
                service.pushUpdate();
            });
        });
    }

    /**
     * Save a user and push an update.
     * @param user User instance.
     * @return Future completion.
     */
    public static CompletableFuture<Void> saveAndPushUpdate(User user) {
        return getUserManager().saveUser(user).thenRun(() -> {
            api.getMessagingService().ifPresent(service -> {
                service.pushUserUpdate(user);
            });
        });
    }

    /**
     * Save a user and push an update.
     * @param playerId Players id.
     * @return Future completion.
     */
    public static CompletableFuture<Void> saveAndPushUpdate(UUID playerId) {
        return saveAndPushUpdate(getUser(playerId));
    }

    /**
     * Get a track by its name.
     * @param name Track name.
     * @return Track instance if exists, else null.
     */
    public static Track getTrack(String name) {
        return getTrackManager().getTrack(name);
    }

    // Save & modify.

    /**
     * Saves a holders data.
     * @param holder Holder instance.
     */
    public static CompletableFuture<Void> saveHolder(PermissionHolder holder) {
        return holder instanceof User ? getUserManager().saveUser((User) holder) : getGroupManager().saveGroup((Group) holder);
    }

    /**
     * Saves a users data.
     * @param user User instance.
     */
    public static CompletableFuture<Void> saveUser(User user) {
        return saveHolder(user);
    }

    /**
     * Saves a groups data.
     * @param group Group instance.
     */
    public static CompletableFuture<Void> saveGroup(Group group) {
        return saveHolder(group);
    }

    /**
     * Saves a users data.
     * @param playerId Player id.
     */
    public static CompletableFuture<Void> saveUser(UUID playerId) {
        return getUserManager().saveUser(getUser(playerId));
    }

    /**
     * Loads, modifies and saves a user.
     * @param playerId Player id.
     * @param consumer Consumer.
     */
    public static CompletableFuture<Void> modifyUser(UUID playerId, Consumer<User> consumer) {
        return getUserManager().modifyUser(playerId, user -> consumer.accept(user));
    }

    // Context.

    /**
     * Register a custom context calculator.
     * @param calculator Custom context calculator.
     */
    public static void registerCalculator(ContextCalculator calculator) {
        getContextManager().registerCalculator(calculator);
    }

    // Data.

    /**
     * Get a holders data.
     * @param holder Holder instance.
     * @return Holders data.
     */
    public static NodeMap getData(PermissionHolder holder) {
        return holder.data();
    }

    /**
     * Get a players data.
     * @param playerId Player id.
     * @return Players data.
     */
    public static NodeMap getData(UUID playerId) {
        return getData(getUser(playerId));
    }

    /**
     * Get a users query options.
     * @param user User instance.
     * @return Query options if exists.
     */
    public static QueryOptions getQueryOptions(User user) {
        Optional<QueryOptions> optionalQueryOptions = getContextManager().getQueryOptions(user);

        return optionalQueryOptions.isPresent() ? optionalQueryOptions.get() : null;
    }

    /**
     * Get a players query options.
     * @param playerId Players id.
     * @return Query options if exists.
     */
    public static QueryOptions getQueryOptions(UUID playerId) {
        return getQueryOptions(getUser(playerId));
    }

    /**
     * Get a holders CachedDataManger.
     * @param holder Holder instance.
     * @return Holders CachedDataManager.
     */
    public static CachedDataManager getCachedData(PermissionHolder holder) {
        return holder.getCachedData();
    }

    /**
     * Get a users CachedDataManger.
     * @param playerId Players id.
     * @return Users CachedDataManager.
     */
    public static CachedDataManager getCachedData(UUID playerId) {
        return getCachedData(getUser(playerId));
    }

    /**
     * Get a holders CachedPermissionData.
     * @param holder Holder instance.
     * @return Holders CachedPermissionData.
     */
    public static CachedPermissionData getCachedPermissionData(PermissionHolder holder) {
        QueryOptions options = holder instanceof User ? getQueryOptions((User) holder) : null;
        CachedDataManager cachedDataManager = getCachedData(holder);

        return options != null ? cachedDataManager.getPermissionData(options) : cachedDataManager.getPermissionData();
    }

    /**
     * Get a users CachedPermissionData.
     * @param playerId Players id.
     * @return Users CachedPermissionData.
     */
    public static CachedPermissionData getCachedPermissionData(UUID playerId) {
        return getCachedPermissionData(getUser(playerId));
    }

    /**
     * Get a holders CachedMetaData.
     * @param holder Holder instance.
     * @return Holders CachedMetaData.
     */
    public static CachedMetaData getCachedMetaData(PermissionHolder holder) {
        QueryOptions options = holder instanceof User ? getQueryOptions((User) holder) : null;
        CachedDataManager cachedDataManager = getCachedData(holder);

        return options != null ? cachedDataManager.getMetaData(options) : cachedDataManager.getMetaData();
    }

    /**
     * Get a users CachedMetaData.
     * @param playerId Players id.
     * @return Users CachedMetaData.
     */
    public static CachedMetaData getCachedMetaData(UUID playerId) {
        return getCachedMetaData(getUser(playerId));
    }

    // Prefix & Suffix.

    /**
     * Get a holders Prefix.
     * @param holder Holder instance.
     * @return Holders prefix.
     */
    public static String getPrefix(PermissionHolder holder) {
        return getCachedMetaData(holder).getPrefix();
    }

    /**
     * Get a players Prefix.
     * @param playerId Players id.
     * @return Players prefix.
     */
    public static String getPrefix(UUID playerId) {
        return getPrefix(getUser(playerId));
    }

    /**
     * Get a holders Suffix.
     * @param holder Holder instance.
     * @return Holders Suffix.
     */
    public static String getSuffix(PermissionHolder holder) {
        return getCachedMetaData(holder).getSuffix();
    }

    /**
     * Get a players Suffix.
     * @param playerId Players id.
     * @return Players suffix.
     */
    public static String getSuffix(UUID playerId) {
        return getSuffix(getUser(playerId));
    }

    /**
     * Get a holders primary group name.
     * @param holder Holder instance.
     * @return Holders primary group name.
     */
    public static String getPrimaryGroup(PermissionHolder holder) {
        return getCachedMetaData(holder).getPrimaryGroup();
    }

    /**
     * Get a players primary group name.
     * @param playerId Player id.
     * @return Players primary group name.
     */
    public static String getPrimaryGroup(UUID playerId) {
        return getPrimaryGroup(getUser(playerId));
    }

    // Set data.

    /**
     * Set a meta value.
     * @param holder Holder instance.
     * @param node Meta node.
     * @param deleteOldValue Delete an eventually existing value.
     * @return Future when data is saved.
     */
    public static CompletableFuture<Void> setMetaValue(PermissionHolder holder, MetaNode node, boolean deleteOldValue) {
        NodeMap data = getData(holder);

        // Clear old value if needed.
        if (deleteOldValue)
            data.clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(node.getMetaKey())));

        // Add node.
        data.add(node);

        // Save holder.
        return saveHolder(holder);
    }

    /**
     * Set a meta value.
     * @param playerId Players id.
     * @param node Meta node.
     * @param deleteOldValue Delete an eventually existing value.
     * @return Future when data is saved.
     */
    public static CompletableFuture<Void> setMetaValue(UUID playerId, MetaNode node, boolean deleteOldValue) {
        return setMetaValue(getUser(playerId), node, deleteOldValue);
    }

    /**
     * Set a meta value. Deletes an eventually existing meta value.
     * @param playerId Players id.
     * @param node Meta node.
     * @return Future when data is saved.
     */
    public static CompletableFuture<Void> setMetaValue(UUID playerId, MetaNode node) {
        return setMetaValue(getUser(playerId), node, true);
    }

    /**
     * Set a meta value.
     * @param playerId Players id.
     * @param metaKey Meta node key.
     * @param metaKey Meta node value.
     * @param deleteOldValue Delete an eventually existing value.
     * @return Future when data is saved.
     */
    public static CompletableFuture<Void> setMetaValue(UUID playerId, String metaKey, String metaValue, boolean deleteOldValue) {
        MetaNode node = MetaNode.builder(metaKey, metaValue).build();
        return setMetaValue(getUser(playerId), node, deleteOldValue);
    }

    /**
     * Set a meta value. Deletes an eventually existing meta value.
     * @param playerId Players id.
     * @param metaKey Meta node key.
     * @param metaKey Meta node value.
     * @return Future when data is saved.
     */
    public static CompletableFuture<Void> setMetaValue(UUID playerId, String metaKey, String metaValue) {
        MetaNode node = MetaNode.builder(metaKey, metaValue).build();
        return setMetaValue(getUser(playerId), node, true);
    }

    /**
     * Get a holders meta value.
     * @param holder Holder instance.
     * @param metaKey Meta node key.
     * @param valueTransformer Transformer for the value.
     * @param defaultValue Default value if value is not present.
     * @return Meta value if exists, else defaultValue.
     */
    public static <T> T getMetaValue(PermissionHolder holder, String metaKey, Function<String,T> valueTransformer, T defaultValue) {
        CachedMetaData cachedMetaData = getCachedMetaData(holder);

        if (valueTransformer == null) {
            String metaValue = cachedMetaData.getMetaValue(metaKey);
            return metaValue != null ? (T) metaValue : null;
        }

        return cachedMetaData.getMetaValue(metaKey, valueTransformer).orElse(defaultValue);
    }

    /**
     * Get a holders meta value.
     * @param holder Holder instance.
     * @param metaKey Meta node key.
     * @param defaultValue Default value if key is not present.
     * @return Meta value if exists, else default value.
     */
    public static String getMetaValue(PermissionHolder holder, String metaKey, String defaultValue) {
        Object value =  getMetaValue(holder, metaKey, null, defaultValue);

        return value != null && value instanceof String ? (String) value : null;
    }

    /**
     * Get a holders meta value.
     * @param holder Holder instance.
     * @param metaKey Meta node key.
     * @return Meta value if exists, else default value.
     */
    public static String getMetaValue(PermissionHolder holder, String metaKey) {
        return getMetaValue(holder, metaKey, null);
    }

    /**
     * Get a holders meta value.
     * @param playerId Player id.
     * @param metaKey Meta node key.
     * @param valueTransformer Transformer for the value.
     * @param defaultValue Default value if value is not present.
     * @return Meta value if exists, else defaultValue.
     */
    public static <T> T getMetaValue(UUID playerId, String metaKey, Function<String,T> valueTransformer, T defaultValue) {
        return getMetaValue(getUser(playerId), metaKey, valueTransformer, defaultValue);
    }

    /**
     * Get a players meta value.
     * @param playerId Player id.
     * @param metaKey Meta node key.
     * @param defaultValue Default value if key is not present.
     * @return Meta value if exists, else default value.
     */
    public static String getMetaValue(UUID playerId, String metaKey, String defaultValue) {
        Object value =  getMetaValue(playerId, metaKey, null, defaultValue);

        return value != null && value instanceof String ? (String) value : null;
    }

    /**
     * Get a players meta value.
     * @param playerId Player id.
     * @param metaKey Meta node key.
     * @return Meta value if exists, else default value.
     */
    public static String getMetaValue(UUID playerId, String metaKey) {
        return getMetaValue(playerId, metaKey, null);
    }

}