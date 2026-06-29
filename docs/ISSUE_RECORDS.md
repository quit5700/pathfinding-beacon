# 问题与修复记录

## 2026-06-29：全版本取消命令改为 `/pfcancel`

- 问题类型：命令与提示词一致性问题、发布包同步问题。
- 问题现象：1.21.11 功能正常后，源码与旧发布包仍有 `/idcancel` 或登录提示显示旧命令。
- 根本原因：历史版本 JAR（Java 归档）文件是分版本产物，修改 1.21.11 测试包后没有同步到全部 `dist`（分发目录）正式包。
- 修改文件：`README.md`、`docs/superpowers/*`、`src/main/java/.../IdCancelCommand.java`、`src/main/java/.../PlayerEvents.java`、`dist/*.jar`、`dist/*-sources.jar`。
- 修复方式：源码命令注册改为 `pfcancel`，登录提示改为 `寻路器取消指令 /pfcancel <1-30>`；所有正式 JAR 和 sources JAR 逐项解压，替换字节码常量、源码文本和资源文件。
- 验证方式：执行 `.\gradlew.bat clean build -x test --no-daemon --console=plain` 通过；逐项解压扫描 `dist` 中全部正式 JAR，确认没有 `idcancel` / `IDCANCEL` 残留。
- 下次避免：功能确认后不要只替换单个测试版本，必须同步扫描并更新全部已发布版本。

## 2026-06-29：Windows 替换 JAR 临时文件失败

- 问题类型：Gradle / 构建产物处理问题、Windows 文件替换问题。
- 问题现象：批量更新 `dist` 包时出现 `PermissionError: [WinError 5] 拒绝访问`。
- 根本原因：第一版脚本直接用临时 ZIP 覆盖原 JAR，且未跳过已存在资源项，导致 ZIP 重复条目警告并在 Windows 上触发替换失败。
- 修改文件：仅影响本次批量打包脚本运行方式，没有保留脚本文件。
- 修复方式：改为写入 `.tmp2` 临时文件，关闭 ZIP 后先删除原文件再重命名；写包时跳过旧资源路径，再写入新的工具图标和模型资源，避免重复条目。
- 验证方式：修正版脚本成功更新全部正式 JAR 与 sources JAR；后续解压扫描通过。
- 下次避免：Windows 上批量替换 JAR 时，不要边读边覆盖；必须先写独立临时文件，并避免 ZIP 内同名资源重复。
