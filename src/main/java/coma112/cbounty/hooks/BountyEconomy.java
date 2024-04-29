package coma112.cbounty.hooks;

import coma112.cbounty.CBounty;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BountyEconomy extends AbstractEconomy {
    private final Map<String, Integer> balances = new HashMap<>();

    private BountyEconomy() {}

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "BountyEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return ((int) amount) + " " + (((int) amount) == 1 ? this.currencyNameSingular() : this.currencyNamePlural());
    }

    @Override
    public String currencyNamePlural() {
        return "Bounties";
    }

    @Override
    public String currencyNameSingular() {
        return "Bounty";
    }

    @Override
    public boolean hasAccount(@NotNull String playerName) {
        return this.hasAccountByName(playerName);
    }

    @Override
    public boolean hasAccount(@NotNull String playerName, @NotNull String worldName) {
        return this.hasAccountByName(playerName);
    }

    @Override
    public double getBalance(@NotNull String playerName) {
        return this.getByName(playerName);
    }

    @Override
    public double getBalance(@NotNull String playerName, @NotNull String world) {
        return this.getByName(playerName);
    }

    @Override
    public boolean has(@NotNull String playerName, double amount) {
        return this.hasByName(playerName, amount);
    }

    @Override
    public boolean has(@NotNull String playerName, @NotNull String worldName, double amount) {
        return this.hasByName(playerName, amount);
    }

    private boolean hasAccountByName(@NotNull String playerName) {
        return this.balances.containsKey(playerName);
    }

    private double getByName(@NotNull String playerName) {
        return this.balances.getOrDefault(playerName, 0);
    }

    private boolean hasByName(@NotNull String playerName, double amount) {
        return this.balances.getOrDefault(playerName, 0) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(@NotNull String playerName, double amount) {
        return this.withdrawPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(@NotNull String playerName, String worldName, double amount) {
        if (amount < 0) return new EconomyResponse(0, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        if (!has(playerName, amount)) return new EconomyResponse(0, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");

        this.balances.put(playerName, (int) (this.getByName(playerName) - amount));

        return new EconomyResponse(amount, this.getByName(playerName), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(@NotNull String playerName, double amount) {
        return this.depositPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(@NotNull String playerName, String worldName, double amount) {
        if (amount < 0) return new EconomyResponse(0, this.getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");

        this.balances.put(playerName, (int) (this.getByName(playerName) + amount));

        return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse createBank(@NotNull String name, @NotNull String player) {
        return new EconomyResponse(0, this.getBalance(player), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse deleteBank(@NotNull String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankBalance(@NotNull String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankHas(@NotNull String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(@NotNull String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(@NotNull String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(@NotNull String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(@NotNull String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(@NotNull String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(@NotNull String playerName, @NotNull String worldName) {
        return false;
    }

    public static void register() {
        Bukkit.getServicesManager().register(Economy.class, new BountyEconomy(), CBounty.getInstance(), ServicePriority.Normal);
    }
}
