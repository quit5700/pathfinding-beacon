package cn.quit5700.pathfindingbeacon.route;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RoutePersistentState extends PersistentState {
    public static final String ID = "pathfinding_beacon_routes";
    public static final Codec<RoutePersistentState> CODEC = Codec.PASSTHROUGH.xmap(
            RoutePersistentState::fromDynamic,
            RoutePersistentState::toDynamic
    );
    public static final PersistentStateType<RoutePersistentState> TYPE = new PersistentStateType<>(
            ID,
            RoutePersistentState::new,
            CODEC,
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

    private static RoutePersistentState fromDynamic(Dynamic<?> dynamic) {
        return fromNbt((NbtCompound) dynamic.convert(NbtOps.INSTANCE).getValue(), null);
    }

    private Dynamic<?> toDynamic() {
        return new Dynamic<>(NbtOps.INSTANCE, writeNbt(new NbtCompound(), null));
    }

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RouteSnapshot snapshot = data.snapshot();
        NbtList owners = new NbtList();
        snapshot.owners().forEach((number, owner) -> {
            NbtCompound entry = new NbtCompound();
            entry.putInt("Number", number);
            entry.putIntArray("Owner", Uuids.toIntArray(owner));
            owners.add(entry);
        });
        nbt.put("Owners", owners);

        NbtList nodes = new NbtList();
        snapshot.nodes().forEach(node -> {
            NbtCompound entry = writePosition(node.position());
            entry.putInt("Number", node.number());
            entry.putIntArray("PlacedBy", Uuids.toIntArray(node.placedBy()));
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
        NbtList ownerList = nbt.getListOrEmpty("Owners");
        for (int i = 0; i < ownerList.size(); i++) {
            NbtCompound entry = ownerList.getCompoundOrEmpty(i);
            owners.put(entry.getInt("Number", 0), readUuid(entry, "Owner"));
        }

        List<RouteNode> nodes = new ArrayList<>();
        NbtList nodeList = nbt.getListOrEmpty("Nodes");
        for (int i = 0; i < nodeList.size(); i++) {
            NbtCompound entry = nodeList.getCompoundOrEmpty(i);
            nodes.add(new RouteNode(
                    entry.getInt("Number", 0),
                    readUuid(entry, "PlacedBy"),
                    readPosition(entry),
                    entry.getBoolean("Active", false)
            ));
        }

        Map<Integer, List<RoutePosition>> orders = new HashMap<>();
        NbtList orderList = nbt.getListOrEmpty("Orders");
        for (int i = 0; i < orderList.size(); i++) {
            NbtCompound entry = orderList.getCompoundOrEmpty(i);
            NbtList values = entry.getListOrEmpty("Positions");
            List<RoutePosition> positions = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                positions.add(readPosition(values.getCompoundOrEmpty(j)));
            }
            orders.put(entry.getInt("Number", 0), positions);
        }

        List<RouteEdge> edges = new ArrayList<>();
        NbtList edgeList = nbt.getListOrEmpty("Edges");
        for (int i = 0; i < edgeList.size(); i++) {
            NbtCompound entry = edgeList.getCompoundOrEmpty(i);
            edges.add(new RouteEdge(
                    entry.getInt("Number", 0),
                    readPosition(entry.getCompoundOrEmpty("First")),
                    readPosition(entry.getCompoundOrEmpty("Second")),
                    entry.getLong("CreatedOrder", 0L)
            ));
        }

        RouteSnapshot snapshot = new RouteSnapshot(
                owners,
                nodes,
                orders,
                edges,
                Math.max(1L, nbt.getLong("NextEdgeOrder", 1L))
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
        return new RoutePosition(nbt.getInt("X", 0), nbt.getInt("Y", 0), nbt.getInt("Z", 0));
    }

    private static UUID readUuid(NbtCompound nbt, String key) {
        return nbt.getIntArray(key)
                .map(Uuids::toUuid)
                .orElse(new UUID(0L, 0L));
    }
}
