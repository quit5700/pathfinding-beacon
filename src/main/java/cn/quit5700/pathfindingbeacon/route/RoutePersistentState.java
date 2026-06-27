package cn.quit5700.pathfindingbeacon.route;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RoutePersistentState extends PersistentState {
    public static final String ID = "pathfinding_beacon_routes";
    public static final Type<RoutePersistentState> TYPE = new Type<>(
            RoutePersistentState::new,
            RoutePersistentState::fromNbt,
            null
    );

    private final RouteData data;
    private final RouteService service;

    public RoutePersistentState() {
        this(new RouteData());
    }

    private RoutePersistentState(RouteData data) {
        this.data = data;
        this.service = new RouteService(data);
    }

    public RouteData data() {
        return data;
    }

    public RouteService service() {
        return service;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RouteSnapshot snapshot = data.snapshot();
        NbtList owners = new NbtList();
        snapshot.owners().forEach((number, owner) -> {
            NbtCompound entry = new NbtCompound();
            entry.putInt("Number", number);
            entry.putUuid("Owner", owner);
            owners.add(entry);
        });
        nbt.put("Owners", owners);

        NbtList nodes = new NbtList();
        snapshot.nodes().forEach(node -> {
            NbtCompound entry = writePosition(node.position());
            entry.putInt("Number", node.number());
            entry.putUuid("PlacedBy", node.placedBy());
            entry.putBoolean("Active", node.active());
            nodes.add(entry);
        });
        nbt.put("Nodes", nodes);

        NbtList orders = new NbtList();
        snapshot.orders().forEach((number, positions) -> {
            NbtCompound entry = new NbtCompound();
            entry.putInt("Number", number);
            NbtList values = new NbtList();
            positions.forEach(position -> values.add(writePosition(position)));
            entry.put("Positions", values);
            orders.add(entry);
        });
        nbt.put("Orders", orders);

        NbtList edges = new NbtList();
        snapshot.edges().forEach(edge -> {
            NbtCompound entry = new NbtCompound();
            entry.putInt("Number", edge.number());
            entry.put("First", writePosition(edge.first()));
            entry.put("Second", writePosition(edge.second()));
            entry.putLong("CreatedOrder", edge.createdOrder());
            edges.add(entry);
        });
        nbt.put("Edges", edges);
        nbt.putLong("NextEdgeOrder", snapshot.nextEdgeOrder());
        return nbt;
    }

    public static RoutePersistentState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Map<Integer, UUID> owners = new HashMap<>();
        NbtList ownerList = nbt.getList("Owners", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < ownerList.size(); i++) {
            NbtCompound entry = ownerList.getCompound(i);
            owners.put(entry.getInt("Number"), entry.getUuid("Owner"));
        }

        List<RouteNode> nodes = new ArrayList<>();
        NbtList nodeList = nbt.getList("Nodes", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nodeList.size(); i++) {
            NbtCompound entry = nodeList.getCompound(i);
            nodes.add(new RouteNode(
                    entry.getInt("Number"),
                    entry.getUuid("PlacedBy"),
                    readPosition(entry),
                    entry.getBoolean("Active")
            ));
        }

        Map<Integer, List<RoutePosition>> orders = new HashMap<>();
        NbtList orderList = nbt.getList("Orders", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < orderList.size(); i++) {
            NbtCompound entry = orderList.getCompound(i);
            NbtList values = entry.getList("Positions", NbtElement.COMPOUND_TYPE);
            List<RoutePosition> positions = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                positions.add(readPosition(values.getCompound(j)));
            }
            orders.put(entry.getInt("Number"), positions);
        }

        List<RouteEdge> edges = new ArrayList<>();
        NbtList edgeList = nbt.getList("Edges", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < edgeList.size(); i++) {
            NbtCompound entry = edgeList.getCompound(i);
            edges.add(new RouteEdge(
                    entry.getInt("Number"),
                    readPosition(entry.getCompound("First")),
                    readPosition(entry.getCompound("Second")),
                    entry.getLong("CreatedOrder")
            ));
        }

        RouteSnapshot snapshot = new RouteSnapshot(
                owners,
                nodes,
                orders,
                edges,
                Math.max(1L, nbt.getLong("NextEdgeOrder"))
        );
        return new RoutePersistentState(RouteData.fromSnapshot(snapshot));
    }

    private static NbtCompound writePosition(RoutePosition position) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("X", position.x());
        nbt.putInt("Y", position.y());
        nbt.putInt("Z", position.z());
        return nbt;
    }

    private static RoutePosition readPosition(NbtCompound nbt) {
        return new RoutePosition(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
    }
}
