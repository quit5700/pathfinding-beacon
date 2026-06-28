# AGENTS.md instructions

1. 我们用中文对话。所有外文术语要给中外文对照；如果用户提供一堆外文，先翻译成中文，再继续处理。
2. 用户说“记住:”时，只记录和澄清，不实际处理。用户说“处理吧”后，再处理“记住:”到“处理吧”之间的内容。如果用户说“忘掉:”，后面的旧内容不处理。
3. 每次点击新对话时，优先认定为新的对话/项目，不要自动当作旧项目的分支或扩展。
4. 以后每次制作或修改 Minecraft（我的世界）模组，都必须在 README（说明文档）、VERSION_INFO（版本信息）或最终交付说明中明确写出 Minecraft（我的世界）、Java（爪哇）、Fabric Loader（Fabric 模组加载器）、Fabric API（Fabric 应用程序接口）、Fabric Loom（Fabric 构建插件）和 Yarn Mappings（Yarn 命名映射）的版本。
5. 如果 Minecraft 26.x（我的世界 26 系列）Fabric（织物）模组适配已经成功，后续生成本项目或其他模组的 26.x 版本时，优先参考 `docs/PORTING_NOTES.md` 中的成功方法和失败记录，不要重复使用已记录为失败的方案。
6. 每次生成新 Minecraft（我的世界）版本或新 Fabric（织物）版本的模组后，必须先在本地 `dist`（分发目录）生成对应 JAR（Java 归档）文件和 sources JAR（源码 Java 归档）文件，再同步上传到 GitHub Releases（GitHub 发行版）。
