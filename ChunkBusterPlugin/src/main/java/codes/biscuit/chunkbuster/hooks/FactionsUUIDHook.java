package codes.biscuit.chunkbuster.hooks;


import codes.biscuit.chunkbuster.ChunkBuster;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.FPerm;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class FactionsUUIDHook {

    // This hook works with FactionsUUID but it should also work with
    // its forks like SavageFactions.

    private ChunkBuster main;

    FactionsUUIDHook(ChunkBuster main) {
        this.main = main;
    }

    boolean hasFaction(Player p) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);
        return fPlayer.hasFaction();
    }

    boolean isWilderness(Location loc) {
        FLocation fLoc = new FLocation(loc);
        Faction fLocFaction = Board.getInstance().getFactionAt(fLoc);
        return fLocFaction.isWilderness();
    }

    boolean compareLocPlayerFaction(Location loc, Player p) {
        FLocation fLocation = new FLocation(loc);
        Faction locFaction = Board.getInstance().getFactionAt(fLocation);
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);
        return FPerm.getPermBuildChunkBuster().has(fPlayer, fLocation, true) || (locFaction.isWilderness() && main.getConfigValues().canPlaceInWilderness());
    }
}
