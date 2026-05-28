# CosPose - Android

Cosplay 动作参考图库 Android 客户端，支持 AI 智能标签、语义搜索、看板管理等功能。

## 功能特性

- **图片管理** - 导入、浏览、搜索本地图片
- **AI 自动标签** - 基于 CLIP 模型的智能标签识别
- **语义搜索** - 支持自然语言描述搜索图片
- **看板管理** - 创建看板，收藏和整理图片
- **智能文件夹** - 基于标签规则自动分类图片
- **标注工具** - 在图片上添加动作标注和说明
- **图片对比** - 并排对比两张图片
- **重复检测** - 查找相似或重复图片
- **评星系统** - 对图片进行 1-5 星评分
- **CameraX 拍照** - 内置相机功能

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin |
| UI 框架 | Jetpack Compose + Material 3 |
| 架构 | MVVM + Hilt 依赖注入 |
| 数据库 | Room (SQLite) |
| 网络 | Retrofit + OkHttp |
| 图片加载 | Coil |
| 分页 | Paging 3 |
| 相机 | CameraX |
| 数据存储 | DataStore Preferences |

## 项目结构

```
app/src/main/java/com/cospose/gallery/
├── ai/             # AI 分类和标签引擎
├── data/           # 数据层（Room 数据库、API、设置）
│   ├── db/         # 数据库实体和 DAO
│   └── remote/     # 网络 API 接口
├── di/             # Hilt 依赖注入模块
├── scoring/        # 图片评分引擎
├── storage/        # 图片存储管理
├── sync/           # 数据同步管理
├── ui/             # UI 界面
│   ├── auth/       # 登录注册
│   ├── board/      # 看板管理
│   ├── compare/    # 图片对比
│   ├── components/ # 通用组件
│   ├── detail/     # 图片详情
│   ├── duplicates/ # 重复检测
│   ├── home/       # 首页
│   ├── navigation/ # 导航路由
│   ├── preview/    # 图片预览
│   ├── search/     # 搜索
│   ├── settings/   # 设置
│   ├── smartfolder/# 智能文件夹
│   ├── theme/      # 主题样式
│   └── upload/     # 图片上传
└── util/           # 工具类
```

## 环境要求

- Android Studio Hedgehog (2023.1) 或更高版本
- JDK 17
- Android SDK 35
- 最低支持 Android 8.0 (API 26)

## 构建与运行

```bash
# 克隆仓库
git clone https://github.com/wcbsdxg/cospose-android.git
cd cospose-android

# 构建 Debug 版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## 许可证

本项目仅供学习和个人使用。
