package net.runelite.client.plugins.microbot.shank.api.fluent.api.bank;

/**
 * Fluent API for withdrawing items from the bank to the player's inventory.
 *
 * <p>This interface provides methods to withdraw items from the bank storage into the player's
 * inventory. It extends {@link FluentBankItemQuantityChange} to inherit quantity-based operations
 * (one, x amount, all) while providing bank-specific withdrawal context.</p>
 *
 * <h2>Withdrawal Behavior</h2>
 * <p>Withdrawal operations are subject to several constraints:</p>
 * <ul>
 *   <li>The bank must be open for withdrawal operations to succeed</li>
 *   <li>Items must exist in the bank in sufficient quantities</li>
 *   <li>The player's inventory must have sufficient space</li>
 *   <li>Some items may have withdrawal restrictions (members items, quest items)</li>
 * </ul>
 *
 * <h2>Inventory Space</h2>
 * <p>Withdrawal operations consider inventory capacity:</p>
 * <ul>
 *   <li>Stackable items require only one inventory slot regardless of quantity</li>
 *   <li>Non-stackable items require one slot per item</li>
 *   <li>Operations may partially complete if inventory space is insufficient</li>
 * </ul>
 *
 * @see FluentBankItemQuantityChange
 * @see FluentBankDeposit
 * @see net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentBank
 */
public interface FluentBankWithdraw extends FluentBankItemQuantityChange {

}
