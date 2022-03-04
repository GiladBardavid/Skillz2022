package bots;

import penguin_game.*;
import java.util.*;

public class BridgeHelper {

    public Map<IceBuilding, List<Bridge>> bridgesFromIceBuilding = new HashMap<>();
    public Map<PenguinGroup, Bridge> penguinGroupToBridge = new HashMap<>();

    public BridgeHelper(Game game) {
        // IceBuilding map
        for(IceBuilding iceBuilding : GameUtil.getAllIceBuildings(game)) {
            if(iceBuilding instanceof Iceberg) {
                Iceberg iceberg = (Iceberg) iceBuilding;
                bridgesFromIceBuilding.put(iceberg, Arrays.asList(iceberg.bridges));
            }
            else if(iceBuilding instanceof BonusIceberg) {
                BonusIceberg bonusIceberg = (BonusIceberg) iceBuilding;
                bridgesFromIceBuilding.put(bonusIceberg, Arrays.asList(bonusIceberg.bridges));
            }
        }

        // PenguinGroup map
        for(PenguinGroup penguinGroup : game.getAllPenguinGroups()) {
            IceBuilding start = penguinGroup.source;
            IceBuilding end = penguinGroup.destination;

            List<Bridge> bridgesFromStart = bridgesFromIceBuilding.get(start);
            if(bridgesFromStart != null) {
                for (Bridge bridge : bridgesFromStart) {
                    if (bridge.getEdges()[0] == end || bridge.getEdges()[1] == end) {
                        penguinGroupToBridge.put(penguinGroup, bridge);
                    }
                }
            }
        }
    }


    public int getActualTurnsTillArrival(PenguinGroup penguinGroup, List<BridgeAction> bridgeActions) {

        double speedMultiplier = 0;
        int turnsLeftForBridge = -1;
        int originalTurnsTillArrival = penguinGroup.turnsTillArrival;

        // Find bridge in pending actions
        for(BridgeAction bridgeAction : bridgeActions) {
            if((bridgeAction.from == penguinGroup.source && bridgeAction.to == penguinGroup.destination) || (bridgeAction.from == penguinGroup.destination && bridgeAction.to == penguinGroup.source)) {
                speedMultiplier = bridgeAction.speedMultiplier;
                turnsLeftForBridge = bridgeAction.duration;
            }
        }

        // find bridge that the penguin group is on
        if(turnsLeftForBridge == -1) {
            Bridge bridge = penguinGroupToBridge.get(penguinGroup);

            if(bridge != null) {
                speedMultiplier = bridge.speedMultiplier;
                turnsLeftForBridge = bridge.duration;
            }
        }

        if (turnsLeftForBridge == -1) {
            return originalTurnsTillArrival;
        }


        if(turnsLeftForBridge * speedMultiplier >= originalTurnsTillArrival) {
            return (int)Math.ceil(originalTurnsTillArrival / speedMultiplier);
        }

        return turnsLeftForBridge + (originalTurnsTillArrival - (int)(Math.floor(turnsLeftForBridge * speedMultiplier)));
    }



    public int getArrivalTurn(IceBuilding sender, IceBuilding destination, int turnsToSend, List<BridgeAction> bridgeActions) {

        int bridgeDuration = -1;
        double speedMultiplier = 0;

        for(BridgeAction bridgeAction : bridgeActions) {
            if((bridgeAction.from == sender && bridgeAction.to == destination) || (bridgeAction.from == destination && bridgeAction.to == sender)) {
                speedMultiplier = bridgeAction.speedMultiplier;
                bridgeDuration = bridgeAction.duration - turnsToSend;
            }
        }

        if(bridgeDuration < 0) {

            List<Bridge> bridgesFromSender = bridgesFromIceBuilding.get(sender);

            for(Bridge bridge : bridgesFromSender) {
                if(bridge.getEdges()[0] == destination || bridge.getEdges()[1] == destination) {
                    bridgeDuration = bridge.duration - turnsToSend;
                    speedMultiplier = bridge.speedMultiplier;

                    break;
                }
            }
        }


        int distance = sender.getTurnsTillArrival(destination);

        int defaultTurnsTillArrival = turnsToSend + distance;

        if(bridgeDuration <= 0) {
            return defaultTurnsTillArrival;
        }

        if(bridgeDuration * speedMultiplier >= distance) {
            return turnsToSend + (int)Math.ceil(distance / speedMultiplier);
        }

        return turnsToSend + bridgeDuration + (distance - (int)(Math.floor(bridgeDuration * speedMultiplier)));
    }


}
