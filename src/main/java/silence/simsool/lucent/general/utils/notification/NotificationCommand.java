package silence.simsool.lucent.general.utils.notification;

import java.util.Locale;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class NotificationCommand {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommandManager.literal("notification")
			.then(ClientCommandManager.argument("type", StringArgumentType.word())
				.suggests((context, builder) -> {
					for (Notification.Type t : Notification.Type.values()) {
						String name = t.name().toLowerCase(Locale.ROOT);
						if (name.startsWith(builder.getRemainingLowerCase())) {
							builder.suggest(name);
						}
					}
					return builder.buildFuture();
				})
				.then(ClientCommandManager.argument("title", StringArgumentType.string())
					.then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
						.executes(context -> {
							String typeStr = StringArgumentType.getString(context, "type");
							String title = StringArgumentType.getString(context, "title");
							String message = StringArgumentType.getString(context, "message");

							Notification.Type type;
							try {
								type = Notification.Type.valueOf(typeStr.toUpperCase(Locale.ROOT));
							} catch (IllegalArgumentException e) {
								type = Notification.Type.INFO;
							}

							NotificationManager.show(title, message, type);
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			)
		);
	}

}