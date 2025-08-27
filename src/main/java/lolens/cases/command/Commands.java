package lolens.cases.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lolens.cases.core.LootCrateManager;
import lolens.cases.item.ModItems;
import lolens.cases.util.CasesUtils;
import lolens.cases.util.IEntityDataSaver;
import lolens.cases.util.RewardHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.function.Predicate;

public class Commands {

    public static void register() {

        Predicate<ServerCommandSource> spOrOP = source -> source.getServer().isSingleplayer() || source.hasPermissionLevel(2);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("cases")
                .then(CommandManager.literal("player")
                        .requires(spOrOP)
                        .then(CommandManager.literal("get")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    for (String playerName : context.getSource().getServer().getPlayerNames()) {
                                                        builder.suggest(playerName);
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .then(CommandManager.literal("rewards")
                                                        .requires(spOrOP)
                                                        .executes(Commands::getRewards)
                                                )
                                        //.then(CommandManager.literal("uncollected")) todo: get collected/uncollected ?
                                )

                        )
                        .then(CommandManager.literal("addRewardFromHand")
                                .requires(spOrOP)
                                .requires(ServerCommandSource::isExecutedByPlayer)
                                .executes(Commands::addFromHand)

                        ))

                .then(CommandManager.literal("server")
                        .requires(spOrOP)
                        .then(CommandManager.literal("reload")
                                .requires(spOrOP)
                                .executes(Commands::serverReload)
                        )
                        .then(CommandManager.literal("get")
                                .then(CommandManager.literal("crate")
                                        .then(CommandManager.literal("all")
                                                .executes(Commands::infoAll)
                                        )
                                        .then(CommandManager.literal("byId")
                                                .then(CommandManager.argument("crateId", StringArgumentType.string())
                                                        .executes(Commands::infoId)
                                                        .suggests((commandContext, builder) -> {
                                                            LootCrateManager.getInstance().getLootCrateIds().forEach(builder::suggest);
                                                            return builder.buildFuture();
                                                        })
                                                        .then(CommandManager.literal("drops")
                                                                .executes(Commands::drops)
                                                        )
                                                        .then(CommandManager.literal("dropById")
                                                                .then(CommandManager.argument("dropId", IntegerArgumentType.integer())
                                                                        .suggests((commandContext, builder) -> {
                                                                            int size = LootCrateManager.getInstance().getLootCrate(StringArgumentType.getString(commandContext, "crateId")).getDrops().size();
                                                                            for (int i = 0; i < size; i++) {
                                                                                builder.suggest(i);
                                                                            }
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(Commands::dropChance)
                                                                )
                                                        )
                                                        .then(CommandManager.literal("itemproperties")
                                                                .executes(Commands::itemProperties)
                                                        )
                                                        .then(CommandManager.literal("screenproperties")
                                                                .executes(Commands::screenProperties)
                                                        )
                                                        .then(CommandManager.literal("issue")
                                                                .then(CommandManager.argument("name", StringArgumentType.string())
                                                                        .suggests((context, builder) -> {
                                                                            for (String playerName : context.getSource().getServer().getPlayerNames()) {
                                                                                builder.suggest(playerName);
                                                                            }
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(Commands::giveToPlayer)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("claim")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .executes(Commands::claim)
                        .then(CommandManager.literal("all")
                                .requires(ServerCommandSource::isExecutedByPlayer)
                                .executes(Commands::claimAll)
                        )
                )
        ));


    }

    private static int giveToPlayer(CommandContext<ServerCommandSource> ctx) {
        String pName = StringArgumentType.getString(ctx, "name");
        String crateId = StringArgumentType.getString(ctx, "crateId");

        ItemStack stack = new ItemStack(ModItems.CASE, 1);
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("caseId", crateId);
        stack.setNbt(nbtCompound);

        ctx.getSource().getServer().getPlayerManager().getPlayer(pName).giveItemStack(stack);
        return 1;
    }

    private static int addFromHand(CommandContext<ServerCommandSource> ctx) {
        ItemStack stack = ctx.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
        RewardHelper.pushReward((IEntityDataSaver) ctx.getSource().getPlayer(), stack);
        return 1;
    }

    private static int getRewards(CommandContext<ServerCommandSource> ctx) {
        String pName = StringArgumentType.getString(ctx, "name");
        ctx.getSource().sendMessage(Text.literal("Uncollected rewards of '" + pName + "':"));
        RewardHelper.peekAllRewards((IEntityDataSaver) ctx.getSource().getServer().getPlayerManager().getPlayer(pName)).forEach(itemStack -> {
            ctx.getSource().sendMessage(Text.literal("Item: x" + itemStack.getCount() + " | " + itemStack.getName() + " | NBT: " + itemStack.getNbt()));
        });
        return 1;
    }

    private static int drops(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.of(LootCrateManager.getInstance().getLootCrate(StringArgumentType.getString(ctx, "crateId")).dropsToStringLN()));
        return 1;
    }

    private static int itemProperties(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.of(LootCrateManager.getInstance().getLootCrate(StringArgumentType.getString(ctx, "crateId")).getItemProperties().toStringLN()));
        return 1;
    }

    private static int screenProperties(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.of(LootCrateManager.getInstance().getLootCrate(StringArgumentType.getString(ctx, "crateId")).getScreenProperties().toStringLN()));
        return 1;
    }

    private static int dropChance(CommandContext<ServerCommandSource> ctx) {
        int targetId = IntegerArgumentType.getInteger(ctx, "dropId");
        ctx.getSource().sendMessage(Text.literal(LootCrateManager.getInstance().getLootCrate(StringArgumentType.getString(ctx, "crateId")).dropsToStringLN(targetId)));
        return 1;
    }


    private static int serverReload(CommandContext<ServerCommandSource> ctx) {
        CasesUtils.refreshAll(ctx.getSource().getServer());
        return 1;
    }

    private static int infoId(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.literal(LootCrateManager.getInstance().getLootCrate(StringArgumentType.getString(ctx, "crateId")).toStringLN()));
        return 1;
    }

    private static int infoAll(CommandContext<ServerCommandSource> ctx) {
        List<String> ids = LootCrateManager.getInstance().getLootCrateIds();
        if (ids.isEmpty()) {
            ctx.getSource().sendMessage(Text.literal("No known loot crates"));
        } else {
            ids.forEach(id -> {
                try {
                    ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("Loot crates info printed in server console").formatted(Formatting.GREEN));
                } catch (CommandSyntaxException ignored) {

                } finally {
                    ctx.getSource().getServer().sendMessage(Text.literal(LootCrateManager.getInstance().getLootCrate(id).toStringLN()));
                }

            });
        }
        return 1;
    }

    private static int claim(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        byte a = CasesUtils.claimReward(player);


        switch (a) {
            case 0 -> {
                StringBuilder sb = new StringBuilder("You have claimed reward! ");
                int remaining = RewardHelper.getRemainingRewardCount((IEntityDataSaver) player);
                if (remaining > 0) sb.append(remaining).append(" more left.");
                player.sendMessage(Text.literal(sb.toString()).formatted(Formatting.GREEN));
            }
            case 1 -> {
                player.sendMessage(Text.literal("You don't have any rewards to claim.").formatted(Formatting.RED));
            }
            case 2 -> {
                player.sendMessage(Text.literal("You can't claim any rewards now.").formatted(Formatting.RED));
            }
        }

        return 1;
    }

    private static int claimAll(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        byte a = CasesUtils.claimAllRewards(player);

        switch (a) {
            case 0 -> {
                player.sendMessage(Text.literal("You have claimed all rewards!").formatted(Formatting.GREEN));
            }
            case 1 -> {
                player.sendMessage(Text.literal("You don't have any rewards to claim.").formatted(Formatting.RED));
            }
            case 2 -> {
                player.sendMessage(Text.literal("You can't claim any rewards now.").formatted(Formatting.RED));
            }
        }

        return 1;
    }


}
