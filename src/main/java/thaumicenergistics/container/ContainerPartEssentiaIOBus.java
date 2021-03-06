package thaumicenergistics.container;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.network.packet.client.PacketClientAspectSlot;
import thaumicenergistics.network.packet.client.PacketClientEssentiaIOBus;
import thaumicenergistics.parts.AEPartEssentiaIO;
import appeng.api.config.RedstoneMode;

/**
 * Inventory container for the import and export busses.
 * 
 * @author Nividica
 * 
 */
public class ContainerPartEssentiaIOBus
	extends ContainerWithNetworkTool
{
	/**
	 * The number of upgrade slots we have
	 */
	private static int NUMBER_OF_UPGRADE_SLOTS = 4;

	/**
	 * The x position of the upgrade slots
	 */
	private static int UPGRADE_X_POS = 187;

	/**
	 * The Y position for the upgrade slots
	 */
	private static int UPGRADE_Y_POS = 8;

	/**
	 * Y position for the player inventory
	 */
	private static int PLAYER_INV_POSITION_Y = 102;

	/**
	 * Y position for the hotbar inventory
	 */
	private static int HOTBAR_INV_POSITION_Y = 160;

	/**
	 * The part associated with this container
	 */
	private AEPartEssentiaIO part;

	/**
	 * Player associated with the container
	 */
	private EntityPlayer player;

	/**
	 * Creates the container.
	 * 
	 * @param part
	 * The AE part associated with the container.
	 * @param player
	 * The owner of the container.
	 */
	public ContainerPartEssentiaIOBus( final AEPartEssentiaIO part, final EntityPlayer player )
	{
		// Set the part
		this.part = part;

		// Set the player
		this.player = player;

		// Bind to the player's inventory
		this.bindPlayerInventory( player.inventory, ContainerPartEssentiaIOBus.PLAYER_INV_POSITION_Y,
			ContainerPartEssentiaIOBus.HOTBAR_INV_POSITION_Y );

		// Add the upgrade slots
		this.addUpgradeSlots( part.getUpgradeInventory(), ContainerPartEssentiaIOBus.NUMBER_OF_UPGRADE_SLOTS,
			ContainerPartEssentiaIOBus.UPGRADE_X_POS, ContainerPartEssentiaIOBus.UPGRADE_Y_POS );

		// Bind to the network tool
		this.bindToNetworkTool( player.inventory, part.getHost().getLocation(), 0, 0 );

		// Register as a listener on the part
		this.part.addListener( this );
	}

	@Override
	protected void retrySlotClick( final int par1, final int par2, final boolean par3, final EntityPlayer player )
	{
		// Ignored
	}

	/**
	 * Who can interact with the container?
	 */
	@Override
	public boolean canInteractWith( final EntityPlayer player )
	{
		return true;
	}

	@Override
	public void onContainerClosed( final EntityPlayer player )
	{
		if( this.part != null )
		{
			this.part.removeListener( this );
		}
	}

	public void setFilteredAspect( final List<Aspect> filteredAspects )
	{
		new PacketClientAspectSlot().createFilterListUpdate( filteredAspects, this.player ).sendPacketToPlayer();
	}

	public void setFilterSize( final byte filterSize )
	{
		new PacketClientEssentiaIOBus().createSetFilterSize( this.player, filterSize ).sendPacketToPlayer();
	}

	public void setRedstoneControlled( final boolean isRedstoneControlled )
	{
		new PacketClientEssentiaIOBus().createSetRedstoneControlled( this.player, isRedstoneControlled ).sendPacketToPlayer();
	}

	public void setRedstoneMode( final RedstoneMode redstoneMode )
	{
		new PacketClientEssentiaIOBus().createSetRedstoneMode( this.player, redstoneMode ).sendPacketToPlayer();
	}

	/**
	 * Called when the player shift+clicks on a slot.
	 */
	@Override
	public ItemStack transferStackInSlot( final EntityPlayer player, final int slotNumber )
	{
		// Get the slot
		Slot slot = this.getSlot( slotNumber );

		// Do we have a valid slot with an item?
		if( ( slot != null ) && ( slot.getHasStack() ) )
		{
			// Can this aspect be added to the filter list?
			if( ( this.part != null ) && ( this.part.addFilteredAspectFromItemstack( player, slot.getStack() ) ) )
			{
				return null;
			}

			// Pass to super
			return super.transferStackInSlot( player, slotNumber );
		}

		return null;
	}

}
