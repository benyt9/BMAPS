![BSMAPS LOGO](https://cdn.modrinth.com/data/cached_images/ff7df9014c34562b6bd74ae14ff6f4e7eb80c0b0.png)

# Elevate your server navigation with BMaps!

Welcome to **BMaps**, the ultimate cross-platform navigation solution for your server. Designed for both **Java and Bedrock** players, **BMaps** offers a seamless way to manage and access server locations with a highly customizable and modern interface.

🌍 **True Cross-Platform Support** Why limit your players? **BMaps** features a native Java GUI for desktop users and a specialized, high-performance **Bedrock UI** (via Geyser) for mobile and console players.

**Unified experience** for all players.

**Native feel** on every device.

🎨 **Full Customization & Custom Models** Take control of your menus! Every single message, title, and button is configurable in the config.yml.

**CustomModelData Support:** Use your own Resource Pack icons in the Java GUI to create a unique look.

**Bedrock Optimized:** Clean, responsive forms for Bedrock players.

🛠️ **Admin Friendly Management** Managing your server's points of interest has never been easier. Use simple in-game commands to set up your entire network.

**Dynamic Updates:** Use /map updatecity while holding an item to instantly change menu icons.

**Easy Setup:** Add cities and warps with just one command.

No more complex warp plugins – just a clean, visual way to travel across your world!


<details>
<summary>Config</summary>


```
# ==========================================
#          BMaps - Konfiguration
# ==========================================

Messages:
  Prefix: "&8[&bBMaps&8] &7"
  No-Permission: "&cDazu hast du keine Rechte!"
  Reload-Success: "&aKonfiguration und Städte erfolgreich neu geladen!"
  Teleport-Success: "&7Teleportiert zu &b%warp% &7(&f%city%&7)."
  World-Not-Found: "&cFehler: Die Zielwelt existiert nicht!"

  # Admin-Befehle (Usage)
  Usage-AddCity: "&cNutze: /map addcity <StadtName>"
  Usage-AddWarp: "&cNutze: /map addwarp <Stadt> <WarpName>"
  Usage-Remove: "&cNutze: /map removecity <Stadt> ODER /map removewarp <Stadt> <Warp>"

  # Admin-Befehle (Feedback)
  City-Created: "&aStadt &e%city% &awurde erfolgreich erstellt."
  City-Removed: "&cStadt &e%city% &cwurde gelöscht."
  Warp-Created: "&aWarp &e%warp% &afür Stadt &e%city% &agesetzt."
  Warp-Removed: "&cWarp &e%warp% &caus Stadt &e%city% &cgelöscht."
  Update-Success: "&bDaten für &e%target% &baktualisiert (Icon gespeichert)!"

Java-UI:
  Main:
    Title: "§0Navigation"
    City-Button-Material: "BRICKS"
    City-Button-Name: "§b§lStädte"
  Cities:
    Title: "§0Städte Auswahl"
  Warps:
    Title: "§0Warps: %city%"
  Back-Button-Name: "§c« Zurück"

Bedrock-UI:
  Main-Title: "&0Navigation"
  Main-Content: "Wähle eine Kategorie aus:"
  Main-Button-Cities: "Städte"
  Cities-Title: "&0Städte Auswahl"
  Cities-Content: "Wähle eine Stadt aus:"
  Warps-Title: "&0Warps: %city%"
  Warps-Content: "Wähle einen Punkt in %city%:"
  Back-Button: "&8« Zurück"
```


</details>


<details>
<summary>Commands & Permissions</summary>


```
/map - Opens the Navigation GUI (Java or Bedrock) - none (default)
/map reload	- Reloads config.yml and cities.yml - bmaps.admin.reload
/map addcity <name> - Creates a new city category - bmaps.admin.update
/map removecity <name> - Deletes a city and all its warps - bmaps.admin.remove
/map addwarp <city> <name> - Sets a warp point at your location - bmaps.admin.update
/map removewarp <city> <name> - Deletes a specific warp point - bmaps.admin.remove
/map updatecity <name> - Updates the icon/model of a city - bmaps.admin.update
/map updatewarp <city> <name> - Updates the icon/model of a warp - bmaps.admin.update
```


</details>

**Pro-Tip:** To set a custom icon or a **CustomModelData** item, simply hold the desired item in your main hand while executing an add or update command!

📂 Files
 - config.yml: Manage all messages, titles, and the main button design.

 - cities.yml: Automatically stores your locations and icon data (no manual editing required).
