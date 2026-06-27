# 寻路信标

适用于《我的世界》Java 版 Fabric（织物）的多版本模组。使用 30 种编号方块建立贯穿世界高度的彩色寻路线。

## 模组信息

- 模组名称：寻路信标
- 模组 ID：`pathfinding_beacon`
- 模组版本：`1.0.0`
- 当前已发布版本见 [SUPPORTED_VERSIONS.md](SUPPORTED_VERSIONS.md)。
- 下载文件见 [Releases（发布页）](https://github.com/quit5700/pathfinding-beacon/releases)。

## 已发布适配版本

| Minecraft（我的世界）Java 版 | Java（爪哇） | Fabric Loader（Fabric 模组加载器） | Fabric API（Fabric 应用程序接口） | 下载文件 |
| --- | --- | --- | --- | --- |
| `1.20.4` | `17` | `0.15.11` | `0.97.3+1.20.4` | `pathfinding-beacon-1.0.0-mc1.20.4-fabric.jar` |
| `1.21.1` | `21` | `0.19.3` | `0.116.12+1.21.1` | `pathfinding-beacon-1.0.0-mc1.21.1-fabric.jar` |

## 必须安装

- 客户端和服务器都必须安装本模组。
- 客户端和服务器都必须安装 Fabric API（Fabric 应用程序接口）。
- 不需要其他前置模组。

## 安装

1. 按上方表格选择与你游戏版本一致的 JAR（Java 归档）。
2. 安装对应版本的 Fabric Loader（Fabric 模组加载器）。
3. 安装对应版本的 Fabric API（Fabric 应用程序接口）。
4. 把下载的 `pathfinding-beacon-...-fabric.jar` 放入游戏的 `mods`（模组）文件夹。
5. 启动游戏。“寻路”创造模式标签页中包含全部方块与工具。

## 配方

- 1 个圆石无序合成 64 个 1 号寻路方块。
- 1 个前一号码寻路方块加 1 个圆石，无序合成 64 个后一号码方块。
- 5 个圆石按镐的 T 字形合成“寻路方块取消器”。
- 5 个圆石按斧形合成“ID顺序重排器”，左右镜像均可。
- 背包中拥有任意原版或其他模组的镐后，自动解锁全部配方。

## 使用

- 同号有效方块按放置顺序连接；单个方块没有光柱。
- 光柱路线为俯视一格宽直线，显示范围从维度最低处到最高处。
- 每种号码在每个维度由首位放置者占用，其他玩家放置的同号方块只发光。
- 生存模式用“寻路方块取消器”连续敲击 3 次或按住左键完成拆除。
- 右键使用“ID顺序重排器”，依次选择两个同号有效方块进行断线连接或顺序重排。
- `/idcancel <1-30>` 删除当前维度对应号码的全部有效/无效方块和光柱，不产生掉落物。指令全小写，所有玩家可用。

## 构建

```powershell
.\gradlew.bat clean build
```

正式模组文件输出到 `build\libs\pathfinding-beacon-1.0.0.jar`。
