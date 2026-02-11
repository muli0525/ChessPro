# 贡献指南

感谢您对中国象棋 Pro 项目的兴趣！我们欢迎各种形式的贡献。

## 📝 贡献方式

### 🐛 报告问题
- 在 [Issue Tracker](https://github.com/yourusername/ChineseChessPro/issues) 中报告Bug
- 描述问题现象和复现步骤
- 提供设备型号、Android版本等信息

### 💡 功能建议
- 提出新功能的想法
- 解释功能的使用场景
- 尽可能提供参考实现或设计草图

### 🔧 提交代码

#### 1. Fork 项目
点击 GitHub 页面右上角的 Fork 按钮

#### 2. 克隆你的 Fork
```bash
git clone https://github.com/YOUR_USERNAME/ChineseChessPro.git
cd ChineseChessPro
```

#### 3. 创建特性分支
```bash
git checkout -b feature/your-feature-name
```

#### 4. 开发你的功能
- 遵循项目的代码规范
- 添加必要的测试
- 更新文档

#### 5. 提交更改
```bash
git add .
git commit -m "Add: 你的功能描述"
```

#### 6. 推送到你的 Fork
```bash
git push origin feature/your-feature-name
```

#### 7. 创建 Pull Request
- 访问你的 Fork 页面
- 点击 "Compare & pull request" 按钮
- 填写 PR 描述
- 提交 PR

## 📐 代码规范

### Kotlin 风格指南
- 使用 **Kotlin Coding Conventions**
- 命名规范：
  - 类/接口：`PascalCase`
  - 函数/变量：`camelCase`
  - 常量：`UPPER_SNAKE_CASE`
- 遵循 Material Design 3 规范

### 提交信息格式
```
类型: 简短描述

详细描述（可选）

解决的问题（可选）
- Issue #123
```

**类型前缀**：
- `Add`: 新功能
- `Fix`: Bug修复
- `Update`: 更新功能
- `Refactor`: 重构代码
- `Docs`: 文档更新
- `Test`: 测试相关
- `Chore`: 构建/工具更新

### 代码审查要点
- ✅ 代码是否可读
- ✅ 是否有适当的注释
- ✅ 是否有测试覆盖
- ✅ 是否遵循项目架构
- ✅ 性能影响
- ✅ 安全性考虑

## 🏗️ 项目架构

请遵循项目的架构模式：
- `core/`: 核心业务逻辑
- `ui/`: 用户界面
- `data/`: 数据层（如有）
- `di/`: 依赖注入（如有）

## 🧪 测试要求

- 新功能需要添加单元测试
- Bug修复需要添加回归测试
- 保持测试覆盖率

## 📖 文档

- 更新 README.md（如果需要）
- 为复杂函数添加注释
- 更新 API 文档（如果涉及）

## 💬 沟通

- 使用 GitHub Issues 讨论
- 保持友好的交流氛围
- 尊重他人的时间和精力

---

再次感谢您的贡献！🎉
