package net.sciencecraft.src.machineblocks.catalyzer;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Slot;
import net.sciencecraft.src.ScienceCraft;
import net.sciencecraft.src.machineblocks.refiner.RefinerRecipes;

public class SlotCatalyzer extends Slot 
{
	private EntityPlayer player;
	private int stackSize;
	
	public SlotCatalyzer(EntityPlayer player, IInventory inventory, int x, int y, int z) 
	{
		super(inventory, x, y, z);
		this.player = player;
	}	
	
	public boolean isItemValid(ItemStack itemstack)
	{
		return false;
	}
	
	public ItemStack decrStackSize(int amt)
	{
		if(this.getHasStack())
		{
			this.stackSize += Math.min(amt, this.getStack().stackSize);
		}
		
		return super.decrStackSize(amt);
	}
	
	public void onPickupFromSlot(ItemStack itemstack)
	{
		this.onCrafting(itemstack);
		super.onPickupFromSlot(itemstack);
	}
	
	protected void onCrafting(ItemStack itemstack, int i)
	{
		this.stackSize += i;
		this.onCrafting(itemstack);
	}
	
	protected void onCrafting(ItemStack itemstack)
	{
		itemstack.onCrafting(this.player.worldObj, this.player, stackSize);
		
		if(!this.player.worldObj.isRemote)
		{
			int size = this.stackSize;
			float f = CatalyzerRecipes.catalyzing().func_77601_c(itemstack.itemID);
			int var;
			
			if(f == 0.0F)
			{
				size = 0;
			}
			else
			if(f < 1.0F)
			{
				var = MathHelper.floor_float((float) size * f);
				
				if(var < MathHelper.ceiling_float_int((float)size * f) && (float)Math.random() < (float)size * f - (float) var)
				{
					++var;
				}
				
				size = var;
			}
			
			while(size > 0)
			{
				var = EntityXPOrb.getXPSplit(size);
				size -= var;
				this.player.worldObj.spawnEntityInWorld(new EntityXPOrb(this.player.worldObj, this.player.posX, this.player.posY + 0.5D, this.player.posZ, var));
			}
		}
		
		this.stackSize = 0;
	
		ScienceCraft.registry.onCatalyzed(itemstack, player);
		
	}
}
