package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankWithdraw;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.general.FluentItemStore;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;

public interface FluentBank extends FluentItemStore {
    boolean isClosed();
    boolean isOpen();

    Action close();
    Action open();

    FluentBankDeposit deposit();
    FluentBankWithdraw withdraw();
}
