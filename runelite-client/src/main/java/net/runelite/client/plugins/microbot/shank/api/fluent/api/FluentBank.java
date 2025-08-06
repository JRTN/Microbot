package net.runelite.client.plugins.microbot.shank.api.fluent.api;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankWithdraw;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface FluentBank {
    boolean isClosed();
    boolean isOpen();

    Stream<Rs2ItemModel> bankItems();
    List<Rs2ItemModel> getBankItems();

    int count(Predicate<Rs2ItemModel> item);
    int count(int id);
    int count(String name);

    boolean contains(Predicate<Rs2ItemModel> item);
    boolean contains(int id);
    boolean contains(String name);

    Optional<Rs2ItemModel> getBankItem(Predicate<Rs2ItemModel> item);
    Optional<Rs2ItemModel> getBankItem(int id);
    Optional<Rs2ItemModel> getBankItem(String name);

    Action close();
    Action open();

    FluentBankDeposit deposit();
    FluentBankWithdraw withdraw();
}
