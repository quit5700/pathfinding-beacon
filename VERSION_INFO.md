# 寻路信标版本信息

## 当前项目约定

- 模组名称：寻路信标
- 模组 ID：`pathfinding_beacon`
- 模组版本：`1.0.0`
- 仓库：`quit5700/pathfinding-beacon`
- 下载页：[GitHub Releases（GitHub 发行版）](https://github.com/quit5700/pathfinding-beacon/releases)
- 每次新增 Minecraft（我的世界）适配版本后，都需要同时生成本地 JAR（Java 归档）文件并上传到 Releases（发行版）。

## 当前源码构建配置

- Minecraft（我的世界）Java 版：`26.2`
- Java（爪哇）：`25`
- Fabric Loader（Fabric 模组加载器）：`0.19.3`
- Fabric API（Fabric 应用程序接口）：`0.153.0+26.2`
- Fabric Loom（Fabric 构建插件）：`1.15-SNAPSHOT`（本机解析为 `1.15.5`）
- Yarn Mappings（Yarn 命名映射）：不使用；26.x 使用 Mojang Official Mappings（Mojang 官方命名映射）
- Gradle Wrapper（Gradle 包装器）：`9.2.1`

## 26.x 构建说明

26.x 版本使用 Mojang Official Mappings（Mojang 官方命名映射）。为了避开 Fabric API（Fabric 应用程序接口）总包中 datagen（数据生成）模块在 26.2 下的 Loom 转换问题，构建脚本只声明本模组实际使用的 Fabric API 子模块；玩家安装时仍只需要安装对应完整 Fabric API。

26.2 使用的 Fabric API 子模块：

- `fabric-api-base`：`2.0.4+ece063239e`
- `fabric-command-api-v2`：`3.1.0+00cb03469e`
- `fabric-creative-tab-api-v1`：`5.0.14+d871b99e9e`
- `fabric-entity-events-v1`：`5.0.5+06488ac19e`
- `fabric-lifecycle-events-v1`：`4.1.3+4575b05f9e`
- `fabric-networking-api-v1`：`6.3.3+72073ef09e`
- `fabric-rendering-v1`：`25.2.0+2b0d8a229e`

## 已生成分发文件

- `dist\pathfinding-beacon-1.0.0-mc1.20.1-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.20.1-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.20.4-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.20.4-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.1-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.1-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.4-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.4-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.8-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.8-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.10-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.10-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.11-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc1.21.11-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc26.1.2-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc26.1.2-fabric-sources.jar`
- `dist\pathfinding-beacon-1.0.0-mc26.2-fabric.jar`
- `dist\pathfinding-beacon-1.0.0-mc26.2-fabric-sources.jar`

## 使用命令

编译命令：

```powershell
.\gradlew.bat clean build
```

运行方式：

1. 安装对应 Minecraft（我的世界）版本的 Fabric Loader（Fabric 模组加载器）。
2. 安装对应 Minecraft（我的世界）版本的 Fabric API（Fabric 应用程序接口）。
3. 将对应版本的 `pathfinding-beacon-...-fabric.jar` 放入 `mods`（模组）文件夹。

常见错误及修复：

- 游戏提示缺少 Fabric API：安装与 Minecraft（我的世界）版本一致的 Fabric API（Fabric 应用程序接口）。
- 游戏提示 Java 版本过低：26.x 版本需要 Java（爪哇）25。
- JAR 放入后没有生效：确认文件名中的 MC 版本与游戏版本一致，例如 26.2 使用 `mc26.2` 文件。
- 构建测试在中文路径下找不到测试类：构建脚本会把测试输出同步到 ASCII（纯英文）临时目录再运行测试。
