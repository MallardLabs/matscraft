import { ActionFormData, ModalFormData } from "@minecraft/server-ui";

const Deposit = (player) => {
  const form = new ModalFormData()
    .title("Deposit")
    .slider("Balance", 1, 10000, 1, 0); // Minimum: 1, Maximum: 10000, Step: 1, Default: 0
  // Utility functions
  form
    .show(player)
    .then((response) => {
      if (response && !response.canceled) {
        player.sendMessage(`You Deposited: ${response.formValues[0]}`);
      } else {
        player.sendMessage("Deposit canceled.");
      }
    })
    .catch((err) => {
      console.error("Error showing deposit form:", err);
    });
};

const Withdraw = (player) => {
  player.sendMessage("Withdraw functionality is not yet implemented."); // Placeholder implementation
};

const Help = (player) => {
  const form = new ActionFormData()
    .title("Help")
    .body(
      "§lDeposit: §rDeposit your Mats balance from Mezo Discord to Matscraft\n\n" +
        "§lWithdraw: §rWithdraw your Mats balance from Matscraft to the Mezo Discord.\n\n\n\n\n"
    )
    .button("§lBack");

  form
    .show(player)
    .then((response) => {
      if (response && !response.canceled) {
        showMainMenu(player);
      }
    })
    .catch((err) => {
      console.error("Error showing help form:", err);
    });
};

export const showMainMenu = (player) => {
  const form = new ActionFormData()
    .title("Matscraft Menu")
    .button("§lBalance")
    .button("§lLink Account")
    .button("§lExit");

  form
    .show(player)
    .then((response) => {
      if (response && !response.canceled) {
        switch (response.selection) {
          case 0:
            Deposit(player);
            break;
          case 1:
            Withdraw(player);
            break;
          case 2:
            Help(player);
            break;
          default:
            player.sendMessage("Exiting menu.");
        }
      }
    })
    .catch((err) => {
      console.error("Error showing main menu:", err);
    });
};
