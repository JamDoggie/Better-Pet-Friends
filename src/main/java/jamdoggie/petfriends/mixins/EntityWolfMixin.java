package jamdoggie.petfriends.mixins;

import com.mojang.nbt.CompoundTag;
import jamdoggie.petfriends.PetFriends;
import net.minecraft.core.entity.animal.EntityAnimal;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityWolf.class, remap = false)
public abstract class EntityWolfMixin extends EntityAnimal
{
	public EntityWolfMixin(World world) {
		super(world);
	}

	@Shadow
	public abstract boolean isWolfTamed();

	@Shadow
	public abstract boolean isWolfAngry();

	@Unique
	public String variant = "";

	private static String[] variants = {"wolf", "wolf_ashen", "wolf_black", "wolf_chestnut", "wolf_rusty",
		"wolf_snowy", "wolf_spotted", "wolf_striped", "wolf_woods"};

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initInject(CallbackInfo ci)
	{
		if (variant.isEmpty())
		{
			int variantIndex = random.nextInt(variants.length);
			variant = variants[variantIndex];
		}
		this.sendAdditionalData = true;
	}

	@Inject(method = "getEntityTexture", at = @At("HEAD"), cancellable = true)
	private void getTexture(CallbackInfoReturnable<String> cir)
	{
		if (this.isWolfTamed())
		{
			cir.setReturnValue("assets/" + PetFriends.MOD_ID + "/textures/entity/wolf/" + variant + "_tame.png");
		}
		else
		{
			if (this.isWolfAngry())
			{
				cir.setReturnValue("assets/" + PetFriends.MOD_ID + "/textures/entity/wolf/" + variant + "_angry.png");
			}
			else
			{
				cir.setReturnValue("assets/" + PetFriends.MOD_ID + "/textures/entity/wolf/" + variant + ".png");
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void saveNbt(CompoundTag tag, CallbackInfo ci)
	{
		tag.putString("wolf_variant", variant);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void loadNbt(CompoundTag tag, CallbackInfo ci)
	{
		variant = tag.getString("wolf_variant");

		if (variant.isEmpty())
		{
			variant = "wolf";
		}
	}
}
