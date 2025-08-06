package net.runelite.client.plugins.microbot.shank.api.fluent.api.bank;

/**
 * Fluent API for depositing items from the player's inventory to the bank.
 *
 * <p>This interface provides methods to deposit items from the player's inventory into bank storage.
 * It extends {@link FluentBankItemQuantityChange} to inherit quantity-based operations (one, x amount, all)
 * while providing bank-specific deposit context.</p>
 *
 * <h2>Deposit Behavior</h2>
 * <p>Deposit operations are subject to several constraints:</p>
 * <ul>
 *   <li>The bank must be open for deposit operations to succeed</li>
 *   <li>Items must exist in the player's inventory</li>
 *   <li>Some items cannot be banked (untradeable items, degraded equipment)</li>
 * </ul>
 *
 * @see FluentBankItemQuantityChange
 * @see FluentBankWithdraw
 * @see net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentBank
 */
public interface FluentBankDeposit extends FluentBankItemQuantityChange {

}
