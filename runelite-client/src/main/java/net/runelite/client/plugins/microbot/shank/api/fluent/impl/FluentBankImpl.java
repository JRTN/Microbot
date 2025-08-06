package net.runelite.client.plugins.microbot.shank.api.fluent.impl;

import net.runelite.client.plugins.microbot.shank.api.fluent.api.FluentBank;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankDeposit;
import net.runelite.client.plugins.microbot.shank.api.fluent.api.bank.FluentBankWithdraw;
import net.runelite.client.plugins.microbot.shank.api.fluent.core.flow.Action;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank.FluentBankDepositImpl;
import net.runelite.client.plugins.microbot.shank.api.fluent.impl.bank.FluentBankWithdrawImpl;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FluentBankImpl implements FluentBank {

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Stream<Rs2ItemModel> bankItems() {
        return null;
    }

    @Override
    public List<Rs2ItemModel> getBankItems() {
        return null;
    }

    @Override
    public int count(Predicate<Rs2ItemModel> item) {
        return 0;
    }

    @Override
    public int count(int id) {
        return 0;
    }

    @Override
    public int count(String name) {
        return 0;
    }

    @Override
    public boolean contains(Predicate<Rs2ItemModel> item) {
        return false;
    }

    @Override
    public boolean contains(int id) {
        return false;
    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public Optional<Rs2ItemModel> getBankItem(Predicate<Rs2ItemModel> item) {
        return Optional.empty();
    }

    @Override
    public Optional<Rs2ItemModel> getBankItem(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Rs2ItemModel> getBankItem(String name) {
        return Optional.empty();
    }

    @Override
    public Action close() {
        return null;
    }

    @Override
    public Action open() {
        return null;
    }

    @Override
    public FluentBankDeposit deposit() {
        return new FluentBankDepositImpl();
    }

    @Override
    public FluentBankWithdraw withdraw() {
        return new FluentBankWithdrawImpl();
    }
}
