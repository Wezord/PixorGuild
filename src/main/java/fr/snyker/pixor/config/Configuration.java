package fr.snyker.pixor.config;

import fr.snyker.pixor.Constants;
import net.minecraftforge.common.config.Config;

@Config(modid = Constants.MODID, name = "guildConfig")
public class Configuration {

    @Config.Comment("Nombre maximum de niveau que peut atteindre la guilde.")
    public static int maxLevel = 10;

    @Config.Comment("Nombre maximum de membre que peut avoir la guilde peu importe le niveau, sauf : (Calculer selon le memberIncrease et si le memberLimit est activé)")
    public static int maxMembers = 10;

    @Config.Comment("Active l ajout de membre en cas de passage de niveau. " +
            "(Si la valeur est sur vrai, puis repasse sur faux, tous les membres excédant la limite seront toujours present.")
    public static boolean memberLimit = false;

    @Config.Comment("Nombre de membre a ajouter quand la guilde prend un niveau.")
    public static int memberIncrease = 1;

    @Config.Comment("Nom du role du chef de guilde.")
    public static String roleOwnerName = "Meneur";

    @Config.Comment("Nom du role du nouveau membre de guilde.")
    public static String roleNewerName = "Nouveau";

    @Config.Comment("Description par défaut de la guilde.")
    public static String defaultDescription = "Aucune description de guilde.";
}
