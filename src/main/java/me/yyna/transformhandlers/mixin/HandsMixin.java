package me.yyna.transformhandlers.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.yyna.transformhandlers.SettingsScreen;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class HandsMixin {
	@Inject(at = @At("HEAD"), method = "renderPlayerArm")
	private void renderPlayerArm(PoseStack matrices, SubmitNodeCollector queue, int light, float equipProgress, float swingProgress, HumanoidArm arm, PlayerRenderState playerRenderState, CallbackInfo ci){
		if (arm == HumanoidArm.RIGHT && SettingsScreen.settings.enable && SettingsScreen.settings.ArmRight.enable){
			matrices.translate((double)SettingsScreen.settings.ArmRight.x/10D, (double)SettingsScreen.settings.ArmRight.y/10D, (double)SettingsScreen.settings.ArmRight.z/10D);
		}else if (arm == HumanoidArm.LEFT  && SettingsScreen.settings.enable && SettingsScreen.settings.ArmLeft.enable) {
			matrices.translate((double)SettingsScreen.settings.ArmLeft.x/10D, (double)SettingsScreen.settings.ArmLeft.y/10D, (double)SettingsScreen.settings.ArmLeft.z/10D);
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER), method = "submitArmWithItem")
	private void submitArmWithItem(PlayerRenderState playerRenderState, FirstPersonHandsAndItemsRenderState handsAndItemsRenderState, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, SubmitNodeCollector queue, int light, CallbackInfo info){
		if (!item.isEmpty() && SettingsScreen.settings.enable){
			if (ChargedCrossbowEnabled(item)){
				matrices.translate((double)SettingsScreen.settings.ChargedCrossbow.x/10D, (double)SettingsScreen.settings.ChargedCrossbow.y/10D, (double)SettingsScreen.settings.ChargedCrossbow.z/10D);
			} else if(FilledMapEnabled(item)) {
				matrices.translate((double)SettingsScreen.settings.FilledMap.x/10D, (double)SettingsScreen.settings.FilledMap.y/10D, (double)SettingsScreen.settings.FilledMap.z/10D);
			}

			if (CheckForDisabledSpecials(item)){
				if (MainHandEnabled(hand)){
					matrices.translate((double)SettingsScreen.settings.ItemsMain.x/10D, (double)SettingsScreen.settings.ItemsMain.y/10D, (double)SettingsScreen.settings.ItemsMain.z/10D);
				}else if (OffHandEnabled(hand)) {
					matrices.translate((double)SettingsScreen.settings.ItemsOff.x/10D, (double)SettingsScreen.settings.ItemsOff.y/10D, (double)SettingsScreen.settings.ItemsOff.z/10D);
				}
			}

		}
	}

	private static boolean MainHandEnabled(InteractionHand hand){
		return hand == InteractionHand.MAIN_HAND && SettingsScreen.settings.ItemsMain.enable;
	}
	private static boolean OffHandEnabled(InteractionHand hand){
		return hand == InteractionHand.OFF_HAND && SettingsScreen.settings.ItemsOff.enable;
	}
	private static boolean CheckForDisabledSpecials(ItemStack item){
		return !(item.is(Items.CROSSBOW) && CrossbowItem.isCharged(item) && !SettingsScreen.settings.ChargedCrossbow.apply)
				&& !(item.is(Items.FILLED_MAP) && !SettingsScreen.settings.FilledMap.apply);
	}
	private static boolean ChargedCrossbowEnabled(ItemStack item){
		return item.is(Items.CROSSBOW) && CrossbowItem.isCharged(item) && SettingsScreen.settings.ChargedCrossbow.enable;
	}
	private static boolean FilledMapEnabled(ItemStack item){
		return item.is(Items.FILLED_MAP) && SettingsScreen.settings.FilledMap.enable;
	}
}
