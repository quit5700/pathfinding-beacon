# 寻路信标 Implementation Plan（实施计划）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为《我的世界》Java 版 1.21.1 构建可安装的 Fabric“寻路信标”模组，实现 30 种方块、两种工具、持久线路、多人占用、取消指令和高性能光柱。

**Architecture:** 服务端以每个 `ServerWorld` 的 `PersistentState` 保存节点、顺序、边和占用者，所有规则集中在可测试的 `RouteService`。客户端接收线路快照，将 Bresenham 路径缓存为列并在世界渲染阶段批量绘制，不创建信标方块实体。

**Tech Stack:** Java 21 目标字节码、Fabric Loader、Fabric API、Fabric Loom、Yarn mappings、Gradle、JUnit 5、Fabric GameTest。

---

### Task 1: Fabric 工程与测试框架

**Files:**
- Create: `settings.gradle`
- Create: `build.gradle`
- Create: `gradle.properties`
- Create: `src/main/resources/fabric.mod.json`
- Create: `src/test/java/cn/quit5700/pathfindingbeacon/SmokeTest.java`

- [ ] **Step 1: 写失败的冒烟测试**

```java
@Test
void exposesThirtyRouteColors() {
    assertEquals(30, RouteColors.size());
}
```

- [ ] **Step 2: 运行测试并确认因 `RouteColors` 不存在而失败**

Run: `./gradlew test --tests '*SmokeTest'`
Expected: `cannot find symbol RouteColors`

- [ ] **Step 3: 添加 Fabric 1.21.1 构建配置与最小颜色注册表**

```java
public final class RouteColors {
    public static final int COUNT = 30;
    public static int size() { return COUNT; }
    private RouteColors() {}
}
```

- [ ] **Step 4: 运行冒烟测试**

Run: `./gradlew test --tests '*SmokeTest'`
Expected: `BUILD SUCCESSFUL`

### Task 2: 纯 Java 线路领域模型

**Files:**
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/RouteNode.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/RouteEdge.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/RouteData.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/RouteService.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/Bresenham.java`
- Test: `src/test/java/cn/quit5700/pathfindingbeacon/route/RouteServiceTest.java`
- Test: `src/test/java/cn/quit5700/pathfindingbeacon/route/BresenhamTest.java`

- [ ] **Step 1: 测试 Bresenham 一格宽连续直线**

```java
@Test
void createsContinuousDiagonal() {
    var points = Bresenham.line(0, 0, 5, 3);
    assertEquals(new Column(0, 0), points.getFirst());
    assertEquals(new Column(5, 3), points.getLast());
    assertTrue(adjacent(points));
}
```

- [ ] **Step 2: 运行并确认 `Bresenham` 缺失失败**

Run: `./gradlew test --tests '*BresenhamTest'`
Expected: FAIL because `Bresenham` is missing

- [ ] **Step 3: 实现整数误差累积直线算法**

```java
while (true) {
    result.add(new Column(x0, z0));
    if (x0 == x1 && z0 == z1) break;
    int e2 = 2 * error;
    if (e2 >= dz) { error += dz; x0 += sx; }
    if (e2 <= dx) { error += dx; z0 += sz; }
}
```

- [ ] **Step 4: 测试占用、无效节点、追加、删除不重连、释放颜色和顺序重排**

```java
@Test void otherPlayerCreatesInactiveNode() { /* assert active false */ }
@Test void removingMiddleDeletesOnlyIncidentEdges() { /* A-B-C-D => keep C-D */ }
@Test void reconnectAddsOnlySelectedEdge() { /* two components => one edge */ }
@Test void reorderMovesNewerBeforeOlder() { /* A-B-C-D => A-D-B-C */ }
```

- [ ] **Step 5: 运行并确认领域测试失败**

Run: `./gradlew test --tests '*RouteServiceTest'`
Expected: FAIL because route operations are missing

- [ ] **Step 6: 实现最小 `RouteService` 并保持数据结构与 Minecraft API 解耦**

```java
public PlacementResult place(int color, UUID player, RoutePosition pos) { ... }
public RemovalResult remove(RoutePosition pos) { ... }
public ReorderResult selectAndReorder(int color, UUID player, RoutePosition a, RoutePosition b, boolean creative) { ... }
public void clearColor(int color) { ... }
```

- [ ] **Step 7: 运行全部领域测试**

Run: `./gradlew test`
Expected: all tests PASS

### Task 3: 注册方块、物品、标签页和配方

**Files:**
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/PathfindingBeaconMod.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/registry/ModBlocks.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/registry/ModItems.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/registry/ModItemGroup.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/block/PathfindingBlock.java`
- Create: `src/main/resources/data/pathfinding_beacon/recipes/*.json`
- Create: `src/main/resources/assets/pathfinding_beacon/lang/zh_cn.json`

- [ ] **Step 1: 添加注册数量测试并确认失败**

```java
@Test void declaresThirtyBlocks() {
    assertEquals(30, ModBlocks.ROUTE_BLOCKS.size());
}
```

- [ ] **Step 2: 实现 30 个亮度 15、不可由普通方式破坏的方块和两个无耐久工具**

```java
AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)
    .luminance(state -> 15)
    .strength(-1.0F, 3_600_000.0F)
    .pistonBehavior(PistonBehavior.BLOCK);
```

- [ ] **Step 3: 添加“寻路”创造模式标签页并按 1 至 30、取消器、重排器排序**

- [ ] **Step 4: 生成 30 个升级配方、取消器 T 字配方和重排器斧形镜像配方**

- [ ] **Step 5: 运行资源处理与测试**

Run: `./gradlew processResources test`
Expected: `BUILD SUCCESSFUL`

### Task 4: 世界持久状态与方块交互

**Files:**
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/RoutePersistentState.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/route/WorldRouteManager.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/item/CancellerItem.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/item/SequenceReordererItem.java`
- Test: `src/test/java/cn/quit5700/pathfindingbeacon/route/RouteSerializationTest.java`

- [ ] **Step 1: 写 NBT 往返测试并确认失败**

```java
@Test void roundTripsOwnersNodesOrderAndEdges() {
    NbtCompound saved = state.writeNbt(new NbtCompound());
    assertEquals(state.snapshot(), RoutePersistentState.fromNbt(saved).snapshot());
}
```

- [ ] **Step 2: 实现 NBT 序列化并运行测试**

Run: `./gradlew test --tests '*RouteSerializationTest'`
Expected: PASS

- [ ] **Step 3: 接入方块放置、重复 X/Z 拦截、占用提示和创造模式移除**

- [ ] **Step 4: 接入取消器三击计数、权限、掉落和进度重置**

- [ ] **Step 5: 接入重排器双右键选择、断线连接和同线重排**

- [ ] **Step 6: 运行测试并启动专用测试服务器检查注册**

Run: `./gradlew test runServer --args nogui`
Expected: tests PASS and server reaches `Done`

### Task 5: 指令、登录提示和配方解锁

**Files:**
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/command/IdCancelCommand.java`
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/event/PlayerEvents.java`
- Test: `src/test/java/cn/quit5700/pathfindingbeacon/command/IdCancelRulesTest.java`

- [ ] **Step 1: 测试 1 至 30 参数校验和清空语义并确认失败**

- [ ] **Step 2: 注册大小写组合可解析的取消指令入口和整数参数**

```java
literal("pfcancel").then(argument("id", integer(1, 30)).executes(...));
```

- [ ] **Step 3: 清除当前维度全部有效/无效节点、边、顺序和占用，不掉落**

- [ ] **Step 4: 登录时仅向进入者显示一次帮助提示，并在制造任意镐后解锁全部配方**

- [ ] **Step 5: 运行指令规则测试**

Run: `./gradlew test --tests '*IdCancelRulesTest'`
Expected: PASS

### Task 6: 服务端同步和客户端光柱

**Files:**
- Create: `src/main/java/cn/quit5700/pathfindingbeacon/network/RouteNetworking.java`
- Create: `src/client/java/cn/quit5700/pathfindingbeacon/PathfindingBeaconClient.java`
- Create: `src/client/java/cn/quit5700/pathfindingbeacon/client/ClientRouteState.java`
- Create: `src/client/java/cn/quit5700/pathfindingbeacon/client/BeamRenderer.java`
- Test: `src/test/java/cn/quit5700/pathfindingbeacon/client/ClientColumnResolverTest.java`

- [ ] **Step 1: 测试交叉后生成覆盖、删除恢复和端点叠放颜色循环并确认失败**

```java
@Test void newestCrossingWinsAndOldColorReturnsAfterRemoval() { ... }
@Test void stackedEndpointsCycleInNumberOrderEachSecond() { ... }
```

- [ ] **Step 2: 实现可测试的列缓存解析器并运行测试**

- [ ] **Step 3: 服务端在登录、换维度和线路变更后发送当前维度快照**

- [ ] **Step 4: 客户端缓存 Bresenham 列，按视距裁剪并批量绘制竖直四面光柱**

- [ ] **Step 5: 执行客户端编译**

Run: `./gradlew compileClientJava test`
Expected: `BUILD SUCCESSFUL`

### Task 7: 材质、模型和资源校验

**Files:**
- Create: `scripts/generate_resources.ps1`
- Create: `src/main/resources/assets/pathfinding_beacon/textures/block/route_*.png`
- Create: `src/main/resources/assets/pathfinding_beacon/textures/item/*.png`
- Create: `src/main/resources/assets/pathfinding_beacon/blockstates/*.json`
- Create: `src/main/resources/assets/pathfinding_beacon/models/block/*.json`
- Create: `src/main/resources/assets/pathfinding_beacon/models/item/*.json`

- [ ] **Step 1: 生成 30 张纯色高反差数字材质及纯蓝工具材质**

- [ ] **Step 2: 生成对应方块状态、方块模型和物品模型**

- [ ] **Step 3: 校验 30 个资源集合完整且 PNG 尺寸一致**

Run: `./gradlew validateAccessWidener processResources`
Expected: no missing resource errors

### Task 8: 完整验证与交付

**Files:**
- Create: `README.md`
- Create: `CHANGELOG.md`

- [ ] **Step 1: 写中文安装和操作说明，列明 Fabric API 前置依赖**

- [ ] **Step 2: 运行所有单元测试**

Run: `./gradlew clean test`
Expected: all tests PASS

- [ ] **Step 3: 构建正式 JAR**

Run: `./gradlew build`
Expected: `BUILD SUCCESSFUL` and remapped JAR under `build/libs`

- [ ] **Step 4: 检查 JAR 内容、模组元数据、配方、语言和 30 套资源**

Run: `jar tf build/libs/pathfinding-beacon-*.jar`
Expected: contains `fabric.mod.json`, classes, recipes, models and textures

- [ ] **Step 5: 记录未能自动验证的游戏内视觉项目**

人工检查项：玻璃/树叶/水穿透、地下挖开显现、交叉颜色恢复、叠放颜色每秒轮换、多人占用提示。
