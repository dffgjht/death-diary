# 贡献指南

感谢你对存证纪项目的关注！欢迎任何形式的贡献。

## 快速开始

### 1. Fork 并克隆

```bash
# Fork 后克隆你的仓库
git clone https://github.com/YOUR_USERNAME/death-diary.git
cd death-diary

# 添加上游仓库
git remote add upstream https://github.com/dffgjht/death-diary.git
```

### 2. 创建分支

```bash
git checkout -b feature/your-feature-name
```

**分支命名规范：**

- `feature/` — 新功能
- `fix/` — Bug 修复
- `docs/` — 文档更新
- `refactor/` — 代码重构
- `test/` — 测试相关

### 3. 开发与提交

```bash
# 开发完成后
git add .
git commit -m "feat: 添加 XX 功能"
```

**Commit 消息格式（Conventional Commits）：**

- `feat:` 新功能
- `fix:` Bug 修复
- `docs:` 文档变更
- `style:` 代码格式调整（不影响功能）
- `refactor:` 代码重构
- `test:` 测试相关
- `chore:` 构建/工具链变更

### 4. 提交 Pull Request

1. 推送到你的 Fork
   ```bash
   git push origin feature/your-feature-name
   ```
2. 在 GitHub 上创建 Pull Request
3. 在 PR 描述中说明：
   - 改了什么
   - 为什么改
   - 如何测试

## 代码规范

- 遵循 [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 空格缩进
- 公共 API 必须添加 KDoc 注释
- 单个函数不超过 80 行
- 文件末尾保留一个空行

## 测试要求

- 新功能必须包含单元测试
- Bug 修复应添加回归测试
- 运行测试：
  ```bash
  ./gradlew test
  ```

## 问题反馈

- 使用 [GitHub Issues](https://github.com/dffgjht/death-diary/issues) 提交 Bug 或建议
- 提交前先搜索是否已有相同 Issue
- 提供复现步骤和环境信息

---

感谢你的贡献！ 🎉
