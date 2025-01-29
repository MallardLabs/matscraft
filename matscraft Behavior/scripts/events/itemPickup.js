import { world, system, ScoreboardObjective } from "@minecraft/server";

export class ItemPickup {
  constructor(identifier, ticks, scoreboardName) {
    this.identifier = identifier;
    this.ticks = ticks;
    this.scoreboardName = scoreboardName;
    this.playerInventories = new Map();
    this.monitoredItems = [identifier];
  }

  initialize() {
    system.runInterval(() => this.trackItemPickups(), this.ticks);
  }

  trackItemPickups() {
    const activePlayers = world
      .getPlayers()
      .filter((player) => player.hasComponent("minecraft:inventory"));

    activePlayers.forEach((player) => {
      const inventory = this.getPlayerInventory(player);
      const currentInventory = this.getCurrentInventory(inventory);
      const previousInventory =
        this.playerInventories.get(player.name) || new Map();

      this.handleInventoryChanges(
        player,
        currentInventory,
        previousInventory,
        inventory
      );
      this.playerInventories.set(player.name, currentInventory);
    });
  }

  getPlayerInventory(player) {
    return player.getComponent("minecraft:inventory").container;
  }

  getCurrentInventory(inventory) {
    const inventoryMap = new Map();
    for (let i = 0; i < inventory.size; i++) {
      const item = inventory.getItem(i);
      if (item && this.monitoredItems.includes(item.typeId)) {
        inventoryMap.set(
          item.typeId,
          (inventoryMap.get(item.typeId) || 0) + item.amount
        );
      }
    }
    return inventoryMap;
  }

  handleInventoryChanges(
    player,
    currentInventory,
    previousInventory,
    inventory
  ) {
    for (const [typeId, amount] of currentInventory) {
      const previousAmount = previousInventory.get(typeId) || 0;

      if (amount > previousAmount) {
        const pickedUpAmount = amount - previousAmount;
        this.notifyPlayer(player, pickedUpAmount);
        this.updateScoreboard(player, pickedUpAmount);
        this.removeItemsFromInventory(typeId, pickedUpAmount, inventory);
      }
    }
  }

  notifyPlayer(player, pickedUpAmount) {
    const itemName = this.identifier.split(":")[1];
    player.runCommand("playsound random.pop @s");
    player.runCommand(
      `title @s actionbar §aYou've picked up ${pickedUpAmount}x ${itemName}!`
    );
    player.runCommand("particle minecraft:critical ~ ~ ~");
    console.warn(`${player.name} picked up ${pickedUpAmount}x ${itemName}`);
  }

  updateScoreboard(player, pickedUpAmount) {
    player.runCommand(
      `scoreboard players add @s ${this.scoreboardName} ${pickedUpAmount}`
    );
  }

  removeItemsFromInventory(typeId, amountToRemove, inventory) {
    let remainingToRemove = amountToRemove;
    for (let i = 0; i < inventory.size && remainingToRemove > 0; i++) {
      const item = inventory.getItem(i);
      if (item && item.typeId === typeId) {
        if (item.amount <= remainingToRemove) {
          inventory.setItem(i, null);
          remainingToRemove -= item.amount;
        } else {
          item.amount -= remainingToRemove;
          inventory.setItem(i, item);
          remainingToRemove = 0;
        }
      }
    }
  }
}
