package cn.quit5700.pathfindingbeacon.route;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class RouteService {
    private final RouteData data;

    public RouteService(RouteData data) {
        this.data = data;
    }

    public PlacementResult place(int number, UUID player, RoutePosition position) {
        validateNumber(number);
        boolean duplicate = data.nodes().stream().anyMatch(node ->
                node.number() == number
                        && node.position().x() == position.x()
                        && node.position().z() == position.z());
        if (duplicate) {
            return new PlacementResult(PlacementStatus.DUPLICATE_XZ, null);
        }

        UUID owner = data.owner(number);
        boolean active = owner == null || owner.equals(player);
        if (owner == null) {
            data.mutableOwners().put(number, player);
        }
        RouteNode node = new RouteNode(number, player, position, active);
        data.mutableNodes().put(position, node);

        if (active) {
            ArrayList<RoutePosition> order = data.mutableOrder(number);
            if (!order.isEmpty()) {
                addEdge(number, order.get(order.size() - 1), position);
            }
            order.add(position);
        }
        return new PlacementResult(active ? PlacementStatus.ACTIVE : PlacementStatus.INACTIVE, node);
    }

    public RouteNode remove(RoutePosition position) {
        RouteNode removed = data.mutableNodes().remove(position);
        if (removed == null) {
            return null;
        }
        if (removed.active()) {
            int number = removed.number();
            data.mutableOrder(number).remove(position);
            data.mutableEdges(number).entrySet().removeIf(entry -> entry.getValue().touches(position));
            boolean hasActive = data.nodes().stream()
                    .anyMatch(node -> node.number() == number && node.active());
            if (!hasActive) {
                data.mutableOwners().remove(number);
            }
        }
        return removed;
    }

    public boolean canRemove(RoutePosition position, UUID player, boolean creative) {
        RouteNode node = data.node(position);
        return node != null && (creative || !node.active() || player.equals(data.owner(node.number())));
    }

    public void removeEdge(int number, RoutePosition first, RoutePosition second) {
        data.mutableEdges(number).remove(new RouteData.EdgeKey(first, second));
    }

    public ReorderStatus reorder(
            int number,
            UUID player,
            RoutePosition first,
            RoutePosition second,
            boolean creative
    ) {
        RouteNode firstNode = data.node(first);
        RouteNode secondNode = data.node(second);
        if (first.equals(second)
                || firstNode == null
                || secondNode == null
                || !firstNode.active()
                || !secondNode.active()
                || firstNode.number() != number
                || secondNode.number() != number) {
            return ReorderStatus.INVALID;
        }
        if (!creative && !player.equals(data.owner(number))) {
            return ReorderStatus.DENIED;
        }

        Set<RoutePosition> component = connectedComponent(number, first);
        if (!component.contains(second)) {
            addEdge(number, first, second);
            return ReorderStatus.RECONNECTED;
        }

        ArrayList<RoutePosition> order = data.mutableOrder(number);
        int firstIndex = order.indexOf(first);
        int secondIndex = order.indexOf(second);
        RoutePosition older = firstIndex < secondIndex ? first : second;
        RoutePosition newer = firstIndex < secondIndex ? second : first;
        order.remove(newer);
        order.add(order.indexOf(older), newer);

        data.mutableEdges(number).entrySet().removeIf(entry ->
                component.contains(entry.getValue().first()) && component.contains(entry.getValue().second()));
        List<RoutePosition> componentOrder = order.stream().filter(component::contains).toList();
        for (int i = 1; i < componentOrder.size(); i++) {
            addEdge(number, componentOrder.get(i - 1), componentOrder.get(i));
        }
        return ReorderStatus.REORDERED;
    }

    public List<RouteNode> clearColor(int number) {
        validateNumber(number);
        List<RouteNode> removed = data.nodes().stream()
                .filter(node -> node.number() == number)
                .toList();
        removed.forEach(node -> data.mutableNodes().remove(node.position()));
        data.mutableOwners().remove(number);
        data.mutableOrders().remove(number);
        data.mutableAllEdges().remove(number);
        return removed;
    }

    private void addEdge(int number, RoutePosition first, RoutePosition second) {
        RouteData.EdgeKey key = new RouteData.EdgeKey(first, second);
        data.mutableEdges(number).computeIfAbsent(key, ignored ->
                new RouteEdge(number, first, second, data.nextEdgeOrder()));
    }

    private Set<RoutePosition> connectedComponent(int number, RoutePosition start) {
        Set<RoutePosition> visited = new HashSet<>();
        ArrayDeque<RoutePosition> pending = new ArrayDeque<>();
        pending.add(start);
        while (!pending.isEmpty()) {
            RoutePosition current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            for (RouteEdge edge : data.edges(number)) {
                if (edge.first().equals(current)) pending.add(edge.second());
                if (edge.second().equals(current)) pending.add(edge.first());
            }
        }
        return visited;
    }

    private static void validateNumber(int number) {
        if (number < 1 || number > 30) {
            throw new IllegalArgumentException("Route number must be between 1 and 30");
        }
    }
}
