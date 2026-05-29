package silence.simsool.lucent.general.models.data.events.guievent;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemTooltipEvent {

	public final ItemStack itemStack;
	public final List<Component> toolTip;
	public final TooltipContext context;
	public final TooltipFlag flags;
	public final Player player;
	private boolean canceled = false;

	public ItemTooltipEvent(ItemStack itemStack, List<Component> toolTip, TooltipContext context, TooltipFlag flags, @Nullable Player player) {
		this.itemStack = itemStack;
		this.toolTip = toolTip;
		this.context = context;
		this.flags = flags;
		this.player = player;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}