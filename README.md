# 中国象棋 Pro - Android 象棋辅助应用

## 📱 项目简介

**中国象棋 Pro** 是一款功能强大的 Android 象棋辅助应用，提供棋盘识别、AI对战、摆棋练习等多种功能。

## ✨ 核心功能

### 🎮 多种游戏模式
- **人机对战**：与AI进行象棋对弈
- **双人对战**：两人在同一设备上对弈
- **摆棋模式**：自由摆放棋子，研究棋局
- **相机识别**：拍摄实体棋盘，自动识别局面并给出走法建议

### 🤖 智能AI引擎
- 使用Alpha-Beta剪枝搜索算法
- 可调节难度等级（简单/入门/中等/困难/大师）
- 实时计算最优走法

### 📷 棋盘识别功能
- 基于OpenCV的图像处理
- 自动检测和校正棋盘
- 棋子自动识别和定位
- 实时走法建议提示

### 🎨 现代化UI设计
- Jetpack Compose 声明式UI
- Material Design 3 设计规范
- 深色/浅色主题支持
- 流畅的动画效果

## 🏗️ 技术架构

### 技术栈
- **语言**：Kotlin
- **UI框架**：Jetpack Compose
- **架构模式**：MVVM + Clean Architecture
- **相机**：CameraX
- **图像处理**：OpenCV Android
- **并发**：Kotlin Coroutines + Flow

### 项目结构
```
com.chesspro.app/
├── core/
│   ├── chess/           # 象棋核心逻辑
│   │   ├── ChessBoard.kt    # 棋盘管理
│   │   ├── ChessPiece.kt    # 棋子模型
│   │   ├── ChessAI.kt       # AI引擎
│   │   ├── Move.kt          # 走法类
│   │   ├── Position.kt      # 位置类
│   │   ├── PieceType.kt     # 棋子类型枚举
│   │   └── PieceColor.kt    # 棋子颜色枚举
│   │
│   └── recognition/     # 棋盘识别
│       └── ChessBoardRecognition.kt  # 识别算法
│
├── ui/
│   ├── screens/         # 界面屏幕
│   │   └── ChessMainScreen.kt  # 主界面
│   │
│   ├── components/      # UI组件
│   │   ├── ChessBoardView.kt   # 棋盘绘制
│   │   ├── CameraPreview.kt    # 相机预览
│   │   └── MoveSuggestionView.kt # 走法提示
│   │
│   ├── theme/           # 主题样式
│   │   ├── Theme.kt
│   │   └── Typography.kt
│   │
│   └── ChessViewModel.kt # 视图模型
│
├── MainActivity.kt      # 主活动
└── ChineseChessProApp.kt # 应用类
```

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK 34+
- Gradle 8.2+

### 构建步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd ChineseChessPro
   ```

2. **打开项目**
   - 使用 Android Studio 打开项目根目录
   - 等待 Gradle 同步完成

3. **运行项目**
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮运行

### 构建 APK
```bash
./gradlew assembleDebug
# 或
./gradlew assembleRelease
```

## 🎯 核心功能详解

### AI对战模式
1. 选择"人机对战"Tab
2. 点击设置图标调节AI难度
3. 红方先手，点击或拖拽棋子走棋
4. AI会自动计算并执行走法

### 相机识别功能
1. 选择"相机识别"Tab
2. 授权相机权限
3. 将实体棋盘放入取景框
4. 点击拍摄按钮识别棋盘
5. 应用会显示最优走法建议

### 摆棋模式
1. 选择"摆棋模式"Tab
2. 点击任意位置添加棋子
3. 长按或拖拽移动棋子
4. 可用于研究残局或摆谱

## 🔧 自定义配置

### AI难度调节
在设置中可以调整AI搜索深度：
- 简单：搜索深度 1-2层
- 中等：搜索深度 3层
- 大师：搜索深度 5层+

### 界面设置
- 显示坐标：显示棋盘行列坐标
- 深色模式：切换深色/浅色主题

## 📚 象棋规则实现

应用完整实现了中国象棋的所有规则：

- **车**：直线移动，无限制
- **马**：走日字格，蹩马腿规则
- **象**：走田字格，不过河，塞象眼规则
- **士**：斜走一格，只在九宫格内
- **将/帅**：直线移动一格，九宫格内，对面将规则
- **炮**：直线移动，吃子需炮架
- **兵/卒**：过河前只能向前，过河后可平移

## 🔮 未来规划

### 短期计划
- [ ] 优化棋盘识别算法
- [ ] 添加悔棋功能
- [ ] 实现棋局复盘
- [ ] 添加开局库

### 长期计划
- [ ] 引入神经网络AI
- [ ] 添加在线对战
- [ ] 棋谱管理功能
- [ ] 多平台支持

## 📄 许可证

本项目采用 MIT 许可证。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系

如有问题或建议，请联系开发者。

---

**享受象棋的魅力！🎲**
