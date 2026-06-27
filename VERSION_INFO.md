# 寻路信标版本信息

## 当前可用文件

- 当前构建文件：`build\libs\pathfinding-beacon-1.0.0.jar`
- 分发目录：`dist`
- 已分发文件：
  - `dist\pathfinding-beacon-1.0.0-mc1.20.4-fabric.jar`
  - `dist\pathfinding-beacon-1.0.0-mc1.21.1-fabric.jar`
- 模组名称：寻路信标
- 模组 ID：`pathfinding_beacon`
- 模组版本：`1.0.0`
- 说明：`build\libs` 只保留最后一次构建出的 JAR（Java 归档）；可分发版本已复制到 `dist`，不会被下一次普通构建直接覆盖。

## 当前适配版本

- Minecraft（我的世界）Java 版：`1.21.1`
- Java（爪哇）：`21`
- Fabric Loader（Fabric 模组加载器）：`0.19.3`
- Fabric API（Fabric 应用程序接口）：`0.116.12+1.21.1`
- Fabric Loom（Fabric 构建插件）：`1.7.4`
- Yarn Mappings（Yarn 命名映射）：`1.21.1+build.3`
- Gradle Wrapper（Gradle 包装器）：`8.8`

## 已确认适配记录

| 状态 | Minecraft（我的世界）Java 版 | Java（爪哇） | Fabric Loader（Fabric 模组加载器） | Fabric API（Fabric 应用程序接口） | Fabric Loom（Fabric 构建插件） | Yarn Mappings（Yarn 命名映射） | 文件状态 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 已生成 | `1.20.4` | `17` | `0.15.11` | `0.97.3+1.20.4` | `1.6.12` | `1.20.4+build.3` | `dist\pathfinding-beacon-1.0.0-mc1.20.4-fabric.jar` |
| 已生成 | `1.21.1` | `21` | `0.19.3` | `0.116.12+1.21.1` | `1.7.4` | `1.21.1+build.3` | `dist\pathfinding-beacon-1.0.0-mc1.21.1-fabric.jar` |

你提到一共做过 3 个 Minecraft（我的世界）适配版本；当前项目文件和对话记录里我能确认并实际生成的是上面 2 个。第三个版本号如果是另一个目录或之前没有写入记录，需要补充版本号后再加入这张表。

## 以后做模组的固定要求

以后每次生成或修改 Minecraft（我的世界）模组，都必须在交付说明和项目文件里写清：

- Minecraft（我的世界）版本
- Java（爪哇）版本
- Fabric Loader（Fabric 模组加载器）版本
- Fabric API（Fabric 应用程序接口）版本
- Fabric Loom（Fabric 构建插件）版本
- Yarn Mappings（Yarn 命名映射）版本
- 输出 JAR（Java 归档）路径
