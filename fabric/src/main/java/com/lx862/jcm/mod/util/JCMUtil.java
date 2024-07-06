package com.lx862.jcm.mod.util;

import com.lx862.jcm.mod.Constants;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.PlayerEntity;

public class JCMUtil {
    /**
     * Whether the player is holding an MTR Brush / An item used for configuring blocks
     */
    public static boolean playerHoldingBrush(PlayerEntity player) {
        return player.isHolding(org.mtr.mod.Items.BRUSH.get());
    }

    /**
     * For block ID migration only, returns an identifier with the latest block id.
     */
    public static Identifier getMigrationId(Identifier checkId) {
        if (checkId.getNamespace().equals("jsblock")) {
            switch (checkId.getPath()) {
                case "bufferstop_1":
                    return Constants.id("buffer_stop");
                case "ceiling_1":
                    return Constants.id("ceiling_slanted");
                case "exit_sign_1":
                    return Constants.id("exit_sign_odd");
                case "faresaver_1":
                    return Constants.id("faresaver");
                case "helpline_3":
                    return Constants.id("helpline_standing_eal");
                case "helpline_4":
                    return Constants.id("helpline_standing");
                case "enquiry_machine_1":
                    return Constants.id("mtr_enquiry_machine");
                case "enquiry_machine_2":
                    return Constants.id("rv_enquiry_machine");
                case "enquiry_machine_3":
                    return Constants.id("mtr_enquiry_machine_wall");
                case "enquiry_machine_4":
                    return Constants.id("kcr_enquiry_machine");
                case "emg_stop_1":
                    return Constants.id("tcl_emg_stop_button");
                case "helpline_5":
                    return Constants.id("tml_emg_stop_button");
                case "helpline_6":
                    return Constants.id("sil_emg_stop_button");
                case "light_1":
                    return Constants.id("light_lantern");
                case "light_2":
                    return Constants.id("spot_lamp");
                case "mtr_stairs_1":
                    return Constants.id("mtr_stairs");
                case "op_button":
                    return Constants.id("operator_button");
                case "pids_4":
                    return Constants.id("pids_lcd");
                case "pids_rv_sil":
                    return Constants.id("pids_rv_sil_1");
                case "station_ceiling_1":
                    return Constants.id("station_ceiling_wrl");
                case "station_ceiling_1_station_color":
                    return Constants.id("station_ceiling_wrl_station_colored");
                case "station_ceiling_pole":
                    return Constants.id("station_ceiling_wrl_pole");
                case "station_name_tall_stand":
                    return Constants.id("station_name_standing");
                case "ticket_barrier_1_entrance":
                    return Constants.id("thales_ticket_barrier_entrance");
                case "ticket_barrier_1_exit":
                    return Constants.id("thales_ticket_barrier_exit");
                case "ticket_barrier_1_bare":
                    return Constants.id("thales_ticket_barrier_bare");
                case "inter_car_barrier_1_left":
                    return Constants.id("lrt_inter_car_barrier_left");
                case "inter_car_barrier_1_middle":
                    return Constants.id("lrt_inter_car_barrier_middle");
                case "inter_car_barrier_1_right":
                    return Constants.id("lrt_inter_car_barrier_right");
                case "subsidy_machine_1":
                    return Constants.id("subsidy_machine");
                case "trespass_sign_1":
                    return Constants.id("mtr_trespass_sign");
                case "trespass_sign_2":
                    return Constants.id("kcr_trespass_sign");
                case "trespass_sign_3":
                    return Constants.id("lrt_trespass_sign");
                case "water_machine_1":
                    return Constants.id("water_machine");
            }
        }
        return checkId;
    }
}
