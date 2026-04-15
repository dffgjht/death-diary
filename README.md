# 死亡日记 (Death Diary) - 安卓应用

一个安全、私密的数字遗产管理安卓应用，包含社区留言板功能。

## 快速开始

### 使用 Android Studio 构建（推荐）

1. 打开 Android Studio
2. File → Open → 选择项目文件夹
3. 等待 Gradle 同步完成（首次 5-10 分钟）
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. 在 `app/build/outputs/apk/debug/app-debug.apk` 获取 APK

详细步骤：[ANDROID_STUDIO_BUILD_GUIDE.md](ANDROID_STUDIO_BUILD_GUIDE.md)

### 使用 GitHub Actions 构建

1. 将项目推送到 GitHub
2. 进入 Actions 页面
3. 点击 "Run workflow"
4. 等待 5-10 分钟后下载 APK

详细步骤：[GITHUB_ACTIONS_GUIDE.md](GITHUB_ACTIONS_GUIDE.md)

## 功能特性

- 🔐 AES-256 加密存储
- 🔐 生物识别认证（指纹/面容）
- 📔 日记系统（带心情标签）
- 🔑 密码保险箱
- 📜 数字遗嘱
- 📸 回忆相册
- 💬 **社区留言板**（新增）
  - 发帖、评论、点赞
  - 分类浏览（推荐、关注、热门）
  - 标签系统
- ⚙️ 设置中心
- 📱 完全离线

## 技术栈

- Kotlin 1.9.20
- Jetpack Compose
- Room Database
- Material Design 3
- Android Keystore (AES-256 加密)

## 系统要求

- Android 7.0 (API 24) 或更高版本
- 推荐使用 Android 10+ 以获得最佳体验

## 文档

- [README.md](README.md) - 项目介绍（本文件）
- [ANDROID_STUDIO_BUILD_GUIDE.md](ANDROID_STUDIO_BUILD_GUIDE.md) - Android Studio 构建指南
- [GITHUB_ACTIONS_GUIDE.md](GITHUB_ACTIONS_GUIDE.md) - GitHub Actions 构建指南
- [FINAL_SUMMARY.md](FINAL_SUMMARY.md) - 项目完成总结

## 项目结构

```
death-diary/
├── app/
│   ├── src/main/
│   │   ├── java/com/deathdiary/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── Database.kt
│   │   │   │   ├── CommunityDao.kt
│   │   │   │   └── entities/
│   │   │   ├── security/
│   │   │   └── ui/screens/
│   │   └── res/
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .github/workflows/
│   └── build-apk.yml
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## 安全特性

- AES-256-GCM 加密
- Android Keystore 硬件级密钥存储
- 生物识别认证
- 主密码保护
- 完全离线，无数据上传

## 许可证

MIT License

## 版本

1.1.0 (新增社区留言板功能)

---

**开发者**: OpenClaw AI Assistant
**项目位置**: `/vol1/@apphome/trim.openclaw/data/workspace/death-diary`
**最后更新**: 2025-01-15
