# 物品管理

物品记是一款使用Jetpack Compose开发的单Activity的小工具。所有记录都保存在手机本地。欢迎大家使用。

## 主要功能

- 📝 物品信息记录（名称、过期时间、分类等）
- 📷 支持拍照或从相册添加物品图片，单个物品最多 5 张
- 🏷️ 自定义分类管理
- ⏰ 过期时间自动追踪和提醒
- 🔍 快速搜索和筛选功能
- 📊 物品状态概览
- 📦 支持 CSV 数据导入导出，导出物品时会同步导出图片文件

## 技术栈

- **Kotlin** - 开发语言
- **Jetpack Compose** - 现代化 UI
- **Room** - 本地数据库

## 项目环境

- Android Studio 2024.3.2
- JDK 11 或更高版本
- Gradle-8.11.1

## 快速开始

1. 克隆项目到本地
2. 使用 Android Studio 打开项目
3. 修改gradle-wrapper.properties中的gradle路径
4. 等待 Gradle 同步完成
5. 连接设备或启动模拟器
6. 运行应用

## 数据备份与恢复

- 导出物品 CSV 时，应用会在导出目录下生成同名图片文件夹，并复制所有物品图片。
- 恢复数据时，先导入物品 CSV，再在导入页面点击“恢复图片”，选择对应的图片文件夹。
- 图片会恢复到应用内部的 `item/images` 目录中。

## 应用截图
![1.jpg](https://cdn.nlark.com/yuque/0/2025/png/12600036/1761379915132-f505fcd7-d6fd-41d9-b89f-da75b428ce9b.png?x-oss-process=image%2Fformat%2Cwebp) ![2.jpg](https://cdn.nlark.com/yuque/0/2025/png/12600036/1761379930940-95114629-5420-4e0c-ba5a-52940a9f9c6d.png?x-oss-process=image%2Fformat%2Cwebp)
## 许可证

本项目采用 MIT 许可证。
