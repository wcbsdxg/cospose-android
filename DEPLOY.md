# CosPose Android 部署指南

## 目录

1. [开发环境](#1-开发环境)
2. [项目配置](#2-项目配置)
3. [构建与运行](#3-构建与运行)
4. [服务器连接](#4-服务器连接)
5. [功能说明](#5-功能说明)
6. [Eagle 风格功能](#6-eagle-风格功能)
7. [发布 APK](#7-发布-apk)
8. [常见问题](#8-常见问题)

---

## 1. 开发环境

### 1.1 必需工具

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| Android Studio | Ladybug (2024.2+) | 官方 IDE |
| JDK | 17 | Android Studio 自带 |
| Android SDK | API 35 (Android 15) | compileSdk |
| Kotlin | 2.1.0 | 项目使用版本 |

### 1.2 安装步骤

1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 打开 Android Studio → SDK Manager → 安装：
   - Android 15 (API 35)
   - Android SDK Build-Tools 35.0.0
   - Android SDK Platform-Tools

---

## 2. 项目配置

### 2.1 打开项目

1. 启动 Android Studio
2. 选择 `File → Open`
3. 导航到 `cospose-android` 目录并打开
4. 等待 Gradle 同步完成（首次可能需要 5-10 分钟）

### 2.2 服务器地址配置

编辑 [AppModule.kt](app/src/main/java/com/cospose/gallery/di/AppModule.kt) 中的 `BASE_URL`：

```kotlin
// 本地开发（模拟器访问宿主机）
private const val BASE_URL = "http://10.0.2.2:3000/"

// 真机测试（同一 WiFi 网络）
// private const val BASE_URL = "http://192.168.1.100:3000/"

// 生产环境
// private const val BASE_URL = "https://your-domain.com/"
```

**网络地址说明：**

| 场景 | 地址 | 说明 |
|------|------|------|
| Android 模拟器 | `http://10.0.2.2:3000/` | 模拟器访问宿主机 localhost |
| 真机 USB 调试 | `http://10.0.2.2:3000/` | 需要端口转发 |
| 真机 WiFi | `http://你的电脑IP:3000/` | 确保同一网络 |
| 生产服务器 | `https://your-domain.com/` | 需要 HTTPS |

### 2.3 网络权限

已配置在 [AndroidManifest.xml](app/src/main/AndroidManifest.xml)：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
```

---

## 3. 构建与运行

### 3.1 连接设备

**使用模拟器：**
1. Android Studio → Tools → Device Manager
2. 创建虚拟设备（推荐 Pixel 7, API 35）
3. 启动模拟器

**使用真机：**
1. 手机开启 `开发者选项` → `USB 调试`
2. USB 连接电脑
3. 手机上确认允许调试

### 3.2 运行项目

1. 在 Android Studio 顶部选择目标设备
2. 点击绿色三角形 `Run` 按钮（或 Shift+F10）
3. 等待编译安装（首次约 2-3 分钟）

### 3.3 命令行构建

```bash
# 进入项目目录
cd cospose-android

# Debug 构建
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug

# 运行测试
./gradlew test
```

**Windows 用户：** 使用 `gradlew.bat` 替代 `./gradlew`

---

## 4. 服务器连接

### 4.1 启动服务器

确保 CosPose Web 服务器已运行：

```bash
cd ../cospose-gallery

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 4.2 启动 CLIP 微服务

AI 功能需要 CLIP 微服务：

```bash
cd ../cospose-gallery/clip-service

# 安装 Python 依赖
pip install fastapi uvicorn open-clip-torch torch Pillow

# 启动服务
python main.py
```

服务地址：`http://localhost:8000`

### 4.3 连接测试

在 App 的 `设置` 页面可以查看：
- 服务器连接状态
- 同步设置
- 服务器地址配置

---

## 5. 功能说明

### 5.1 离线模式

App 支持完全离线使用：

| 功能 | 离线 | 在线 |
|------|------|------|
| 浏览图片 | ✓ | ✓ |
| 搜索（关键词） | ✓ | ✓ |
| 搜索（语义/以图搜图） | ✗ | ✓ |
| 上传图片 | ✓ | ✓ |
| AI 自动标签 | ✗ | ✓ |
| 评分/点赞/评论 | ✓ | ✓ |
| 收藏板 | ✓ | ✓ |
| 智能文件夹 | ✓ | ✓ |
| 重复检测 | ✓ | ✓ |
| 数据同步 | ✗ | ✓ |

### 5.2 AI 功能

所有 AI 功能通过服务器处理：

- **图片分类**：上传图片 → 服务器 CLIP 分类 → 返回标签
- **语义搜索**：文本 → 服务器向量化 → 返回相似图片
- **以图搜图**：图片 → 服务器向量化 → 返回相似图片

### 5.3 数据同步

同步策略：
- 本地操作标记为 `PENDING`
- 连接服务器后自动同步
- 冲突时服务器数据优先

---

## 6. Eagle 风格功能

参考 Eagle 图片管理软件，实现了以下高级功能：

### 6.1 Phase 1 — 核心体验

#### 高级筛选器
- 按评分范围、尺寸、比例、日期、文件类型筛选
- 首页顶部 FilterChip 快速访问
- 筛选面板支持多条件组合

#### 全屏预览滑动
- 点击图片进入全屏模式
- 左右滑动切换图片
- 双击点赞（带爱心动画）
- 捏合缩放查看细节

#### 批量操作
- 长按图片进入多选模式
- 底部操作栏：全选、批量打标签、批量评分、批量删除
- 批量移到收藏板

#### 增强标签系统
- 标签按类别分组（动作、角色、动漫等）
- 每个类别显示不同颜色标识
- 标签树形选择器
- 支持手动标签和 AI 标签分离显示

### 6.2 Phase 2 — 发现与组织

#### 智能文件夹
- 用户定义规则（评分、标签、日期等）
- 自动归类符合条件的图片
- 规则使用 JSON 存储，灵活扩展

#### 颜色筛选
- 自动提取图片主色调
- 按颜色筛选图片
- 12 种预设颜色快速选择

#### 图片元数据
- 查看 EXIF 信息（相机型号、光圈、ISO 等）
- 文件大小、分辨率、格式
- 同步状态显示

#### 图片注释
- 在图片上添加文字注释
- 标记点显示注释位置
- 注释列表管理

### 6.3 Phase 3 — 高级功能

#### 重复图片检测
- 基于 CLIP embedding 余弦相似度
- 可调节相似度阈值（70%-99%）
- 分组显示重复图片
- 支持删除重复项

#### 图片对比
- 两张图片并排显示
- 同步缩放和拖拽
- 显示文件信息对比

#### 导出/分享
- 分享图片到其他应用
- 保存到系统相册
- 使用 FileProvider 安全共享

#### 快捷手势
- 双击点赞（带爱心动画）
- 长按进入多选
- 滑动切换图片
- 捏合缩放

#### 设置页面
- 主题模式（跟随系统/浅色/深色）
- 动态颜色开关（Material You）
- 网格列数调整（2/3/4 列）
- 服务器同步设置
- 压缩上传开关
- 所有设置持久化到 DataStore

---

## 7. 发布 APK

### 7.1 生成签名密钥

```bash
keytool -genkey -v -keystore cospose-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias cospose
```

### 7.2 配置签名

在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../cospose-release.jks")
            storePassword = "your-store-password"
            keyAlias = "cospose"
            keyPassword = "your-key-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}
```

### 7.3 构建 Release APK

```bash
./gradlew assembleRelease
```

APK 输出位置：`app/build/outputs/apk/release/app-release.apk`

### 7.4 构建 App Bundle (AAB)

用于 Google Play 发布：

```bash
./gradlew bundleRelease
```

AAB 输出位置：`app/build/outputs/bundle/release/app-release.aab`

---

## 8. 常见问题

### Q1: Gradle 同步失败

**症状：** `Could not resolve...` 错误

**解决：**
1. 检查网络连接
2. File → Invalidate Caches → Restart
3. 删除 `.gradle` 目录后重新同步

### Q2: 无法连接服务器

**症状：** 网络请求超时

**解决：**
1. 确认服务器已启动（`http://localhost:3000`）
2. 模拟器使用 `10.0.2.2` 而非 `localhost`
3. 真机确保同一 WiFi 网络
4. 检查防火墙设置

### Q3: AI 功能不工作

**症状：** 分类/搜索返回空结果

**解决：**
1. 确认 CLIP 微服务已启动（`http://localhost:8000/health`）
2. 检查服务器日志是否有错误
3. 首次启动需下载模型，等待几分钟

### Q4: 编译内存不足

**症状：** `OutOfMemoryError`

**解决：**
1. 编辑 `gradle.properties`：
   ```properties
   org.gradle.jvmargs=-Xmx4096m
   ```
2. 重启 Android Studio

### Q5: 打开相机崩溃

**症状：** 点击拍照闪退

**解决：**
1. 确保 AndroidManifest.xml 中有 CAMERA 权限
2. 真机需在设置中手动授予相机权限
3. 模拟器需配置摄像头

### Q6: 图片加载缓慢

**症状：** 瀑布流滚动卡顿

**解决：**
1. 检查网络连接
2. 减少 Coil 内存缓存：
   ```kotlin
   ImageLoader.Builder(context)
       .memoryCachePolicy(CachePolicy.DISABLED)
       .build()
   ```

### Q7: 数据库迁移失败

**症状：** 升级后 App 崩溃

**解决：**
1. 检查 `AppDatabase.kt` 中的版本号
2. 确保 `MIGRATION_1_2` 已正确配置
3. 如无法修复，清除应用数据重新开始

### Q8: 重复检测结果不准确

**症状：** 相似图片未检测到

**解决：**
1. 调低相似度阈值（设置 → 重复检测 → 阈值滑块）
2. 确保图片已上传并生成 embedding
3. 重新点击"刷新"按钮

---

## 项目结构

```
cospose-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/cospose/gallery/
│   │   │   ├── ai/              # AI 服务调用
│   │   │   ├── data/
│   │   │   │   ├── db/          # Room 数据库
│   │   │   │   │   ├── dao/     # 数据访问对象
│   │   │   │   │   ├── entity/  # 实体类
│   │   │   │   │   └── AppDatabase.kt
│   │   │   │   ├── remote/      # 网络 API
│   │   │   │   └── SettingsRepository.kt  # 设置持久化
│   │   │   ├── di/              # 依赖注入
│   │   │   ├── scoring/         # 推荐算法
│   │   │   ├── storage/         # 本地存储
│   │   │   ├── sync/            # 数据同步
│   │   │   ├── util/            # 工具类
│   │   │   │   ├── ColorExtractor.kt    # 主色调提取
│   │   │   │   ├── EmbeddingUtils.kt    # 向量相似度计算
│   │   │   │   ├── ExifReader.kt        # EXIF 读取
│   │   │   │   └── ShareHelper.kt       # 分享导出
│   │   │   └── ui/
│   │   │       ├── auth/        # 登录注册
│   │   │       ├── board/       # 收藏板
│   │   │       ├── compare/     # 图片对比 (新增)
│   │   │       ├── components/  # 通用组件
│   │   │       │   ├── AnnotationDialog.kt    # 注释弹窗
│   │   │       │   ├── AnnotationOverlay.kt   # 注释覆盖层
│   │   │       │   ├── BatchActionBar.kt       # 批量操作栏
│   │   │       │   ├── BatchBoardSheet.kt      # 批量移到收藏板
│   │   │       │   ├── BatchTagDialog.kt       # 批量打标签
│   │   │       │   ├── ColorPickerSheet.kt     # 颜色选择器
│   │   │       │   ├── FilterBottomSheet.kt    # 筛选面板
│   │   │       │   ├── HeartAnimation.kt       # 爱心动画
│   │   │       │   ├── ImageCard.kt            # 图片卡片
│   │   │       │   ├── MetadataSheet.kt        # 元数据弹窗
│   │   │       │   ├── SelectionState.kt       # 多选状态
│   │   │       │   ├── StarRating.kt           # 评分星星
│   │   │       │   ├── TagChip.kt              # 标签芯片
│   │   │       │   ├── TagGroupChip.kt         # 分组标签
│   │   │       │   └── TagTreeSheet.kt         # 标签树选择器
│   │   │       ├── detail/      # 图片详情
│   │   │       ├── duplicates/  # 重复检测 (新增)
│   │   │       ├── home/        # 首页
│   │   │       ├── navigation/  # 导航路由
│   │   │       ├── preview/     # 全屏预览 (新增)
│   │   │       ├── search/      # 搜索
│   │   │       ├── settings/    # 设置
│   │   │       ├── smartfolder/ # 智能文件夹 (新增)
│   │   │       ├── theme/       # 主题
│   │   │       └── upload/      # 上传
│   │   ├── AndroidManifest.xml
│   │   └── res/                 # 资源文件
│   └── build.gradle.kts         # 模块配置
├── build.gradle.kts              # 项目配置
├── settings.gradle.kts           # 设置
└── DEPLOY.md                     # 本文档
```

---

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| UI | Jetpack Compose | BOM 2024.12.01 |
| 数据库 | Room (SQLite) | 2.6.1 |
| 网络 | Retrofit + OkHttp | 2.11.0 |
| 图片 | Coil | 2.7.0 |
| DI | Hilt | 2.53.1 |
| 导航 | Navigation Compose | 2.8.5 |
| 分页 | Paging 3 | 3.3.4 |
| 相机 | CameraX | 1.4.1 |
| EXIF | ExifInterface | 1.3.7 |
| 设置 | DataStore Preferences | 1.1.1 |

---

## 数据库结构

### Version 2 (当前)

**images 表：**
- id, title, description, filename, filePath, mediumPath, thumbnailPath
- width, height, fileSize, mimeType, url
- ratingAvg, ratingCount, likesCount, commentsCount, score
- dominantColor (新增 - 主色调)
- syncStatus, serverId, createdAt, updatedAt

**tags 表：**
- id, name, category, parentId

**image_tags 表：**
- imageId, tagId, source (MANUAL/AI), confidence

**smart_folders 表 (新增)：**
- id, name, rules (JSON), icon, createdAt, updatedAt

**annotations 表 (新增)：**
- id, imageId, content, xRatio, yRatio, createdAt, updatedAt

**其他表：**
- users, boards, board_images, comments, ratings, likes, text_embeddings

---

## 路由说明

| 路由 | 说明 |
|------|------|
| `home` | 首页瀑布流 |
| `search` | 搜索页 |
| `profile` | 收藏板列表 |
| `settings` | 设置页 |
| `image/{imageId}` | 图片详情 |
| `upload` | 上传图片 |
| `preview/{startIndex}?imageIds={ids}` | 全屏预览 |
| `board/{boardId}` | 收藏板详情 |
| `smart_folders` | 智能文件夹列表 |
| `smart_folder/{folderId}` | 智能文件夹详情 |
| `duplicates` | 重复图片检测 |
| `compare/{id1}/{id2}` | 图片对比 |
| `login` | 登录 |
| `register` | 注册 |

---

## 联系与支持

- 项目仓库：`cospose-gallery`
- 服务器部署：参考 `cospose-gallery/DEPLOY.md`
- 问题反馈：提交 GitHub Issue
