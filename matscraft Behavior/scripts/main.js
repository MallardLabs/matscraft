import { ActionFormData, ModalFormData } from "@minecraft/server-ui";
import { world } from "@minecraft/server";
import { ItemPickup } from "./events/itemPickup";
import { BlockBreakEvent } from "./events/blockBreak";
import { showMainMenu } from "./gui/menu";
import { initialize } from "./scoreboard/main";
// ########### Block Break Event ###########
const blockBreakEvent = new BlockBreakEvent();
blockBreakEvent.initialize();

/* ########### Item Pickup Event ###########
@param {string} identifier - The identifier of the item to track (e.g., "minecraft:apple")
@param {number} ticks - The interval in ticks at which to track item pickups
@param {string} scoreboardName - The name of the scoreboard objective to use for tracking
*/

const itemPickup = new ItemPickup("matscraft:mats", 20, "Mats");
itemPickup.initialize();
initialize();
// Event subscriptions
world.afterEvents.itemUse.subscribe((data) => {
  const player = data.source;

  if (data.itemStack.typeId === "minecraft:compass") {
    player.sendMessage(`You used: ${data.itemStack.nameTag}`);
    showMainMenu(player);
  }
});

world.afterEvents.playerSpawn.subscribe(async ({ player, initialSpawn }) => {
  const Discord = player.getDynamicProperty("Discord");
  if (Discord != "Not Linked") {
    console.warn(`${player.name} is already linked to Discord: ${Discord}`);
    return;
  }
  console.warn(`${player.name} is Not linked to Discord: ${Discord}`);
  player.setDynamicProperty("Discord", "zaapr0x");
});
