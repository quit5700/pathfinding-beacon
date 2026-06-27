# 寻路信标

适用于《我的世界》Java 版的 Fabric（织物）多版本模组。使用 30 种编号方块建立贯穿世界高度的彩色寻路线。

## 模组信息

- 模组名称：寻路信标
- 模组 ID：`pathfinding_beacon`
- 模组版本：`1.0.0`
- 下载位置：[GitHub Releases（GitHub 发布页）](https://github.com/quit5700/pathfinding-beacon/releases)
- 前置模组：需要 Fabric API（Fabric 应用程序接口），不需要其他前置模组。

## 已发布适配版本

| Minecraft（我的世界）Java 版 | Java（爪哇） | Fabric Loader（Fabric 模组加载器） | Fabric API（Fabric 应用程序接口） | 下载文件 |
| --- | --- | --- | --- | --- |
| `1.20.1` | `17` | `0.15.11` | `0.92.9+1.20.1` | `pathfinding-beacon-1.0.0-mc1.20.1-fabric.jar` |
| `1.20.4` | `17` | `0.15.11` | `0.97.3+1.20.4` | `pathfinding-beacon-1.0.0-mc1.20.4-fabric.jar` |
| `1.21.1` | `21` | `0.19.3` | `0.116.12+1.21.1` | `pathfinding-beacon-1.0.0-mc1.21.1-fabric.jar` |
| `1.21.4` | `21` | `0.19.3` | `0.119.4+1.21.4` | `pathfinding-beacon-1.0.0-mc1.21.4-fabric.jar` |
| `1.21.8` | `21` | `0.19.3` | `0.136.1+1.21.8` | `pathfinding-beacon-1.0.0-mc1.21.8-fabric.jar` |
| `1.21.10` | `21` | `0.19.3` | `0.138.4+1.21.10` | `pathfinding-beacon-1.0.0-mc1.21.10-fabric.jar` |
| `1.21.11` | `21` | `0.19.3` | `0.141.4+1.21.11` | `pathfinding-beacon-1.0.0-mc1.21.11-fabric.jar` |

`26.1.2` 和 `26.2` 当前有 Fabric API（Fabric 应用程序接口）包，但 Fabric Maven（Fabric 依赖仓库）暂未提供对应 Yarn Mappings（Yarn 命名映射），所以本项目暂不能按正常 Yarn/Fabric 流程构建这两个版本。

## 安装

1. 按上表选择与你游戏版本一致的 JAR（Java 归档）文件。
2. 安装对应版本的 Fabric Loader（Fabric 模组加载器）。
3. 安装对应版本的 Fabric API（Fabric 应用程序接口）。
4. 把下载的 `pathfinding-beacon-...-fabric.jar` 放入游戏的 `mods`（模组）文件夹。
5. 启动游戏后，创造模式物品栏里会出现“寻路”标签页。

## 主要功能

- 30 种编号寻路方块，名称为 `1号寻路方块` 到 `30号寻路方块`。
- 同号有效方块按放置顺序连接；只有一个方块时没有光柱。
- 光柱按 Bresenham（布雷森汉姆）直线算法生成一格宽路线，并从维度最低处显示到最高处。
- 同一维度内，同一颜色只能由首位使用者占用；其他玩家放置同号方块只发光，不参与线路。
- 生存模式只能用“寻路方块取消器”拆除，创造模式可直接破坏并掉落自身。
- “ID顺序重排器”可右键两个同号有效方块，连接断开的线路或调整同线路顺序。
- `/idcancel <1-30>` 删除当前维度对应号码的全部有效/无效方块和光柱，不产生掉落物。

## 配方

- 1 个圆石无序合成 64 个 `1号寻路方块`。
- 前一号码寻路方块 + 1 个圆石，无序合成 64 个后一号码寻路方块。
- 圆石按镐的 T 字形摆放，合成“寻路方块取消器”。
- 圆石按斧子的形状摆放，合成“ID顺序重排器”，左右镜像均可。
- 背包中拥有任意原版或其他模组的镐后，自动解锁全部配方。

## 构建

```powershell
.\gradlew.bat clean build
```

当前仓库源码配置保留在最新已验证版本 `1.21.11`。历史版本 JAR（Java 归档）文件保存在 `dist`，并上传到 GitHub Releases（GitHub 发布页）。
