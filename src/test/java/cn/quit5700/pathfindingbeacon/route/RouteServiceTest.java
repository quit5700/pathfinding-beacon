package cn.quit5700.pathfindingbeacon.route;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteServiceTest {
    private static final UUID ALICE = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID BOB = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private RouteData data;
    private RouteService service;

    @BeforeEach
    void setUp() {
        data = new RouteData();
        service = new RouteService(data);
    }

    @Test
    void firstPlayerOwnsColorAndOtherPlayerCreatesInactiveNode() {
        assertEquals(PlacementStatus.ACTIVE, service.place(1, ALICE, pos(0)).status());
        PlacementResult second = service.place(1, BOB, pos(1));

        assertEquals(PlacementStatus.INACTIVE, second.status());
        assertEquals(ALICE, data.owner(1));
        assertFalse(second.node().active());
        assertEquals(List.of(pos(0)), data.order(1));
    }

    @Test
    void rejectsSameNumberAtSameHorizontalCoordinates() {
        service.place(1, ALICE, new RoutePosition(2, 10, 3));

        assertEquals(
                PlacementStatus.DUPLICATE_XZ,
                service.place(1, ALICE, new RoutePosition(2, 80, 3)).status()
        );
    }

    @Test
    void removingMiddleNodeDeletesOnlyIncidentEdges() {
        RoutePosition a = pos(0);
        RoutePosition b = pos(1);
        RoutePosition c = pos(2);
        RoutePosition d = pos(3);
        service.place(1, ALICE, a);
        service.place(1, ALICE, b);
        service.place(1, ALICE, c);
        service.place(1, ALICE, d);

        service.remove(b);

        assertEquals(List.of(a, c, d), data.order(1));
        assertEquals(1, data.edges(1).size());
        assertTrue(data.hasEdge(1, c, d));
        assertFalse(data.hasEdge(1, a, c));
    }

    @Test
    void releasesOwnershipOnlyAfterLastActiveNodeIsRemoved() {
        RoutePosition a = pos(0);
        RoutePosition b = pos(1);
        service.place(4, ALICE, a);
        service.place(4, ALICE, b);

        service.remove(a);
        assertEquals(ALICE, data.owner(4));
        service.remove(b);

        assertNull(data.owner(4));
    }

    @Test
    void reconnectingComponentsAddsOnlySelectedEdge() {
        RoutePosition a = pos(0);
        RoutePosition b = pos(1);
        RoutePosition c = pos(2);
        RoutePosition d = pos(3);
        service.place(2, ALICE, a);
        service.place(2, ALICE, b);
        service.place(2, ALICE, c);
        service.place(2, ALICE, d);
        service.removeEdge(2, b, c);

        assertEquals(ReorderStatus.RECONNECTED, service.reorder(2, ALICE, b, c, false));
        assertTrue(data.hasEdge(2, a, b));
        assertTrue(data.hasEdge(2, b, c));
        assertTrue(data.hasEdge(2, c, d));
    }

    @Test
    void reorderingConnectedNodesMovesNewerBeforeOlder() {
        RoutePosition a = pos(0);
        RoutePosition b = pos(1);
        RoutePosition c = pos(2);
        RoutePosition d = pos(3);
        service.place(3, ALICE, a);
        service.place(3, ALICE, b);
        service.place(3, ALICE, c);
        service.place(3, ALICE, d);

        assertEquals(ReorderStatus.REORDERED, service.reorder(3, ALICE, d, b, false));
        assertEquals(List.of(a, d, b, c), data.order(3));
        assertTrue(data.hasEdge(3, a, d));
        assertTrue(data.hasEdge(3, d, b));
        assertTrue(data.hasEdge(3, b, c));
        assertEquals(3, data.edges(3).size());
    }

    @Test
    void survivalPlayerCannotEditAnotherPlayersColorButCreativeCan() {
        RoutePosition a = pos(0);
        RoutePosition b = pos(1);
        service.place(5, ALICE, a);
        service.place(5, ALICE, b);

        assertEquals(ReorderStatus.DENIED, service.reorder(5, BOB, a, b, false));
        assertEquals(ReorderStatus.REORDERED, service.reorder(5, BOB, a, b, true));
    }

    private static RoutePosition pos(int x) {
        return new RoutePosition(x, 64, 0);
    }
}
