# 版本迁移与问题修复记录

本文件记录寻路信标在 Minecraft（我的世界）版本迁移、Fabric（织物）适配、构建发布过程中已经验证成功的方法，以及已经尝试失败的方案。以后生成本项目或其他模组的 26.x 版本时，优先按这里的方法处理。

## 2026-06-28：Minecraft 26.x Fabric 适配成功记录

### 适用范围

- Minecraft（我的世界）Java 版：`26.1.2`、`26.2`
- Java（爪哇）：`25`
- Fabric Loader（Fabric 模组加载器）：`0.19.3`
- Fabric API（Fabric 应用程序接口）：`0.153.0+26.1.2`、`0.153.0+26.2`
- Fabric Loom（Fabric 构建插件）：`1.15-SNAPSHOT`，本机解析为 `1.15.5`
- Mappings（命名映射）：Mojang Official Mappings（Mojang 官方命名映射）
- Gradle Wrapper（Gradle 包装器）：`9.2.1`

### 成功做法

1. 26.x 不使用 Yarn Mappings（Yarn 命名映射）。
2. `build.gradle` 中不要声明 `mappings` 依赖，让 Fabric Loom（Fabric 构建插件）按 26.x 的 Mojang Official Mappings（Mojang 官方命名映射）方式处理。
3. Java 编译版本统一设为 `25`，本机使用 `C:/Program Files/Zulu/zulu-25`。
4. 不依赖完整 Fabric API（Fabric 应用程序接口）总包参与编译，只声明本模组实际使用的 Fabric API 子模块；玩家安装时仍然要求安装完整 Fabric API。
5. 当前源码保留在最新已验证版本 `26.2`，需要生成 `26.1.2` 时临时切换 `gradle.properties` 和 `fabric.mod.json` 的版本号与子模块版本，构建完成后再切回 `26.2`。
6. 中文路径环境下，测试任务使用 ASCII（纯英文）临时目录同步 class（类）输出，避免 Gradle（构建工具）测试 worker（工作进程）读中文路径时乱码。

### 26.2 已验证配置

- Minecraft（我的世界）Java 版：`26.2`
- Fabric API（Fabric 应用程序接口）：`0.153.0+26.2`
- `fabric-api-base`：`2.0.4+ece063239e`
- `fabric-command-api-v2`：`3.1.0+00cb03469e`
- `fabric-creative-tab-api-v1`：`5.0.14+d871b99e9e`
- `fabric-entity-events-v1`：`5.0.5+06488ac19e`
- `fabric-lifecycle-events-v1`：`4.1.3+4575b05f9e`
- `fabric-networking-api-v1`：`6.3.3+72073ef09e`
- `fabric-rendering-v1`：`25.2.0+2b0d8a229e`

### 26.1.2 已验证配置

- Minecraft（我的世界）Java 版：`26.1.2`
- Fabric API（Fabric 应用程序接口）：`0.153.0+26.1.2`
- `fabric-api-base`：`2.0.3+ece063234c`
- `fabric-command-api-v2`：`3.0.5+e2bdee784c`
- `fabric-creative-tab-api-v1`：`5.0.11+d871b99e4c`
- `fabric-entity-events-v1`：`5.0.2+e2bdee784c`
- `fabric-lifecycle-events-v1`：`4.1.1+df84eb3d4c`
- `fabric-networking-api-v1`：`6.3.1+554860db4c`
- `fabric-rendering-v1`：`23.2.0+c9da2e974c`

### 必须同步修改的文件

- `gradle.properties`：Minecraft（我的世界）、Java（爪哇）、Fabric Loader（Fabric 模组加载器）、Fabric API（Fabric 应用程序接口）、Fabric Loom（Fabric 构建插件）、Fabric API 子模块版本。
- `build.gradle`：确认使用 Java 25、Mojang Official Mappings（Mojang 官方命名映射）方式、Fabric API 子模块依赖、中文路径测试兼容逻辑。
- `src/main/resources/fabric.mod.json`：Minecraft（我的世界）版本范围、Java（爪哇）版本、Fabric API（Fabric 应用程序接口）版本。
- `README.md`、`VERSION_INFO.md`、`SUPPORTED_VERSIONS.md`：交付前写清版本信息和下载文件。

### 验证方式

每次交付前至少运行：

```powershell
.\gradlew.bat clean build --no-daemon --console=plain
```

构建成功后必须先复制到本地 `dist`（分发目录）：

- `build/libs/pathfinding-beacon-1.0.0.jar`
- `build/libs/pathfinding-beacon-1.0.0-sources.jar`

到 `dist`（分发目录），并按目标 Minecraft（我的世界）版本重命名，例如：

- `pathfinding-beacon-1.0.0-mc26.2-fabric.jar`
- `pathfinding-beacon-1.0.0-mc26.2-fabric-sources.jar`

本地 JAR（Java 归档）文件确认存在后，再发布到 GitHub Releases（GitHub 发行版）。发布时使用 `gh`（GitHub 命令行）：

```powershell
gh release upload v1.0.0 dist\pathfinding-beacon-1.0.0-mc26.2-fabric.jar dist\pathfinding-beacon-1.0.0-mc26.2-fabric-sources.jar --clobber
```

## 已尝试失败：26.x 使用完整 Fabric API 总包参与编译

### 问题类型

- mapping / Yarn / Fabric API 问题
- Gradle 构建问题
- 版本适配问题

### 问题现象

在 26.2 下声明完整 Fabric API（Fabric 应用程序接口）总包后，Fabric Loom（Fabric 构建插件）转换 datagen（数据生成）模块时报错，构建失败。

### 根本原因

Fabric API 总包中的 datagen（数据生成）模块在 26.2 + Loom 1.15 路径下触发类型变量转换问题；本模组实际不需要 datagen 模块参与运行或编译。

### 修复方式

不要把完整 Fabric API 总包作为编译依赖；只声明实际使用的 Fabric API 子模块。玩家端仍然安装完整 Fabric API。

### 如何验证已经修好

运行：

```powershell
.\gradlew.bat clean build --no-daemon --console=plain
```

26.1.2 与 26.2 均已通过构建。

### 下次应避免

不要在 26.x 适配中重复回到完整 Fabric API 总包编译方案，除非 Fabric API 或 Fabric Loom 官方后续明确修复并重新验证成功。

## 已尝试失败：忽略中文路径导致测试类加载异常

### 问题类型

- 中文路径 / 编码问题
- Gradle 构建问题

### 问题现象

项目位于中文目录时，Gradle（构建工具）测试 worker（工作进程）可能因为路径乱码导致找不到测试 class（类）。

### 根本原因

部分 Java/Gradle 测试进程在中文路径、控制台编码或临时目录组合下会出现路径编码不一致。

### 修复方式

在 `build.gradle` 中增加测试输出同步逻辑，把 main/test class（主代码/测试代码类文件）复制到 `java.io.tmpdir/pathfinding-beacon-test/<mc_version>` 这种 ASCII（纯英文）临时目录，再让 test（测试）任务从该目录读取。

### 如何验证已经修好

在中文路径 `C:\Users\quit5700\Documents\mc虚拟线路模组` 下运行：

```powershell
.\gradlew.bat clean build --no-daemon --console=plain
```

构建和测试均通过。

### 下次应避免

不要把中文路径构建失败直接归类为用户环境问题；优先检查编码、路径引用、命令参数引号和测试 classpath（类路径）。

## 2026-06-28：26.x 启动崩溃，Block id not set

### 问题类型

- 版本适配问题
- 旧版本 API 迁移问题
- 客户端与服务端环境问题

### 问题现象

Minecraft（我的世界）26.2 客户端启动时，在初始化 `pathfinding_beacon` 主入口阶段崩溃：

```text
Could not execute entrypoint stage 'main'
Caused by: java.lang.NullPointerException: Block id not set
```

崩溃位置：

- `PathfindingBlock.<init>`
- `ModBlocks.registerBlocks`
- `PathfindingBeaconMod.onInitialize`

### 根本原因

26.x 的 `BlockBehaviour.Properties`（方块行为属性）和 `Item.Properties`（物品属性）在构造 Block（方块）/Item（物品）前需要设置 `ResourceKey`（资源键）。旧写法是先 `new Block` / `new Item`，再 `Registry.register`（注册），编译能过，但运行时会因为默认掉落表或描述 ID（标识符）推导时找不到注册键而崩溃。

### 修改了哪些文件

- `src/main/java/cn/quit5700/pathfindingbeacon/block/PathfindingBlock.java`
- `src/main/java/cn/quit5700/pathfindingbeacon/registry/ModBlocks.java`
- `src/main/java/cn/quit5700/pathfindingbeacon/registry/ModItems.java`
- `SUPPORTED_VERSIONS.md`
- `docs/PORTING_NOTES.md`

### 具体修复方式

1. 方块注册时先创建 `ResourceKey<Block>`（方块资源键）。
2. 调用 `BlockBehaviour.Properties.setId(key)`（设置注册键）后再构造 `PathfindingBlock`。
3. 方块物品和两个工具物品注册时先创建 `ResourceKey<Item>`（物品资源键）。
4. 调用 `Item.Properties.setId(key)`（设置注册键）后再构造 `BlockItem`、`CancellerItem`、`SequenceReordererItem`。

### 如何验证已经修好

已分别验证：

```powershell
.\gradlew.bat clean build --no-daemon --console=plain
```

验证版本：

- Minecraft（我的世界）26.1.2：构建成功，并重新生成本地 JAR（Java 归档）。
- Minecraft（我的世界）26.2：构建成功，并重新生成本地 JAR（Java 归档）。

### 下次遇到类似问题应避免什么

不要只以 `./gradlew build`（Gradle 构建）成功判断 26.x 适配完成。26.x 中 Block（方块）/Item（物品）注册键缺失属于运行时初始化问题，必须检查 `Properties.setId(...)` 是否在构造前完成。

## 2026-06-28：1.21.11 启动崩溃，Block id not set

### 问题类型

- 版本适配问题
- 旧版本 API 迁移问题
- Yarn / Fabric API 问题

### 问题现象

Minecraft（我的世界）1.21.11 客户端启动时，在初始化 `pathfinding_beacon` 主入口阶段崩溃：

```text
Could not execute entrypoint stage 'main'
Caused by: java.lang.NullPointerException: Block id not set
```

崩溃位置：

- `PathfindingBlock.<init>`
- `ModBlocks.registerBlocks`
- `PathfindingBeaconMod.onInitialize`

### 根本原因

1.21.11 的 Yarn Mappings（Yarn 命名映射）下，`AbstractBlock.Settings`（方块设置）和 `Item.Settings`（物品设置）也需要在构造 Block（方块）/Item（物品）前绑定 `RegistryKey`（注册键）。旧写法先构造对象再注册，编译能通过，但启动时默认掉落表或翻译键推导会找不到方块 ID。

### 修改了哪些文件

- 临时 1.21.11 工作树：`.worktrees/mc12111-fix/src/main/java/cn/quit5700/pathfindingbeacon/block/PathfindingBlock.java`
- 临时 1.21.11 工作树：`.worktrees/mc12111-fix/src/main/java/cn/quit5700/pathfindingbeacon/registry/ModBlocks.java`
- 临时 1.21.11 工作树：`.worktrees/mc12111-fix/src/main/java/cn/quit5700/pathfindingbeacon/registry/ModItems.java`
- `dist/pathfinding-beacon-1.0.0-mc1.21.11-fabric.jar`
- `dist/pathfinding-beacon-1.0.0-mc1.21.11-fabric-sources.jar`
- `SUPPORTED_VERSIONS.md`
- `docs/PORTING_NOTES.md`

### 具体修复方式

1. 方块注册时先创建 `RegistryKey<Block>`（方块注册键）。
2. 调用 `AbstractBlock.Settings.registryKey(key)`（设置注册键）后再构造 `PathfindingBlock`。
3. 方块物品和两个工具物品注册时先创建 `RegistryKey<Item>`（物品注册键）。
4. 调用 `Item.Settings.registryKey(key)`（设置注册键）后再构造 `BlockItem`、`CancellerItem`、`SequenceReordererItem`。

### 如何验证已经修好

已在 1.21.11 临时工作树验证：

```powershell
.\gradlew.bat clean build -x test --no-daemon --console=plain
```

说明：完整 `build`（构建）在旧 1.21.11 工作树的 `test`（测试）阶段会因为中文路径下测试类加载问题失败；该问题已在当前主线 26.x 构建脚本中修复，但旧 1.21.11 临时工作树未迁入该测试脚本。`compileJava`（编译 Java）、`remapJar`（重映射 JAR）、`remapSourcesJar`（重映射源码 JAR）均已成功。

### 下次遇到类似问题应避免什么

不要以为 `Block id not set` 只影响 26.x。至少 1.21.11 和 26.x 都需要在构造方块/物品前设置注册键；Yarn（Yarn 命名映射）方法名是 `registryKey(...)`，Mojang Official Mappings（Mojang 官方命名映射）方法名是 `setId(...)`。
