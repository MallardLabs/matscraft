import { world } from "@minecraft/server";
//import { http } from "@minecraft/server-net"; // Pastikan paket ini diaktifkan

export class BlockBreakEvent {
  constructor() {
    this.logBatch = [];
    this.batchSize = 20; // Kirim data setiap 20 log
    this.apiEndpoint = "https://your-database-api-endpoint.com/log"; // Ganti dengan endpoint Anda
  }

  initialize() {
    world.afterEvents.playerBreakBlock.subscribe((event) =>
      this.handleBlockBreak(event)
    );
  }

  handleBlockBreak(event) {
    const {
      player,
      brokenBlockPermutation: { type: block },
      block: { location: position },
    } = event;
    const blockName = block.id;

    // Log block break event
    this.logBlockBreak(player, blockName, position);

    // Notify player (optional)
    this.notifyPlayer(player, blockName, position);
  }

  logBlockBreak(player, blockName, position) {
    const data = {
      playerName: player.name,
      uuid: player.xuid,
      blockName: blockName.replace("matscraft:", ""),
      position: {
        x: position.x,
        y: position.y,
        z: position.z,
      },
    };

    // Tambahkan data ke batch
    this.logBatch.push(data);

    // Periksa apakah sudah mencapai batas batch
    if (this.logBatch.length >= this.batchSize) {
      console.warn("Batch full, sending to database...");
      //this.sendBatchToDatabase();
    }
  }
  /*
  async sendBatchToDatabase() {
    try {
      const payload = JSON.stringify(this.logBatch);
      const response = await http.request({
        url: this.apiEndpoint,
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: payload,
      });

      if (response.status === 200) {
        console.warn("Batch successfully sent to the database.");
      } else {
        console.error(`Failed to send batch: ${response.statusText}`);
      }
    } catch (error) {
      console.error(`Error sending batch to database: ${error.message}`);
    } finally {
      // Bersihkan batch setelah terkirim atau jika terjadi error
      this.logBatch = [];
    }
  }
*/
  notifyPlayer(player, blockName, position) {
    if (!blockName.includes("matscraft")) {
      return;
    }
    player.runCommand(
      `title @s actionbar §aYou broke ${blockName.replace(
        "matscraft:",
        ""
      )} at ${position.x.toFixed(1)}, ${position.y.toFixed(
        1
      )}, ${position.z.toFixed(1)}!`
    );
  }
}
